package com.search.suggestion.text.recover;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.util.ApplicationProperties;
import com.search.suggestion.util.DateUtil;

public class RecoverFromText extends AbstractRecover {

	public RecoverFromText(SearchEngine<SuggestPayload> suggestCurrent, SearchEngine<SuggestPayload> suggestFuture) {
		super(suggestCurrent, suggestFuture);
	}

	@Override
    public void updateIndexer() throws IOException, InterruptedException {

		ExecutorService es = Executors.newCachedThreadPool();
		int days = Integer.parseInt(ApplicationProperties.getProperty("recover.method.file.days"));
		String userdir = (String) System.getProperties().get("user.dir");
		String storage = ApplicationProperties.getProperty("recover.method.file.backup.path");

		for(int i=0;i<days;i++) {
            String filename = DateUtil.getStringDate("yyyy-MM-dd", DateUtil.getDate(-i));
			String filePath =  String.format("%s%s%s.txt", userdir, storage, filename);

			es.execute(new ReIndex(filePath, futureEngine));
		}
		es.shutdown();
		es.awaitTermination(1, TimeUnit.DAYS );
    }
}