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

**PLUGIN ORIGINALMENTE HECHO PARA SU USO EN PLAY.RACSU.COM**
