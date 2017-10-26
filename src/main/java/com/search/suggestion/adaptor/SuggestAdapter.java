package com.search.suggestion.adaptor;


import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SearchPayload;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.text.index.FuzzyIndex;
import com.search.suggestion.text.index.PatriciaTrie;
import com.search.suggestion.text.match.EditDistanceAutomaton;

import java.io.Serializable;
import java.util.Collection;

public class SuggestAdapter implements IndexAdapter<SuggestPayload>, Serializable
{
    private FuzzyIndex<SuggestPayload> index = new PatriciaTrie<>();
    private Boolean thresholdCheckNotRequired = false; 
    @Override
    public Collection<ScoredObject<SuggestPayload>> get(String token)
    {
    	//System.out.println("HI");
    	double threshold = Math.log(token.length());
    	if(thresholdCheckNotRequired == true ) {
    		threshold = 0;
    	}
    	//System.out.println("Threshold value is "+ threshold);
    	EditDistanceAutomaton eda = new EditDistanceAutomaton(token, threshold);
        return index.getAny(eda);
    }
    public Collection<ScoredObject<SuggestPayload>> get(String token, SearchPayload json)
    {
    	//System.out.println("HI");
    	double threshold = Math.log(token.length());

    	if ( thresholdCheckNotRequired == true ) {
    		threshold = 0;
    	}
    	threshold = Math.round(threshold);
    	//System.out.println("New Threshold value is "+ threshold);
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

    @Override
    public boolean remove(SuggestPayload value)
    {
        return index.remove(value);
    }
    public FuzzyIndex<SuggestPayload> getIndex() {
    	return index;
    }
    public void setIndex(FuzzyIndex<SuggestPayload> index) {
    	this.index = index;
    }
}
