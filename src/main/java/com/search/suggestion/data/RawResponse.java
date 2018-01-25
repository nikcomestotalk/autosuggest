package com.search.suggestion.data;

import java.util.HashMap;
import java.util.Map;

public class RawResponse {
    private String matched;
    private Map<String,Integer> parameters = new HashMap<String,Integer>();
    private String error = "";
    private String message = "";

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

    public void setError(String error) {
        if(error != null)
            this.error = error;
    }
    public void setMessage(String message) {
        if(message != null) {
            this.message = message;
        }
    }
}
