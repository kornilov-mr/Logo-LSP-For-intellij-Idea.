package org.example.communication.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.communication.LSPAny;

import java.util.List;

public class DocumentSymbol extends LSPAny {

    @JsonProperty("name")
    public String name;

    @JsonProperty("kind")
    public int kind;

    @JsonProperty("range")
    public Range range;

    @JsonProperty("selectionRange")
    public Range selectionRange;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("children")
    public List<DocumentSymbol> children;

    public DocumentSymbol(String name, int kind, Range range, Range selectionRange, List<DocumentSymbol> children) {
        this.name = name;
        this.kind = kind;
        this.range = range;
        this.selectionRange = selectionRange;
        this.children = children;
    }
}
