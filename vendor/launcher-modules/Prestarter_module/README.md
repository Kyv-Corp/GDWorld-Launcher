# Prestarter ServerModule

Integrates the new prestarter with LaunchServer. The launcher normally ships as a JAR; this module automatically creates a native Windows EXE with a Java downloader. Sources: [LauncherPrestarter](https://github.com/GravitLauncher/LauncherPrestarter).

The module concatenates the built prestarter EXE and launcher JAR, and signs the result when the appropriate signing module is installed. The prestarter is a lightweight Rust/Tauri application with a Svelte frontend, responsible for initial startup, downloading the required Java runtime and preparing the client environment.

1. In a running LaunchServer, run `modules load Prestarter`.
2. Put the LauncherPrestarter binary in the LaunchServer root directory.
3. Restart LaunchServer and run `build`; the EXE appears in updates/.

For design, icon or logic customization, clone the repository using the command below. Its README describes NodeJS/Yarn/Rust/Visual Studio setup and `yarn tauri build`.

## Configuration and command examples

```text
   modules load Prestarter
   ```

```bash
git clone https://github.com/GravitLauncher/LauncherPrestarter.git
```
