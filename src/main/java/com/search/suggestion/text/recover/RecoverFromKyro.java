package com.search.suggestion.text.recover;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.text.index.OptimizedTrie;
import com.search.suggestion.util.ApplicationProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RecoverFromKyro extends AbstractRecover {

    public RecoverFromKyro(SearchEngine<SuggestPayload> suggestCurrent, SearchEngine<SuggestPayload> suggestFuture) {
        super(suggestCurrent, suggestFuture);
    }

    public void updateIndexer() throws IOException {
		Kryo kryo = new Kryo();

        SuggestAdapter adapter = (SuggestAdapter)(futureEngine.indexUsed());

		String contentPath = System.getProperties().get("user.dir") + ApplicationProperties.getProperty("recover.method.serialize.path");
        File f = new File(contentPath);
        if(f.isFile()) {
            try {
                Input input = new Input(new FileInputStream(contentPath));
                OptimizedTrie index = kryo.readObject(input, OptimizedTrie.class);
                input.close();

                adapter.setIndex(index);
				futureEngine.setIndexAdapter(adapter);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
	}
}