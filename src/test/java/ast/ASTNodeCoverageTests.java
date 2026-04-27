package ast;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * One test per distinct AST node type or listener code path.
 * <p>
 * Node coverage map:
 * <p>
 *  logoIf               → CallNode (if not yet a SPECIAL_FORM in resolveSpecialForm → IfNode gap)
 *  logoIfElse           → CallNode (ifelse gap → IfElseNode)
 *  logoWhile            → CallNode (while gap → WhileNode)
 *  logoQuotedWord       → VariableRefNode("unknown") (QUOTED_WORD gap → WordLiteral)
 *  logoNestedRepeat     → RepeatNode inside RepeatNode, BlockNode
 *  logoRecursiveProcedure → ProcedureDeclarationNode, recursive CallNode, stop
 *  logoLogical          → CallNode (and, or, not)
 *  logoMathFunctions    → CallNode for sqrt, abs, sin, cos, power, random
 */
public class ASTNodeCoverageTests extends ParserAbstractTest {

    private final File logoIf = new File("src/test/resources/ast/programs/validLogo/logoIf.logo");
    private final File logoIfElse = new File("src/test/resources/ast/programs/validLogo/logoIfElse.logo");
    private final File logoWhile = new File("src/test/resources/ast/programs/validLogo/logoWhile.logo");
    private final File logoQuotedWord = new File("src/test/resources/ast/programs/validLogo/logoQuotedWord.logo");
    private final File logoNestedRepeat = new File("src/test/resources/ast/programs/validLogo/logoNestedRepeat.logo");
    private final File logoRecursiveProcedure = new File("src/test/resources/ast/programs/validLogo/logoRecursiveProcedure.logo");
    private final File logoLogical = new File("src/test/resources/ast/programs/validLogo/logoLogical.logo");
    private final File logoMathFunctions = new File("src/test/resources/ast/programs/validLogo/logoMathFunctions.logo");

    public ASTNodeCoverageTests() {
        super(false);
    }

    /** if :x [body] — should produce IfNode; currently produces CallNode */
    @Test
    public void convertIf() throws IOException {
        super.assertMatchesExpectedParsing(logoIf);
    }

    /** ifelse :a [then] [else] — should produce IfElseNode; currently produces CallNode */
    @Test
    public void convertIfElse() throws IOException {
        super.assertMatchesExpectedParsing(logoIfElse);
    }

    /** while :count [body] — should produce WhileNode; currently produces CallNode */
    @Test
    public void convertWhile() throws IOException {
        super.assertMatchesExpectedParsing(logoWhile);
    }

    /** print "hello / make "label "world — should produce WordLiteral; currently VariableRefNode("unknown") */
    @Test
    public void convertQuotedWord() throws IOException {
        super.assertMatchesExpectedParsing(logoQuotedWord);
    }

    /** repeat 4 [ repeat 3 [...] ] — RepeatNode containing RepeatNode, nested BlockNode */
    @Test
    public void convertNestedRepeat() throws IOException {
        super.assertMatchesExpectedParsing(logoNestedRepeat);
    }

    /** to drawSquare / to shrinkingSquares — ProcedureDeclarationNode, recursive CallNode */
    @Test
    public void convertRecursiveProcedure() throws IOException {
        super.assertMatchesExpectedParsing(logoRecursiveProcedure);
    }

    /** and :a :b / or :a :c / not :c — CallNode for logical builtins */
    @Test
    public void convertLogicalOperators() throws IOException {
        super.assertMatchesExpectedParsing(logoLogical);
    }

    /** sqrt / abs / sin / cos / power / random — CallNode for math builtins */
    @Test
    public void convertMathFunctions() throws IOException {
        super.assertMatchesExpectedParsing(logoMathFunctions);
    }
}
