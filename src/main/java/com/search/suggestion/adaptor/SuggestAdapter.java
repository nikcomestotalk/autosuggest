package com.search.suggestion.adaptor;


import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SearchPayload;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.text.index.FuzzyIndex;
import com.search.suggestion.text.index.OptimizedTrie;
import com.search.suggestion.text.match.EditDistanceAutomaton;

import java.io.Serializable;
import java.util.Collection;

public class SuggestAdapter implements IndexAdapter<SuggestPayload>, Serializable
{
    private FuzzyIndex<SuggestPayload> index = new OptimizedTrie<>();
    private Boolean thresholdCheckNotRequired = false;
    private int minChars = 3;
    public Collection<ScoredObject<SuggestPayload>> get(String token, SearchPayload json)
    {
    	double threshold = Math.log(token.length());

    	if ( thresholdCheckNotRequired == true ) {
    		threshold = 0;
    	}
    	threshold = Math.round(threshold);
    	if(token.length() <= minChars) {
    	    threshold = 0;
        }
    	EditDistanceAutomaton eda = new EditDistanceAutomaton(token, threshold);
        return index.getAny(eda,json);
    }
    public void setFaultTolerant(Boolean bool) {
    	thresholdCheckNotRequired = bool;
    }
    @Override
    public boolean put(String token, SuggestPayload value)
    {
        return index.put(token, value);
    }

    public FuzzyIndex<SuggestPayload> getIndex() {
    	return index;
    }

    public void setIndex(FuzzyIndex<SuggestPayload> index) {
    	this.index = index;
    }
}
