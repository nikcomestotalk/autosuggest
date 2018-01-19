package com.search.suggestion.text.index;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * Associative data structure that maps text keys to multiple values.
 */
public interface Index<V>
{
    /**
     * Removes all key-value associations.
     */
    void clear();

    /**
     * Returns a {@link Set} of all values associated with a key.
     *
     * @throws NullPointerException if {@code key} is null;
     */
    Set<V> getAll(String key);
    

    /**
     * Returns {@code true} if no key-value associations exist.
     */
    boolean isEmpty();

    /**
     * Associates a single value with a key.
     *
     * @throws NullPointerException if {@code key} is null;
     */
    boolean put(String key, @Nullable V value);

    /**
     * Associates a collection of values with a key.
     *
     * @throws NullPointerException if {@code key} or {@code values} are null;
     */
    boolean putAll(String key, Collection<V> values);

    /**
     * Returns the number of key-value associations.
     */
    int size();
}
