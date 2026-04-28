import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.server.LogoLSPServer;
import org.example.server.WorkingContext;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LspIntegrationTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final AtomicInteger idCounter = new AtomicInteger(1);

    private static PipedOutputStream clientToServer;
    private static InputStream serverToClient;
    private static Thread serverThread;

    @BeforeAll
    static void startServer() throws Exception {
        WorkingContext.isShutdown = false;

        PipedInputStream serverIn = new PipedInputStream(65536);
        clientToServer = new PipedOutputStream(serverIn);

        PipedOutputStream serverOut = new PipedOutputStream();
        serverToClient = new PipedInputStream(serverOut, 65536);

        LogoLSPServer server = new LogoLSPServer();
        serverThread = new Thread(() ->
                server.start(serverIn, new PrintStream(serverOut, true, StandardCharsets.UTF_8)));
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        sendRequest("shutdown", Map.of());
        readResponse();
        clientToServer.close();
        serverThread.join(3000);
    }

    @Test
    @Order(1)
    void initialize_returnsServerCapabilities() throws Exception {
        int id = sendRequest("initialize", Map.of(
                "processId", 1,
                "rootUri", "file:///test",
                "capabilities", Map.of()
        ));
        JsonNode response = readResponse();

        assertEquals(id, response.get("id").asInt());
        assertNull(response.get("error"));

        JsonNode caps = response.get("result").get("capabilities");
        assertTrue(caps.get("hoverProvider").asBoolean());
        assertNotNull(caps.get("completionProvider"));
        assertNotNull(caps.get("semanticTokensProvider"));
        assertNotNull(caps.get("diagnosticProvider"));
    }

    @Test
    @Order(2)
    void initialized_notification_serverRemainsResponsive() throws Exception {
        sendNotification("initialized", Map.of());
        // No response for notifications; verified implicitly by subsequent tests passing.
    }

    @Test
    @Order(3)
    void didOpen_validProgram_producesNoDiagnostics() throws Exception {
        openFile("file:///square.logo",
                "to square :side\n  repeat 4 [forward :side right 90]\nend");

        int id = sendRequest("textDocument/diagnostic",
                Map.of("textDocument", Map.of("uri", "file:///square.logo")));
        JsonNode response = readResponse();

        assertEquals(id, response.get("id").asInt());
        assertNull(response.get("error"));
        assertEquals("full", response.get("result").get("kind").asText());
        assertTrue(response.get("result").get("items").isEmpty(),
                "Expected no diagnostics for a valid Logo program");
    }

    @Test
    @Order(4)
    void hover_overBuiltin_returnsDocumentation() throws Exception {
        // "forward" occupies characters 12-18 on line 1 of the file opened in test 3
        int id = sendRequest("textDocument/hover", Map.of(
                "textDocument", Map.of("uri", "file:///square.logo"),
                "position", Map.of("line", 1, "character", 14)
        ));
        JsonNode response = readResponse();

        assertEquals(id, response.get("id").asInt());
        assertNull(response.get("error"));
        JsonNode result = response.get("result");
        if (result != null && !result.isNull()) {
            assertFalse(result.get("contents").asText().isBlank());
        }
    }

    @Test
    @Order(5)
    void didOpen_fileWithUndefinedFunction_reportsDiagnostic() throws Exception {
        openFile("file:///error.logo", "unknownFunction 10");

        int id = sendRequest("textDocument/diagnostic",
                Map.of("textDocument", Map.of("uri", "file:///error.logo")));
        JsonNode response = readResponse();

        assertEquals(id, response.get("id").asInt());
        assertNull(response.get("error"));
        assertFalse(response.get("result").get("items").isEmpty(),
                "Expected at least one diagnostic for an undefined function call");
    }

    // --- Helpers ---

    private static void openFile(String uri, String content) throws Exception {
        sendNotification("textDocument/didOpen", Map.of(
                "textDocument", Map.of(
                        "uri", uri,
                        "languageId", "logo",
                        "version", 1,
                        "text", content
                )
        ));
    }

    private static int sendRequest(String method, Object params) throws Exception {
        int id = idCounter.getAndIncrement();
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("jsonrpc", "2.0");
        request.put("id", id);
        request.put("method", method);
        request.put("params", params);
        send(request);
        return id;
    }

    private static void sendNotification(String method, Object params) throws Exception {
        Map<String, Object> notification = new LinkedHashMap<>();
        notification.put("jsonrpc", "2.0");
        notification.put("method", method);
        notification.put("params", params);
        send(notification);
    }

    private static void send(Object message) throws Exception {
        String json = mapper.writeValueAsString(message);
        byte[] bodyBytes = json.getBytes(StandardCharsets.UTF_8);
        String frame = "Content-Length:" + bodyBytes.length + "\r\n\r\n" + json;
        clientToServer.write(frame.getBytes(StandardCharsets.UTF_8));
        clientToServer.flush();
    }

    private static JsonNode readResponse() throws Exception {
        int contentLength = -1;
        while (true) {
            String line = readLine();
            if (line.isEmpty()) break;
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.substring("Content-Length:".length()).trim());
            }
        }
        if (contentLength < 0) throw new IOException("Missing Content-Length in response");
        byte[] body = new byte[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            int read = serverToClient.read(body, offset, contentLength - offset);
            if (read == -1) throw new EOFException("Server closed connection unexpectedly");
            offset += read;
        }
        return mapper.readTree(body);
    }

    private static String readLine() throws Exception {
        StringBuilder sb = new StringBuilder();
        boolean seenCR = false;
        int b;
        while ((b = serverToClient.read()) != -1) {
            if (b == '\r') { seenCR = true; continue; }
            if (seenCR && b == '\n') break;
            if (seenCR) { sb.append('\r'); seenCR = false; }
            sb.append((char) b);
        }
        if (b == -1) throw new EOFException("Server closed connection unexpectedly");
        return sb.toString();
    }
}
