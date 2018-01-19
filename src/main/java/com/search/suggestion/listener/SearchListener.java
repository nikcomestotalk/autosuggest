package com.search.suggestion.listener;

import com.search.suggestion.data.RawResponse;
import com.search.suggestion.data.RawSeachRequest;
import com.search.suggestion.data.SearchPayload;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.interfaces.RawRequestInterface;
import com.search.suggestion.interfaces.ServerInterface;
import com.search.suggestion.socket.ServerHandler;
import com.search.suggestion.util.ApplicationProperties;
import com.search.suggestion.util.PostQueryHandler;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhil on 24/7/17.
 */
public class SearchListener implements  ServerInterface {

    private final SearchEngine<SuggestPayload> searchEngine;
    private int port;

    public SearchListener(SearchEngine<SuggestPayload> searchEngine) {
        this.searchEngine = searchEngine;
    }

    public void startService(ServerHandler serverHandler) throws IOException {
        port = Integer.parseInt(ApplicationProperties.getProperty("listener.port"));
        serverHandler.startServer(this);
    }

    @Override
    public List<RawResponse> getResponse(RawRequestInterface rawRequest) {

        RawSeachRequest rawSeachRequest = (RawSeachRequest) rawRequest;
        SearchPayload searchPayload = new SearchPayload();
        searchPayload.setSearch(rawSeachRequest.getQuery());
        searchPayload.setFilter(rawSeachRequest.getFilter());
        searchPayload.setBucket(rawSeachRequest.getBucket());
        searchPayload.setLimit(rawSeachRequest.getLimit());

        List<SuggestPayload> list = searchEngine.search(searchPayload);
        List<RawResponse> result = new ArrayList<RawResponse>();
        int limit = 0;
        try {
            if(rawSeachRequest.getLimit() == 0) {
                limit = Integer.parseInt(ApplicationProperties.getProperty("listener.limit"));
            }
            if(rawSeachRequest.getLimit() > 0) {
                limit = rawSeachRequest.getLimit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i=0;
        for(SuggestPayload search : list) {
            RawResponse rawResponse = new RawResponse();
            rawResponse.setMatched(search.getRealText());
            rawResponse.setParameters(search.getFilter());
            result.add(rawResponse);
            i++;
            if(i >= limit) {
                break;
            }
        }
        return result;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getContext() throws IOException {
        return ApplicationProperties.getProperty("listener.url");
    }

    @Override
    public HttpHandler getContextCallee() throws IOException {
        return new PostQueryHandler(this);
    }


}
