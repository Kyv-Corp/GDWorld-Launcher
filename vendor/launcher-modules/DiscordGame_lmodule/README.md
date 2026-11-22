# DiscordGame

Adds Discord game activity to the launcher. When Discord is running on a player's computer, their account can show that they are playing on your project.

1. Copy DiscordGame_lmodule.jar to /LaunchServer/launcher-modules/.
2. Download the SDK library from [discord-game-sdk4j](https://jitpack.io/com/github/JnCrMx/discord-game-sdk4j/v1.0.0/discord-game-sdk4j-v1.0.0.jar).
3. Place the SDK JAR in /LaunchServer/launcher-libraries/. The historical instructions name discord-game-sdk4j-0.5.x.jar while linking v1.0.0; check the version required by your pinned module.

The configuration below is an example, not a recommended copy-and-paste setup. details is the first activity line after the application name; state is the second. largeImageKey/smallImageKey select the main/thumbnail image (a PNG URL larger than512×512 is also accepted). largeImageText/smallImageText provide their hover text. The Discord application name appears as the game name.

Scopes: login appears during startup before user data; authorized after sign-in; client after game launch. %profileName% expands to the launched client's title.

Create an application at https://discord.com/developers/applications and copy APPLICATION ID into appId. To show a user avatar, option1 is a script serving a512×512 skin avatar. It works only in scopes.authorized/scopes.client, in largeImageKey or smallImageKey, because it uses %username%. Including that placeholder, the URL must not exceed127 bytes; changing the discord-game-sdk4j-0.5.x.jar sources can lift this limit. Option2 uses the JSON TextureProvider method: return an AVATAR URL, shared by the runtime and module, then use %avatarUrl%. The endpoint must always return an avatar, even without a skin. For per-profile images, put %profileUUID% or %profileHash% in scopes.client.largeImageKey or smallImageKey and name the image with the matching UUID/hash. Upload custom images under the application's Rich Presence → Art Assets → Add Image(s); rename them if convenient. The module ignores the application's App Icon.

Placeholders: %uuid% user UUID; %profileVersion% profile version; %profileName% profile title; %profileUUID% profile UUID; %profileHash% profile UUID without hyphens; %username% username; %launcherVersion% launcher version; %javaVersion% active Java version; %javaBits% Java bitness; %os% operating system.

## Configuration and command examples

```json
{
  "enable": true,
  "appId": 810913859371532298,
  "scopes": {
    "login": {
      "details": "The best Minecraft project",
      "state": "Signing in",
      "largeImageKey": "large",
      "smallImageKey": "small",
      "largeImageText": "Everything",
      "smallImageText": "Everything",
      "firstButtonEnable": false,
      "firstButtonName": "Site",
      "firstButtonUrl": "https://example.com",
      "secondButtonEnable": false,
      "secondButtonName": "Discord",
      "secondButtonUrl": "https://example.com"
    },
    "authorized": {
      "details": "The best Minecraft project",
      "state": "Choosing a server",
      "largeImageKey": "large",
      "smallImageKey": "small",
      "largeImageText": "Everything",
      "smallImageText": "Everything",
      "firstButtonEnable": false,
      "firstButtonName": "Site",
      "firstButtonUrl": "https://example.com",
      "secondButtonEnable": false,
      "secondButtonName": "Discord",
      "secondButtonUrl": "https://example.com"
    },
    "client": {
      "details": "The best Minecraft project",
      "state": "Playing on %profileName%",
      "largeImageKey": "large",
      "smallImageKey": "small",
      "largeImageText": "Everything",
      "smallImageText": "Everything",
      "firstButtonEnable": false,
      "firstButtonName": "Site",
      "firstButtonUrl": "https://example.com",
      "secondButtonEnable": false,
      "secondButtonName": "Discord",
      "secondButtonUrl": "https://example.com"
    }
  }
}
```
