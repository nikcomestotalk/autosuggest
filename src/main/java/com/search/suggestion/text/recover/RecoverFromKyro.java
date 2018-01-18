package com.search.suggestion.text.recover;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.text.index.FuzzyIndex;
import com.search.suggestion.text.index.PatriciaTrie;
import com.search.suggestion.util.ApplicationProperties;
import com.search.suggestion.util.ReIndexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
                PatriciaTrie index = kryo.readObject(input, PatriciaTrie.class);
                input.close();

                adapter.setIndex(index);
				futureEngine.setIndexAdapter(adapter);
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
	}

	@Override
	public void swap() {
		searchEngine = futureEngine;
	}
}