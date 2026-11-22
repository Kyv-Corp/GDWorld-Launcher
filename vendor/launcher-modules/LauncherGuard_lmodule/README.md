# LauncherGuard

Adds native protection support.

1. Copy LauncherGuard_lmodule.jar to /LaunchServer/launcher-modules/.
2. Place your native guard EXE/DLL files in /LaunchServer/launcher-pack/guard/ARCH-OS/, for example x86-64-mustdie for Windows x64.
3. Configure /LaunchServer/config/LauncherGuard/Config.json as illustrated below.
4. For protectLauncher=true, also enable launcher.alwaysUseRelauncher=true in LaunchServer configuration.
5. Run `build` to rebuild the launcher.

## Configuration and command examples

```json
{
  "files": {
    "x86-64-mustdie": [
      "GravitGuard2.exe",
      "GuardDLL.dll"
    ],
    "x86-mustdie": [
      "GravitGuard2.exe",
      "GuardDLL.dll"
    ]
  },
  "exeFile": {
    "x86-64-mustdie": "GravitGuard2.exe",
    "x86-mustdie": "GravitGuard2.exe"
  },
  "renameExeFile": true,
  "useClasspathProperty": true,
  "protectLauncher": false,
  "nativeAgent": {
    "x86-64-mustdie": "GuardDLL",
    "x86-mustdie": "GuardDLL"
  }
}
```
