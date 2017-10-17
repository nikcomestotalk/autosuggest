package com.search.suggestion.data;

import com.search.suggestion.interfaces.RawRequestInterface;

import java.util.HashMap;
import java.util.Map;

public class RawSearchUpdateRequest implements RawRequestInterface{
    private String query;
    private Map<String,Integer> parameter = new HashMap<String,Integer>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Integer> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, Integer> parameter) {
        this.parameter = parameter;
    }
}
