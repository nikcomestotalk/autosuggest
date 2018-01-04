package com.search.suggestion.socket;

import com.search.suggestion.interfaces.ServerInterface;
import com.search.suggestion.util.HttpServerStream;

import java.util.HashMap;
import java.util.Map;

public class ServerHandler {
    private Map<Integer,Thread> localServer = new HashMap<Integer,Thread>();

    public void startServer(ServerInterface server)
    {
        Thread thread = null;
        if(!localServer.containsKey(server.getPort())) {
            thread = new Thread(new HttpServerStream(server));
            thread.start();
        }
        localServer.put(server.getPort(), thread);
    }
}