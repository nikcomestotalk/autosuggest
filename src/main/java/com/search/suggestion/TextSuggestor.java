package com.search.suggestion;

import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.listener.SearchListener;
import com.search.suggestion.socket.ServerHandler;
import com.search.suggestion.text.analyze.SuggestAnalyzer;
import com.search.suggestion.updater.SearchUpdater;
import com.search.suggestion.util.BackupTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class TextSuggestor
{
    private TextSuggestor() { }

    public static void main(String[] args) throws IOException {

       /* List<String> a = new ArrayList<String>();
        a.add("HI");
        a.add("HasdfaI");
        a.add("asdfa");
        String b = String.join("<",a);
        a.clear();*/
        SearchEngine<SuggestPayload> suggest = new SearchEngine.Builder<SuggestPayload>()
                .setIndex(new SuggestAdapter())
                .setAnalyzer(new SuggestAnalyzer())
                .build();

        BackupTree backupTree = new BackupTree(suggest);
        backupTree.updateIndexer();
        ServerHandler serverHandler = new ServerHandler();
        (new SearchUpdater(suggest)).startService(serverHandler);

        (new SearchListener(suggest)).startService(serverHandler);

        (new Thread(backupTree)).start();
    }
}
/* Map<String, Integer> m = new HashMap<String,Integer>();
        m.put("user",1);
        m.put("category",12);
        m.put("location",14);
        SuggestPayload sr = new SuggestPayload("name nikhil", m);
        Map<String, Integer> m1 = new HashMap<String,Integer>();
        m1.put("user",2);
        m1.put("category",12);
        m1.put("location",10);
        SuggestPayload sr1 = new SuggestPayload("dhiman nicks",m1);
        Map<String, Integer> m2 = new HashMap<String,Integer>();
        m2.put("user",1);
        m2.put("category",10);
        m2.put("location",14);
        SuggestPayload sr2 = new SuggestPayload("nik dhiemn who",m2);

        suggest.add(sr);
        //suggest.add(sr1);
        suggest.add(sr2);

        Map<String, Integer> msearch = new HashMap<String,Integer>();
        msearch.put("user",1);
        msearch.put("category",10);
        msearch.put("location",14);
        SuggestPayload search = new SuggestPayload("ni", m);*/

//System.out.println(suggest.search(search));
