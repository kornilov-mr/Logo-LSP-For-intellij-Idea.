package handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.communication.DTO.CompletionList;
import org.example.communication.requests.CompletionParams;
import org.example.server.handlers.TextDocumentCompletionHandler;
import org.example.project.ProjectContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CompletionProviderTests {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final File variableCompletionFile= new File("src/test/resources/ast/programs/forCompletion/variableCompletion.logo");
    private static final TextDocumentCompletionHandler completionHandler = new TextDocumentCompletionHandler();
    @BeforeAll
    public static void setup() throws IOException {
        ProjectContext.didOpenFile("src/test/resources/ast/programs/forCompletion/variableCompletion.logo", Files.readString(variableCompletionFile.toPath()));
    }
    @Test
    public void testVariableCompletionProvider() throws Exception {
        String expected = "{\"isIncomplete\":false,\"itemDefaults\":null,\"items\":[{\"label\":\"a\",\"labelDetails\":null,\"kind\":6,\"tags\":null,\"detail\":null,\"documentation\":null,\"deprecated\":null,\"preselect\":null,\"sortText\":null,\"filterText\":null,\"insertText\":null,\"insertTextFormat\":null,\"insertTextMode\":null,\"textEdit\":null,\"additionalTextEdits\":null,\"commitCharacters\":null,\"command\":null,\"data\":null},{\"label\":\"b\",\"labelDetails\":null,\"kind\":6,\"tags\":null,\"detail\":null,\"documentation\":null,\"deprecated\":null,\"preselect\":null,\"sortText\":null,\"filterText\":null,\"insertText\":null,\"insertTextFormat\":null,\"insertTextMode\":null,\"textEdit\":null,\"additionalTextEdits\":null,\"commitCharacters\":null,\"command\":null,\"data\":null},{\"label\":\"c\",\"labelDetails\":null,\"kind\":6,\"tags\":null,\"detail\":null,\"documentation\":null,\"deprecated\":null,\"preselect\":null,\"sortText\":null,\"filterText\":null,\"insertText\":null,\"insertTextFormat\":null,\"insertTextMode\":null,\"textEdit\":null,\"additionalTextEdits\":null,\"commitCharacters\":null,\"command\":null,\"data\":null}]}";
        String requestParams ="{\"context\":{\"triggerKind\":2,\"triggerCharacter\":\":\"},\"textDocument\":{\"uri\":\"src/test/resources/ast/programs/forCompletion/variableCompletion.logo\"},\"position\":{\"line\":12,\"character\":6}}";
        CompletionParams completionParams = objectMapper.readValue(requestParams, CompletionParams.class);

        CompletionList list = (CompletionList) completionHandler.handleMessage(completionParams);
        Assertions.assertEquals(expected, objectMapper.writeValueAsString(list));
    }
}
