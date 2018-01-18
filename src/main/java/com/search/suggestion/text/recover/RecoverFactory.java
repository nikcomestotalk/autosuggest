package com.search.suggestion.text.recover;

import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.util.ApplicationProperties;

import java.io.IOException;

/**
 * Created by nikhil on 19/1/18.
 */

public class RecoverFactory {
    public static Recover getInstance(SearchEngine<SuggestPayload> suggestCurrent, SearchEngine<SuggestPayload> suggestFuture) throws IOException {
        Recover recover = null;
        String method = ApplicationProperties.getProperty("recover.method");
        if(method.equals("file")) {
            recover = new RecoverFromText(suggestCurrent, suggestFuture);
        }
        if(method.equals("serialize")) {
            recover = new RecoverFromKyro(suggestCurrent, suggestFuture);
        }
        return recover;
    }
}
