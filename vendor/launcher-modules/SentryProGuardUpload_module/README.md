# SentryProGuardUpload

Uploads ProGuard mappings when compiling the launcher so Sentry can deobfuscate incoming launcher stack traces. Installed on LaunchServer as an addition to [Sentry_lmodule](https://github.com/GravitLauncher/LauncherModules/tree/master/Sentry_lmodule). Requires enabled ProGuard and mapping-file generation.

Install [Sentry CLI](https://docs.sentry.io/product/cli/installation/), which uploads mappings. A historical Linux install command and symlink example are below. [gravit-eggs](https://github.com/gravit-core/gravit-eggs) provides a Pterodactyl egg containing an automatically updated sentry-cli.

1. Copy SentryProGuardUpload_module.jar to /LaunchServer/modules/, or create the shown symlink.
2. Start and stop LaunchServer.jar to generate configuration.
3. Create an access token at https://<ORGANIZATION>.sentry.io/settings/account/api/auth-tokens/ with project:read, project:releases and org:read scopes.
4. Add `-useuniqueclassmembernames` to /LaunchServer/proguard/proguard.config.
5. Set stripLineNumbers=false in /LaunchServer/LaunchServer.json.
6. Set authToken to that token, org to your organization and project to your launcher project.

Configuration: /LaunchServer/config/SentryProGuardUpload/Config.json. All token/organization/project values below are placeholders.

## Configuration and command examples

```bash
    curl -sL https://sentry.io/get-cli/ | sh
    ```

```
     cd modules
     ln -s ../src/modules/SentryProGuardUpload_module/build/libs/SentryProGuardUpload_module.jar
     ```

```
   https://<ORGANIZATION>.sentry.io/settings/account/api/auth-tokens/
   ```

```
     project:read
     project:releases
     org:read
     ```

```json
  {
    "sentryCliPath": "sentry-cli",
    "authToken": "<YOUR_TOKEN>",
    "org": "<ORGANIZATION>",
    "project": "<LAUNCHER_PROJECT>",
    "url": "",
    "mappingPath": "proguard/mappings.pro",
    "customArgs": [],
    "customArgsBefore": []
  }
  ```

![sentry-cli](https://user-images.githubusercontent.com/12544425/236628763-dbbf9a6d-f74c-40a8-a222-7a636c51c53f.png)
