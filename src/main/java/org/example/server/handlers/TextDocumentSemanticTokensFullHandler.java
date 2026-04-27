package org.example.server.handlers;

import org.example.communication.DTO.Range;
import org.example.communication.DTO.SemanticTokens;
import org.example.communication.requests.SemanticTokensParams;
import org.example.project.FileNode;
import org.example.project.ProjectContext;
import org.example.project.ast.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles the full semantic tokens request for a text document in a Language Server Protocol (LSP) implementation.
 * <p>
 * This handler processes the {@link SemanticTokensParams} request, which contains information about the text document
 * for which semantic tokenization is required. It analyzes the Abstract Syntax Tree (AST) nodes of the document,
 * extracts semantic token information, organizes it into a sorted list of tokens, and returns the encoded token data
 * packed into a {@link SemanticTokens} response.
 * <p>
 * Semantic tokens represent syntactic or semantic elements such as keywords, variables, functions, or operators
 * with additional modifiers, aiding in features like syntax highlighting.
 * <p>
 * This class builds on the {@link LSPHandler} framework and provides the specific implementation of the
 * {@link #handle(SemanticTokensParams)} method, delegating the actual token extraction and encoding to
 * utility methods.
 * <p>
 * Specific responsibilities of this class include:
 * - Traversing the program's AST to identify relevant tokens.
 * - Categorizing tokens based on their type (e.g., function, variable, keyword).
 * - Encoding token data as a delta-based array for efficient communication.
 */
public class TextDocumentSemanticTokensFullHandler extends LSPHandler<SemanticTokensParams, SemanticTokens> {

    private static final int TYPE_FUNCTION  = 0;
    private static final int TYPE_VARIABLE  = 1;
    private static final int TYPE_NUMBER    = 2;
    private static final int TYPE_STRING    = 3;
    private static final int TYPE_PARAMETER = 4;
    private static final int TYPE_KEYWORD   = 5;
    private static final int TYPE_OPERATOR  = 6;

    private static final int MOD_DECLARATION = 1;

    @Override
    protected SemanticTokens handle(SemanticTokensParams params) {
        FileNode fileNode = ProjectContext.getFileNode(params.textDocument.uri);
        ProgramNode programNode = fileNode.programNode;

        List<int[]> tokens = new ArrayList<>();

        for (ASTNode node : programNode.getDescendants()) {
            if (node.getSpan() == null) continue;
            collectToken(tokens, node);
        }

        tokens.sort(Comparator.comparingInt((int[] t) -> t[0]).thenComparingInt(t -> t[1]));

        return new SemanticTokens(null, encodeDeltas(tokens));
    }

    private void collectToken(List<int[]> tokens, ASTNode node) {
        Range span = node.getSpan();

        switch (node) {
            case CallNode callNode ->
                    add(tokens, span.start.line, span.start.character, callNode.name.length(), TYPE_FUNCTION, MOD_DECLARATION);
            case FunctionRef _ -> add(tokens, span.start.line, span.start.character,
                    span.end.character - span.start.character, TYPE_FUNCTION, MOD_DECLARATION);
            case ProcedureDeclarationNode procDecl -> {
                // 'to' keyword
                add(tokens, span.start.line, span.start.character, "to".length(), TYPE_KEYWORD, 0);
                // procedure name (exact position from parser)
                if (procDecl.nameRange != null) {
                    Range nr = procDecl.nameRange;
                    add(tokens, nr.start.line, nr.start.character,
                            nr.end.character - nr.start.character, TYPE_FUNCTION, MOD_DECLARATION);
                }
                // parameter declarations in the header line
                for (ParameterNode param : procDecl.parameters) {
                    Range pr = param.getSpan();
                    if (pr != null) {
                        add(tokens, pr.start.line, pr.start.character,
                                pr.end.character - pr.start.character, 1, 2);
                    }
                }
                // 'end' keyword: span.end.character points past the last char of END
                add(tokens, span.end.line, span.end.character - "end".length(), "end".length(), TYPE_KEYWORD, 0);
            }
            case VariableDeclaration varDecl -> {
                // 'make' keyword
                add(tokens, span.start.line, span.start.character, "make".length(), TYPE_KEYWORD, 0);
                // variable name: nameRange covers the sigil + name (e.g. "foo or :foo), skip 1 char
                if (varDecl.nameRange != null) {
                    Range nr = varDecl.nameRange;
                    add(tokens, nr.start.line, nr.start.character,
                            varDecl.variableName.length() + 1, TYPE_VARIABLE, 2);
                }
            }
            case VariableRefNode _ -> add(tokens, span.start.line, span.start.character,
                    span.end.character - span.start.character, TYPE_VARIABLE, 2);
            case NumberNode _ -> add(tokens, span.start.line, span.start.character,
                    span.end.character - span.start.character, TYPE_NUMBER, 0);
            case WordLiteral _ -> add(tokens, span.start.line, span.start.character,
                    span.end.character - span.start.character, TYPE_STRING, 0);
            case RepeatNode _ ->
                    add(tokens, span.start.line, span.start.character, "repeat".length(), TYPE_KEYWORD, 0);
            case IfElseNode _ ->
                    add(tokens, span.start.line, span.start.character, "ifelse".length(), TYPE_KEYWORD, 0);
            case IfNode _ -> add(tokens, span.start.line, span.start.character, "if".length(), TYPE_KEYWORD, 0);
            case WhileNode _ ->
                    add(tokens, span.start.line, span.start.character, "while".length(), TYPE_KEYWORD, 0);
            case BinaryExpressionNode binExpr ->
                    add(tokens, span.start.line, span.start.character, binExpr.operator.length(), TYPE_OPERATOR, 0);
            case UnaryExpressionNode unaryExpr ->
                    add(tokens, span.start.line, span.start.character, unaryExpr.operator.length(), TYPE_OPERATOR, 0);
            default -> {
            }
        }
    }

    private void add(List<int[]> tokens, int line, int character, int length, int type, int modifiers) {
        if (length > 0) {
            tokens.add(new int[]{line, character, length, type, modifiers});
        }
    }

    private int[] encodeDeltas(List<int[]> tokens) {
        int[] data = new int[tokens.size() * 5];
        int prevLine = 0;
        int prevChar = 0;
        for (int i = 0; i < tokens.size(); i++) {
            int[] t = tokens.get(i);
            int deltaLine = t[0] - prevLine;
            int deltaChar = deltaLine == 0 ? t[1] - prevChar : t[1];
            data[i * 5]     = deltaLine;
            data[i * 5 + 1] = deltaChar;
            data[i * 5 + 2] = t[2];
            data[i * 5 + 3] = t[3];
            data[i * 5 + 4] = t[4];
            prevLine = t[0];
            prevChar = t[1];
        }
        return data;
    }
}
