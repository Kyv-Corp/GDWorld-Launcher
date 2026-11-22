# Sentry LaunchServer

Integrates [LaunchServer](https://github.com/GravitLauncher/Launcher) with Sentry to track errors. Launcher errors include stack traces and additional diagnostic information to help identify, reproduce and resolve failures.

1. Copy Sentry_module.jar into /LaunchServer/modules/, or create the symlink shown below.
2. Start and stop LaunchServer.jar to generate configuration.
3. Create an account and a Java project at Sentry.io.
4. Copy the DSN from Settings → Projects → your project → Client Keys (DSN), or https://<ORGANIZATION>.sentry.io/settings/sentry/projects/<PROJECT>/keys/.
5. Replace YOUR_DSN in /LaunchServer/config/SentryServerModule/Config.json with your DSN.

Account language, timezone and theme settings are available under [Account Details](https://sentry.io/settings/account/details/).

For deobfuscation of launcher stack traces, see [SentryProGuardUpload](https://github.com/GravitLauncher/LauncherModules/tree/master/SentryProGuardUpload_module).

## Configuration and command examples

```
     cd modules
     ln -s ../src/modules/Sentry_module/build/libs/Sentry_module.jar
     ```

```json
  {
    "dsn": "YOUR_DSN",
    "sampleRate": 1.0,
    "enableTracing": false,
    "tracingSampleRate": 1.0,
    "addSentryAppender": true,
    "filterExceptions": true,
    "requestTracker": true,
    "captureRequestData": false,
    "captureRequestError": false,
    "appenderLogLevel": "ERROR",
    "ignoreErrors": [
      "auth.wrongpassword", "auth.require2fa", "auth.usernotfound", "auth.require.factor."
    ]
  }
  ```

![sentry-java](https://user-images.githubusercontent.com/12544425/236633515-88d3d837-35c0-47e4-a4c1-765d9cb152bc.png)
