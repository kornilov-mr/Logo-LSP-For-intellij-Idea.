package org.example.server;

import org.example.communication.LSPAny;
import org.example.communication.ResponseErrorsCode;
import org.example.communication.requests.LSPRequestWrapper;
import org.example.communication.responses.LSPResponseError;

import java.util.HashMap;
import java.util.Map;

public class MessageDispatcher {
    public final Map<String, EndPointContext> endPoints = new HashMap<>();

    public MessageDispatcher() {
        registerEndPointsFromContext();
    }
    private void registerEndPointsFromContext() {
        for (EndPointContext endPointContext : EndPointContext.values()){
            endPoints.put(endPointContext.methodName, endPointContext);
        }
    }
    public LSPAny DispatchAndProcessLSPRequest(LSPRequestWrapper lspMessage){
        String methodName = lspMessage.methodName;
        if (WorkingContext.isShutdown && !methodName.equals("exit"))
            return new LSPResponseError(ResponseErrorsCode.InvalidRequest, "Server is shutting down");
        if(!endPoints.containsKey(methodName))
            throw new RuntimeException("Endpoint for Method:"+methodName+" not found");
        EndPointContext endPoint = endPoints.get(methodName);
        try{
            return endPoint.handler.handleMessage(lspMessage.params);
        }catch (Exception e){
            return new LSPResponseError(ResponseErrorsCode.InternalError,e.getMessage());
        }
    }
}
