# UnsafeCommandPack

Adds extra commands to [GravitLauncher](https://github.com/GravitLauncher/Launcher).

Copy UnsafeCommandPack_module.jar to /LaunchServer/modules/. No configuration is required.

| Command | Purpose |
| --- | --- |
| `setsecuritymanager [allow, logger, system]` | Call System.setSecurityManager for testing. |
| `sendauth [connectUUID] [username] [auth_id] [client type] (permissions) (client uuid)` | Manually send AuthEvent to a connection, bypassing AuthProvider. |
| `newdownloadasset [version] [dir]` | Download assets for any version directly from Mojang. |
| `newdownloadclient [version] [dir]` | Download a client directly from Mojang; create its profile separately. |
| `patcher [patcher name or class] [path] [test mode(true/false)] (other args)` | Run an ASM patcher: findPacketHack finds packet hacks; findRemote/findDefineClass search for RAT behavior; findSun searches for UnsafeSunAPI; pro.gravit.launchermodules.unsafecommands.patcher.StaticReplacerPatcher searches/replaces INVOKESTATIC calls. |
| `loadjar [jarfile]` | Add a JAR to SystemClassLoader using the javaagent. |
| `registercomponent [name] [classname]` | Register a component by class name. |
