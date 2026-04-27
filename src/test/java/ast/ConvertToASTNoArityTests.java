package ast;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class ConvertToASTNoArityTests extends ParserAbstractTest {

    private final File logoSimple1 = new File("src/test/resources/ast/programs/validLogo/logoSimple1.logo");
    private final File logoExpressions = new File("src/test/resources/ast/programs/validLogo/logoExpressions.logo");
    private final File logoVariableReference = new File("src/test/resources/ast/programs/validLogo/logoVariableReference.logo");
    private final File logoArithmetic = new File("src/test/resources/ast/programs/validLogo/logoArithmetic.logo");
    private final File logoRepeat = new File("src/test/resources/ast/programs/validLogo/logoRepeat.logo");
    private final File logoMake = new File("src/test/resources/ast/programs/validLogo/logoMake.logo");
    private final File logoVariableArity = new File("src/test/resources/ast/programs/validLogo/logoVariableArity.logo");
    private final File logoProcedure       = new File("src/test/resources/ast/programs/validLogo/logoProcedure.logo");
    private final File logoArityResolve1    = new File("src/test/resources/ast/programs/validLogo/logoArityResolve1.logo");

    public ConvertToASTNoArityTests() {
        super(false);
    }

    @Test
    public void convertSimpleLogoWithNoArityResolve() throws IOException {
        super.assertMatchesExpectedParsing(logoSimple1);
    }
    @Test
    public void convertLogoExpressionsWithNoArityResolve() throws IOException {
        super.assertMatchesExpectedParsing(logoExpressions);
    }
    @Test
    public void convertLogoVariableReference() throws IOException {
        super.assertMatchesExpectedParsing(logoVariableReference);
    }

    @Test
    public void convertMakeSyntaxes() throws IOException {
        super.assertMatchesExpectedParsing(logoMake);
    }

    @Test
    public void convertArithmeticOperations() throws IOException {
        super.assertMatchesExpectedParsing(logoArithmetic);
    }

    @Test
    public void convertVariableArityFunctions() throws IOException {
        super.assertMatchesExpectedParsing(logoVariableArity);
    }


    @Test
    public void convertRepeatBlock() throws IOException {
        super.assertMatchesExpectedParsing(logoRepeat);
    }


    @Test
    public void convertProcedureDefinition() throws IOException {
        super.assertMatchesExpectedParsing(logoProcedure);
    }

    /* forward sum 10 20  ->  sum is recursively resolved as the argument to forward */
    @Test
    public void convertNestedCalls() throws IOException {
        super.assertMatchesExpectedParsing(logoArityResolve1);
    }
}