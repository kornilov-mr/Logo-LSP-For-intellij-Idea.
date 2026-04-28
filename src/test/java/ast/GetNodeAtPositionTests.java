package ast;

import org.example.communication.DTO.Position;
import org.example.project.FileNode;
import org.example.project.ast.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ProgramNode.getNodesInSpan(Position).
 *
 * The method returns the smallest AST node whose span contains the given position.
 * "Smallest" means fewest lines spanned, then fewest characters, so a leaf is
 * preferred over any of its ancestors.
 *
 * Column numbers used in comments below are 0-based LSP positions.
 *
 * Program A:  "forward 100"
 *              0123456789...
 *   CallNode("forward"):  (0,0)–(0,11)
 *   NumberNode("100"):    (0,8)–(0,11)
 *
 * Program B:  "to sq :n\n  forward :n\nend"
 *   line 0: "to sq :n"
 *   line 1: "  forward :n"
 *   line 2: "end"
 *   ProcedureDeclarationNode("sq"):  (0,0)–(2,3)
 *   ParameterNode("n"):              (0,6)–(0,8)
 *   CallNode("forward"):             (1,2)–(1,12)
 *   VariableRefNode("n"):            (1,10)–(1,12)
 */
class GetNodeAtPositionTests {

    private static final String PROGRAM_A = "forward 100";
    private static final String PROGRAM_B = "to sq :n\n  forward :n\nend";

    private static ProgramNode parse(String source) {
        return ConvertToAST.convert(new FileNode(source));
    }

    // -------------------------------------------------------------------------
    // Program A — single-line
    // -------------------------------------------------------------------------

    @Test
    void onLeafNode_returnsLeaf() {
        // Position inside "100" (cols 8–10): NumberNode is smaller than CallNode
        ASTNode result = parse(PROGRAM_A).getNodesInSpan(new Position(0, 9));
        assertInstanceOf(NumberNode.class, result);
    }

    @Test
    void onFunctionName_returnsCallNode() {
        // Position inside "forward" (cols 0–6): no child node covers this range
        ASTNode result = parse(PROGRAM_A).getNodesInSpan(new Position(0, 3));
        assertInstanceOf(CallNode.class, result);
        assertEquals("forward", ((CallNode) result).name);
    }

    @Test
    void atStartOfLeaf_returnsLeaf() {
        // Position exactly at the first character of "100" (col 8)
        ASTNode result = parse(PROGRAM_A).getNodesInSpan(new Position(0, 8));
        assertInstanceOf(NumberNode.class, result);
    }

    @Test
    void atEndOfLeaf_returnsLeaf() {
        // Position exactly at end col of "100" (col 11): end is inclusive in containsPosition
        ASTNode result = parse(PROGRAM_A).getNodesInSpan(new Position(0, 11));
        assertInstanceOf(NumberNode.class, result);
    }

    // -------------------------------------------------------------------------
    // Program B — multi-line procedure
    // -------------------------------------------------------------------------

    @Test
    void insideProcedureBody_returnsInnerCallNotProcedure() {
        // Position inside "forward" on line 1 (col 5): CallNode is smaller than ProcedureDeclarationNode
        ASTNode result = parse(PROGRAM_B).getNodesInSpan(new Position(1, 5));
        assertInstanceOf(CallNode.class, result);
        assertEquals("forward", ((CallNode) result).name);
    }

    @Test
    void onVariableRef_returnsVariableRefNotCallNode() {
        // Position inside ":n" on line 1 (col 11): VariableRefNode is smaller than its parent CallNode
        ASTNode result = parse(PROGRAM_B).getNodesInSpan(new Position(1, 11));
        assertInstanceOf(VariableRefNode.class, result);
        assertEquals("n", ((VariableRefNode) result).name);
    }

    @Test
    void onParameter_returnsParameterNode() {
        // Position inside ":n" on line 0 (col 7): ParameterNode is smaller than ProcedureDeclarationNode
        ASTNode result = parse(PROGRAM_B).getNodesInSpan(new Position(0, 7));
        assertInstanceOf(ParameterNode.class, result);
    }

    @Test
    void onProcedureName_returnsProcedureDeclarationNode() {
        // Position on "sq" (col 3), before any child node starts
        ASTNode result = parse(PROGRAM_B).getNodesInSpan(new Position(0, 3));
        assertInstanceOf(ProcedureDeclarationNode.class, result);
        assertEquals("sq", ((ProcedureDeclarationNode) result).name);
    }

    // -------------------------------------------------------------------------
    // Fallback
    // -------------------------------------------------------------------------

    @Test
    void fallback_whenPositionOutsideAllNodes_returnsNonNull() {
        // Position on a line that does not exist in the program
        ASTNode result = parse(PROGRAM_A).getNodesInSpan(new Position(99, 0));
        assertNotNull(result);
    }
}
