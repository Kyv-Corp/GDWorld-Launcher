# MirrorHelper

Semi-automatic Forge, Fabric and Vanilla client preparation with an authlib patch. Requires UnsafeCommandsPack.

1. Install UnsafeCommandsPack, then copy MirrorHelper_module.jar to /LaunchServer/modules/.
2. After first startup, add your CurseForge API key to the module configuration.
3. Prepare the workspace, then run `installclient`.

Workspace: /LaunchServer/config/MirrorHelper/workspace. authlib/ must contain LauncherAuthlib1.jar, LauncherAuthlib2.jar, LauncherAuthlib3.jar, LauncherAuthlib3-1.19.jar and LauncherAuthlib3-1.19.1.jar. installers/ must contain fabric-installer.jar and forge-{VERSION}-installer.jar (for example forge-1.16.5-installer.jar). Forge installation requires a GUI.

workdir/ overlays are applied in order: ALL/, {FABRIC/FORGE/VANILLA}/, lwjgl{2/3}/, java{17/8}/, {VERSION}/ALL/, {VERSION}/{FABRIC/FORGE/VANILLA}/.

The historical Fabric1.19.2 example downloads/caches the client/profile, copies the original client into updates, patches authlib using LauncherAuthlib3-1.19.1.jar, installs Fabric, then overlays ALL/, FABRIC/, lwjgl3/, java17/, 1.18.2/ALL/ and1.18.2/FABRIC/. The1.18.2 paths are retained from that upstream example; adjust them to your target. It then installs configured CurseForge mods, runs deduplibraries and creates a profile.
