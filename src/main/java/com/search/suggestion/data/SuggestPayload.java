package com.search.suggestion.data;

import java.io.Serializable;
import java.util.*;

public class SuggestPayload implements Indexable,Serializable
{
    private String search;
    private int count=1;
    private int recency =1;
    private  Boolean isapprox;
    private int records = 1;

	private Map<String, Integer> filter;
	public Map<String, Integer> getFilter() {
		return filter;
	}
    public Integer getFilter(String name) {
	    return filter.get(name);
    }
	public void setFilter(Map<String, Integer> filter) {
		this.filter = filter;
	}

	private String realText;

	public SuggestPayload()
	{

	}
    public SuggestPayload(String search, Map<String, Integer> filter)
    {
        this.search = search;
        this.filter = filter;
        this.isapprox = false;
        this.setCount("1");
    }
   
    public void copy(SuggestPayload sr)
	{
		search =sr.getSearch();
		filter = sr.getFilter();
		realText = sr.getRealText();
	}

    private Boolean isNonZero(int i)
	{
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
    public void setSearch(String search)
	{
    	this.search = search;
    }

    public Boolean getApproximation()
    {
    	return isapprox;
    }


	public int getCount()
	{
		return count;
	}

	public void setCount(String count)
	{
		try {
    		this.count= Integer.parseInt(count);
    	}
    	catch(Exception ex) {
    		this.count =1;
    	}
	}
	public void setCount(int count)
	{
		this.count =count;
	}

	public int getRecency()
	{
		return recency;
	}

	public void setRecency(int recency)
	{
		this.recency = recency;
	}

	public String getRealText()
	{
    	if(realText == null) {
    		return "";
		}
		return realText;
	}

	public void setRealText(String realText)
	{
		this.realText = realText;
	}

	public int getRecords()
	{
		return records;
	}

	public void setRecords(int records)
	{
		this.records = records;
	}
    public String toString()
	{
    	return "search"+ getSearch()+"realText"+ getRealText();
    }
    public Boolean ignoreFilter(String filter)
    {
        /*if(filter.equals("user")) {
            return true;
        }*/
        return false;
    }
}
