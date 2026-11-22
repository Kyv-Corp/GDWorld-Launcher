# AdditionalHash

This historical documentation does not apply to LaunchServer v5.2+. Use the current [PasswordVerifier documentation](https://gravitlauncher.com/auth/#%D0%BA%D0%BE%D0%BD%D1%84%D0%B8%D0%B3%D1%83%D1%80%D0%B0%D1%86%D0%B8%D1%8F-passwordverifier).

Adds AuthProvider types for CMS password hashes, including WordPress and newer DLE versions. In most cases the request AuthProvider can replace this module.

| CMS | Type |
| --- | --- |
| DLE11+ | mysql-bcrypt |
| WordPress | mysql-phphash |

1. Copy AdditionalHash_module.jar to /LaunchServer/modules/.
2. Download jbcrypt-0.4.jar into /LaunchServer/libraries/ using the commands below.
3. Configure the auth provider with type mysql-bcrypt or mysql-phphash and your MySQL/MariaDB connection.

address is the database host; port is its port; username/password are database credentials; database selects the database. In query, replace dle_users with your account table. The example requires a permission column; add it with the SQL below if absent. Example credentials are placeholders.

## Configuration and command examples

```sh
wget https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar
mv jbcrypt-0.4.jar /LaunchServer/libraries/
```

```json
{
  "provider": {
    "type": "mysql-bcrypt",
    "mySQLHolder": {
      "address": "localhost",
      "port": 3306,
      "username": "root",
      "password": "db_password",
      "database": "database?serverTimezone=UTC"
    },
    "query": "SELECT password, name, permission FROM dle_users WHERE (email=? OR name=?)",
    "queryParams": [ "%login%", "%login%" ],
    "usePermission": true,
    "message": "Incorrect password!"
  }
}
```

```sql
ALTER TABLE `dle_users` ADD `permission` TINYINT NOT NULL DEFAULT '0';
```

[jbcrypt-0.4.jar]: https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar
