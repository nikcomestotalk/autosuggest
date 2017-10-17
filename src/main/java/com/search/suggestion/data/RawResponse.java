package com.search.suggestion.data;

import java.util.HashMap;
import java.util.Map;

public class RawResponse {
    private String matched;
    private Map<String,Integer> parameters = new HashMap<String,Integer>();

    public String getMatched() {
        return matched;
    }

    public void setMatched(String matched) {
        this.matched = matched;
    }

    public Map<String, Integer> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Integer> parameters) {
        this.parameters = parameters;
    }
}
