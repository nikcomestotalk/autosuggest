package com.search.suggestion.text.recover;

import com.search.suggestion.data.RawSeachRequest;
import com.search.suggestion.data.RawSearchUpdateRequest;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.util.StopWords;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.util.Date;

/**
 * Created by nikhil on 10/1/18.
 */
public class ReIndex implements Runnable{

    private String path;
    private SearchEngine<SuggestPayload> engine;
    private StopWords stopWords;
    public ReIndex(String file, SearchEngine<SuggestPayload> engine) throws IOException {
        path = file;
        this.engine = engine;
        stopWords = new StopWords();
    }
    @Override
    public void run() {

        File f = new File(path);
        String line = "";
        if(f.isFile()) {
            System.out.println("file found"+path);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(f));

                System.out.println("Starting time"+ path+ (new Date()));
                while ((line = br.readLine()) != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    RawSearchUpdateRequest rawRequest = objectMapper.readValue(line, RawSearchUpdateRequest.class);
                    rawRequest.setQuery(stopWords.removeStopWords(rawRequest.getQuery()));

                    SuggestPayload sr = new SuggestPayload();
                    String text = rawRequest.getQuery();
                    sr.setSearch(stopWords.removeStopWords(text));
                    sr.setRealText(text);

                    sr.setFilter(rawRequest.getParameter());
                    engine.add(sr);
                    //stopWordsSet.add(line.toUpperCase());
                }
                System.out.println("Starting time"+ path+(new Date()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
