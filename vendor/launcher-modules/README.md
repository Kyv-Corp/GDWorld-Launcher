# LauncherModules

This is an upstream submodule. Compiled files are available in the main [GravitLauncher] repository's releases.

Public modules for different GravitLauncher functions. Each module directory contains its configuration and instructions. Some GitHub instructions may be outdated; the [Wiki] is the primary documentation source.

## LauncherRuntime modules

These use the `_lmodule` suffix and go in `launcher-modules/`. Obtain them from LauncherModules.zip in [Releases], or build from source at `src/modules/<ModuleName>_lmodule/build/libs/<ModuleName>_lmodule.jar`.

| Module | Purpose |
| --- | --- |
| [DiscordGame] | Discord game activity. |
| [LauncherGuard] | Native protection support. |
| [LauncherStartScreen] | Splash screen before runtime initialization. |
| [Sentry] | Launcher error tracking with Sentry. |

## LaunchServer modules

These use the `_module` suffix and go in `modules/`. Obtain them from LaunchServerModules.zip in [Releases], or build from source at `src/modules/<ModuleName>_module/build/libs/<ModuleName>_module.jar`.

| Module | Purpose |
| --- | --- |
| [AdditionalHash] | PHPASS password hashing; see the Wiki's PasswordVerifier configuration. |
| [DiscordBotConsole] | Interact with LaunchServer through Discord. |
| [FileAuthSystem] | JSON-file user database; recommended for development; see the Wiki's FileAuthSystem method. |
| [GenerateCertificate] | Generate launcher JAR/EXE signing certificates. |
| [MirrorHelper] | Prepare clients with the GravitLauncher authlib patch. |
| [MojangSupport] | Mojang/Microsoft premium account support. |
| [OpenSSLSignCode] | Sign EXE files with your certificate. |
| [RemoteControl] | Execute LaunchServer console commands over HTTP. |
| [S3Updates] | Synchronize updates/ with S3 object storage. |
| [SentryProGuardUpload] | Deobfuscate incoming launcher stack traces through uploaded mappings. |
| [Sentry LaunchServer] | LaunchServer error tracking with Sentry. |
| [SystemdNotifer] | Correct LaunchServer startup ordering under systemd. |
| [TelegramBotConsole] | Interact with LaunchServer through Telegram. |
| [UnsafeCommandPack] | Additional GravitLauncher commands. |

[GravitLauncher]: https://github.com/GravitLauncher/Launcher
[Launcher]: https://github.com/GravitLauncher/LauncherRuntime
[LaunchServer]: https://github.com/GravitLauncher/Launcher
[Wiki]: https://gravitlauncher.com
[Releases]: https://github.com/GravitLauncher/Launcher/releases

[AdditionalHash]: https://github.com/GravitLauncher/LauncherModules/tree/master/AdditionalHash_module
[DiscordBotConsole]: https://github.com/GravitLauncher/LauncherModules/tree/master/DiscordBotConsole_module
[DiscordGame]: https://github.com/GravitLauncher/LauncherModules/tree/master/DiscordGame_lmodule
[FileAuthSystem]: https://github.com/GravitLauncher/LauncherModules/tree/master/FileAuthSystem_module
[GenerateCertificate]: https://github.com/GravitLauncher/LauncherModules/tree/master/GenerateCertificate_module
[LauncherGuard]: https://github.com/GravitLauncher/LauncherModules/tree/master/LauncherGuard_lmodule
[LauncherStartScreen]: https://github.com/GravitLauncher/LauncherModules/tree/master/LauncherStartScreen_lmodule
[MirrorHelper]: https://github.com/GravitLauncher/LauncherModules/tree/master/MirrorHelper_module
[MojangSupport]: https://github.com/GravitLauncher/LauncherModules/tree/master/MojangSupport_module
[OpenSSLSignCode]: https://github.com/GravitLauncher/LauncherModules/tree/master/OpenSSLSignCode_module
[RemoteControl]: https://github.com/GravitLauncher/LauncherModules/tree/master/RemoteControl_module
[S3Updates]: https://github.com/GravitLauncher/LauncherModules/tree/master/S3Updates_module
[SentryProGuardUpload]: https://github.com/GravitLauncher/LauncherModules/tree/master/SentryProGuardUpload_module
[Sentry]: https://github.com/GravitLauncher/LauncherModules/tree/master/Sentry_lmodule
[Sentry LaunchServer]: https://github.com/GravitLauncher/LauncherModules/tree/master/Sentry_module
[SystemdNotifer]: https://github.com/GravitLauncher/LauncherModules/tree/master/SystemdNotifer_module
[TelegramBotConsole]: https://github.com/GravitLauncher/LauncherModules/tree/master/TelegramBotConsole_module
[UnsafeCommandPack]: https://github.com/GravitLauncher/LauncherModules/tree/master/UnsafeCommandPack_module
