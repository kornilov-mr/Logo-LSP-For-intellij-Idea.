package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.communication.DTO.SemanticTokens;
import org.example.communication.requests.SemanticTokensParams;
import org.example.project.ProjectContext;
import org.example.server.handlers.TextDocumentSemanticTokensFullHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SemanticTokensProviderTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TextDocumentSemanticTokensFullHandler handler = new TextDocumentSemanticTokensFullHandler();
    private static final String URI = "src/test/resources/ast/programs/forSemanticTokens/shrinkingSquares.logo";

    private static SemanticTokens result;

    @BeforeAll
    public static void setup() throws IOException {
        ProjectContext.didOpenFile(URI, Files.readString(new File(URI).toPath()));
        String requestJson = "{\"textDocument\":{\"uri\":\"" + URI + "\"}}";
        SemanticTokensParams params = objectMapper.readValue(requestJson, SemanticTokensParams.class);
        try {
            result = (SemanticTokens) handler.handleMessage(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTokenCount() {
        // 27 tokens × 5 fields each
        Assertions.assertEquals(135, result.data.length);
    }

    @Test
    public void testKeywords() {
        // Expected keyword tokens (type=5, mod=0), by absolute position in the data array:
        // token 1:  [0,0,2,5,0]   "to"      drawSquare header
        // token 4:  [1,4,6,5,0]   "repeat"
        // token 10: [2,0,3,5,0]   "end"     drawSquare footer
        // token 11: [1,0,2,5,0]   "to"      shrinkingSquares header
        // token 16: [1,4,2,5,0]   "if"
        // token 22: [2,0,3,5,0]   "end"     shrinkingSquares footer
        // token 23: [2,0,4,5,0]   "make"
        assertToken(0,  0, 0,  2, 5, 0); // to (drawSquare)
        assertToken(3,  1, 4,  6, 5, 0); // repeat
        assertToken(9,  2, 0,  3, 5, 0); // end (drawSquare)
        assertToken(10, 1, 0,  2, 5, 0); // to (shrinkingSquares)
        assertToken(15, 1, 4,  2, 5, 0); // if
        assertToken(21, 2, 0,  3, 5, 0); // end (shrinkingSquares)
        assertToken(22, 2, 0,  4, 5, 0); // make
    }

    @Test
    public void testProcedureNameTokens() {
        // "drawSquare" declaration at line 0, col 3, len 10, type=function(0), mod=1
        assertToken(1, 0, 3, 10, 0, 1);
        // "shrinkingSquares" declaration at line 6, col 3, len 16, type=function(0), mod=1
        assertToken(11, 0, 3, 16, 0, 1);
    }

    @Test
    public void testParameterDeclarationTokens() {
        // :size in "to drawSquare :size" — line 0, col 14, len 5, type=parameter(4), mod=2
        assertToken(2, 0, 11, 5, 1, 2);
        // :size in "to shrinkingSquares :size" — line 6, col 20, len 5, type=parameter(4), mod=2
        assertToken(12, 0, 17, 5, 1, 2);
    }

    @Test
    public void testVariableReferenceTokens() {
        // :size inside repeat body (forward :size), line 2 col 16
        assertToken(6,  0, 8,  5, 1, 2);
        // :size argument to drawSquare call, line 7 col 15
        assertToken(14, 0, 11, 5, 1, 2);
        // :size condition in if, line 8 col 7
        assertToken(16, 0, 3,  5, 1, 2);
        // :size argument to difference, line 9 col 36
        assertToken(19, 0, 11, 5, 1, 2);
    }

    @Test
    public void testFunctionCallTokens() {
        // forward, line 2 col 8, len 7
        assertToken(5,  1, 8,  7, 0, 1);
        // right, line 3 col 8, len 5
        assertToken(7,  1, 8,  5, 0, 1);
        // drawSquare call in shrinkingSquares body, line 7 col 4, len 10
        assertToken(13, 1, 4, 10, 0, 1);
        // shrinkingSquares recursive call, line 9 col 8, len 16
        assertToken(17, 1, 8, 16, 0, 1);
        // difference, line 9 col 25, len 10
        assertToken(18, 0, 17, 10, 0, 1);
        // shrinkingSquares top-level call, line 14 col 0, len 16
        assertToken(25, 1, 0, 16, 0, 1);
    }

    @Test
    public void testNumberTokens() {
        // 4 (repeat count), line 1 col 11, len 1
        assertToken(4,  0, 7, 1, 2, 0);
        // 90 (right angle), line 3 col 14, len 2
        assertToken(8,  0, 6, 2, 2, 0);
        // 10 (decrement), line 9 col 42, len 2
        assertToken(20, 0, 6, 2, 2, 0);
        // 100 (initial size), line 13 col 11, len 3
        assertToken(24, 0, 6, 3, 2, 0);
    }

    @Test
    public void testFullTokenData() {
        int[] expected = {
            0,  0, 2,  5, 0,   // to (drawSquare)
            0,  3, 10, 0, 1,   // drawSquare name
            0, 11, 5,  1, 2,   // :size parameter
            1,  4, 6,  5, 0,   // repeat
            0,  7, 1,  2, 0,   // 4
            1,  8, 7,  0, 1,   // forward
            0,  8, 5,  1, 2,   // :size varref (forward arg)
            1,  8, 5,  0, 1,   // right
            0,  6, 2,  2, 0,   // 90
            2,  0, 3,  5, 0,   // end (drawSquare)
            1,  0, 2,  5, 0,   // to (shrinkingSquares)
            0,  3, 16, 0, 1,   // shrinkingSquares name
            0, 17, 5,  1, 2,   // :size parameter
            1,  4, 10, 0, 1,   // drawSquare call
            0, 11, 5,  1, 2,   // :size varref (drawSquare arg)
            1,  4, 2,  5, 0,   // if
            0,  3, 5,  1, 2,   // :size varref (condition)
            1,  8, 16, 0, 1,   // shrinkingSquares recursive call
            0, 17, 10, 0, 1,   // difference
            0, 11, 5,  1, 2,   // :size varref (difference arg)
            0,  6, 2,  2, 0,   // 10
            2,  0, 3,  5, 0,   // end (shrinkingSquares)
            2,  0, 4,  5, 0,   // make
            0,  5, 5,  1, 2,   // "size variable name
            0,  6, 3,  2, 0,   // 100
            1,  0, 16, 0, 1,   // shrinkingSquares top-level call
            0, 17, 5,  1, 2,   // :size varref (top-level arg)
        };
        Assertions.assertArrayEquals(expected, result.data);
    }

    /**
     * Asserts a single token at index {@code tokenIndex} in the encoded data array.
     * Each token is 5 consecutive ints: [deltaLine, deltaChar, length, type, modifier].
     */
    private static void assertToken(int tokenIndex, int deltaLine, int deltaChar, int length, int type, int modifier) {
        int base = tokenIndex * 5;
        Assertions.assertEquals(deltaLine, result.data[base],     "token " + tokenIndex + " deltaLine");
        Assertions.assertEquals(deltaChar, result.data[base + 1], "token " + tokenIndex + " deltaChar");
        Assertions.assertEquals(length,    result.data[base + 2], "token " + tokenIndex + " length");
        Assertions.assertEquals(type,      result.data[base + 3], "token " + tokenIndex + " type");
        Assertions.assertEquals(modifier,  result.data[base + 4], "token " + tokenIndex + " modifier");
    }
}
