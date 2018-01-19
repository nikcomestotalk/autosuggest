package com.search.suggestion.util;

import com.search.suggestion.interfaces.ServerInterface;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by nikhil on 30/7/17.
 */
public class HttpServerStream implements Runnable{
    private final ServerInterface server;

    public HttpServerStream(ServerInterface server) {
        this.server = server;
    }
    @Override
    public void run() {
        try {
            listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void listen() throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(server.getPort()), 0);

        Logger.log("server started at " + server.getPort());

        httpServer.createContext(server.getContext(), server.getContextCallee());
        httpServer.setExecutor(null);
        httpServer.start();
    }
}
