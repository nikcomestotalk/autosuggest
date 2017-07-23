package com.search.suggestion.adaptor;


import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SuggestRecord;
import com.search.suggestion.text.index.FuzzyIndex;
import com.search.suggestion.text.index.PatriciaTrie;
import com.search.suggestion.text.match.EditDistanceAutomaton;

import java.io.Serializable;
import java.util.Collection;

public class SuggestAdapter implements IndexAdapter<SuggestRecord>, Serializable
{
    private FuzzyIndex<SuggestRecord> index = new PatriciaTrie<>();
    private Boolean thresholdCheckNotRequired = false; 
    @Override
    public Collection<ScoredObject<SuggestRecord>> get(String token)
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
    public Collection<ScoredObject<SuggestRecord>> get(String token,SuggestRecord json)
    {
    	//System.out.println("HI");
    	double threshold = Math.log(token.length());
    	if(thresholdCheckNotRequired == true ) {
    		threshold = 0;
    	}
    	threshold = Math.ceil(threshold);
    	//System.out.println("New Threshold value is "+ threshold);
    	EditDistanceAutomaton eda = new EditDistanceAutomaton(token, threshold);
        return index.getAny(eda,json);
    }
    public void setFaultTolerant(Boolean bool) {
    	thresholdCheckNotRequired = bool;
    }
    @Override
    public boolean put(String token, SuggestRecord value)
    {
        return index.put(token, value);
    }

    @Override
    public boolean remove(SuggestRecord value)
    {
        return index.remove(value);
    }
    public FuzzyIndex<SuggestRecord> getIndex() {
    	return index;
    }
    public void setIndex(FuzzyIndex<SuggestRecord> index) {
    	this.index = index;
    }
}
