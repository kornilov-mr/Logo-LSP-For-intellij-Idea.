package org.example.communication.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.communication.LSPAny;


public class NullResult extends LSPAny {
    @JsonProperty("result")
    public Object result = null;
}
