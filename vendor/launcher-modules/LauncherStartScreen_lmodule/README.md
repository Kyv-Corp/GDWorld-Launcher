# LauncherStartScreen

Shows a splash window immediately after launcher startup, before runtime initialization.

1. Copy LauncherStartScreen_lmodule.jar to /LaunchServer/launcher-modules/.
2. Place splash.png at /LaunchServer/launcher-pack/splash.png.
3. Place favicon.ico at /LaunchServer/launcher-pack/favicon.ico.
4. Run `build`.

Configuration: /LaunchServer/config/StartScreen/Config.json. If startup throws ClassNotFoundException for pro.gravit.launchermodules.startscreen.TestConfig, replace TestConfig with Config in /META-INF/MANIFEST.MF.

## Configuration and command examples

```json
{
  "imageURL": "splash.png",
  "faviconURL": "favicon.ico",
  "colorR": 1.0,
  "colorG": 1.0,
  "colorB": 1.0,
  "colorA": 0.0
}
```
