package com.search.suggestion.interfaces;

import com.search.suggestion.data.RawResponse;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public interface ServerInterface {
    public List<RawResponse> getResponse(RawRequestInterface response);
    public int getPort();
    public String getContext() throws IOException;
    public HttpHandler getContextCallee() throws IOException;
}
