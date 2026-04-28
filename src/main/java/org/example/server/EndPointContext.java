package org.example.server;

import org.example.communication.LSPAny;
import org.example.communication.NoResponse;
import org.example.communication.notification.DidChangeConfigurationParams;
import org.example.communication.notification.DidChangeTextDocumentParams;
import org.example.communication.notification.DidCloseTextDocumentParams;
import org.example.communication.notification.DidOpenTextDocumentParams;
import org.example.communication.notification.InitializedParams;
import org.example.communication.DTO.CompletionItem;
import org.example.communication.DTO.CompletionList;
import org.example.communication.DTO.DocumentSymbol;
import org.example.communication.DTO.SemanticTokens;
import org.example.communication.requests.CompletionParams;
import org.example.communication.requests.DeclarationParams;
import org.example.communication.requests.DocumentSymbolParams;
import org.example.communication.requests.SemanticTokensParams;
import org.example.communication.requests.HoverParams;
import org.example.communication.requests.InitializeParams;
import org.example.communication.requests.NoParameters;
import org.example.communication.responses.DocumentSymbolList;
import org.example.communication.responses.HoverResult;
import org.example.communication.responses.InitializeResult;
import org.example.communication.responses.NullResult;
import org.example.communication.requests.DocumentDiagnosticParams;
import org.example.communication.responses.DocumentDiagnosticReport;
import org.example.server.handlers.*;

import java.util.HashMap;
import java.util.Map;

public enum EndPointContext {
    textDocument_Hover("textDocument/hover", HoverParams.class, HoverResult.class, new TextDocumentHoverHandler()),
    textDocument_Completion("textDocument/completion", CompletionParams.class, CompletionList.class, new TextDocumentCompletionHandler()),
    completionItem_Resolve("completionItem/resolve", CompletionItem.class, CompletionItem.class, new CompletionItemResolveHandler()),
    textDocument_Declaration("textDocument/declaration", DeclarationParams.class, LSPAny.class, new TextDocumentDeclarationHandler()),
    textDocument_SemanticTokensFull("textDocument/semanticTokens/full", SemanticTokensParams.class, SemanticTokens.class, new TextDocumentSemanticTokensFullHandler()),
    initialize("initialize", InitializeParams.class, InitializeResult.class, new InitializationHandler()),
    initialized("initialized", InitializedParams.class, NoResponse.class, new InitializedHandler()),
    didOpenFile("textDocument/didOpen", DidOpenTextDocumentParams.class, NoResponse.class, new TextDocumentDidOpenHandler()),
    didChangeFile("textDocument/didChange", DidChangeTextDocumentParams.class, NoResponse.class, new TextDocumentDidChangeHandler()),
    didCloseFile("textDocument/didClose", DidCloseTextDocumentParams.class, NoResponse.class, new TextDocumentDidCloseHandler()),
    shutDown("shutdown", NoParameters.class, NullResult.class, new ShutDownHandler()),
    exit("exit", NoParameters.class, NoResponse.class, new ExitHandler()),
    didChangeConfiguration("workspace/didChangeConfiguration", DidChangeConfigurationParams.class, NoResponse.class, new DidChangeConfigurationHandler()),
    textDocument_Diagnostic("textDocument/diagnostic", DocumentDiagnosticParams.class, DocumentDiagnosticReport.class, new TextDocumentDiagnosticHandler()),
    textDocument_DocumentSymbol("textDocument/documentSymbol", DocumentSymbolParams.class, DocumentSymbolList.class, new TextDocumentDocumentSymbolHandler());
    public final static Map<String, Class<? extends LSPAny>> methodToRequestType = new HashMap<>();
    public final String methodName;
    public final Class<? extends LSPAny> request;
    public final Class<? extends LSPAny> response;
    public final LSPHandler<? extends LSPAny, ? extends LSPAny> handler;

    static {
        for (EndPointContext endPointContext : EndPointContext.values()) {
            methodToRequestType.put(endPointContext.methodName, endPointContext.request);
        }
    }

    EndPointContext(String methodName, Class<? extends LSPAny> request, Class<? extends LSPAny> response, LSPHandler<? extends LSPAny, ? extends LSPAny> handler) {
        this.methodName = methodName;
        this.request = request;
        this.response = response;
        this.handler = handler;
    }

    public static Class<? extends LSPAny> getRequestType(String methodName) {
        return methodToRequestType.get(methodName);
    }
}
