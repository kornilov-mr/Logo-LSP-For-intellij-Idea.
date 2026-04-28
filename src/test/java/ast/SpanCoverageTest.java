package ast;

import org.example.communication.DTO.Position;
import org.example.project.FileNode;
import org.example.project.ast.ASTNode;
import org.example.project.ast.ConvertToAST;
import org.example.project.ast.ProgramNode;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * For every .logo file directly inside the programs/ resource directory:
 *
 *   1. Parse it.
 *   2. Collect every node from ProgramNode.getDescendants() that has a non-null range.
 *   3. Sort nodes by (start.line, start.character).
 *   4. For each consecutive pair (A, B) where A.range.end < B.range.start, extract
 *      the source text in that gap and assert it is pure whitespace.
 *
 * A non-whitespace gap means a source token is not covered by any AST node's span —
 * a range calculation bug in the listener.  Overlapping spans (parent/child) produce a
 * zero-or-negative gap and are skipped automatically.
 *
 * All failing files are collected before the assertion so a single run reports every
 * problem at once.
 */
class SpanCoverageTest {

    @Test
    void noBlanksBetweenSpans() throws IOException {
        File dir = new File("src/test/resources/ast/programs");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".logo"));
        assertNotNull(files, "Programs directory not found: " + dir.getAbsolutePath());

        List<String> failures = new ArrayList<>();
        for (File logoFile : files) {
            String source = Files.readString(logoFile.toPath());
            ProgramNode program = ConvertToAST.convert(
                    new FileNode(source));

            List<ASTNode> nodes = program.getDescendants().stream()
                    .filter(n -> n.range != null
                            && n.range.start != null
                            && n.range.end != null)
                    .sorted(Comparator
                            .comparingInt((ASTNode n) -> n.range.start.line)
                            .thenComparingInt(n -> n.range.start.character))
                    .toList();

            for (int i = 0; i + 1 < nodes.size(); i++) {
                ASTNode a = nodes.get(i);
                ASTNode b = nodes.get(i + 1);

                int endOffset   = toOffset(source, a.range.end);
                int startOffset = toOffset(source, b.range.start);
                if (startOffset <= endOffset) continue; // overlap or adjacent

                String gap = source.substring(endOffset, startOffset);
                if (!gap.isBlank()) {
                    failures.add("'" + logoFile.getName() + "':\n"
                            + "  A ends   at " + fmt(a.range.end)   + " → " + a + "\n"
                            + "  B starts at " + fmt(b.range.start) + " → " + b + "\n"
                            + "  Gap: \"" + gap.replace("\n", "\\n").replace("\r", "\\r") + "\"");
                    break; // one report per file is enough
                }
            }
        }

        if (!failures.isEmpty()) {
            fail("Span gaps found in " + failures.size() + " file(s):\n\n"
                    + String.join("\n\n", failures));
        }
    }

    // -------------------------------------------------------------------------

    /** Converts an LSP (line, character) position to a character offset in source. */
    private static int toOffset(String source, Position pos) {
        int offset = 0;
        int line = 0;
        for (int i = 0; i < source.length(); i++) {
            if (line == pos.line) return offset + pos.character;
            if (source.charAt(i) == '\n') { line++; offset = i + 1; }
        }
        return offset + pos.character;
    }

    private static String fmt(Position pos) {
        return pos.line + ":" + pos.character;
    }
}
