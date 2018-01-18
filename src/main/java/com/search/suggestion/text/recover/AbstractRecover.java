package com.search.suggestion.text.recover;

import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;

/**
 * Created by nikhil on 19/1/18.
 */
public abstract class AbstractRecover implements Recover{
    protected SearchEngine<SuggestPayload> futureEngine;
    protected SearchEngine<SuggestPayload> searchEngine;

    public AbstractRecover() {

    }
    public AbstractRecover(SearchEngine<SuggestPayload> suggestCurrent, SearchEngine<SuggestPayload> suggestFuture) {
        this.searchEngine = suggestCurrent;
        this.futureEngine = suggestFuture;
    }

}
