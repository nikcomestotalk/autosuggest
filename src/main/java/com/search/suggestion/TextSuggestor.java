package com.search.suggestion;

import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestRecord;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.text.analyze.SuggestAnalyzer;

import java.util.HashMap;
import java.util.Map;

public final class TextSuggestor
{
    @SuppressWarnings("checkstyle:leftcurly")
    private TextSuggestor() { }

    public static void main(String[] args)
    {
        SearchEngine<SuggestRecord> suggest = new SearchEngine.Builder<SuggestRecord>()
                .setIndex(new SuggestAdapter())
                .setAnalyzer(new SuggestAnalyzer())
                .build();

        Map<String, Integer> m = new HashMap<String,Integer>();
        m.put("user",1);
        m.put("category",12);
        m.put("location",14);
        SuggestRecord sr = new SuggestRecord("name nikhil", m);
        Map<String, Integer> m1 = new HashMap<String,Integer>();
        m1.put("user",2);
        m1.put("category",12);
        m1.put("location",10);
        SuggestRecord sr1 = new SuggestRecord("dhiman nicks",m1);
        Map<String, Integer> m2 = new HashMap<String,Integer>();
        m2.put("user",1);
        m2.put("category",10);
        m2.put("location",14);
        SuggestRecord sr2 = new SuggestRecord("nik dhiemn who",m2);

        suggest.add(sr);
        //suggest.add(sr1);
        suggest.add(sr2);

        Map<String, Integer> msearch = new HashMap<String,Integer>();
        msearch.put("user",1);
        msearch.put("category",10);
        msearch.put("location",14);
        SuggestRecord search = new SuggestRecord("ni", m);

        System.out.println(suggest.search(search));
        /*System.out.println(suggest.search("samsung is rediff and infoedge"));
        System.out.println(suggest.search("snfo"));
        System.out.println(suggest.search("edge"));*/
    }
}

