package org.example.server.handlers;

import org.example.communication.DTO.CompletionItem;
import org.example.communication.DTO.CompletionItemKind;
import org.example.communication.DTO.CompletionList;
import org.example.communication.requests.CompletionParams;
import org.example.project.FileNode;
import org.example.project.ProjectContext;
import org.example.project.StandardFunctionDeclaration;
import org.example.project.UserVariableDeclaration;
import org.example.project.ast.ASTNode;
import org.example.project.ast.ProgramNode;
import org.example.project.staticAnalyser.LocalScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles document completion requests as part of the Language Server Protocol (LSP).
 * <p>
 * The {@code TextDocumentCompletionHandler} class extends {@link LSPHandler}
 * to provide functionality for processing {@link CompletionParams} and generating
 * a corresponding {@link CompletionList}.
 * <p>
 * This handler is responsible for analyzing the context of the text document and position
 * provided in the {@link CompletionParams}, extracting relevant information such as:
 * - The local scope of variables and function declarations.
 * - User-defined variables and functions.
 * - Standard function declarations.
 * <p>
 * Based on the context, it constructs a {@link CompletionList} containing {@link CompletionItem}s
 * that represent the possible completions at the given position in the document.
 * These items are categorized as variables, functions, etc.
 */
public class TextDocumentCompletionHandler extends LSPHandler<CompletionParams, CompletionList> {

    @Override
    protected CompletionList handle(CompletionParams params) {
        FileNode fileNode = ProjectContext.getFileNode(params.textDocument.uri);
        ProgramNode programNode = fileNode.programNode;
        ASTNode node = programNode.getNodesInSpan(params.position);
        LocalScope scope = node.scope;

        String trigger = params.context != null ? params.context.triggerCharacter : null;
        boolean variableOnly = ":".equals(trigger) || "\"".equals(trigger);

        List<CompletionItem> items = new ArrayList<>();

        for (Map.Entry<String, UserVariableDeclaration> pair : scope.declaredVariables.entrySet()) {
            CompletionItem item = new CompletionItem(pair.getValue().name);
            item.kind = CompletionItemKind.Variable;
            items.add(item);
        }

        if (!variableOnly) {
            scope.localFunctionDeclarations.declarationsMap.forEach((name, declaration) -> {
                CompletionItem item = new CompletionItem(declaration.name);
                item.kind = CompletionItemKind.Function;
                items.add(item);
            });

            StandardFunctionDeclaration.getDeclaration().declarationsMap.forEach((name, declaration) -> {
                CompletionItem item = new CompletionItem(name);
                item.kind = CompletionItemKind.Function;
                items.add(item);
            });
            //Since we also have keywords
            items.add(new CompletionItem("to"));
            items.add(new CompletionItem("end"));
        }

        return new CompletionList(false, null, items.toArray(new CompletionItem[0]));
    }
}
