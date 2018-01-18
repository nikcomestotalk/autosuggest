package com.search.suggestion;

import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.listener.SearchListener;
import com.search.suggestion.socket.ServerHandler;
import com.search.suggestion.text.analyze.SuggestAnalyzer;
import com.search.suggestion.updater.SearchUpdater;
import com.search.suggestion.util.BackupTree;
import com.search.suggestion.util.ReIndexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class TextSuggestor
{
    private TextSuggestor() { }

    public static void main(String[] args) throws IOException {

       SearchEngine<SuggestPayload> suggestCurrent = new SearchEngine.Builder<SuggestPayload>()
                .setIndex(new SuggestAdapter())
                .setAnalyzer(new SuggestAnalyzer())
                .build();

       SearchEngine<SuggestPayload> suggestFuture = new SearchEngine.Builder<SuggestPayload>()
                .setIndex(new SuggestAdapter())
                .setAnalyzer(new SuggestAnalyzer())
                .build();


        BackupTree backupTree = new BackupTree(suggestCurrent,suggestFuture);
        backupTree.updateIndexer();

        ServerHandler serverHandler = new ServerHandler();
        (new SearchUpdater(suggestCurrent)).startService(serverHandler);

        (new SearchListener(suggestCurrent)).startService(serverHandler);

        (new Thread(backupTree)).start();

    }
}
