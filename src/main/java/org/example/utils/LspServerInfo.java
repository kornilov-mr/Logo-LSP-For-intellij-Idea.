package org.example.utils;

import org.example.communication.DTO.CompletionOptions;
import org.example.communication.DTO.DiagnosticOptions;
import org.example.communication.DTO.SemanticTokensLegend;
import org.example.communication.DTO.SemanticTokensOptions;
import org.example.communication.DTO.ServerCapabilities;
import org.example.communication.DTO.TextDocumentSyncOptions;
import org.example.communication.responses.InitializeResult;

public class LspServerInfo {

    public static final SemanticTokensLegend SEMANTIC_TOKENS_LEGEND = new SemanticTokensLegend(
            new String[]{"function", "variable", "number", "string", "parameter", "keyword", "operator"},
            new String[]{"declaration","static"}
    );

    public static ServerCapabilities getDefaultCapabilities(){
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.hoverProvider = true;
        capabilities.completionProvider = new CompletionOptions(new String[]{":", "\""},new String[]{},true,null);
        capabilities.textDocumentSync = new TextDocumentSyncOptions(true, 2);
        capabilities.semanticTokensProvider = new SemanticTokensOptions(SEMANTIC_TOKENS_LEGEND, true,true);
        capabilities.declarationProvider = true;
        capabilities.diagnosticProvider = new DiagnosticOptions(false, false);
        return capabilities;
    }
    public static InitializeResult.ServerInfo getServerInfo(){
        return new InitializeResult.ServerInfo("logoLsp","1");
    }
}
