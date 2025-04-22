package racsu.planet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class BattlyCapePlugin extends JavaPlugin implements CommandExecutor {

    private FileConfiguration config;
    private FileConfiguration usedConfig;
    private String commandName;
    private String token;
    private String codeUrl;
    private String claimUrl;
    private String permission;
    private String bypassPermission = "battlycape.bypass";

    @Override
    public void onEnable() {
        // Guardar el config.yml predeterminado si no existe
        saveDefaultConfig();
        config = getConfig();

        // Cargar configuraciones
        commandName = config.getString("command", "capabattly");
        permission = config.getString("permission", "battlycape.use");
        token = config.getString("token");
        codeUrl = config.getString("url");
        claimUrl = config.getString("claim-url", "https://battlylauncher.com/es/claim?code=");

        // Cargar o crear used.yml
        usedConfig = YamlConfiguration.loadConfiguration(getDataFolder().toPath().resolve("used.yml").toFile());
        if (!usedConfig.contains("used")) {
            usedConfig.set("used", new ArrayList<String>());
            try {
                usedConfig.save(getDataFolder().toPath().resolve("used.yml").toFile());
            } catch (Exception e) {
                getLogger().severe("Error al crear used.yml: " + e.getMessage());
            }
        }

        getLogger().info("BattlyCapePlugin habilitado! Versión 1.0.3");
        try {
            getCommand(commandName).setExecutor(this);
            getLogger().info("Comando /" + commandName + " registrado correctamente con permiso: " + permission);
        } catch (Exception e) {
            getLogger().severe("Error al registrar el comando /" + commandName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("BattlyCapePlugin deshabilitado!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(config.getString("messages.only-players", "§cEste comando solo puede ser ejecutado por jugadores."));
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();

        // Verificar el permiso para usar el comando
        if (!player.hasPermission(permission)) {
            player.sendMessage(config.getString("messages.no-permission", "§cNo tienes permiso para usar este comando."));
            return true;
        }

        // Verificar si el jugador tiene el permiso de bypass
        boolean hasBypass = player.hasPermission(bypassPermission);

        // Si no tiene bypass, comprobar si ya ha generado un código
        if (!hasBypass) {
            List<String> usedList = usedConfig.getStringList("used");
            if (usedList.contains(playerUUID.toString())) {
                player.sendMessage("§cYa has generado un código promocional. No puedes generar otro.");
                return true;
            }
        }

        String username = player.getName();
        getLogger().info("Ejecutando /" + commandName + " para el jugador: " + username);

        // Paso 1: Verificar si el usuario está registrado en Battly Launcher
        try {
            String verifyUrl = "https://api.battlylauncher.com/user/profile-username/" + username;
            getLogger().info("Enviando solicitud GET a: " + verifyUrl);
            String verifyResponse = sendGetRequest(verifyUrl);
            getLogger().info("Respuesta de verificación: " + verifyResponse);

            if (verifyResponse == null || !verifyResponse.contains("\"id\"")) {
                player.sendMessage(config.getString("messages.not-verified", "§cNo eres usuario de Battly Launcher. Descarga el launcher e inténtalo de nuevo."));
                return true;
            }
            player.sendMessage(config.getString("messages.verified", "§aEstás verificado como usuario de Battly Launcher."));
        } catch (Exception e) {
            getLogger().severe("Error al verificar la membresía: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage(config.getString("messages.verify-error", "§cError al verificar la membresía: %error%").replace("%error%", e.getMessage()));
            return true;
        }

        // Paso 2: Generar el código promocional
        try {
            getLogger().info("Enviando solicitud POST a: " + codeUrl);
            String codeResponse = sendPostRequest(codeUrl, token);
            getLogger().info("Respuesta de generación de código: " + codeResponse);

            JSONObject jsonResponse = new JSONObject(codeResponse);
            if (jsonResponse.getString("status").equals("OK")) {
                String code = jsonResponse.getString("code");
                String fullClaimUrl = claimUrl + code;

                // Crear el enlace clickable
                Component linkComponent = Component.text("Click aquí para canjear tu código")
                        .color(NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.openUrl(fullClaimUrl))
                        .hoverEvent(HoverEvent.showText(Component.text("Abre el enlace para canjear tu código: " + code)));

                // Enviar el mensaje con el enlace
                String messageTemplate = config.getString("messages.code-generated", "§aCódigo promocional generado! %link%");
                player.sendMessage(messageTemplate.replace("%link%", "").replace("§", "&"));
                player.sendMessage(linkComponent);

                // Si no tiene bypass, registrar el uso exitoso
                if (!hasBypass) {
                    List<String> usedList = usedConfig.getStringList("used");
                    usedList.add(playerUUID.toString());
                    usedConfig.set("used", usedList);
                    usedConfig.save(getDataFolder().toPath().resolve("used.yml").toFile());
                }
            } else {
                String errorMessage = jsonResponse.optString("message", "Error desconocido");
                player.sendMessage(config.getString("messages.code-error", "§cError al generar el código promocional: %message%").replace("%message%", errorMessage));
            }
        } catch (Exception e) {
            getLogger().severe("Error al generar el código: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage(config.getString("messages.code-request-error", "§cError al generar el código: %error%").replace("%error%", e.getMessage()));
        }

        return true;
    }

    private String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        getLogger().info("Código de respuesta GET: " + responseCode);

        if (responseCode != 200) {
            throw new Exception("Error en la solicitud GET: Código de respuesta " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }

    private String sendPostRequest(String urlString, String token) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write("".getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        getLogger().info("Código de respuesta POST: " + responseCode);

        if (responseCode != 200) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder errorResponse = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    errorResponse.append(inputLine);
                }
                throw new Exception("Error en la solicitud POST: Código de respuesta " + responseCode + ", Respuesta: " + errorResponse.toString());
            }
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }
}