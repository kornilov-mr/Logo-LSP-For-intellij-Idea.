// Generated from C:/Users/korni/IdeaProjects/LogoSupport/src/main/java/org/example/project/parser/Logo.g4 by ANTLR 4.13.2
package gen;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class LogoLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TO=1, END=2, VARIABLE=3, QUOTED_WORD=4, IDENTIFIER=5, NUMBER=6, LBRACKET=7, 
		RBRACKET=8, INFIX_OP=9, LPAREN=10, RPAREN=11, COMMENT=12, WS=13;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"TO", "END", "VARIABLE", "QUOTED_WORD", "IDENTIFIER", "NUMBER", "LBRACKET", 
			"RBRACKET", "INFIX_OP", "LPAREN", "RPAREN", "COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, "'['", "']'", null, "'('", 
			"')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TO", "END", "VARIABLE", "QUOTED_WORD", "IDENTIFIER", "NUMBER", 
			"LBRACKET", "RBRACKET", "INFIX_OP", "LPAREN", "RPAREN", "COMMENT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public LogoLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Logo.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\r]\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0005"+
		"\u0004+\b\u0004\n\u0004\f\u0004.\t\u0004\u0001\u0005\u0004\u00051\b\u0005"+
		"\u000b\u0005\f\u00052\u0001\u0005\u0001\u0005\u0004\u00057\b\u0005\u000b"+
		"\u0005\f\u00058\u0003\u0005;\b\u0005\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003"+
		"\bH\b\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0005"+
		"\u000bP\b\u000b\n\u000b\f\u000bS\t\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0004\fX\b\f\u000b\f\f\fY\u0001\f\u0001\f\u0000\u0000\r\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f"+
		"\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u0001\u0000\u000b\u0002"+
		"\u0000TTtt\u0002\u0000OOoo\u0002\u0000EEee\u0002\u0000NNnn\u0002\u0000"+
		"DDdd\u0003\u0000AZ__az\u0004\u000009AZ__az\u0001\u000009\u0004\u0000*"+
		"+--//<>\u0002\u0000\n\n\r\r\u0003\u0000\t\n\r\r  e\u0000\u0001\u0001\u0000"+
		"\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000"+
		"\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000"+
		"\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000"+
		"\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000"+
		"\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000"+
		"\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000"+
		"\u0001\u001b\u0001\u0000\u0000\u0000\u0003\u001e\u0001\u0000\u0000\u0000"+
		"\u0005\"\u0001\u0000\u0000\u0000\u0007%\u0001\u0000\u0000\u0000\t(\u0001"+
		"\u0000\u0000\u0000\u000b0\u0001\u0000\u0000\u0000\r<\u0001\u0000\u0000"+
		"\u0000\u000f>\u0001\u0000\u0000\u0000\u0011G\u0001\u0000\u0000\u0000\u0013"+
		"I\u0001\u0000\u0000\u0000\u0015K\u0001\u0000\u0000\u0000\u0017M\u0001"+
		"\u0000\u0000\u0000\u0019W\u0001\u0000\u0000\u0000\u001b\u001c\u0007\u0000"+
		"\u0000\u0000\u001c\u001d\u0007\u0001\u0000\u0000\u001d\u0002\u0001\u0000"+
		"\u0000\u0000\u001e\u001f\u0007\u0002\u0000\u0000\u001f \u0007\u0003\u0000"+
		"\u0000 !\u0007\u0004\u0000\u0000!\u0004\u0001\u0000\u0000\u0000\"#\u0005"+
		":\u0000\u0000#$\u0003\t\u0004\u0000$\u0006\u0001\u0000\u0000\u0000%&\u0005"+
		"\"\u0000\u0000&\'\u0003\t\u0004\u0000\'\b\u0001\u0000\u0000\u0000(,\u0007"+
		"\u0005\u0000\u0000)+\u0007\u0006\u0000\u0000*)\u0001\u0000\u0000\u0000"+
		"+.\u0001\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000"+
		"\u0000-\n\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000/1\u0007\u0007"+
		"\u0000\u00000/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000020\u0001"+
		"\u0000\u0000\u000023\u0001\u0000\u0000\u00003:\u0001\u0000\u0000\u0000"+
		"46\u0005.\u0000\u000057\u0007\u0007\u0000\u000065\u0001\u0000\u0000\u0000"+
		"78\u0001\u0000\u0000\u000086\u0001\u0000\u0000\u000089\u0001\u0000\u0000"+
		"\u00009;\u0001\u0000\u0000\u0000:4\u0001\u0000\u0000\u0000:;\u0001\u0000"+
		"\u0000\u0000;\f\u0001\u0000\u0000\u0000<=\u0005[\u0000\u0000=\u000e\u0001"+
		"\u0000\u0000\u0000>?\u0005]\u0000\u0000?\u0010\u0001\u0000\u0000\u0000"+
		"@A\u0005<\u0000\u0000AH\u0005=\u0000\u0000BC\u0005>\u0000\u0000CH\u0005"+
		"=\u0000\u0000DE\u0005<\u0000\u0000EH\u0005>\u0000\u0000FH\u0007\b\u0000"+
		"\u0000G@\u0001\u0000\u0000\u0000GB\u0001\u0000\u0000\u0000GD\u0001\u0000"+
		"\u0000\u0000GF\u0001\u0000\u0000\u0000H\u0012\u0001\u0000\u0000\u0000"+
		"IJ\u0005(\u0000\u0000J\u0014\u0001\u0000\u0000\u0000KL\u0005)\u0000\u0000"+
		"L\u0016\u0001\u0000\u0000\u0000MQ\u0005;\u0000\u0000NP\b\t\u0000\u0000"+
		"ON\u0001\u0000\u0000\u0000PS\u0001\u0000\u0000\u0000QO\u0001\u0000\u0000"+
		"\u0000QR\u0001\u0000\u0000\u0000RT\u0001\u0000\u0000\u0000SQ\u0001\u0000"+
		"\u0000\u0000TU\u0006\u000b\u0000\u0000U\u0018\u0001\u0000\u0000\u0000"+
		"VX\u0007\n\u0000\u0000WV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000"+
		"YW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000"+
		"\u0000[\\\u0006\f\u0000\u0000\\\u001a\u0001\u0000\u0000\u0000\b\u0000"+
		",28:GQY\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}