package handlers;

import org.example.communication.DTO.DocumentSymbol;
import org.example.communication.DTO.TextDocumentIdentifier;
import org.example.communication.requests.DocumentSymbolParams;
import org.example.communication.responses.DocumentSymbolList;
import org.example.project.ProjectContext;
import org.example.server.handlers.TextDocumentDocumentSymbolHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentSymbolProviderTests {

    private static final TextDocumentDocumentSymbolHandler handler = new TextDocumentDocumentSymbolHandler();
    private static final String URI = "src/test/resources/ast/programs/forSemanticTokens/shrinkingSquares.logo";

    @BeforeAll
    public static void setup() throws IOException {
        ProjectContext.didOpenFile(URI, Files.readString(new File(URI).toPath()));
    }

    @Test
    public void returnsOneSymbolPerProcedureAndTopLevelVariable() throws Exception {
        // shrinkingSquares.logo has: drawSquare, shrinkingSquares, make "size
        List<DocumentSymbol> symbols = invoke(URI);
        assertEquals(3, symbols.size());
    }

    @Test
    public void procedureSymbolHasCorrectNameAndKind() throws Exception {
        List<DocumentSymbol> symbols = invoke(URI);
        assertEquals("drawSquare", symbols.get(0).name);
        assertEquals(12, symbols.get(0).kind); // SymbolKind.Function
        assertEquals("shrinkingSquares", symbols.get(1).name);
        assertEquals(12, symbols.get(1).kind);
    }

    @Test
    public void procedureSymbolHasParametersAsChildren() throws Exception {
        List<DocumentSymbol> symbols = invoke(URI);
        DocumentSymbol drawSquare = symbols.get(0);
        assertNotNull(drawSquare.children);
        assertEquals(1, drawSquare.children.size());
        assertEquals(":size", drawSquare.children.get(0).name);
        assertEquals(13, drawSquare.children.get(0).kind); // SymbolKind.Variable
    }

    @Test
    public void topLevelVariableSymbolHasCorrectNameAndKind() throws Exception {
        List<DocumentSymbol> symbols = invoke(URI);
        DocumentSymbol sizeVar = symbols.get(2);
        assertEquals("size", sizeVar.name);
        assertEquals(13, sizeVar.kind); // SymbolKind.Variable
        assertNull(sizeVar.children);
    }

    @Test
    public void allSymbolsHaveNonNullRanges() throws Exception {
        List<DocumentSymbol> symbols = invoke(URI);
        for (DocumentSymbol symbol : symbols) {
            assertNotNull(symbol.range, "range must not be null for " + symbol.name);
            assertNotNull(symbol.selectionRange, "selectionRange must not be null for " + symbol.name);
        }
    }

    @Test
    public void selectionRangeCoversOnlyTheName() throws Exception {
        List<DocumentSymbol> symbols = invoke(URI);
        DocumentSymbol drawSquare = symbols.get(0);
        // selectionRange should be narrower than (or equal to) the full range
        assertTrue(drawSquare.selectionRange.start.line >= drawSquare.range.start.line);
        assertTrue(drawSquare.selectionRange.end.line <= drawSquare.range.end.line);
    }

    @Test
    public void procedureWithNoParametersHasNullChildren() throws Exception {
        String src = "to noParams\n  forward 100\nend\n";
        String uri = "test://noparams.logo";
        ProjectContext.didOpenFile(uri, src);
        List<DocumentSymbol> symbols = invoke(uri);
        assertEquals(1, symbols.size());
        assertNull(symbols.get(0).children);
    }

    private List<DocumentSymbol> invoke(String uri) throws Exception {
        DocumentSymbolParams params = new DocumentSymbolParams(new TextDocumentIdentifier(uri));
        return ((DocumentSymbolList) handler.handleMessage(params)).getSymbols();
    }
}
