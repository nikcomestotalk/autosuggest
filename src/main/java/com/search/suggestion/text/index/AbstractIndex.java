package com.search.suggestion.text.index;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * Skeletal implementation of the {@link Index} interface.
 */
public abstract class AbstractIndex<V> implements Index<V>
{
    @Override
    public boolean put(String key, @Nullable V value)
    {
        return putAll(key, Arrays.asList(value));
    }
}
