# FileAuthSystem

Stores user accounts in a JSON file.

1. Copy FileAuthSystem_module.jar to /LaunchServer/modules/.
2. Start LaunchServer and run `fileauthsystem install`.
3. Register users with `config auth.std.core register [username] email [password]`.

Enter `config auth.std.core` and press TAB to list commands. autoSave saves the database file when LaunchServer stops. AuthCoreProvider type is fileauthsystem; the install command configures it automatically.

Use `config auth.std.core updateskin` and `config auth.std.core updatecloak` to set skins/capes. Files default to updates/skins/{HASH}. Their URLs are stored in the database file; changing host/IP/port requires updating existing skin/cape URLs.

## Configuration and command examples

```
{
   "autoSave": true
}
```

```json
{
  "type": "fileauthsystem"
}
```
