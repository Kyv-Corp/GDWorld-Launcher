# OpenSSLSignCode

Signs Launcher.exe with your certificate using osslsigncode.

1. Copy OpenSSLSignCode_module.jar to /LaunchServer/modules/.
2. Create a self-signed certificate or purchase one; see the [Wiki]. Set sign.enabled=true in LaunchServer.json.
3. Install osslsigncode. Debian-family systems: `sudo apt install osslsigncode`. Historical CentOS7 and CentOS8 Stream commands are retained below.
4. Run `build` in LaunchServer; a correctly configured EXE is signed.

Configuration: /LaunchServer/config/OSSLSignCode/Config.json. `osslsignexe [path to input exe] [path to output exe]` manually signs a launch4j-created EXE.

Timestamp servers can make signature sizes differ between the first and second signing calls. If necessary, remove timestampServer from the configuration to disable timestamping, understanding that this changes timestamp behavior.

## Configuration and command examples

```sh
cd /etc/yum.repos.d/
wget https://download.opensuse.org/repositories/home:danimo/CentOS_7/home:danimo.repo
yum install osslsigncode calc
```

```sh
cd /etc/yum.repos.d/
wget -O VortexOBS.repo https://download.opensuse.org/repositories/home:VortexOBS/CentOS_8/home:VortexOBS.repo
dnf -y install osslsigncode
```

```json
{
  "timestampServer": "http://timestamp.globalsign.com/scripts/timstamp.dll",
  "osslsigncodePath": "osslsigncode",
  "customArgs": [],
  "checkSignSize": true,
  "checkCorrectSign": true,
  "checkCorrectJar": true
}
```

```
osslsignexe [path to input exe] [path to output exe] - manually sign an EXE created by launch4j
```

[Wiki]: https://gravitlauncher.com
