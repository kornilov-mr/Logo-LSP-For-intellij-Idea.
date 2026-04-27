package org.example.communication.requests;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.example.communication.LSPAny;
import org.example.communication.Message;
import org.example.server.EndPointContext;

import java.io.IOException;

public class LSPWrapperDeserializer extends JsonDeserializer<LSPRequestWrapper> {
    @Override
    public LSPRequestWrapper deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode root = p.getCodec().readTree(p);

        String jsonRpcVersion = root.path("jsonrpc").asText("2.0");
        int id = root.path("id").asInt();
        String method = root.path("method").asText(null);

        LSPAny params = null;
        JsonNode paramsNode = root.get("params");
        if (paramsNode != null && !paramsNode.isNull()) {
            Class<? extends LSPAny> paramsType = EndPointContext.getRequestType(method);
            params = p.getCodec().treeToValue(paramsNode, paramsType);
        }

        LSPRequestWrapper wrapper = new LSPRequestWrapper(id, method, params);
        wrapper.jsonRPCVersion = jsonRpcVersion;
        return wrapper;
    }
}
