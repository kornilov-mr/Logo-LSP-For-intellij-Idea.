package org.example.project.ast;

import org.example.communication.DTO.Position;
import org.example.communication.DTO.Range;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends ASTNode {
    public final List<ASTNode> statements;
    public final List<ErrorNode> parserErrors = new ArrayList<>();
    public final List<ErrorNode> staticErrors = new ArrayList<>();

    public ProgramNode(Range range, List<ASTNode> statements) {
        super(range);
        this.statements = statements;
    }

    public ASTNode getNodesInSpan(Position position) {
        List<ASTNode> nodes = this.getDescendants();

        ASTNode best = null;
        int bestLines = Integer.MAX_VALUE;
        int bestChars = Integer.MAX_VALUE;

        for (ASTNode node : nodes) {
            Range span = node.getSpan();
            if (!containsPosition(span, position)) continue;

            int lines = span.end.line - span.start.line;
            int chars = span.end.character - span.start.character;

            if (lines < bestLines || (lines == bestLines && chars < bestChars)) {
                best = node;
                bestLines = lines;
                bestChars = chars;
            }
        }

        return best != null ? best : nodes.getLast();
    }

    private static boolean containsPosition(Range span, Position pos) {
        boolean afterStart = span.start.line < pos.line
                || (span.start.line == pos.line && span.start.character <= pos.character);
        boolean beforeEnd = span.end.line > pos.line
                || (span.end.line == pos.line && span.end.character >= pos.character);
        return afterStart && beforeEnd;
    }

@Override
public Range getSpan() {
    return null;
}

@Override
public List<ASTNode> getChildren() {
    return statements;
}

@Override
public String toString() {
    return "ProgramNode{" +
            "statements=" + statements +
            ", parserErrors=" + parserErrors +
            ", staticErrors=" + staticErrors +
            '}';
}
}
