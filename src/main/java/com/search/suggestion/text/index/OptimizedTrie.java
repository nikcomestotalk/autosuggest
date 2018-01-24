package com.search.suggestion.text.index;

import com.search.suggestion.common.Strings;
import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SearchPayload;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.text.match.Automaton;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import static com.search.suggestion.common.Precondition.checkPointer;

/**
 * Trie based implementation of the {@link FuzzyIndex} interface.
 *
 * <p>Note that this implementation is not synchronized.
 */
public class OptimizedTrie<V> extends AbstractIndex<V> implements FuzzyIndex<V>,Serializable
{
    private Node<V> root;

    /**
     * Constructs a new {@link OptimizedTrie}.
     */
    public OptimizedTrie()
    {
        root = new Node<V>();
    }

    @Override
    public void clear()
    {
        root = new Node<V>();
    }

    @Override
    public Set<V> getAll(String key)
    {
        checkPointer(key != null);
        Node<V> node = find(root, key);
        if (node != null)
        {
            return new HashSet<>(node.values());
        }
        return new HashSet<>();
    }
    @Override
    public Set<ScoredObject<V>> getAny(Automaton matcher, SearchPayload json)
    {
    	checkPointer(matcher != null);
        Set<ScoredObject<V>> result = new HashSet<>();
        for (FuzzyMatch match : findAll(root, matcher, "",json))
        {
            result.addAll(values(match.getNode(), match.getMatcher(), json));
        }
        return result;
    }

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Override
    public boolean putAll(String key, Collection<V> values)
    {
        checkPointer(key != null);
        checkPointer(values != null);
        return putAll(root, key, values);
    }

    @Override
    public int size()
    {
        return size(root);
    }

    private Node<V> find(Node<V> node, String key)
    {
        assert node != null;
        assert key != null;
        if (key.length() <= 0)
        {
            return node;
        }
        else
        {
            for (Entry<String, Node<V>> entry : node.childEntries())
            {
                String edge = entry.getKey();
                Node<V> child = entry.getValue();
                int commonPrefixLength = Strings.getCommonPrefixLength(edge, key);
                // Exact match
                if (commonPrefixLength >= edge.length())
                {
                    return find(child, key.substring(commonPrefixLength));
                }
            }
        }
        return null;
    }

    @SuppressWarnings("checkstyle:parameterassignment")
    private Collection<FuzzyMatch> findAll(Node<V> node, Automaton matcher, String word,SearchPayload json)
    {
        assert node != null;
        assert matcher != null;
        assert word != null;

        if (matcher.isWordAccepted())
        {

            if (matcher.getWord().length() < word.length())
            {
                String suffix = word.substring(matcher.getWord().length());
                matcher = matcher.step(suffix);
            }
            
            return Arrays.asList(new FuzzyMatch(node, matcher));
        }
        else if (!matcher.isWordRejected())
        {
            List<FuzzyMatch> result = new LinkedList<>();
            for (Entry<String, Node<V>> entry : node.childEntries())
            {
                String edge = entry.getKey();
                Node<V> child = entry.getValue();
                Automaton newmatcher = matcher.stepUntilWordAccepted(edge);
                result.addAll(findAll(child, newmatcher, word + edge,json));
            }
            return result;
        }
        return Collections.emptyList();
    }
    private boolean putAll(Node<V> node, String key, Collection<V> values)
    {
        assert node != null;
        assert key != null;
        assert values != null;
        if (key.length() <= 0)
        {
        	//System.out.println("finally coming here"+ node+values.toString());
            return node.addAllValues(values);
        }
        else
        {
            Node<V> child = null;
            int commonPrefixLength = 0;
            for (Entry<String, Node<V>> entry : node.childEntries())
            {
                String edge = entry.getKey();
                commonPrefixLength = Strings.getCommonPrefixLength(edge, key);
                // Exact match
                if (commonPrefixLength >= edge.length())
                {
                    child = entry.getValue();
                    break;
                }
                // Prefix match
                else if (commonPrefixLength > 0)
                {
                    child = node.bisect(edge, commonPrefixLength);
                    break;
                }
            }
            if (child == null)
            {
                child = new Node<V>();
                commonPrefixLength = key.length();
                node.putChild(key, child);
            }
            return putAll(child, key.substring(commonPrefixLength), values);
        }
    }

    private int size(Node<V> node)
    {
        assert node != null;
        int result = node.values().size();
        for (Node<V> child : node.childNodes())
        {
            result += size(child);
        }
        return result;
    }

    private Set<ScoredObject<V>> values(Node<V> node, Automaton matcher)
    {
        assert node != null;
        assert matcher != null;
        Set<ScoredObject<V>> result = new HashSet<>();
        for (V value : node.values())
        {
        	
            result.add(new ScoredObject<>(value, matcher.getScore()));
        }
        for (Entry<String, Node<V>> entry : node.childEntries())
        {
            result.addAll(values(entry.getValue(), matcher.step(entry.getKey())));
        }
        return result;
    }
    private Set<ScoredObject<V>> values(Node<V> node, Automaton matcher,SearchPayload sp)
    {
        assert node != null;
        assert matcher != null;
        Set<ScoredObject<V>> result = new HashSet<>();
        Map<String, Integer> filterMap = sp.getFilter();
        for (V value : node.values())
        {
        	SuggestPayload sr = (SuggestPayload)value;
            Map<String, Integer> searchFilterMap = sr.getFilter();
            Boolean filterPresent = false;
            Boolean allFilterPassed = true;
            for( String filter: filterMap.keySet()) {
                if(!sr.ignoreFilter(filter)) {
                    filterPresent = true;
                    int nodeFilterValue = 0;
                    int queryFilterValue = 0;
                    if(filterMap.containsKey(filter)) {
                        nodeFilterValue = filterMap.get(filter);
                    }
                    if(searchFilterMap.containsKey(filter)) {
                        queryFilterValue = searchFilterMap.get(filter);
                    }
                    if (nodeFilterValue != queryFilterValue) {
                        allFilterPassed = false;
                    }

                }
            }

            /*System.out.println("---------------");
            System.out.println(sr.getSearch());
            System.out.println(matcher.getScore());
            System.out.println("---------------");*/
            //When no filter is there
            if(!filterPresent) {
                if(matcher.getScore()>.5)
                result.add(new ScoredObject<>(value, matcher.getScore()));
            }

            //When filter is there and all filter passed.
            if(filterPresent && allFilterPassed) {
                if(matcher.getScore()>.5)
                result.add(new ScoredObject<>(value, matcher.getScore()));
            }

            
        }
        for (Entry<String, Node<V>> entry : node.childEntries())
        {
            result.addAll(values(entry.getValue(), matcher.step(entry.getKey()), sp));
        }
        return result;
    }



    private class FuzzyMatch
    {
        private Node<V> node;
        private Automaton matcher;

        FuzzyMatch(Node<V> node, Automaton matcher)
        {
            assert node != null;
            assert matcher != null;
            this.node = node;
            this.matcher = matcher;
        }

        Node<V> getNode()
        {
            return node;
        }

        Automaton getMatcher()
        {
            return matcher;
        }
    }
}
