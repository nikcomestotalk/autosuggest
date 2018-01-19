package com.search.suggestion.text.index;

import java.util.*;
import java.util.Map.Entry;

import static com.search.suggestion.common.Precondition.checkPointer;

/**
 * Hashing based implementation of the {@link Index} interface.
 *
 * <p>Note that this implementation is not synchronized.
 */
public class HashMultiMap<V> extends AbstractIndex<V> implements Index<V>
{
    private final Map<String, Set<V>> map;

    /**
     * Constructs a new {@link HashMultiMap}.
     */
    public HashMultiMap()
    {
        map = new HashMap<>();
    }

    @Override
    public void clear()
    {
        map.clear();
    }

    @Override
    public Set<V> getAll(String key)
    {
        checkPointer(key != null);
        Set<V> value = map.get(key);
        if (value != null)
        {
            return new HashSet<>(value);
        }
        return new HashSet<>();
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public boolean putAll(String key, Collection<V> values)
    {
        checkPointer(key != null);
        checkPointer(values != null);
        Set<V> value = map.get(key);
        if (value == null)
        {
            value = new HashSet<>();
            map.put(key, value);
        }
        return value.addAll(values);
    }
    @Override
    public int size()
    {
        int size = 0;
        for (Entry<String, Set<V>> entry : map.entrySet())
        {
            size += entry.getValue().size();
        }
        return size;
    }
}
