## Build & run

Requires JDK 17+.

```bash
./gradlew test          # run tests (118 cases)
./gradlew shadowJar     # build fat JAR
java -jar app/build/libs/logo-lsp.jar   # start LSP server (stdio)
```

## Connect to an LSP client

### IntelliJ (LSP4IJ plugin)

1. Install the [LSP4IJ](https://plugins.jetbrains.com/plugin/23257-lsp4ij) plugin
2. Settings → Languages & Frameworks → Language Servers → Add
3. Command: `java -jar /path/to/logo-lsp.jar`
4. File association: `*.logo`
