package com.search.suggestion;

import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.listener.SearchListener;
import com.search.suggestion.socket.ServerHandler;
import com.search.suggestion.text.analyze.SuggestAnalyzer;
import com.search.suggestion.text.backup.SerializeDump;
import com.search.suggestion.text.recover.Recover;
import com.search.suggestion.text.recover.RecoverFactory;
import com.search.suggestion.updater.SearchUpdater;
import com.search.suggestion.text.recover.RecoverFromText;

import java.io.IOException;

public final class TextSuggestor
{
    private TextSuggestor() { }

    public static void main(String[] args) throws IOException, InterruptedException {

       SearchEngine<SuggestPayload> suggestCurrent = new SearchEngine.Builder<SuggestPayload>()
               .setIndex(new SuggestAdapter())
               .setAnalyzer(new SuggestAnalyzer())
               .build();

       SearchEngine<SuggestPayload> suggestFuture = new SearchEngine.Builder<SuggestPayload>()
                .setIndex(new SuggestAdapter())
                .setAnalyzer(new SuggestAnalyzer())
                .build();


        Recover recover = RecoverFactory.getInstance(suggestCurrent, suggestFuture);
        recover.updateIndexer();
        recover.swap();
        ServerHandler serverHandler = new ServerHandler();
        (new SearchUpdater(suggestCurrent)).startService(serverHandler);

        (new SearchListener(suggestCurrent)).startService(serverHandler);

        (new Thread(new SerializeDump(suggestCurrent))).start();

    }
}
