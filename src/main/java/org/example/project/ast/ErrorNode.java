package org.example.project.ast;

import org.example.communication.DTO.Range;

public class ErrorNode extends ASTNode {
    private final String message;

    public ErrorNode(String message, Range span) {
        super(span);
        this.message = message;
    }

    public String getMessage() { return message; }

    @Override
    public String toString() {
        return "Error(" + message + ")";
    }
}