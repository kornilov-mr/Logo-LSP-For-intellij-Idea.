package org.example.project.ast;

import org.antlr.v4.runtime.misc.Pair;
import org.example.communication.DTO.Position;
import org.example.communication.DTO.Range;

import java.util.ArrayList;
import java.util.Comparator;
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
        List<Pair<Pair<Integer, Integer>,ASTNode>> nodesInsideTheSpan = new ArrayList<>();
        for (ASTNode node : nodes) {
            if((node.getSpan().start.line == position.line && node.getSpan().end.line == position.line) &&
            !(node.getSpan().start.character <= position.character && node.getSpan().end.character >= position.character)){
                continue;
            }
            if (node.getSpan().start.line == position.line && node.getSpan().end.line == position.line &&
                    node.getSpan().start.character <= position.character && node.getSpan().end.character >= position.character) {
                    nodesInsideTheSpan.add(new Pair<>(new Pair<>(0, node.getSpan().end.character - node.getSpan().start.character), node));
            }
            Boolean s = node.getSpan().start.line == position.line && node.getSpan().start.character <= position.character;
            Boolean e = node.getSpan().end.line == position.line && node.getSpan().end.character >= position.character;
            Boolean l =node.getSpan().start.line < position.line && node.getSpan().end.line > position.line;
            if((node.getSpan().start.line == position.line && node.getSpan().start.character <= position.character)||
                    (node.getSpan().end.line == position.line && node.getSpan().end.character >= position.character)||
                    (node.getSpan().start.line < position.line && node.getSpan().end.line > position.line)) {
                int lines = node.getSpan().end.line - node.getSpan().start.line;
                int size = node.getSpan().end.character - node.getSpan().start.character;
                nodesInsideTheSpan.add(new Pair<>(new Pair<>(lines, size), node));
            }
        }
        if(nodesInsideTheSpan.isEmpty())
            return nodes.getLast();
        nodesInsideTheSpan.sort(Comparator.comparingInt((Pair<Pair<Integer, Integer>, ASTNode> a) -> a.a.a).thenComparingInt(a -> a.a.b));
        return nodesInsideTheSpan.getFirst().b;
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
