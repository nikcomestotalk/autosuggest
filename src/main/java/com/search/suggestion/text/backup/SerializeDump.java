package com.search.suggestion.text.backup;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.text.index.FuzzyIndex;
import com.search.suggestion.util.ApplicationProperties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by nikhil on 19/1/18.
 */
public class SerializeDump implements Runnable {
    private SearchEngine<SuggestPayload> searchEngine;;
    private FuzzyIndex<SuggestPayload> indexdata;
    private int timegap = 5000;

    public SerializeDump(SearchEngine<SuggestPayload> engine) {
        this.searchEngine = engine;
    }
    @Override
    public void run() {
        try {
            if(ApplicationProperties.getProperty("recover.method").equals("serialize")) {
                Kryo kryo = new Kryo();
                while (true) {
                    SuggestAdapter adapter = (SuggestAdapter) (searchEngine.indexUsed());
                    indexdata = adapter.getIndex();
                    FileOutputStream fos = null;
                    ObjectOutputStream out = null;
                    try {
                        String contentPath = System.getProperties().get("user.dir") + ApplicationProperties.getProperty("recover.method.serialize.path");
                        Output output = new Output(new FileOutputStream(contentPath));
                        kryo.writeObject(output, indexdata);
                        output.close();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            Thread.sleep(timegap);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
