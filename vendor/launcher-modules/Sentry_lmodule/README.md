# Sentry LauncherRuntime

Integrates [LauncherRuntime](https://github.com/GravitLauncher/LauncherRuntime) with Sentry to track errors. Launcher errors include stack traces and additional diagnostic information to help identify, reproduce and resolve failures.

1. Copy Sentry_lmodule.jar into /LaunchServer/launcher-modules/, or create the symlink shown below.
2. Start and stop LaunchServer.jar to generate configuration.
3. Create an account and a Java project at Sentry.io.
4. Copy the DSN from Settings → Projects → your project → Client Keys (DSN), or https://<ORGANIZATION>.sentry.io/settings/sentry/projects/<PROJECT>/keys/.
5. Replace YOUR_DSN in /LaunchServer/config/Sentry/Config.json with your DSN.

Account language, timezone and theme settings are available under [Account Details](https://sentry.io/settings/account/details/).

For deobfuscation of launcher stack traces, see [SentryProGuardUpload](https://github.com/GravitLauncher/LauncherModules/tree/master/SentryProGuardUpload_module).

## Configuration and command examples

```
     cd launcher-modules
     ln -s ../src/modules/Sentry_lmodule/build/libs/Sentry_lmodule.jar
     ```

```json
  {
     "dsn": "YOUR_DSN",
     "collectSystemInfo": true,
     "collectMemoryInfo": true,
     "ignoreErrors": [
        "auth.wrongpassword"
     ]
  }
  ```

![sentry](https://user-images.githubusercontent.com/12544425/236625413-5a7593f3-e5da-4f99-b1df-f1ffb86eb838.jpg)
