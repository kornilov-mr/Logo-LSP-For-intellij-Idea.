# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LogoSupport is a Language Server Protocol (LSP) server for the [Logo programming language](https://en.wikipedia.org/wiki/Logo_(programming_language)). It communicates with LSP-enabled editors (e.g. IntelliJ via the LSP4IJ plugin) over stdio using JSON-RPC, providing diagnostics, completion, hover, go-to-declaration, and semantic tokens for `.logo` files.

## Build & Test

Requires JDK 25. Build tool is Maven.

```bash
mvn test                  # run all tests
mvn -Dtest=ClassName test # run a single test class
mvn clean package         # build fat JAR → target/LogoSupport-1.0-SNAPSHOT.jar
java -jar target/LogoSupport-1.0-SNAPSHOT.jar  # start LSP server (stdio)
```

ANTLR-generated code lives in `src/main/java/gen/` and is committed. If `Logo.g4` is changed, regenerate with ANTLR 4.13.1 and update that directory.

## Architecture

### Request Lifecycle

```
stdin → JsonRPCScanner → LogoLSPServer → MessageDispatcher
          (reads Content-Length framed JSON-RPC)       ↓
                                              EndPointContext (enum)
                                                       ↓
                                              LSPHandler<Req, Res>
                                                       ↓
                                              stdout (JSON-RPC response)
```

`MessageDispatcher` deserializes the incoming JSON into a typed `LSPRequestWrapper`, looks up the matching `EndPointContext` by method name, and delegates to the corresponding handler.

### File Processing Pipeline

Triggered on `textDocument/didOpen` and `textDocument/didChange`:

```
FileNode.processNode()
  └─ ConvertToAST.convert()
       ├─ ANTLR lexer + parser  (Logo.g4 — intentionally permissive grammar)
       ├─ LogoArityResolverListener  (ANTLR listener → builds AST, resolves arity)
       ├─ ScopeBuilder  (walks AST, assigns LocalScope to every ASTNode)
       └─ StaticAnalyzer  (walks AST, emits undefined-function/variable errors)
```

Results (AST + diagnostics) are stored on `FileNode` and read by individual LSP handlers on demand.

### Key Design Decisions

**Two-pass parsing**: The ANTLR grammar (`Logo.g4`) is intentionally weak — it cannot resolve function call arity because user-defined procedures are declared anywhere. `LogoArityResolverListener` does a second pass that consults `FunctionDeclarationTable` (which is pre-populated with builtins from `StandardLogoFunctions`) to determine how many arguments each call consumes and build the typed AST.

**Scope tree**: Every `ASTNode` holds a reference to its `LocalScope`. `ScopeBuilder` constructs the tree while walking the AST; scopes carry declared variables and the set of functions visible at that point. Completion, hover, declaration, and diagnostics all query `LocalScope` rather than the root.

**Error representation**: Both parse errors (`LogoSyntaxErrorCollector`) and static errors (`StaticAnalyzer`) are accumulated on `ProgramNode`. The diagnostic handler reads them together and maps them to LSP `Diagnostic` objects.

**Incremental sync**: `FileNode.applyChanges()` receives LSP `TextDocumentContentChangeEvent` objects and reconstructs the full file content before re-parsing.

### Package Map

| Package | Responsibility |
|---|---|
| `org.example.server` | JSON-RPC I/O, message dispatch, handler base class |
| `org.example.server.handlers` | One class per LSP method |
| `org.example.project` | `FileNode`, `ProjectContext` (file registry), `FunctionDeclarationTable`, `StandardLogoFunctions` |
| `org.example.project.ast` | All AST node types + `ConvertToAST` orchestrator |
| `org.example.project.parser` | ANTLR listener (`LogoArityResolverListener`), error collector |
| `org.example.project.staticAnalyser` | `ScopeBuilder`, `StaticAnalyzer` |
| `org.example.communication` | LSP protocol DTOs (Position, Range, Diagnostic, CompletionItem, …) |
| `gen` | ANTLR-generated lexer/parser (do not edit manually) |

### Testing

Tests live in `src/test/java` and mirror the source package structure under `ast/` and `handlers/`. Logo source programs used as fixtures are in `src/test/resources/ast/programs/`. Expected outputs for comparison tests are stored alongside them. `ParserAbstractTest` is the shared base class for parser/AST tests.

## Connecting to IntelliJ

1. Install the [LSP4IJ](https://plugins.jetbrains.com/plugin/23257-lsp4ij) plugin.
2. Settings → Languages & Frameworks → Language Servers → Add.
3. Command: `java -jar /path/to/LogoSupport-1.0-SNAPSHOT.jar`
4. File association: `*.logo`
