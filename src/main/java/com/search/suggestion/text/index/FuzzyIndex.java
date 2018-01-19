package com.search.suggestion.text.index;

import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SearchPayload;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.text.match.Automaton;

import java.util.Set;


/**
 * {@link Index} with approximate key matching.
 */
public interface FuzzyIndex<V> extends Index<V>
{
    /**
     * Returns a {@link Set} of all values associated with a key matcher.
     *
     * @throws NullPointerException if {@code matcher} is null;
     */
    Set<ScoredObject<V>> getAny(Automaton matcher, SearchPayload json);
}
