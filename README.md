# BattlyCapePlugin

**BattlyCapePlugin** es un plugin para servidores de Minecraft (Paper 1.21.4) que permite a los jugadores verificar su membresía en Battly Launcher y generar códigos promocionales únicos. Los códigos están limitados a un solo uso por jugador, con opciones para administradores que deseen generar múltiples códigos. El plugin es completamente configurable y compatible con sistemas de permisos como LuckPerms.

## Características

- **Verificación de Battly Launcher**: Comprueba si un jugador está registrado en Battly Launcher mediante su API.
- **Generación de códigos promocionales**: Genera códigos únicos válidos por 24 horas para Battly Launcher.
- **Límite de uso**: Restringe la generación de códigos a un solo uso por jugador (configurable).
- **Permisos avanzados**: Incluye permisos para controlar quién puede usar el comando y quién puede saltarse el límite de uso.
- **Configuración personalizable**:
  - Mensajes, comando, token y URL configurables a través de `config.yml`.
  - Soporte para colores en los mensajes usando códigos (`§`).
- **Integración con LuckPerms**: Gestiona fácilmente los permisos para diferentes grupos de jugadores.
- **Registro de usos**: Almacena los UUIDs de los jugadores que han generado un código en `used.yml`.

## Requisitos

- **Servidor**: Paper 1.21.4 (puede funcionar en otras versiones de Paper/Spigot, pero no está garantizado).
- **Java**: Java 21 (requerido por Paper 1.21.4).
- **LuckPerms** (opcional, pero recomendado): Para gestionar permisos.

## Instalación

1. **Descarga el plugin**:
   - Descarga el archivo `BattlyCapePlugin-1.0.2.jar` desde la [sección de Releases](https://github.com/racsuline/BattlyCapePlugin/releases).

2. **Coloca el archivo en tu servidor**:
   - Copia el archivo `.jar` a la carpeta `plugins/` de tu servidor Paper.

3. **Reinicia el servidor**:
   - Inicia o reinicia tu servidor para cargar el plugin. Verás un mensaje en la consola: `BattlyCapePlugin habilitado!`.

4. **Configura el plugin**:
   - Un archivo `config.yml` se generará automáticamente en `plugins/BattlyCapePlugin/`.
   - Edita el archivo según tus necesidades (ver sección de Configuración).

## Configuración

El archivo `config.yml` permite personalizar el comportamiento del plugin. Aquí tienes un ejemplo con las opciones disponibles:

```yaml
# Comando para generar códigos promocionales (sin la barra /)
command: capabattly

# Permiso requerido para usar el comando
permission: battlycape.use

# Token de autorización para la API
token: tu_token_aqui

# URL base para generar códigos promocionales
url: tu_url_aqui

# Mensajes (soporta códigos de color con §)
messages:
  only-players: "§cEste comando solo puede ser ejecutado por jugadores."
  no-permission: "§cNo tienes permiso para usar este comando."
  verified: "§aEstás verificado como usuario de Battly Launcher."
  not-verified: "§cNo eres usuario de Battly Launcher. Descarga el launcher e inténtalo de nuevo."
  verify-error: "§cError al verificar la membresía: %error%"
  code-generated: "§aCódigo promocional generado: §f%code%"
  code-error: "§cError al generar el código promocional: %message%"
  code-request-error: "§cError al generar el código: %error%"
