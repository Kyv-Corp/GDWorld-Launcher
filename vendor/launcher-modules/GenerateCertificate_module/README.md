# GenerateCertificate

Generates certificates for signing Launcher.jar/Launcher.exe.

1. Copy GenerateCertificate_module.jar to /LaunchServer/modules/.
2. Start LaunchServer.jar and run `generatecertificate` in its console.
3. Stop LaunchServer with `stop`.
4. Copy the generated configuration from the log or terminal.
5. Replace the corresponding LaunchServerConfig.json block with that configuration.

The module can be removed after generating the certificates.
