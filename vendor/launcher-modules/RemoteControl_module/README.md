# RemoteControl

Executes LaunchServer console commands remotely over HTTP.

1. Copy RemoteControl_module.jar to /LaunchServer/modules/.
2. Restart LaunchServer.
3. Configure the module using commands or its configuration file.

Requests can be made with curl, a browser or a script. The examples below show direct and reverse-proxied addresses; YOUR_ADDRESS and YOUR_TOKEN are placeholders. The pinned upstream configuration example is empty.

## Configuration and command examples

```bash
curl http://YOUR_ADDRESS:9274/webapi/remotecontrol/command?token=YOUR_TOKEN&command=build&log=true
```

```bash
curl https://YOUR_ADDRESS/webapi/remotecontrol/command?token=YOUR_TOKEN&command=build&log=true
```

```

```
