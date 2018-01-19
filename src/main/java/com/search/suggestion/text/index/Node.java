package com.search.suggestion.text.index;

import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.text.util.ArraySet;

import java.io.Serializable;
import java.util.*;

/**
 * Created by nikhil on 4/12/17.
 */
public class Node<V> implements Serializable
{
    private Map<String, Node<V>> children;
    private Set<V> values;

    @SuppressWarnings("checkstyle:hiddenfield")
    boolean addAllValues(Collection<V> values)
    {
        assert values != null;
        SuggestPayload realSr = null;
        int donotadd = 0;
        try {
            for(V rv : values) {
                realSr = (SuggestPayload)rv;
            }
            if (this.values == null)
            {
                this.values = new ArraySet<>();
            }
            else {
                for(V sr : this.values) {
                    SuggestPayload srv = (SuggestPayload)sr;

                    if(recordAlreadyPresent(srv,realSr)) {
                        srv.setCount(srv.getCount()+realSr.getCount());
                        donotadd =1;
                    }
                }

            }
        }
        catch(Exception ex) {
            if (this.values == null)
            {
                this.values = new ArraySet<>();
            }
        }
        if(donotadd == 0)
            return this.values.addAll(values);
        else
            return false;
    }
    boolean recordAlreadyPresent(SuggestPayload first, SuggestPayload second) {
        return first.getSearch().equals(second.getSearch()) && first.getFilter().equals(second.getFilter());
    }
    Node<V> bisect(String key, int pivot)
    {
        assert key != null;
        String prefix = key.substring(0, pivot);
        String suffix = key.substring(pivot);
        Node<V> child = new Node<V>();
        child.putChild(suffix, removeChild(key));
        putChild(prefix, child);
        return child;
    }

    Collection<Map.Entry<String, Node<V>>> childEntries()
    {
        if (children == null)
        {
            return Collections.emptyList();
        }
        return children.entrySet();
    }

    Collection<Node<V>> childNodes()
    {
        if (children == null)
        {
            return Collections.emptyList();
        }
        return children.values();
    }

    boolean isEmpty()
    {
        return (children == null || children.isEmpty()) && (values == null || values.isEmpty());
    }

    boolean isUnary()
    {
        return (values == null || values.isEmpty()) && children != null && children.size() == 1;
    }

    Node<V> putChild(String key, Node<V> value)
    {
        assert key != null;
        assert value != null;
        if (children == null)
        {
            children = new HashMap<>(4);
        }
        return children.put(key, value);
    }

    Set<V> removeAllValues()
    {
        if (values == null)
        {
            return new ArraySet<>();
        }
        Set<V> result = values;
        values = null;
        return result;
    }

    @SuppressWarnings("checkstyle:hiddenfield")
    boolean removeAllValues(Collection<V> values)
    {
        assert values != null;
        if (this.values == null)
        {
            return false;
        }
        return this.values.removeAll(values);
    }

    Node<V> removeChild(String key)
    {
        assert key != null;
        if (children == null)
        {
            return null;
        }
        return children.remove(key);
    }

    Node<V> squash(String key)
    {
        assert key != null;
        Node<V> child = removeChild(key);
        for (Map.Entry<String, Node<V>> entry : child.childEntries())
        {
            String edge = entry.getKey();
            putChild(key.concat(edge), child.removeChild(edge));
        }
        return child;
    }

    Set<V> values()
    {
        if (values == null)
        {
            return Collections.emptySet();
        }
        return values;
    }
}
