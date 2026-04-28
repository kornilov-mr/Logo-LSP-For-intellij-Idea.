package org.example.communication.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.communication.DTO.TextDocumentIdentifier;
import org.example.communication.Message;

public class DocumentSymbolParams extends Message {

    @JsonProperty("textDocument")
    public TextDocumentIdentifier textDocument;

    @JsonCreator
    public DocumentSymbolParams(@JsonProperty("textDocument") TextDocumentIdentifier textDocument) {
        this.textDocument = textDocument;
    }
}
