// Generated from C:/Users/korni/IdeaProjects/LogoSupport/src/main/java/org/example/project/parser/Logo.g4 by ANTLR 4.13.2
package gen;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LogoParser}.
 */
public interface LogoListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LogoParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(LogoParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogoParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(LogoParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogoParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(LogoParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogoParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(LogoParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogoParser#procedureDefinition}.
	 * @param ctx the parse tree
	 */
	void enterProcedureDefinition(LogoParser.ProcedureDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogoParser#procedureDefinition}.
	 * @param ctx the parse tree
	 */
	void exitProcedureDefinition(LogoParser.ProcedureDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogoParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(LogoParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogoParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(LogoParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(LogoParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(LogoParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogoParser#atom}.
	 * @param ctx the parse tree
	 */
	void enterAtom(LogoParser.AtomContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogoParser#atom}.
	 * @param ctx the parse tree
	 */
	void exitAtom(LogoParser.AtomContext ctx);
	/**
	 * Enter a parse tree produced by {@link LogoParser#list}.
	 * @param ctx the parse tree
	 */
	void enterList(LogoParser.ListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LogoParser#list}.
	 * @param ctx the parse tree
	 */
	void exitList(LogoParser.ListContext ctx);
}