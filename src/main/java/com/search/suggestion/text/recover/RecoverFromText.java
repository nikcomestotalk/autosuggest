package com.search.suggestion.text.recover;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.util.ApplicationProperties;
import com.search.suggestion.util.DateUtil;
import com.search.suggestion.util.ReIndexer;

public class RecoverFromText extends AbstractRecover {

	public RecoverFromText(SearchEngine<SuggestPayload> suggestCurrent, SearchEngine<SuggestPayload> suggestFuture) {
		super(suggestCurrent, suggestFuture);
	}

	@Override
    public void updateIndexer() throws IOException, InterruptedException {

		ExecutorService es = Executors.newCachedThreadPool();
		int days = Integer.parseInt(ApplicationProperties.getProperty("recover.method.file.days"));
		for(int i=0;i<days;i++) {
			es.execute(new ReIndexer(DateUtil.getStringDate("yyyy-MM-dd", DateUtil.getDate(-i)), futureEngine ));
		}
		es.shutdown();
		es.awaitTermination(1, TimeUnit.DAYS );
    }

	@Override
	public void swap() {
		searchEngine = futureEngine;
	}
}