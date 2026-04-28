grammar Logo;

program
    : statement* EOF
    ;

/*
 * =========================
 * Statements
 * =========================
 */

statement
    : procedureDefinition
    | list
    | expression
    ;

/*
 * =========================
 * Procedure Definition
 * =========================
 */

procedureDefinition
    : TO IDENTIFIER parameter* statement* END
    ;

parameter
    : VARIABLE
    ;

/*
 * =========================
 * Expressions
 * =========================
 *
 * Keep this weak on purpose.
 * Do NOT try to encode arity here.
 */

expression
    : atom+
    ;

atom
    : NUMBER
    | VARIABLE
    | QUOTED_WORD
    | IDENTIFIER
    | INFIX_OP
    | list
    | LPAREN expression RPAREN
    ;

/*
 * =========================
 * Lists
 * =========================
 *
 * Lists are important because:
 * repeat 4 [ forward 100 ]
 *
 * The parser must preserve them
 * as structure, not execute them.
 */

list
    : LBRACKET statement* RBRACKET
    ;

/*
 * =========================
 * Lexer Rules
 * =========================
 */

/*
 * Keywords
 */

TO      : [tT][oO];
END     : [eE][nN][dD];

/*
 * Variable reference
 *
 * Example:
 * :x
 */

VARIABLE
    : ':' IDENTIFIER
    ;

/*
 * Quoted word
 *
 * Example:
 * "hello
 */

QUOTED_WORD
    : '"' IDENTIFIER
    ;

/*
 * Identifiers
 *
 * Includes:
 * - builtins (forward, repeat, sum)
 * - user procedures
 * - words
 *
 * Arity resolution decides meaning later.
 */

IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

/*
 * Numbers
 */

NUMBER
    : [0-9]+ ('.' [0-9]+)?
    ;

/*
 * Brackets
 */

LBRACKET   : '[';
RBRACKET   : ']';

/*
 * Infix operators
 *
 * Longest match: <= >= <> must come before < > =
 */

INFIX_OP   : '<=' | '>=' | '<>' | '+' | '-' | '*' | '/' | '=' | '<' | '>';

LPAREN     : '(';
RPAREN     : ')';

/*
 * Comments
 *
 * Example:
 * ; this is a comment
 */

COMMENT
    : ';' ~[\r\n]* -> skip
    ;

/*
 * Whitespace
 */

WS
    : [ \t\r\n]+ -> skip
    ;