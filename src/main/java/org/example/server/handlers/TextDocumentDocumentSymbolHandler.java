package org.example.server.handlers;

import org.example.communication.DTO.DocumentSymbol;
import org.example.communication.requests.DocumentSymbolParams;
import org.example.communication.responses.DocumentSymbolList;
import org.example.project.FileNode;
import org.example.project.ProjectContext;
import org.example.project.ast.*;

import java.util.ArrayList;
import java.util.List;

public class TextDocumentDocumentSymbolHandler extends LSPHandler<DocumentSymbolParams, DocumentSymbolList> {

    private static final int KIND_FUNCTION = 12;
    private static final int KIND_VARIABLE = 13;

    @Override
    protected DocumentSymbolList handle(DocumentSymbolParams params) {
        FileNode fileNode = ProjectContext.getFileNode(params.textDocument.uri);
        ProgramNode programNode = fileNode.programNode;

        List<DocumentSymbol> symbols = new ArrayList<>();

        for (ASTNode statement : programNode.statements) {
            if (statement instanceof ProcedureDeclarationNode proc) {
                symbols.add(procedureSymbol(proc));
            } else if (statement instanceof VariableDeclaration varDecl) {
                symbols.add(variableSymbol(varDecl));
            }
        }

        return new DocumentSymbolList(symbols);
    }

    private DocumentSymbol procedureSymbol(ProcedureDeclarationNode proc) {
        List<DocumentSymbol> children = new ArrayList<>();
        for (ParameterNode param : proc.parameters) {
            children.add(new DocumentSymbol(
                    ":" + param.name,
                    KIND_VARIABLE,
                    param.getSpan(),
                    param.getSpan(),
                    null
            ));
        }
        return new DocumentSymbol(
                proc.name,
                KIND_FUNCTION,
                proc.getSpan(),
                proc.nameRange,
                children.isEmpty() ? null : children
        );
    }

    private DocumentSymbol variableSymbol(VariableDeclaration varDecl) {
        return new DocumentSymbol(
                varDecl.variableName,
                KIND_VARIABLE,
                varDecl.getSpan(),
                varDecl.nameRange,
                null
        );
    }
}
