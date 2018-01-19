package com.search.suggestion.util;

import com.google.gson.Gson;
import com.search.suggestion.data.RawResponse;
import com.search.suggestion.data.RawSearchUpdateRequest;
import com.search.suggestion.interfaces.ServerInterface;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created by nikhil on 29/7/17.
 */
public class InputOutputStream implements Runnable{

    private ServerInterface server;
    private Socket socket;
    public InputOutputStream(ServerInterface server)
    {
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
        ServerSocket serverSocket = new ServerSocket(server.getPort());
        Logger.log("Now listening on port "+server.getPort());
        while (true)
        {
            socket = serverSocket.accept();
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder out = new StringBuilder();
            String line;
            String input = "";

            input = br.readLine();

            BufferedWriter bw  =null;
            OutputStreamWriter osw = null;
            OutputStream os =null;
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                RawSearchUpdateRequest rawRequest = objectMapper.readValue(input, RawSearchUpdateRequest.class);

                os = socket.getOutputStream();
                osw = new OutputStreamWriter(os);
                bw = new BufferedWriter(osw);
                List<RawResponse> list = this.server.getResponse(rawRequest);
                String gson = new Gson().toJson(list);
                bw.write(gson);
                bw.flush();
                bw.close();

            } catch(Exception e) {

                e.printStackTrace();
                if(bw !=null) {
                    bw.write("Server Error");
                    bw.flush();
                    bw.close();
                }
                if(bw == null) {
                    os = socket.getOutputStream();
                    osw = new OutputStreamWriter(os);
                    bw = new BufferedWriter(osw);
                    bw.write("Server Error");
                    bw.flush();
                    bw.close();
                }

            }

        }
    }
}
