package com.search.suggestion.updater;

import com.search.suggestion.data.RawResponse;
import com.search.suggestion.data.RawSearchUpdateRequest;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.interfaces.RawRequestInterface;
import com.search.suggestion.interfaces.ServerInterface;
import com.search.suggestion.socket.ServerHandler;
import com.search.suggestion.util.ApplicationProperties;
import com.search.suggestion.util.StopWords;
import com.search.suggestion.util.UpdateIndexHandler;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SearchUpdater implements ServerInterface {

    private final StopWords stopWords;
    private int port;
    private final SearchEngine<SuggestPayload> searchEngine;
    public SearchUpdater(SearchEngine<SuggestPayload> searchEngine) throws IOException {
        stopWords = new StopWords();
        this.searchEngine = searchEngine;
    }

    public void startService(ServerHandler serverHandler) throws IOException {
        port = Integer.parseInt(ApplicationProperties.getProperty("updater.port"));
        serverHandler.startServer(this);
    }

    @Override
    public List<RawResponse> getResponse(RawRequestInterface request) {
        RawSearchUpdateRequest rawRequest = (RawSearchUpdateRequest) request;
        SuggestPayload sr = new SuggestPayload();
        String text = rawRequest.getQuery();
        sr.setSearch(stopWords.removeStopWords(text));
        sr.setRealText(text);

        sr.setFilter(rawRequest.getParameter());
        searchEngine.add(sr);

        return new ArrayList<RawResponse>();
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public String getContext() throws IOException {
        return ApplicationProperties.getProperty("updater.url");
    }

    @Override
    public HttpHandler getContextCallee() throws IOException {
        return new UpdateIndexHandler(this);
    }

}
