package com.search.suggestion.data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SearchPayload implements Indexable,Serializable
{
    private String search;
    private  Boolean isapprox;

	private Map<String, Integer> filter;
	private Map<String, Map<String,Integer>> bucket;
	private int limit;

	private String realText;

	public SearchPayload()
	{

	}
    public SearchPayload(String search, Map<String, Integer> filter)
    {
        this.search = search;
        this.filter = filter;
        this.isapprox = false;
    }

	public Map<String, Map<String,Integer>> getBucket() {
		return bucket;
	}

	public void setBucket(Map<String, Map<String,Integer>> bucket) {
		this.bucket = bucket;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Map<String, Integer> getFilter() {
		return filter;
	}
	public Integer getFilter(String name) {
		return filter.get(name);
	}
	public Map<String,Integer> getBucket(String name) {
	    return bucket.get(name);
    }
    public String getFirstBucket() {
		String name = "";
		for (Map.Entry<String, Map<String,Integer>> entry : bucket.entrySet())
		{
			name = entry.getKey();
		}
		return name;

	}

	public void setFilter(Map<String, Integer> filter) {
		this.filter = filter;
	}



    private Boolean isNonZero(int i) {
    	if(i>0)
    		return true;
    	return false;
    }
    @Override
    public List<String> getFields()
    {
        return Arrays.asList(search);
    }
    
    public String getSearch()
    {
        return search;
    }
    public void setSearch(String search) {
    	this.search = search;
    }


	public String getRealText() {
    	if(realText == null) {
    		return "";
		}
		return realText;
	}

	public void setRealText(String realText) {
		this.realText = realText;
	}

    public String toString() {
    	return "search"+ getSearch()+"realText"+ getRealText();
    }

}
