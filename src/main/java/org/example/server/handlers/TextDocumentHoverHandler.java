package org.example.server.handlers;

import org.example.communication.requests.HoverParams;
import org.example.communication.responses.HoverResult;
import org.example.project.FileNode;
import org.example.project.HoverFormatter;
import org.example.project.ProjectContext;
import org.example.project.ast.ASTNode;
import org.example.project.ast.ProgramNode;

/**
 * A handler for processing hover requests in a Language Server Protocol (LSP) environment.
 * This class facilitates responding to hover interactions within a text document by analyzing
 * the document's content and generating an appropriate hover result.
 *
 * <p>
 * Extends the {@link LSPHandler} class to implement the specific behavior for handling hover requests.
 *
 * <p>
 * The handler retrieves the relevant file node corresponding to the URI provided in the {@link HoverParams}.
 * It then locates the program node and the Abstract Syntax Tree (AST) node at the specified position in the
 * document. The content of the identified AST node is returned as a hover result.
 *
 * <ul>
 * Responsibilities:
 * - Retrieves the file node from the project context using the given document URI.
 * - Accesses the program node of the file.
 * - Identifies the AST node corresponding to the position within the document.
 * - Constructs a {@link HoverResult} containing the representation of the identified AST node.
 *
 * <ul>
 * Key components involved:
 * - {@link HoverParams}: Represents the hover request parameters, including the document
 *   and position details.
 * - {@link HoverResult}: Encapsulates the result of a hover request.
 * - {@link FileNode}: Represents the file within the project context, containing its AST and other metadata.
 * - {@link ProjectContext}: Provides context for accessing project-level resources, including file nodes.
 *
 * @param  {@link HoverParams} Input parameters for the hover request, encapsulated in a {@link HoverParams} object.
 * @return A {@link HoverResult} object containing the hover content derived from the AST node.
 */
public class TextDocumentHoverHandler extends LSPHandler<HoverParams, HoverResult> {
    @Override
    public HoverResult handle(HoverParams hoverParams) {
        FileNode fileNode = ProjectContext.getFileNode(hoverParams.textDocument.uri);
        ProgramNode programNode = fileNode.programNode;
        ASTNode node = programNode.getNodesInSpan(hoverParams.position);
        return new HoverResult(HoverFormatter.format(node, fileNode));
    }
}
