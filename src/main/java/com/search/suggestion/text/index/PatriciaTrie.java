package com.search.suggestion.text.index;

import com.search.suggestion.common.Strings;
import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SuggestRecord;
import com.search.suggestion.text.match.Automaton;
import com.search.suggestion.text.match.EqualityAutomaton;
import com.search.suggestion.text.util.ArraySet;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import static com.search.suggestion.common.Precondition.checkPointer;

/**
 * Trie based implementation of the {@link FuzzyIndex} interface.
 *
 * <p>Note that this implementation is not synchronized.
 */
public class PatriciaTrie<V> extends AbstractIndex<V> implements FuzzyIndex<V>,Serializable
{
    private Node root;

    /**
     * Constructs a new {@link PatriciaTrie}.
     */
    public PatriciaTrie()
    {
        root = new Node();
    }

    @Override
    public void clear()
    {
        root = new Node();
    }

    @Override
    public Set<V> getAll(String key)
    {
        checkPointer(key != null);
        Node node = find(root, key);
        if (node != null)
        {
            return new HashSet<>(node.values());
        }
        return new HashSet<>();
    }

    @Override
    public Set<ScoredObject<V>> getAny(String fragment)
    {
        checkPointer(fragment != null);
        
        Set<ScoredObject<V>> result = new HashSet<>();
        for (FuzzyMatch match : findAll(root, new EqualityAutomaton(fragment), ""))
        {
            result.addAll(values(match.getNode(), match.getMatcher()));
        }
        return result;
    }
    @Override
    public Set<ScoredObject<V>> getAny(Automaton matcher, SuggestRecord json)
    {
    	checkPointer(matcher != null);
        Set<ScoredObject<V>> result = new HashSet<>();
        for (FuzzyMatch match : findAll(root, matcher, "",json))
        {
            result.addAll(values(match.getNode(), match.getMatcher(),json));
        }
        return result;
    }

    @Override
    public Set<ScoredObject<V>> getAny(Automaton matcher)
    {
        checkPointer(matcher != null);
        Set<ScoredObject<V>> result = new HashSet<>();
        for (FuzzyMatch match : findAll(root, matcher, ""))
        {
            result.addAll(values(match.getNode(), match.getMatcher()));
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
    public boolean removeAll(Collection<V> values)
    {
        checkPointer(values != null);
        return removeAll(root, values);
    }

    @Override
    public Set<V> removeAll(String key)
    {
        checkPointer(key != null);
        return removeAll(root, key);
    }

    @Override
    public boolean removeAll(String key, Collection<V> values)
    {
        checkPointer(key != null);
        checkPointer(values != null);
        return removeAll(root, key, values);
    }

    @Override
    public int size()
    {
        return size(root);
    }

    private Node find(Node node, String key)
    {
        assert node != null;
        assert key != null;
        if (key.length() <= 0)
        {
            return node;
        }
        else
        {
            for (Entry<String, Node> entry : node.childEntries())
            {
                String edge = entry.getKey();
                Node child = entry.getValue();
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
    private Collection<FuzzyMatch> findAll(Node node, Automaton matcher, String word)
    {
        assert node != null;
        assert matcher != null;
        assert word != null;
        if (matcher.isWordAccepted())
        {
            // Resume partial match
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
            for (Entry<String, Node> entry : node.childEntries())
            {
                String edge = entry.getKey();
                Node child = entry.getValue();
                Automaton newmatcher = matcher.stepUntilWordAccepted(edge);
                result.addAll(findAll(child, newmatcher, word + edge));
            }
            return result;
        }
        return Collections.emptyList();
    }
    @SuppressWarnings("checkstyle:parameterassignment")
    private Collection<FuzzyMatch> findAll(Node node, Automaton matcher, String word,SuggestRecord json)
    {
        assert node != null;
        assert matcher != null;
        assert word != null;
        if (matcher.isWordAccepted())
        {
            // Resume partial match
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
            for (Entry<String, Node> entry : node.childEntries())
            {
                String edge = entry.getKey();
                Node child = entry.getValue();
                Automaton newmatcher = matcher.stepUntilWordAccepted(edge);
                result.addAll(findAll(child, newmatcher, word + edge,json));
            }
            return result;
        }
        return Collections.emptyList();
    }
    private boolean putAll(Node node, String key, Collection<V> values)
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
            Node child = null;
            int commonPrefixLength = 0;
            for (Entry<String, Node> entry : node.childEntries())
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
                child = new Node();
                commonPrefixLength = key.length();
                node.putChild(key, child);
            }
            return putAll(child, key.substring(commonPrefixLength), values);
        }
    }

    private boolean removeAll(Node node, Collection<V> values)
    {
        assert node != null;
        assert values != null;
        boolean result = node.removeAllValues(values);
        List<String> legacyEdges = new LinkedList<>();
        Iterator<Entry<String, Node>> iterator = node.childEntries().iterator();
        while (iterator.hasNext())
        {
            Entry<String, Node> entry = iterator.next();
            String edge = entry.getKey();
            Node child = entry.getValue();
            if (removeAll(child, values))
            {
                result = true;
            }
            if (child.isEmpty())
            {
                iterator.remove();
            }
            else if (child.isUnary())
            {
                legacyEdges.add(edge);
            }
        }
        for (String edge : legacyEdges)
        {
            node.squash(edge);
        }
        return result;
    }

    private Set<V> removeAll(Node node, String key)
    {
        assert node != null;
        assert key != null;
        if (key.length() <= 0)
        {
            return node.removeAllValues();
        }
        else
        {
            for (Entry<String, Node> entry : node.childEntries())
            {
                String edge = entry.getKey();
                int commonPrefixLength = Strings.getCommonPrefixLength(edge, key);
                // Exact match
                if (commonPrefixLength >= edge.length())
                {
                    Node child = entry.getValue();
                    Set<V> result = removeAll(child, key.substring(commonPrefixLength));
                    if (child.isEmpty())
                    {
                        node.removeChild(edge);
                    }
                    else if (child.isUnary())
                    {
                        node.squash(edge);
                    }
                    return result;
                }
            }
        }
        return Collections.emptySet();
    }

    private boolean removeAll(Node node, String key, Collection<V> values)
    {
        assert node != null;
        assert key != null;
        assert values != null;
        if (key.length() <= 0)
        {
            return node.removeAllValues(values);
        }
        else
        {
            for (Entry<String, Node> entry : node.childEntries())
            {
                String edge = entry.getKey();
                int commonPrefixLength = Strings.getCommonPrefixLength(edge, key);
                // Exact match
                if (commonPrefixLength >= edge.length())
                {
                    Node child = entry.getValue();
                    boolean result = removeAll(child, key.substring(commonPrefixLength), values);
                    if (child.isEmpty())
                    {
                        node.removeChild(edge);
                    }
                    else if (child.isUnary())
                    {
                        node.squash(edge);
                    }
                    return result;
                }
            }
        }
        return false;
    }

    private int size(Node node)
    {
        assert node != null;
        int result = node.values().size();
        for (Node child : node.childNodes())
        {
            result += size(child);
        }
        return result;
    }

    private Set<ScoredObject<V>> values(Node node, Automaton matcher)
    {
        assert node != null;
        assert matcher != null;
        Set<ScoredObject<V>> result = new HashSet<>();
        for (V value : node.values())
        {
        	
            result.add(new ScoredObject<>(value, matcher.getScore()));
        }
        for (Entry<String, Node> entry : node.childEntries())
        {
            result.addAll(values(entry.getValue(), matcher.step(entry.getKey())));
        }
        return result;
    }
    private Set<ScoredObject<V>> values(Node node, Automaton matcher,SuggestRecord sp)
    {
        assert node != null;
        assert matcher != null;
        Set<ScoredObject<V>> result = new HashSet<>();
        Map<String, Integer> filterMap = sp.getFilter();
        for (V value : node.values())
        {
        	SuggestRecord sr = (SuggestRecord)value;
            Map<String, Integer> searchFilterMap = sr.getFilter();
            Boolean filterPresent = false;
            Boolean allFilterPassed = true;
            for( String filter: filterMap.keySet()) {
                if(!sr.ignoreFilter(filter)) {
                    filterPresent = true;
                    if (filterMap.get(filter) != searchFilterMap.get(filter)) {
                        allFilterPassed = false;
                    }
                }
            }
            //When no filter is there
            if(!filterPresent) {
                result.add(new ScoredObject<>(value, matcher.getScore()));
            }

            //When filter is there and all filter passed.
            if(filterPresent && allFilterPassed) {
                result.add(new ScoredObject<>(value, matcher.getScore()));
            }
        	/*if(sp.getCategoryl1()== 0 || sp.getCategoryl1() == sr.getCategoryl1()) {
        		if(sp.getCategoryl1()!=0 || sp.getCategoryl2()== 0 || sp.getCategoryl2() == sr.getCategoryl2()) {
        			if(sp.getRegion()== 0 || sp.getRegion() == sr.getRegion()) {
        				if(sp.getRegion()!=0 || sp.getCity()== 0 || sp.getCity() == sr.getCity()) {
        					result.add(new ScoredObject<>(value, matcher.getScore()));
        				}
        			}
        		}
        	}*/

            //result.add(new ScoredObject<>(value, matcher.getScore()));
            
        }
        for (Entry<String, Node> entry : node.childEntries())
        {
            result.addAll(values(entry.getValue(), matcher.step(entry.getKey())));
        }
        return result;
    }

    private class Node implements Serializable
    {
        private Map<String, Node> children;
        private Set<V> values;

        @SuppressWarnings("checkstyle:hiddenfield")
        boolean addAllValues(Collection<V> values)
        {
            assert values != null;
            SuggestRecord realSr = null;
            int donotadd = 0;
            try {
	            for(V rv : values) {
	            	realSr = (SuggestRecord)rv;
	            }
	            if (this.values == null)
	            {
	                this.values = new ArraySet<>();
	            }
	            else {
	            	for(V sr : this.values) {
	            		SuggestRecord srv = (SuggestRecord)sr;
	            		//System.out.println("First one "+srv.getSearch()+" count is "+srv.getCount());
	            		if(recordAlreadyPresent(srv,realSr)) {
	            			srv.setCount(srv.getCount()+realSr.getCount());
	            			donotadd =1;
	            		}
	            		//System.out.println("Corrent one "+srv.getSearch()+" count is "+srv.getCount());
	            	}
	            	
	            }
            }
            catch(Exception ex) {
            	if (this.values == null)
	            {
	                this.values = new ArraySet<>();
	            }
            	System.out.println("Error in suggest Recrod insert"+ex);
            }
            if(donotadd == 0)
            	return this.values.addAll(values);
            else
            	return false;
        }
        boolean recordAlreadyPresent(SuggestRecord first, SuggestRecord second) {
        	/*if(first.getSearch().equals(second.getSearch())) {
        		if(first.getCity() == second.getCity()) {
        			if(first.getRegion() == second.getRegion()) {
        				if(first.getCategoryl1() == second.getCategoryl1()) {
        					if(first.getCategoryl2() == second.getCategoryl2()) {
            					if(first.getUser() == second.getUser()) {
            						return true;
            					}
        						
        					}
        				}
        			}
        		}
        	}*/
        	if(first.getFilter().equals(second.getFilter())) {
        	    return true;
            }
        	return false;
        }
        Node bisect(String key, int pivot)
        {
            assert key != null;
            String prefix = key.substring(0, pivot);
            String suffix = key.substring(pivot);
            Node child = new Node();
            child.putChild(suffix, removeChild(key));
            putChild(prefix, child);
            return child;
        }

        Collection<Entry<String, Node>> childEntries()
        {
            if (children == null)
            {
                return Collections.emptyList();
            }
            return children.entrySet();
        }

        Collection<Node> childNodes()
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

        Node putChild(String key, Node value)
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

        Node removeChild(String key)
        {
            assert key != null;
            if (children == null)
            {
                return null;
            }
            return children.remove(key);
        }

        Node squash(String key)
        {
            assert key != null;
            Node child = removeChild(key);
            for (Entry<String, Node> entry : child.childEntries())
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

    private class FuzzyMatch
    {
        private Node node;
        private Automaton matcher;

        FuzzyMatch(Node node, Automaton matcher)
        {
            assert node != null;
            assert matcher != null;
            this.node = node;
            this.matcher = matcher;
        }

        Node getNode()
        {
            return node;
        }

        Automaton getMatcher()
        {
            return matcher;
        }
    }
}
