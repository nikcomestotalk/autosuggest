package com.search.suggestion.text.recover;

import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.text.index.FuzzyIndex;

/**
 * Created by nikhil on 19/1/18.
 */
public abstract class AbstractRecover implements Recover{
    protected SearchEngine<SuggestPayload> futureEngine;
    protected SearchEngine<SuggestPayload> searchEngine;

    public AbstractRecover(SearchEngine<SuggestPayload> suggestCurrent, SearchEngine<SuggestPayload> suggestFuture) {
        this.searchEngine = suggestCurrent;
        this.futureEngine = suggestFuture;
    }
    @Override
    public void swap()
    {
        SuggestAdapter futureAdapter = (SuggestAdapter) (futureEngine.indexUsed());
        SuggestAdapter currentAdapter = (SuggestAdapter) (searchEngine.indexUsed());
        FuzzyIndex<SuggestPayload> indexdata = futureAdapter.getIndex();
        currentAdapter.setIndex(indexdata);
        searchEngine.setIndexAdapter(currentAdapter);

    }

}
