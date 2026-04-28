package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.communication.DTO.ServerCapabilities;
import org.example.communication.DTO.TextDocumentSyncOptions;
import org.example.communication.requests.InitializeParams;
import org.example.communication.responses.InitializeResult;
import org.example.server.handlers.InitializationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InitializationHandlerTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final InitializationHandler handler = new InitializationHandler();
    private static InitializeResult result;

    @BeforeAll
    public static void setup() throws Exception {
        String json = "{\"processId\":1,\"rootUri\":\"file:///test\",\"capabilities\":{}}";
        InitializeParams params = objectMapper.readValue(json, InitializeParams.class);
        result = (InitializeResult) handler.handleMessage(params);
    }

    @Test
    public void testHoverProviderEnabled() {
        Assertions.assertTrue(result.capabilities.hoverProvider);
    }

    @Test
    public void testCompletionProviderEnabled() {
        Assertions.assertNotNull(result.capabilities.completionProvider);
        Assertions.assertTrue(result.capabilities.completionProvider.resolveProvider);
        // Colon triggers completion for variable references
        Assertions.assertArrayEquals(new String[]{":","\""}, result.capabilities.completionProvider.triggerCharacters);
    }

    @Test
    public void testSemanticTokensProviderHasCorrectLegend() {
        ServerCapabilities caps = result.capabilities;
        Assertions.assertNotNull(caps.semanticTokensProvider);
        Assertions.assertTrue(caps.semanticTokensProvider.full);

        String[] expectedTypes = {"function", "variable", "number", "string", "parameter", "keyword", "operator"};
        Assertions.assertArrayEquals(expectedTypes, caps.semanticTokensProvider.legend.tokenTypes);

        String[] expectedModifiers = {"declaration", "static"};
        Assertions.assertArrayEquals(expectedModifiers, caps.semanticTokensProvider.legend.tokenModifiers);
    }

    @Test
    public void testDiagnosticProviderEnabled() {
        Assertions.assertNotNull(result.capabilities.diagnosticProvider);
        Assertions.assertFalse(result.capabilities.diagnosticProvider.interFileDependencies);
        Assertions.assertFalse(result.capabilities.diagnosticProvider.workspaceDiagnostics);
    }

    @Test
    public void testDeclarationProviderEnabled() {
        Assertions.assertNotNull(result.capabilities.declarationProvider);
    }

    @Test
    public void testTextDocumentSyncIsIncremental() {
        // textDocumentSync = 2 means incremental sync
        Assertions.assertEquals(new TextDocumentSyncOptions(true,2), result.capabilities.textDocumentSync);
    }

    @Test
    public void testServerInfoIsCorrect() {
        Assertions.assertNotNull(result.serverInfo);
        Assertions.assertEquals("logoLsp", result.serverInfo.name);
        Assertions.assertEquals("1", result.serverInfo.version);
    }
}
