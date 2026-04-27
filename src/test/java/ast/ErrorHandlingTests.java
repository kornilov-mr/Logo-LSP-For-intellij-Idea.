package ast;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

/**
 * One test per error category handled by the parser / listener.
 * <p>
 * Grammar-level errors are collected by LogoSyntaxErrorCollector (ANTLR syntaxError callback)
 * and flushed into the program scope in exitProgram.
 * <p>
 * Semantic errors are injected as ErrorNode instances by LogoArityResolverListener during
 * arity resolution (resolveSpecialForm, resolveFixedArity, consumeBlock).
 * <p>
 * Error coverage map:
 * <p>
 *  errGrammarUnexpectedToken    → ANTLR reports unexpected '+' token
 *  errGrammarUnclosedList       → ANTLR reports missing ']'
 *  errGrammarMissingEnd         → ANTLR reports missing 'end' for procedure
 *  errSemanticNestedProcedure   → nested TO inside TO → ErrorNode from enterProcedureDefinition
 *  errSemanticMissingArg        → forward/right/sum with too few args → ErrorNode in resolveFixedArity
 *  errSemanticMakeNoVarName     → bare 'make' with nothing following → ErrorNode (Error 8)
 *  errSemanticMakeInvalidVar    → make 42 100 → ErrorNode (Error 9)
 *  errSemanticMakeMissingValue  → make "x with no value → ErrorNode (Error 10)
 *  errSemanticIfMissingBlock    → if cond with no '[' body → ErrorNode (Error 12)
 *  errSemanticIfelseMissingElse → ifelse cond [then] with no '[' else → ErrorNode (Error 13)
 *  errSemanticRepeatMissingCount → repeat [block] → ErrorNode (Error 14)
 *  errSemanticWhileMissingBlock → while cond with no '[' body → ErrorNode (Error 12)
 */
public class ErrorHandlingTests extends ParserAbstractTest {

    // --- Grammar-level errors ---

    private final File errGrammarUnexpectedToken =
            new File("src/test/resources/ast/programs/withSyntaxError/errGrammarUnexpectedToken.logo");
    private final File errGrammarUnclosedList =
            new File("src/test/resources/ast/programs/withSyntaxError/errGrammarUnclosedList.logo");
    private final File errGrammarMissingEnd =
            new File("src/test/resources/ast/programs/withSyntaxError/errGrammarMissingEnd.logo");

    // --- Semantic errors ---

    private final File errSemanticNestedProcedure =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticNestedProcedure.logo");
    private final File errSemanticMissingArg =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticMissingArg.logo");
    private final File errSemanticMakeNoVarName =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticMakeNoVarName.logo");
    private final File errSemanticMakeInvalidVar =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticMakeInvalidVar.logo");
    private final File errSemanticMakeMissingValue =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticMakeMissingValue.logo");
    private final File errSemanticIfMissingBlock =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticIfMissingBlock.logo");
    private final File errSemanticIfelseMissingElse =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticIfelseMissingElse.logo");
    private final File errSemanticRepeatMissingCount =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticRepeatMissingCount.logo");
    private final File errSemanticWhileMissingBlock =
            new File("src/test/resources/ast/programs/withSyntaxError/errSemanticWhileMissingBlock.logo");

    public ErrorHandlingTests() {
        super(false);
    }

    @Test
    public void grammarUnexpectedToken() throws IOException {
        assertMatchesExpectedParsing(errGrammarUnexpectedToken);
    }

    @Test
    public void grammarUnclosedList() throws IOException {
        assertMatchesExpectedParsing(errGrammarUnclosedList);
    }

    @Test
    public void grammarMissingEnd() throws IOException {
        assertMatchesExpectedParsing(errGrammarMissingEnd);
    }

    @Test
    public void semanticNestedProcedure() throws IOException {
        assertMatchesExpectedParsing(errSemanticNestedProcedure);
    }

    @Test
    public void semanticMissingArg() throws IOException {
        assertMatchesExpectedParsing(errSemanticMissingArg);
    }

    @Test
    public void semanticMakeNoVarName() throws IOException {
        assertMatchesExpectedParsing(errSemanticMakeNoVarName);
    }

    @Test
    public void semanticMakeInvalidVar() throws IOException {
        assertMatchesExpectedParsing(errSemanticMakeInvalidVar);
    }

    @Test
    public void semanticMakeMissingValue() throws IOException {
        assertMatchesExpectedParsing(errSemanticMakeMissingValue);
    }

    @Test
    public void semanticIfMissingBlock() throws IOException {
        assertMatchesExpectedParsing(errSemanticIfMissingBlock);
    }

    @Test
    public void semanticIfElseMissingElse() throws IOException {
        assertMatchesExpectedParsing(errSemanticIfelseMissingElse);
    }

    @Test
    public void semanticRepeatMissingCount() throws IOException {
        assertMatchesExpectedParsing(errSemanticRepeatMissingCount);
    }

    @Test
    public void semanticWhileMissingBlock() throws IOException {
        assertMatchesExpectedParsing(errSemanticWhileMissingBlock);
    }
}
