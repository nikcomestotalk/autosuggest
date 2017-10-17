package com.search.suggestion.util;

import com.google.gson.Gson;
import com.search.suggestion.data.RawResponse;
import com.search.suggestion.data.RawSeachRequest;
import com.search.suggestion.interfaces.ServerInterface;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpHeaders;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nikhil on 30/7/17.
 */
public class PostQueryHandler implements HttpHandler {
    private final ServerInterface server;
    private final StopWords stopWords;

    public PostQueryHandler(ServerInterface server) throws IOException {
        this.server = server;
        stopWords = new StopWords();

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        System.out.println(httpExchange.getRequestMethod());
        if (httpExchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            httpExchange.sendResponseHeaders(204, -1);
            return ;
        }
        System.out.println("out of itunes");
        Map<String, Object> parameters = new HashMap<String, Object>();

        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            stringBuffer.append(line);
        }
        String query = stringBuffer.toString();

        ObjectMapper objectMapper = new ObjectMapper();
        RawSeachRequest rawRequest = objectMapper.readValue(query, RawSeachRequest.class);
        rawRequest.setQuery(stopWords.removeStopWords(rawRequest.getQuery()));
        // send response
        List<RawResponse> list = server.getResponse(rawRequest);

        String response = new Gson().toJson(list);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.sendResponseHeaders(200, response.length());


        //httpExchange.getRequestHeaders().set("Content-Type", "application/json; charset=utf8");
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
