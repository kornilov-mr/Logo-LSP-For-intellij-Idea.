package org.example.project;

import java.util.HashMap;
import java.util.Map;

public class StandardFunctionDeclaration {
    public static StandardFunctionDeclaration declaration;

    public static StandardFunctionDeclaration getDeclaration(){
        if(declaration == null){
            declaration = new StandardFunctionDeclaration();
        }
        return declaration;
    }

    public final Map<String, FunctionDeclaration> declarationsMap = new HashMap<>();

    public StandardFunctionDeclaration() {
        fillWithStandardDeclarations();
        fillWithDescriptions();
    }

    private void fillWithStandardDeclarations(){
        for (StandardLogoFunctions standardLogoFunctions : StandardLogoFunctions.values()){
            declarationsMap.put(standardLogoFunctions.declaration.name, standardLogoFunctions.declaration);
        }
    }

    private void setDesc(String name, String desc) {
        FunctionDeclaration d = declarationsMap.get(name);
        if (d != null) d.description = desc;
    }

    private void fillWithDescriptions() {
        // Turtle movement
        setDesc("forward", "forward distance\nMove the turtle forward by the given number of steps.\nExample: forward 100");
        setDesc("fd",      "fd distance\nAlias for forward.\nExample: fd 100");
        setDesc("back",    "back distance\nMove the turtle backward by the given number of steps.\nExample: back 50");
        setDesc("bk",      "bk distance\nAlias for back.\nExample: bk 50");
        setDesc("left",    "left degrees\nTurn the turtle left by the given number of degrees.\nExample: left 90");
        setDesc("lt",      "lt degrees\nAlias for left.\nExample: lt 90");
        setDesc("right",   "right degrees\nTurn the turtle right by the given number of degrees.\nExample: right 90");
        setDesc("rt",      "rt degrees\nAlias for right.\nExample: rt 90");

        // Turtle position
        setDesc("setxy",      "setxy x y\nMove the turtle to the given (x, y) coordinates.\nExample: setxy 50 100");
        setDesc("setx",       "setx x\nSet the turtle's x position.\nExample: setx 100");
        setDesc("sety",       "sety y\nSet the turtle's y position.\nExample: sety -50");
        setDesc("setheading", "setheading degrees\nSet the turtle's heading (0 = up, 90 = right).\nExample: setheading 180");

        // Turtle state
        setDesc("home",        "home\nReturn the turtle to the center (0, 0) facing up.\nExample: home");
        setDesc("clearscreen", "clearscreen\nClear the screen and send the turtle home.\nExample: clearscreen");
        setDesc("cs",          "cs\nAlias for clearscreen.\nExample: cs");

        // Pen
        setDesc("penup",   "penup\nLift the pen — turtle moves without drawing.\nExample: penup");
        setDesc("pu",      "pu\nAlias for penup.\nExample: pu");
        setDesc("pendown", "pendown\nPut the pen down — turtle draws while moving.\nExample: pendown");
        setDesc("pd",      "pd\nAlias for pendown.\nExample: pd");
        setDesc("setpensize",  "setpensize width\nSet the pen line width.\nExample: setpensize 3");
        setDesc("setpencolor", "setpencolor n\nSet the pen color by color number.\nExample: setpencolor 4");

        // Turtle visibility
        setDesc("hideturtle", "hideturtle\nHide the turtle cursor.\nExample: hideturtle");
        setDesc("ht",         "ht\nAlias for hideturtle.\nExample: ht");
        setDesc("showturtle", "showturtle\nShow the turtle cursor.\nExample: showturtle");
        setDesc("st",         "st\nAlias for showturtle.\nExample: st");

        // Control flow
        setDesc("repeat",  "repeat count [block]\nRepeat the block the given number of times.\nExample: repeat 4 [forward 100 right 90]");
        setDesc("if",      "if condition [block]\nExecute the block if the condition is true.\nExample: if :x > 0 [forward :x]");
        setDesc("ifelse",  "ifelse condition [then] [else]\nExecute the then-block if true, else-block if false.\nExample: ifelse :n > 0 [forward 10] [back 10]");
        setDesc("while",   "while condition [block]\nRepeat the block while the condition is true.\nExample: while :x > 0 [make \"x :x - 1]");
        setDesc("forever", "forever [block]\nRepeat the block indefinitely.\nExample: forever [forward 1 right 1]");
        setDesc("for",     "for [var start end step] [block]\nIterate var from start to end by step.\nExample: for [i 1 10 1] [print :i]");
        setDesc("stop",    "stop\nStop the current procedure and return to the caller.\nExample: stop");
        setDesc("output",  "output value\nReturn a value from the current procedure.\nExample: output :n * 2");
        setDesc("run",     "run [block]\nExecute the contents of a list as Logo commands.\nExample: run [forward 100]");

        // Arithmetic
        setDesc("sum",        "sum a b\nReturn a + b.\nExample: sum 3 4 -> 7");
        setDesc("difference", "difference a b\nReturn a - b.\nExample: difference 10 3 -> 7");
        setDesc("product",    "product a b\nReturn a * b.\nExample: product 5 4 -> 20");
        setDesc("quotient",   "quotient a b\nReturn a / b.\nExample: quotient 10 2 -> 5");
        setDesc("remainder",  "remainder a b\nReturn the remainder of a / b.\nExample: remainder 10 3 -> 1");
        setDesc("sqrt",       "sqrt n\nReturn the square root of n.\nExample: sqrt 9 -> 3");
        setDesc("power",      "power base exp\nReturn base raised to the power exp.\nExample: power 2 8 -> 256");
        setDesc("abs",        "abs n\nReturn the absolute value of n.\nExample: abs -5 -> 5");
        setDesc("round",      "round n\nRound n to the nearest integer.\nExample: round 3.7 -> 4");
        setDesc("int",        "int n\nTruncate n to an integer.\nExample: int 3.9 -> 3");

        // Trigonometry
        setDesc("sin", "sin degrees\nReturn the sine of the angle in degrees.\nExample: sin 90 -> 1");
        setDesc("cos", "cos degrees\nReturn the cosine of the angle in degrees.\nExample: cos 0 -> 1");
        setDesc("tan", "tan degrees\nReturn the tangent of the angle in degrees.\nExample: tan 45 -> 1");

        // Random
        setDesc("random", "random n\nReturn a random integer from 0 to n-1.\nExample: random 10");

        // Lists
        setDesc("list",      "list items...\nCreate a list from the given items.\nExample: list 1 2 3 -> [1 2 3]");
        setDesc("sentence",  "sentence items...\nCreate a sentence (flat list) from the given items.\nExample: sentence \"hello \"world");
        setDesc("first",     "first list\nReturn the first item of the list.\nExample: first [1 2 3] -> 1");
        setDesc("last",      "last list\nReturn the last item of the list.\nExample: last [1 2 3] -> 3");
        setDesc("butfirst",  "butfirst list\nReturn the list without its first item.\nExample: butfirst [1 2 3] -> [2 3]");
        setDesc("butlast",   "butlast list\nReturn the list without its last item.\nExample: butlast [1 2 3] -> [1 2]");
        setDesc("item",      "item n list\nReturn the nth item (1-based) of the list.\nExample: item 2 [a b c] -> b");
        setDesc("count",     "count list\nReturn the number of items in the list.\nExample: count [1 2 3] -> 3");
        setDesc("fput",      "fput item list\nAdd item to the front of the list.\nExample: fput 0 [1 2] -> [0 1 2]");
        setDesc("lput",      "lput item list\nAdd item to the back of the list.\nExample: lput 3 [1 2] -> [1 2 3]");

        // Strings / words
        setDesc("word",      "word parts...\nConcatenate words into one.\nExample: word \"hello \"world -> helloworld");
        setDesc("uppercase", "uppercase str\nConvert string to uppercase.\nExample: uppercase \"hello -> HELLO");
        setDesc("lowercase", "lowercase str\nConvert string to lowercase.\nExample: lowercase \"HELLO -> hello");
        setDesc("char",      "char n\nReturn the character with ASCII code n.\nExample: char 65 -> A");
        setDesc("ascii",     "ascii char\nReturn the ASCII code of the first character.\nExample: ascii \"A -> 65");

        // Variables
        setDesc("make",    "make \"name value\nAssign a value to a variable.\nExample: make \"x 10");
        setDesc("name?",   "name? \"name\nReturn true if the variable exists.\nExample: name? \"x");
        setDesc("thing",   "thing \"name\nReturn the value of the named variable.\nExample: thing \"x -> 10");
        setDesc("local",   "local \"name\nCreate a local variable scoped to the current procedure.\nExample: local \"temp");
        setDesc("erase",   "erase \"name\nDelete a variable.\nExample: erase \"x");

        // I/O
        setDesc("print",    "print value\nPrint the value followed by a newline.\nExample: print \"hello");
        setDesc("show",     "show value\nPrint the value with list brackets.\nExample: show [1 2 3]");
        setDesc("type",     "type value\nPrint the value without a trailing newline.\nExample: type \"hello");
        setDesc("readword", "readword\nRead a word from standard input.\nExample: make \"input readword");
        setDesc("readlist", "readlist\nRead a list from standard input.\nExample: make \"data readlist");

        // Logic
        setDesc("and",      "and a b\nReturn true if both a and b are true.\nExample: and :x > 0 :y > 0");
        setDesc("or",       "or a b\nReturn true if either a or b is true.\nExample: or :x = 0 :y = 0");
        setDesc("not",      "not value\nReturn the logical negation of value.\nExample: not :x > 5");
        setDesc("equal?",   "equal? a b\nReturn true if a equals b.\nExample: equal? :x 5");
        setDesc("greater?", "greater? a b\nReturn true if a is greater than b.\nExample: greater? :x 5");
        setDesc("less?",    "less? a b\nReturn true if a is less than b.\nExample: less? :x 5");

        // Meta
        setDesc("apply",  "apply functionName [args]\nApply a function to a list of arguments.\nExample: apply \"forward [100]");
        setDesc("invoke", "invoke functionName args...\nInvoke a function with the given arguments.\nExample: invoke \"forward 100");
    }
    public FunctionDeclaration getFunctionDeclaration(String name){
        return declarationsMap.get(name.toLowerCase());
    }
    public boolean contains(String name){
        return declarationsMap.containsKey(name.toLowerCase());
    }
}
