
# LSP for Logo programming language


## Features

- **Diagnostics** - parse and static errors (undefined functions, undefined variables) underlined inline.
- **Completion** - typing `:` suggests in-scope variable names; anywhere else suggests built-in commands, user-defined procedures, and keywords.

- **Syntax highlighting** - colors keywords, built-in commands, variables, numbers, strings, operators, and comments via semantic tokens.
- **Go-to-declaration** - jump to the `make`/parameter definition for a `:variable`, or to the `to` block for a procedure name.
- **Hover**  - while hovering over a procedure name, displays it's body, or a description and examples or usage for built-in procedures.
- **Document symbols** - outline view listing all procedures (with their parameters as children) and top-level variables; powers the Structure panel and breadcrumb navigation in IntelliJ.

![logo.gif](logo.gif)![usage example gif.gif](usage%20example%20gif.gif)
## Build & run

Requires JDK 17.

```bash
mvn clean package                 
java -jar target/LogoSupport-1.0-SNAPSHOT.jar   
```

## Connect to an LSP client

### IntelliJ via LSP4IJ

* Install the [LSP4IJ](https://plugins.jetbrains.com/plugin/23257-lsp4ij) plugin from the marketplace. 
* Open **Settings → Languages & Frameworks → Language Servers** and add a new server. 
* Set the server command to `java -jar /absolute/path/to/LogoSupport-1.0-SNAPSHOT.jar`. 
* Add a file-name pattern `*.logo` to associate Logo files with this server.

---

## Project structure

![LSPServerArchitecture.drawio.svg](LSPServerArchitecture.drawio.svg)
```

src/main/java/org/example/
├── server/
│   ├── LogoLSPServer.java          # entry point — reads JSON-RPC from stdin
│   ├── MessageDispatcher.java      # routes messages to handlers by method name
│   ├── EndPointContext.java        # enum of all supported LSP methods
│   ├── JsonRPCScanner.java         # Content-Length framed message reader
│   └── handlers/                  # one class per LSP method
│
├── project/
│   ├── FileNode.java               # open file: text content + parsed AST
│   ├── ProjectContext.java         # in-memory registry of open files
│   ├── FunctionDeclaration.java    # metadata for a function (arity, kind, description)
│   ├── FunctionDeclarationTable.java
│   └── StandardLogoFunctions.java  # 150+ built-in Logo functions
│
├── project/ast/
│   ├── ConvertToAST.java           # orchestrates the full parse → AST → analysis pipeline
│   ├── ASTNode.java                # base class; carries Range + LocalScope
│   └── *.java                      # node types: ProcedureDeclarationNode, CallNode,
│                                   #   RepeatNode, IfNode, IfElseNode, WhileNode,
│                                   #   BinaryExpressionNode, VariableRefNode, …
│
├── project/parser/
│   ├── Logo.g4                     # ANTLR grammar (intentionally permissive)
│   ├── LogoArityResolverListener.java  # ANTLR listener that builds the AST
│   └── LogoSyntaxErrorCollector.java
│
├── project/staticAnalyser/
│   ├── StaticAnalyzer.java         # detects undefined functions and variables
│   └── ScopeBuilder.java           # assigns LocalScope to every ASTNode
│
└── communication/                  # LSP protocol DTOs (Position, Range, Diagnostic, …)

src/main/java/gen/                  # ANTLR-generated code — do not edit manually
src/test/resources/ast/programs/    # Logo source fixtures used by tests
```

---

## How Logo is parsed

Processing is triggered on `textDocument/didOpen` and `textDocument/didChange` and runs entirely inside `ConvertToAST.convert()`.


### Step 1 — ANTLR lex + parse

Antlr is used to create a shallow structure and handle whitespace, unexpected characters, defined listeners, etc.
Because Logo requires additional resolve based on function arity, the [grammar](src/main/antlr4/gen/Logo.g4) for expression is deliberately permissive.
```antlrv4
expression
    : atom+
    ;
```
The source text is fed to the generated `gen.LogoLexer` and `gen.LogoParser`. A `LogoSyntaxErrorCollector` attached to both collects any lex/parse errors. The result is a concrete parse tree where every expression is a flat bag of atoms.

### Step 2 — Arity resolution (`LogoArityResolverListener`)

`ParseTreeWalker` walks the parse tree with this listener. The listener maintains a scope stack (one frame per open procedure/list) and consumes atoms left-to-right to build typed AST nodes.

**Function call resolution** works as follows:

- When the current atom is an `IDENTIFIER` found in `FunctionDeclarationTable`, the listener checks its `Kind`:
  - `SPECIAL_FORM` (`repeat`, `if`, `ifelse`, `while`, `make`) → dedicated parsing logic that constructs the matching AST node type.
  - fixed arity (`arity >= 0`) → `resolveFixedArity`: recursively resolves exactly *N* argument terms.
  - variable arity (`arity == -1`, e.g. `sentence`) → `resolveVariableArity`: consumes atoms on the same line until a known function identifier is encountered.
- Arguments are resolved recursively via `resolveTerm`, which handles one primary (possibly another call) and then applies **precedence climbing** for infix operators (`+`, `-`, `*`, `/`, `=`, `<`, `>`, `<=`, `>=`, `<>`).
- `procedureDefinition` (`to name :params … end`) is registered in `FunctionDeclarationTable` on *enter* so recursive calls and forward references both resolve correctly.
- Lists `[…]` open a new scope frame. If a list is an inline atom inside an expression (e.g. `repeat 4 [forward 100]`) the finished `BlockNode` is queued in `pendingBlocks` and retrieved by the enclosing call's argument resolution.

**Error recovery** — rather than aborting, malformed constructs (missing argument, missing `[`, nested `to`) produce an `ErrorNode` that is inserted in place of the missing node. The enclosing AST node is still constructed, so subsequent handlers degrade gracefully.

### Step 3 — Scope building (`ScopeBuilder`)

Walks the finished AST and attaches a `LocalScope` to every node. Each scope records declared variables (procedure parameters + `make` assignments) and inherits function visibility from its parent. This is what enables completion, hover, and declaration to answer scope-aware questions without re-parsing.

### Step 4 — Static analysis (`StaticAnalyzer`)

A second AST walk checks every `CallNode`, `FunctionRef`, and `VariableRefNode` against the `LocalScope` attached to it. Unresolved names produce `ErrorNode` entries in `ProgramNode.staticErrors`.

### Logo example
```
to drawSquare :size
  repeat 4 [
    forward :size
    right 90
  ]
END
to shrinkingSquares :size
  drawSquare :size
  if :size [
    shrinkingSquares difference :size 10 + 30
  ]
end
make "size 100
make "size 100 + 300
shrinkingSquares :size
forward :size
```