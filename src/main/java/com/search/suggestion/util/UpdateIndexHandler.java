package com.search.suggestion.util;

import com.search.suggestion.data.RawSearchUpdateRequest;
import com.search.suggestion.interfaces.ServerInterface;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikhil on 30/7/17.
 */
public class UpdateIndexHandler implements HttpHandler {
    private final ServerInterface server;
    private final StopWords stopWords;

    public UpdateIndexHandler(ServerInterface server) throws IOException {
        this.server = server;
        stopWords = new StopWords();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Map<String, Object> parameters = new HashMap<String, Object>();
        Object b = httpExchange.getRequestBody();

        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            stringBuffer.append(line);
        }
        String query = stringBuffer.toString();

        ObjectMapper objectMapper = new ObjectMapper();
        RawSearchUpdateRequest rawRequest = objectMapper.readValue(query, RawSearchUpdateRequest.class);
        // send response
        server.getResponse(rawRequest);

        DumpData.save(query);
        String response = "Success";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
