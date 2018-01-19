package com.search.suggestion.adaptor;

import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SearchPayload;
import com.search.suggestion.data.SuggestPayload;

import java.util.Collection;

import javax.annotation.Nullable;


/**
 * Adapter for any index data structure.
 */
public interface IndexAdapter<T>
{
    /**
     * Returns a {@link Collection} of all values associated with a token.
     */
    Collection<ScoredObject<T>> get(String token,SearchPayload json);

    /**
     * Associates a single value with a token.
     */
    boolean put(String token, @Nullable T value);
    
    /**
     * Whether threshold check is needed or not
     */
    void setFaultTolerant(Boolean b);
}
