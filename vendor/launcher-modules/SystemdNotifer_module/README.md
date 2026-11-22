# SystemdNotifer

Notifies systemd when LaunchServer is ready, allowing correct service startup ordering.

1. Copy SystemdNotifer_module.jar to /LaunchServer/modules/.
2. Install screen: `sudo apt install screen` on Debian-family systems, or `sudo yum install screen` on CentOS.
3. Create /etc/systemd/system/Launcher.service using the example below.
4. Set Description to the service description, WorkingDirectory to LaunchServer's absolute directory, User to the service account and Group to its group (often matching the username).
5. Run `systemctl daemon-reload`, `systemctl enable Launcher`, then `systemctl start Launcher`.
6. Check `systemctl status Launcher`; the sample output below shows the expected active state.

## Configuration and command examples

```
● Launcher.service - LaunchServer
   Loaded: loaded (/etc/systemd/system/launcher.service; enabled; vendor preset: disabled)
   Active: active (running)
   CGroup: /system.slice/Launcher.service
           ├─36023 /usr/bin/SCREEN -DmS LaunchServer /usr/bin/java -Xmx512M -javaagent:LaunchServer.jar -jar LaunchServer.jar
           └─36024 /usr/bin/java -Xmx512M -javaagent:LaunchServer.jar -jar LaunchServer.jar
systemd[1]: Starting LaunchServer...
systemd[1]: Started LaunchServer.
```

```bash
[Unit]
Description=LaunchServer
After=network.target

[Service]
WorkingDirectory=/home/launchserver/
Type=notify
User=launchserver
Group=servers
NotifyAccess=all
Restart=always

ExecStart=/usr/bin/screen -DmS launchserver /usr/bin/java -Xmx512M -javaagent:LaunchServer.jar -jar LaunchServer.jar
ExecStop=/usr/bin/screen -p 0 -S launchserver -X eval 'stuff "stop"\015'

[Install]
WantedBy=multi-user.target
```
