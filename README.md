# GDWorld Launcher

GDWorld launcher sources based on GravitLauncher5.7.14 / JavaRuntime5.0.9.

[Download Windows EXE](https://github.com/Kyv-Corp/GDWorld-Launcher/releases/latest/download/Launcher.exe) · [Download JAR for macOS/Linux](https://github.com/Kyv-Corp/GDWorld-Launcher/releases/latest/download/Launcher.jar) · [Releases and SHA-256](https://github.com/Kyv-Corp/GDWorld-Launcher/releases) · [Android APK and sources](https://github.com/Kyv-Corp/GD-World-Android-Launcher)

## Repository contents

- `gdworld/java`: GDWorld code for the native skin/cape account panel, loading mini-games, translations, profile buttons and runtime patch installer.
- `runtime`: the published FXML, CSS, artwork and all five UI languages.
- `vendor/launcher`: GravitLauncher and LaunchServer core sources.
- `vendor/java-runtime`: the original JavaFX interface sources.
- `vendor/launcher-modules`: the exact official module revision pinned by upstream.
- `vendor/prestarter`: native Windows bootstrap sources, Prestarter2.1.0.
- `UPSTREAM.json`: component versions, links and pinned commits.
- `scripts`: interface build scripts; `tests`: mini-game rules and JavaFX checks with fixture data.

GDWorld extensions are distributed under GPLv3. Upstream licenses and copyright notices are retained. Names, logos and artwork belong to their respective owners; source publication grants no rights to trademarks or third-party images. Documentation, including vendored instructions, is provided in English; upstream code revisions remain pinned.

## How the launcher works

The launcher signs in to GDWorld through Gravit and UnicoreCMS, obtains the pack profile, checks/downloads its files and Java, then starts Minecraft. The account panel reuses the current session to upload/reset skins and capes through the GDWorld API. Mini-games run locally without network access. The launcher settings support Russian, English, Ukrainian, Belarusian and Polish.

Project website: [gd-network.ddns.net](https://gd-network.ddns.net). Microsoft/LittleSkin sign-in is not configured. Private CMS implementation, player databases and private keys are not part of this client repository.

## Building from source

Install JDK21+ (JDK25 is recommended for current Gradle releases), Python3 and Maven/Gradle network access. Run:

```sh
bash scripts/build-runtime.sh
```

The script copies pristine upstream sources into `build`, adds GDWorld sources and two loading-screen hooks, then builds `JavaRuntime.jar` with Gradle. It leaves `vendor` unchanged. Outputs: `build/java-runtime/build/libs/JavaRuntime.jar` and the `build/runtime` theme.

For a full core build, use `vendor/launcher`, first copy `vendor/launcher-modules` into `vendor/launcher/modules`, then run its normal `./gradlew installDist`. See the [official LaunchServer setup guide](https://gravitlauncher.com/quickstart). Install the built JavaRuntime and theme, add `-keep class pro.gravit.launcher.gui.components.LoadingGames { *; }` to ProGuard, configure your own authentication, certificate and project address, then run `build` in LaunchServer. `config/JavaRuntime.json` provides sample public UI settings.

The Windows EXE combines official Prestarter2.1.0 with the signed JAR. `UPSTREAM.json` pins the original Prestarter.exe SHA-256, matching its official release; Rust sources are in `vendor/prestarter`.

Production GDWorld binaries use the project signing key and Gravit's normal obfuscator. A self-build uses your own keys/settings, so byte-for-byte reproducibility with the release is not promised. `PatchRuntime.java` also documents the exact binary-overlay installation logic for the official JavaRuntime.jar; the primary build path here compiles from source.

## Checking a download

Each release contains `Launcher.exe`, `Launcher.jar`, a complete source snapshot and `SHA256SUMS`.

```sh
sha256sum -c SHA256SUMS
```

PowerShell:

```powershell
Get-FileHash .\Launcher.exe -Algorithm SHA256
```

Compare the result for your file. Checksums verify that a download matches the release; source access lets you inspect behavior and build your own version.

## Automatic releases

After a normal Gravit build, the server automatically saves a matching JAR/EXE pair, public sources and SHA-256 values in an immutable build directory. GitHub Actions checks for new builds approximately every five minutes; GitHub can delay schedules. It imports sources into repository history and publishes a release. Manual trigger: **Actions → Publish launcher → Run workflow**. Website buttons use `releases/latest/download`.

Installed launchers update directly through Gravit, independently of GitHub publication. The game server stores no GitHub token: Actions publishes with its temporary `GITHUB_TOKEN`. Documentation changes are also applied to the server's public source staging tree so a later snapshot does not revert them.
