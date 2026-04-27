package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.communication.requests.HoverParams;
import org.example.communication.responses.HoverResult;
import org.example.server.handlers.TextDocumentHoverHandler;
import org.example.project.ProjectContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HoverProviderTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TextDocumentHoverHandler hoverHandler = new TextDocumentHoverHandler();
    private static final String URI = "src/test/resources/ast/programs/forSemanticTokens/shrinkingSquares.logo";

    @BeforeAll
    public static void setup() throws IOException {
        ProjectContext.didOpenFile(URI, Files.readString(new File(URI).toPath()));
    }

    @Test
    public void testHoverOverParameter() throws Exception {
        // ":size" in "to drawSquare :size" — line 0, col 16
        String expected = "{\"contents\":\"parameter: size\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":0,\"character\":16}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }

    @Test
    public void testHoverOverFunctionCall() throws Exception {
        // "forward" on line 2, col 10
        String expected = "{\"contents\":\"forward distance\\nMove the turtle forward by the given number of steps.\\nExample: forward 100\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":2,\"character\":10}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }

    @Test
    public void testHoverOverVariableReference() throws Exception {
        // ":size" argument to forward on line 2, col 17
        String expected = "{\"contents\":\"variable: size\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":2,\"character\":17}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }

    @Test
    public void testHoverOverCallWithNumberArg() throws Exception {
        // "right" on line 3, col 10
        String expected = "{\"contents\":\"right degrees\\nTurn the turtle right by the given number of degrees.\\nExample: right 90\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":3,\"character\":10}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }

    @Test
    public void testHoverOverNumberLiteral() throws Exception {
        // "90" on line 3, col 15
        String expected = "{\"contents\":\"90\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":3,\"character\":15}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }

    @Test
    public void testHoverOverUserDefinedFunctionCall() throws Exception {
        // "drawSquare" on line 7, col 8 — user-defined: show first 5 lines of definition
        String expected = "{\"contents\":\"to drawSquare :size\\n    repeat 4 [\\n        forward :size\\n        right 90\\n    ]\\n...\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":7,\"character\":8}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }

    @Test
    public void testHoverOverMakeDeclaration() throws Exception {
        // "make" on line 13, col 2 — VariableDeclaration node
        String expected = "{\"contents\":\"variable size = 100\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":13,\"character\":2}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }

    @Test
    public void testHoverOverNumberLiteralInMake() throws Exception {
        // "100" on line 13, col 12
        String expected = "{\"contents\":\"100\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":13,\"character\":12}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }

    @Test
    public void testHoverOverTopLevelCall() throws Exception {
        // "shrinkingSquares" on line 14, col 5 — user-defined: show first 5 lines of definition
        String expected = "{\"contents\":\"to shrinkingSquares :size\\n    drawSquare :size\\n    if :size [\\n        shrinkingSquares difference :size 10\\n    ]\\n...\"}";
        String requestParams = "{\"textDocument\":{\"uri\":\"" + URI + "\"},\"position\":{\"line\":14,\"character\":5}}";
        HoverParams params = objectMapper.readValue(requestParams, HoverParams.class);

        HoverResult result = (HoverResult) hoverHandler.handleMessage(params);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(result));
    }
}
