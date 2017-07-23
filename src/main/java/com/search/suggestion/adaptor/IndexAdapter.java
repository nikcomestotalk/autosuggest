package com.search.suggestion.adaptor;

import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SuggestRecord;

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
    Collection<ScoredObject<T>> get(String token);
    Collection<ScoredObject<T>> get(String token,SuggestRecord json);

    /**
     * Associates a single value with a token.
     */
    boolean put(String token, @Nullable T value);

    /**
     * Removes a single value associated with any tokens.
     */
    boolean remove(T value);
    
    /**
     * Whether threshold check is needed or not
     */
    void setFaultTolerant(Boolean b);
}
