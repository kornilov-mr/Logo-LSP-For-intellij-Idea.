package org.example.project.parser;

import gen.LogoBaseListener;
import gen.LogoParser;
import org.example.communication.DTO.Position;
import org.example.communication.DTO.Range;
import org.example.project.FunctionDeclaration;
import org.example.project.FunctionDeclarationTable;
import org.example.project.ast.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


/**
 * The {@code LogoArityResolverListener} class is responsible for analyzing the syntax tree of a
 * Logo program and resolving function and procedure calls based on their arity and context. It extends
 * the {@code LogoBaseListener} class to provide custom handling of grammar rules during the parsing process.
 * <p>
 * This class maintains state during parsing, including a stack for intermediate computations and tables
 * for tracking function declarations and resolving their arguments. It also handles error detection for
 * malformed or invalid constructs, ensuring that the syntax tree accurately represents the source program,
 * even in the presence of errors.
 * <p>
 * State Tracking:
 * - {@code functionDeclarationTable}: Tracks the declarations of functions and procedures in the program.
 * - {@code stack}: Holds intermediate nodes during parsing for procedural and expression resolution.
 * - {@code pendingBlocks}: Maintains pending blocks that are processed during parsing of list constructs.
 * - {@code pendingParenExprs}: Tracks parenthesized expressions that have not yet been fully resolved.
 * - {@code errors}: Collects error information during syntax tree traversal.
 * - {@code procedureDepth}: Tracks the nesting level of procedures to manage scope-specific behavior.
 * <p>
 * Primary Responsibilities:
 * - Resolves arity-based argument counts for fixed and variable arity functions.
 * - Constructs abstract syntax tree (AST) nodes for expressions, procedures, and special forms.
 * - Performs precedence climbing to resolve operator precedence for expressions.
 * - Handles special forms and error detection for malformed constructs.
 * - Supports procedure scope tracking to handle nested definitions and argument resolution.
 */
public class LogoArityResolverListener extends LogoBaseListener {

    private final FunctionDeclarationTable functionDeclarationTable;
    private final List<String> sourceLines;

    // Scope stack: each frame collects the ASTNodes produced by its statements.
    private final Deque<List<ASTNode>> stack = new ArrayDeque<>();

    // FIFO queue for BlockNodes built by exitList when the list is an inline atom
    // (not a standalone statement). Consumed by convertAtom in left-to-right order.
    private final Deque<BlockNode> pendingBlocks = new ArrayDeque<>();

    // FIFO queue for results of parenthesised sub-expressions: (expr).
    // exitExpression stores one result here; convertAtom retrieves it.
    private final Deque<ASTNode> pendingParenExprs = new ArrayDeque<>();

    private final List<ErrorNode> errors = new ArrayList<>();
    // Tracks nesting depth to detect nested procedure definitions.
    private int procedureDepth = 0;

    public LogoArityResolverListener(FunctionDeclarationTable functionDeclarationTable, List<String> sourceLines) {
        this.functionDeclarationTable = functionDeclarationTable;
        this.sourceLines = sourceLines;
    }

    public ProgramNode getResult() {
        ProgramNode program = new ProgramNode(null, stack.peek());
        program.parserErrors.addAll(errors);
        return program;
    }

    // -------------------------------------------------------------------------
    // Program scope
    // -------------------------------------------------------------------------

    @Override
    public void enterProgram(LogoParser.ProgramContext ctx) {
        stack.push(new ArrayList<>());
    }

    // -------------------------------------------------------------------------
    // Procedure scope
    //
    // enterProcedureDefinition registers the procedure BEFORE the body is walked
    // so that recursive calls and calls that follow the definition both resolve.
    // Nested TO definitions are detected here and flagged with an ErrorNode.
    // -------------------------------------------------------------------------

    @Override
    public void enterProcedureDefinition(LogoParser.ProcedureDefinitionContext ctx) {
        procedureDepth++;
        if (procedureDepth > 1) {
            Range span = rangeOf(ctx.start, ctx.stop);
            ErrorNode error = new ErrorNode("Nested procedure definitions are not allowed", span);
            errors.add(error);
            assert stack.peek() != null;
            stack.peek().add(error);
        }
        stack.push(new ArrayList<>());
        if (ctx.IDENTIFIER() != null) {
            String name = ctx.IDENTIFIER().getText();
            int arity = ctx.parameter().size();
            functionDeclarationTable.add(name,
                    new FunctionDeclaration(name, arity, FunctionDeclaration.Kind.USER));
        }
    }

    @Override
    public void exitProcedureDefinition(LogoParser.ProcedureDefinitionContext ctx) {
        procedureDepth--;
        List<ASTNode> bodyStatements = stack.pop();
        String name = ctx.IDENTIFIER() != null ? ctx.IDENTIFIER().getText() : "<unknown>";

        List<ParameterNode> parameters = new ArrayList<>();
        for (LogoParser.ParameterContext p : ctx.parameter()) {
            if (p.VARIABLE() != null) {
                Range pSpan = rangeOf(p.getStart(), p.getStop());
                parameters.add(new ParameterNode(pSpan, p.VARIABLE().getText().substring(1)));
            }
        }

        Range spanFun = rangeOf(ctx.start, ctx.stop);
        fillProcedureDescription(name, spanFun);
        int blockStart = parameters.isEmpty() ? ctx.IDENTIFIER().getSymbol().getCharPositionInLine() + name.length()+1: parameters.getLast().getSpan().start.character+1;
        Range spanBlock = new Range(new Position(spanFun.start.line, blockStart), spanFun.end);
        Range nameRange = ctx.IDENTIFIER() != null
                ? rangeOf(ctx.IDENTIFIER().getSymbol(), ctx.IDENTIFIER().getSymbol())
                : null;
        BlockNode body = new BlockNode(bodyStatements, spanBlock);
        assert stack.peek() != null;
        stack.peek().add(new ProcedureDeclarationNode(name, parameters, body, spanFun, nameRange));
    }

    // -------------------------------------------------------------------------
    // Lists  [ ... ]
    //
    // enterList always opens a new scope so that the list's inner statements
    // land in the right place.
    //
    // exitList decides where the finished BlockNode goes:
    //   - standalone statement  → straight into the enclosing scope
    //   - inline atom in expr   → pendingBlocks queue (FIFO), consumed by
    //                             convertAtom when it encounters the list atom
    // -------------------------------------------------------------------------

    @Override
    public void enterList(LogoParser.ListContext ctx) {
        stack.push(new ArrayList<>());
    }

    @Override
    public void exitList(LogoParser.ListContext ctx) {
        List<ASTNode> body = stack.pop();
        Range span = rangeOf(ctx.getStart(), ctx.getStop());
        BlockNode block = new BlockNode(body, span);

        if (ctx.parent instanceof LogoParser.StatementContext) {
            assert stack.peek() != null;
            stack.peek().add(block);
        } else {
            // Part of an atom inside an expression — queue so resolveTerm picks
            // blocks up in left-to-right order (critical for ifelse [then] [else]).
            pendingBlocks.offer(block);
        }
    }

    // -------------------------------------------------------------------------
    // Expressions
    //
    // Logo grammar: expression : atom+ ;
    // An expression is either:
    //   (a) a normal statement-level expression  → resolve atoms onto scope stack
    //   (b) a parenthesised sub-expression atom  → resolve and queue one result
    // -------------------------------------------------------------------------

    @Override
    public void exitExpression(LogoParser.ExpressionContext ctx) {
        if (ctx.parent instanceof LogoParser.AtomContext) {
            // (expr) — produce one node and park it for the outer expression
            List<LogoParser.AtomContext> atoms = ctx.atom();
            int[] idx = {0};
            pendingParenExprs.offer(resolveTerm(atoms, idx));
            return;
        }

        List<LogoParser.AtomContext> atoms = ctx.atom();
        int[] idx = {0};
        while (idx[0] < atoms.size()) {
            assert stack.peek() != null;
            stack.peek().add(resolveTerm(atoms, idx));
        }
    }

    // -------------------------------------------------------------------------
    // Core resolution
    // -------------------------------------------------------------------------

    /*
     * Entry point: resolves one primary, then absorbs any following infix
     * operators via precedence climbing (left-associative).
     *
     * print :a + :b * :c  →  Call(print, [BinaryExpr(:a, +, BinaryExpr(:b, *, :c))])
     */
    private ASTNode resolveTerm(List<LogoParser.AtomContext> atoms, int[] idx) {
        ASTNode left = resolvePrimary(atoms, idx);
        return resolveInfix(atoms, idx, left, 0);
    }

    /*
     * Resolves one primary (function call, literal, variable, unary minus).
     * Does NOT consume infix operators — that is delegated to resolveInfix.
     */
    private ASTNode resolvePrimary(List<LogoParser.AtomContext> atoms, int[] idx) {
        LogoParser.AtomContext atom = atoms.get(idx[0]);

        // Unary minus: - <primary>
        if (atom.INFIX_OP() != null && atom.INFIX_OP().getText().equals("-")) {
            int opIdx = idx[0];
            idx[0]++;
            ASTNode operand = resolvePrimary(atoms, idx);
            Range span = rangeOf(atoms.get(opIdx), atoms.get(idx[0] - 1));
            return new UnaryExpressionNode(span, "-", operand);
        }

        if (atom.IDENTIFIER() != null) {
            String name = atom.IDENTIFIER().getText();
            if (functionDeclarationTable.contains(name)) {
                FunctionDeclaration sym = functionDeclarationTable.getFunctionDeclaration(name);
                if (sym.kind == FunctionDeclaration.Kind.SPECIAL_FORM) {
                    return resolveSpecialForm(name, sym, atoms, idx);
                }
                if (sym.arity == -1) {
                    return resolveVariableArity(name, atoms, idx);
                }
                return resolveFixedArity(name, sym.arity, atoms, idx);
            }
        }

        ASTNode node = convertAtom(atom);
        idx[0]++;
        return node;
    }

    /*
     * Precedence climbing: consumes infix operators whose precedence >= minPrec
     * and folds them left-associatively into BinaryExpressionNodes.
     *
     *   =  <  >  <=  >=  <>  → precedence 1  (comparison)
     *   +  -                  → precedence 2  (additive)
     *   *  /                  → precedence 3  (multiplicative)
     */
    private ASTNode resolveInfix(List<LogoParser.AtomContext> atoms, int[] idx, ASTNode left, int minPrec) {
        while (idx[0] < atoms.size()) {
            LogoParser.AtomContext next = atoms.get(idx[0]);
            if (next.INFIX_OP() == null) break;
            // Operator must be on the same line as the atom immediately before it.
            if (next.getStart().getLine() != atoms.get(idx[0] - 1).getStart().getLine()) break;
            String op = next.INFIX_OP().getText();
            int prec = infixPrecedence(op);
            if (prec < minPrec) break;
            int opIdx = idx[0];
            idx[0]++;
            ASTNode right = resolvePrimary(atoms, idx);
            right = resolveInfix(atoms, idx, right, prec + 1);
            Range span = rangeOf(atoms.get(opIdx), atoms.get(idx[0] - 1));
            left = new BinaryExpressionNode(span, left, op, right);
        }
        return left;
    }

    private static int infixPrecedence(String op) {
        return switch (op) {
            case "=", "<", ">", "<=", ">=", "<>" -> 1;
            case "+", "-" -> 2;
            case "*", "/" -> 3;
            default -> -1;
        };
    }

    /*
     * Handles every SPECIAL_FORM keyword and emits targeted ErrorNodes for
     * malformed usages. The error is either returned in place of the node
     * (for make) or injected into the current scope before a partial node
     * (for consumeBlock failures, repeat missing count).
     *
     * Unknown special forms (forever, for, …) have no dedicated AST node;
     * they fall back to resolveFixedArity so they still produce a CallNode.
     */
    private ASTNode resolveSpecialForm(String name, FunctionDeclaration sym,
                                       List<LogoParser.AtomContext> atoms, int[] idx) {
        int start = idx[0];
        idx[0]++; // consume keyword

        String nameLower = name.toLowerCase();
        switch (nameLower) {
            case "repeat" -> {
                // Error 14: list where count expected  →  repeat [forward 100]
                if (idx[0] < atoms.size() && atoms.get(idx[0]).list() != null) {
                    Range errSpan = spanFrom(atoms, start, idx);
                    ErrorNode error = new ErrorNode("Expected repeat count before '['", errSpan);
                    this.errors.add(error);
                    assert stack.peek() != null;
                    stack.peek().add(error);
                    BlockNode body = consumeBlock(atoms, idx, start);
                    ErrorNode error2 = new ErrorNode("Missing count", errSpan);
                    return new RepeatNode(
                            error2,
                            body, spanFrom(atoms, start, idx));
                }
                ASTNode count = resolveTerm(atoms, idx);
                BlockNode body = consumeBlock(atoms, idx, start);
                return new RepeatNode(count, body, spanFrom(atoms, start, idx));
            }
            case "if" -> {
                ASTNode condition = resolveTerm(atoms, idx);
                BlockNode thenBranch = consumeBlock(atoms, idx, start); // Error 12 detected inside

                return new IfNode(spanFrom(atoms, start, idx), condition, thenBranch);
            }
            case "ifelse" -> {
                ASTNode condition = resolveTerm(atoms, idx);
                BlockNode thenBranch = consumeBlock(atoms, idx, start); // Error 12 detected inside

                BlockNode elseBranch = consumeBlock(atoms, idx, start); // Error 13 detected inside

                return new IfElseNode(spanFrom(atoms, start, idx), condition, thenBranch, elseBranch);
            }
            case "while" -> {
                ASTNode condition = resolveTerm(atoms, idx);
                BlockNode body = consumeBlock(atoms, idx, start); // Error 12 detected inside

                return new WhileNode(spanFrom(atoms, start, idx), condition, body);
            }
            case "make" -> {
                // Error 8: nothing at all after make
                if (idx[0] >= atoms.size()) {
                    ErrorNode error = new ErrorNode("Expected variable name after 'make'", spanFrom(atoms, start, idx));
                    this.errors.add(error);
                    return error;
                }
                LogoParser.AtomContext nameAtom = atoms.get(idx[0]);
                // Error 9: not a quoted word or variable reference
                if (nameAtom.QUOTED_WORD() == null && nameAtom.VARIABLE() == null) {
                    idx[0]++;
                    if (idx[0] < atoms.size()) resolveTerm(atoms, idx); // discard the value token
                    ErrorNode error = new ErrorNode("Expected variable name (\"name or :name) after 'make', got '"
                            + nameAtom.getText() + "'",
                            rangeOf(nameAtom.getStart(), nameAtom.getStop()));
                    this.errors.add(error);
                    return error;
                }
                String fullName = nameAtom.getText();
                if (fullName.charAt(0) != '"') {
                    ErrorNode error = new ErrorNode("\" expected before variable name", spanFrom(atoms, start, idx));
                    this.errors.add(error);
                    return error;
                }
                String varName = extractVarName(nameAtom);
                idx[0]++;
                // Error 10: variable name present but no value follows (on same line)
                if (idx[0] >= atoms.size()
                        || atoms.get(idx[0]).getStart().getLine() != atoms.get(start).getStart().getLine()) {
                    ErrorNode error = new ErrorNode("Expected value after variable name in 'make'", spanFrom(atoms, start, idx));
                    this.errors.add(error);
                    return error;
                }
                ASTNode value = resolveTerm(atoms, idx);
                Range varNameRange = rangeOf(nameAtom.getStart(), nameAtom.getStop());
                return new VariableDeclaration(varName, value, spanFrom(atoms, start, idx), varNameRange);
            }
        }

        // Special form with no dedicated AST node (forever, for, …)
        idx[0] = start;
        return resolveFixedArity(name, sym.arity, atoms, idx);
    }

    /*
     * Consumes the next atom if it is a list and pops the pre-built BlockNode.
     * Error 12/13: when no list atom is present, injects an ErrorNode into the
     * current scope and returns an empty block so the enclosing node can still
     * be constructed.
     */
    private BlockNode consumeBlock(List<LogoParser.AtomContext> atoms, int[] idx, int start) {
        if (idx[0] < atoms.size() && atoms.get(idx[0]).list() != null) {
            idx[0]++;
            return pendingBlocks.poll();
        }
        org.antlr.v4.runtime.Token errToken = idx[0] < atoms.size()
                ? atoms.get(idx[0]).getStart()
                : atoms.get(Math.max(0, idx[0] - 1)).getStop();
        ErrorNode error = new ErrorNode("Expected '['", rangeOf(errToken, errToken));
        this.errors.add(error);
        assert stack.peek() != null;
        stack.peek().add(error);
        return new BlockNode(new ArrayList<>(), spanFrom(atoms, start, idx));
    }

    /*
     * forward 100
     * sum 2 3
     * forward sum 2 3   <- sum is recursively resolved as the argument to forward
     *
     * Error 7: if fewer args exist than the arity requires, each missing slot
     * is filled with an ErrorNode so the CallNode structure is preserved.
     */
    private ASTNode resolveFixedArity(String name, int arity, List<LogoParser.AtomContext> atoms, int[] idx) {
        int start = idx[0];
        int callLine = atoms.get(start).getStart().getLine();
        idx[0]++;

        List<ASTNode> args = new ArrayList<>();
        for (int j = 0; j < arity && idx[0] < atoms.size(); j++) {
            // Block arguments ([...]) are always allowed on the next line.
            // All other arguments must start on the same line as the function keyword.
            if (atoms.get(idx[0]).list() == null
                    && atoms.get(idx[0]).getStart().getLine() != callLine) break;
            args.add(resolveTerm(atoms, idx));
        }
        for (int j = args.size(); j < arity; j++) {
            ErrorNode error = new ErrorNode("Missing argument " + (j + 1) + " for '" + name + "'", spanFrom(atoms, start, idx));
            this.errors.add(error);
            args.add(error);
        }

        return new CallNode(name, args, spanFrom(atoms, start, idx));
    }

    /*
     * sentence 1 2 3   <- consumes non-callable atoms until a known function is met.
     * The break-at-callable rule keeps the top-level structure intact so that
     * "sentence 1 2 forward 10" does not accidentally swallow "forward 10".
     */
    private ASTNode resolveVariableArity(String name, List<LogoParser.AtomContext> atoms, int[] idx) {
        int start = idx[0];
        int callLine = atoms.get(start).getStart().getLine();
        idx[0]++;

        List<ASTNode> args = new ArrayList<>();
        while (idx[0] < atoms.size()) {
            LogoParser.AtomContext next = atoms.get(idx[0]);
            if (next.getStart().getLine() != callLine) break;
            if (next.IDENTIFIER() != null && functionDeclarationTable.contains(next.IDENTIFIER().getText())) {
                break;
            }
            args.add(resolveTerm(atoms, idx));
        }

        return new CallNode(name, args, spanFrom(atoms, start, idx));
    }

    // -------------------------------------------------------------------------
    // Procedure description
    // -------------------------------------------------------------------------

    private void fillProcedureDescription(String name, Range spanFun) {
        FunctionDeclaration decl = functionDeclarationTable.getFunctionDeclaration(name);
        if (decl == null || decl.kind != FunctionDeclaration.Kind.USER) return;
        int startLine = spanFun.start.line;
        int endLine   = spanFun.end.line;
        int previewEnd = Math.min(startLine + 4, endLine);
        StringBuilder sb = new StringBuilder();
        for (int i = startLine; i <= previewEnd && i < sourceLines.size(); i++) {
            String line = sourceLines.get(i);
            if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);
            sb.append(line).append("\n");
        }
        if (previewEnd < endLine) {
            sb.append("...");
        } else {
            if (!sb.isEmpty()) sb.deleteCharAt(sb.length() - 1); // trim trailing \n
        }
        decl.description = sb.toString();
    }

    // -------------------------------------------------------------------------
    // Atom conversion
    // -------------------------------------------------------------------------

    private String extractVarName(LogoParser.AtomContext atom) {
        if (atom.VARIABLE() != null) return atom.VARIABLE().getText().substring(1);    // strip ':'
        if (atom.QUOTED_WORD() != null) return atom.QUOTED_WORD().getText().substring(1); // strip '"'
        return atom.getText();
    }

    private ASTNode convertAtom(LogoParser.AtomContext atom) {
        Range span = rangeOf(atom.getStart(), atom.getStop());

        if (atom.NUMBER() != null)
            return new NumberNode(atom.NUMBER().getText(), span);

        if (atom.VARIABLE() != null)
            return new VariableRefNode(atom.VARIABLE().getText().substring(1), span);

        if (atom.QUOTED_WORD() != null)
            return new WordLiteral(span, atom.QUOTED_WORD().getText().substring(1));

        if (atom.IDENTIFIER() != null)
            return new FunctionRef(atom.IDENTIFIER().getText(), span);

        if (atom.list() != null)
            return pendingBlocks.poll();

        if (atom.expression() != null) // LPAREN expression RPAREN
            return pendingParenExprs.poll();

        return new VariableRefNode("unknown", span);
    }

    // -------------------------------------------------------------------------
    // Range helpers
    // -------------------------------------------------------------------------

    private Range spanFrom(List<LogoParser.AtomContext> atoms, int start, int[] idx) {
        int end = Math.max(start, idx[0] - 1);
        return rangeOf(atoms.get(start), atoms.get(end));
    }

    // ANTLR lines are 1-based; LSP positions are 0-based.
    private Range rangeOf(org.antlr.v4.runtime.Token start, org.antlr.v4.runtime.Token stop) {
        Position startPos = new Position(start.getLine() - 1, start.getCharPositionInLine());
        Position endPos = new Position(stop.getLine() - 1, stop.getCharPositionInLine() + stop.getText().length());
        return new Range(startPos, endPos);
    }

    private Range rangeOf(LogoParser.AtomContext start, LogoParser.AtomContext stop) {
        return rangeOf(start.getStart(), stop.getStop());
    }
}
