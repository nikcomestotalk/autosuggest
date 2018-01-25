package com.search.suggestion.util;

import com.google.gson.Gson;
import com.search.suggestion.data.RawResponse;
import com.search.suggestion.data.RawSearchUpdateRequest;
import com.search.suggestion.interfaces.ServerInterface;
import com.search.suggestion.text.backup.DumpData;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class UpdateIndexHandler implements HttpHandler {
    private final ServerInterface server;
    private final StopWords stopWords;

    public UpdateIndexHandler(ServerInterface server) throws IOException {
        this.server = server;
        stopWords = new StopWords();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Object b = httpExchange.getRequestBody();

        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            stringBuffer.append(line);
        }
        String query = stringBuffer.toString();

        RawSearchUpdateRequest rawRequest = null;
        RawResponse rawResponse = new RawResponse();
        List<RawResponse> list = new ArrayList<RawResponse>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            rawRequest = objectMapper.readValue(query, RawSearchUpdateRequest.class);
        }
        catch(JsonMappingException e) {
            rawResponse.setError(e.getPath().get(0).getFieldName() + " field invalid");
            list.add(rawResponse);
        }
        if(rawRequest != null) {
            server.getResponse(rawRequest);
            DumpData.save(query);

            rawResponse.setMessage("success");
            list.add(rawResponse);
        }

        // send response
        String response = new Gson().toJson(list);
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
