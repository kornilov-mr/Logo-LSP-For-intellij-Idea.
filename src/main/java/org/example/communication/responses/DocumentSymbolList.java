package org.example.communication.responses;

import com.fasterxml.jackson.annotation.JsonValue;
import org.example.communication.DTO.DocumentSymbol;
import org.example.communication.LSPAny;

import java.util.List;

public class DocumentSymbolList extends LSPAny {

    @JsonValue
    private final List<DocumentSymbol> symbols;

    public DocumentSymbolList(List<DocumentSymbol> symbols) {
        this.symbols = symbols;
    }

    public List<DocumentSymbol> getSymbols() {
        return symbols;
    }
}
