package org.example.communication.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerCapabilities extends org.example.communication.LSPAny {
    @JsonProperty("positionEncoding")
    public PositionEncodingKind positionEncoding;

    @JsonProperty("textDocumentSync")
    public int textDocumentSync;

    @JsonProperty("notebookDocumentSync")
    public Object notebookDocumentSync;

    @JsonProperty("completionProvider")
    public CompletionOptions completionProvider;

    @JsonProperty("hoverProvider")
    public boolean hoverProvider;

    @JsonProperty("signatureHelpProvider")
    public SignatureHelpOptions signatureHelpProvider;

    @JsonProperty("declarationProvider")
    public Object declarationProvider;

    @JsonProperty("definitionProvider")
    public Object definitionProvider;

    @JsonProperty("typeDefinitionProvider")
    public Object typeDefinitionProvider;

    @JsonProperty("implementationProvider")
    public Object implementationProvider;

    @JsonProperty("referencesProvider")
    public Object referencesProvider;

    @JsonProperty("documentHighlightProvider")
    public Object documentHighlightProvider;

    @JsonProperty("documentSymbolProvider")
    public Object documentSymbolProvider;

    @JsonProperty("codeActionProvider")
    public Object codeActionProvider;

    @JsonProperty("codeLensProvider")
    public CodeLensOptions codeLensProvider;

    @JsonProperty("documentLinkProvider")
    public DocumentLinkOptions documentLinkProvider;

    @JsonProperty("colorProvider")
    public Object colorProvider;

    @JsonProperty("documentFormattingProvider")
    public Object documentFormattingProvider;

    @JsonProperty("documentRangeFormattingProvider")
    public Object documentRangeFormattingProvider;

    @JsonProperty("documentOnTypeFormattingProvider")
    public DocumentOnTypeFormattingOptions documentOnTypeFormattingProvider;

    @JsonProperty("renameProvider")
    public Object renameProvider;

    @JsonProperty("foldingRangeProvider")
    public Object foldingRangeProvider;

    @JsonProperty("executeCommandProvider")
    public ExecuteCommandOptions executeCommandProvider;

    @JsonProperty("selectionRangeProvider")
    public Object selectionRangeProvider;

    @JsonProperty("linkedEditingRangeProvider")
    public Object linkedEditingRangeProvider;

    @JsonProperty("callHierarchyProvider")
    public Object callHierarchyProvider;

    @JsonProperty("semanticTokensProvider")
    public SemanticTokensOptions semanticTokensProvider;

    @JsonProperty("monikerProvider")
    public Object monikerProvider;

    @JsonProperty("typeHierarchyProvider")
    public Object typeHierarchyProvider;

    @JsonProperty("inlineValueProvider")
    public Object inlineValueProvider;

    @JsonProperty("inlayHintProvider")
    public Object inlayHintProvider;

    @JsonProperty("diagnosticProvider")
    public DiagnosticOptions diagnosticProvider;

    @JsonProperty("workspaceSymbolProvider")
    public Object workspaceSymbolProvider;

    @JsonProperty("workspace")
    public WorkspaceCapabilities workspace;

    @JsonProperty("experimental")
    public Object experimental;

    public static class WorkspaceCapabilities {
        @JsonProperty("workspaceFolders")
        public WorkspaceFoldersServerCapabilities workspaceFolders;

        @JsonProperty("fileOperations")
        public FileOperationsCapabilities fileOperations;
    }

    public static class FileOperationsCapabilities {
        @JsonProperty("didCreate")
        public FileOperationRegistrationOptions didCreate;

        @JsonProperty("willCreate")
        public FileOperationRegistrationOptions willCreate;

        @JsonProperty("didRename")
        public FileOperationRegistrationOptions didRename;

        @JsonProperty("willRename")
        public FileOperationRegistrationOptions willRename;

        @JsonProperty("didDelete")
        public FileOperationRegistrationOptions didDelete;

        @JsonProperty("willDelete")
        public FileOperationRegistrationOptions willDelete;
    }
}

class PositionEncodingKind {
}

class TextDocumentSyncOptions {
}

class TextDocumentSyncKind {
}

class NotebookDocumentSyncOptions {
}

class NotebookDocumentSyncRegistrationOptions {
}



class HoverOptions {
}

class SignatureHelpOptions {
}

class DeclarationOptions {
}

class DeclarationRegistrationOptions {
}

class DefinitionOptions {
}

class TypeDefinitionOptions {
}

class TypeDefinitionRegistrationOptions {
}

class ImplementationOptions {
}

class ImplementationRegistrationOptions {
}

class ReferenceOptions {
}

class DocumentHighlightOptions {
}

class DocumentSymbolOptions {
}

class CodeActionOptions {
}

class CodeLensOptions {
}

class DocumentLinkOptions {
}

class DocumentColorOptions {
}

class DocumentColorRegistrationOptions {
}

class DocumentFormattingOptions {
}

class DocumentRangeFormattingOptions {
}

class DocumentOnTypeFormattingOptions {
}

class RenameOptions {
}

class FoldingRangeOptions {
}

class FoldingRangeRegistrationOptions {
}

class ExecuteCommandOptions {
}

class SelectionRangeOptions {
}

class SelectionRangeRegistrationOptions {
}

class LinkedEditingRangeOptions {
}

class LinkedEditingRangeRegistrationOptions {
}

class CallHierarchyOptions {
}

class CallHierarchyRegistrationOptions {
}


class SemanticTokensRegistrationOptions {
}

class MonikerOptions {
}

class MonikerRegistrationOptions {
}

class TypeHierarchyOptions {
}

class TypeHierarchyRegistrationOptions {
}

class InlineValueOptions {
}

class InlineValueRegistrationOptions {
}

class InlayHintOptions {
}

class InlayHintRegistrationOptions {
}

class DiagnosticRegistrationOptions {
}

class WorkspaceSymbolOptions {
}

class WorkspaceFoldersServerCapabilities {
}

class FileOperationRegistrationOptions {
}

class LSPAny {
}
