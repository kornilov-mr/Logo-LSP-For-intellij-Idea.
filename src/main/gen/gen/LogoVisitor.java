// Generated from C:/Users/korni/IdeaProjects/LogoSupport/src/main/java/org/example/project/parser/Logo.g4 by ANTLR 4.13.2
package gen;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LogoParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LogoVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LogoParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(LogoParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link LogoParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(LogoParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LogoParser#procedureDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedureDefinition(LogoParser.ProcedureDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LogoParser#parameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParameter(LogoParser.ParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link LogoParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(LogoParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LogoParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtom(LogoParser.AtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link LogoParser#list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitList(LogoParser.ListContext ctx);
}