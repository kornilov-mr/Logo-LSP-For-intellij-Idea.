package org.example.server;

import org.example.communication.EncodingMessage;
import org.example.communication.LSPAny;
import org.example.communication.NoResponse;
import org.example.communication.requests.LSPRequestWrapper;
import org.example.communication.responses.LSPResponseWrapper;

import java.io.*;
import java.util.logging.Logger;

public class LogoLSPServer {

    private static final MessageDispatcher dispatcher = new MessageDispatcher();
    private final Logger logger = Logger.getLogger(String.valueOf(LogoLSPServer.class));

    static void main(String[] args) {
        LogoLSPServer server = new LogoLSPServer();
        server.start();
        System.exit(WorkingContext.isShutdown ? 0 : 1);
    }

    public void start() {
        start(System.in, System.out);
    }

    public void start(InputStream in, PrintStream out) {
        JsonRPCScanner scanner = new JsonRPCScanner(in);
        while (true) {
            String jsonRPC;
            try {
                jsonRPC = scanner.readNextRPCJson();
            } catch (EOFException e) {
                logger.info("Client closed connection");
                return;
            } catch (IOException e) {
                logger.severe("IO error reading from client: " + e.getMessage());
                return;
            }
            try {
                logger.info("receiving request: " + jsonRPC);
                LSPRequestWrapper lspMessage = (LSPRequestWrapper) EncodingMessage.decodeFromJsonRPC(jsonRPC);
                LSPAny result = dispatcher.DispatchAndProcessLSPRequest(lspMessage);
                if (result instanceof NoResponse)
                    continue;
                LSPResponseWrapper lspResponse = new LSPResponseWrapper(lspMessage.id, result);
                String encodedLspResponse = EncodingMessage.encodeToJsonRPC(lspResponse);
                logger.info("Sending response: " + encodedLspResponse);
                out.print(encodedLspResponse);
                out.flush();
            } catch (Exception e) {
                logger.severe("Error processing message: " + e.getMessage());
            }
        }
    }

}
