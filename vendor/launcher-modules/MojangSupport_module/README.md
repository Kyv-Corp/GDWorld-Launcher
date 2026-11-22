# MojangSupport

Adds Mojang/Microsoft account support to [GravitLauncher].

The pinned upstream instructions name GenerateCertificate_module.jar as the file to place in /LaunchServer/modules/; verify the correct MojangSupport artifact for your distribution before installation. Configure the auth core provider for Microsoft as in the example below.

## Configuration and command examples

```json
  "auth": {
    "microsoft": {
      "core": {
        "type": "microsoft"
      }
    }
  },
```

[GravitLauncher]: https://github.com/GravitLauncher/Launcher
