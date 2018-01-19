package com.search.suggestion.engine;

import com.search.suggestion.adaptor.IndexAdapter;
import com.search.suggestion.common.Aggregator;
import com.search.suggestion.data.Indexable;
import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SearchPayload;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.text.analyze.Analyzer;
import com.search.suggestion.text.index.Index;


import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.search.suggestion.common.Precondition.checkPointer;


/**
 * Facade for indexing and searching {@link Indexable} elements.
 */
public final class SearchEngine<T extends Indexable> implements Serializable
{
    private final Analyzer analyzer;
    private final Comparator<ScoredObject<T>> comparator;
    private IndexAdapter<T> index;
    private final Lock read;
    private final Lock write;
    private int pos;
    public void faultTolerant(Boolean bool) {
        index.setFaultTolerant(bool);
    }
    public IndexAdapter indexUsed() {
        return index;
    }
    public void setPosition( int pos) {
        this.pos =pos;
    }
    public int getPosition() {
        return pos;
    }
    public void setIndexAdapter(IndexAdapter index) {
        this.index = index;
    }
    
    private SearchEngine(Builder<T> builder)
    {
        assert builder != null;
        this.analyzer = builder.analyzer;
        this.comparator = builder.comparator;
        this.index = builder.index;
        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.read = lock.readLock();
        this.write = lock.writeLock();
    }

    /**
     * Indexes a single element.
     *
     * @throws NullPointerException if {@code element} is null;
     */
    public boolean add(T element)
    {
        return addAll(Arrays.asList(element));
    }

    /**
     * Indexes a collection of elements.
     *
     * @throws NullPointerException if {@code elements} is null or contains a null element;
     */
    public boolean addAll(Collection<T> elements)
    {
        checkPointer(elements != null);
        boolean result = false;
        for (T element : elements)
        {
            checkPointer(element != null);
            write.lock();
            try
            {
                for (String field : element.getFields())
                {
                    for (String token : analyzer.apply(field))
                    {
                        result |= index.put(token, element);
                    }
                }
            }
            finally
            {
                write.unlock();
            }
        }
        return result;
    }

    public TreeMap <Double,List<SuggestPayload>> search(SearchPayload sr, boolean val)
    {
        checkPointer(sr != null);
        String query = sr.getSearch();

        checkPointer(query != null);
        read.lock();
        try
        {
            Aggregator<T> aggregator = new Aggregator<>(comparator);
            Iterator<String> tokens = analyzer.apply(query).iterator();
            if (tokens.hasNext())
            {
                aggregator.addAll(index.get(tokens.next(),sr));

            }
            while (tokens.hasNext())
            {
                if (aggregator.isEmpty())
                {
                    break;
                }
                aggregator.retainAll(index.get(tokens.next(),sr));

            }
            return aggregator.values(sr,true);
        }
        finally
        {
            read.unlock();
        }
    }
    /**
     * Returns a {@link List} of all elements that match a query, sorted
     * according to the default comparators.
     */
    public List<T> search(SearchPayload searchPayload)
    {
        checkPointer(searchPayload != null);
        String query = searchPayload.getSearch();
        System.out.println(query);

        checkPointer(query != null);
        read.lock();
        try
        {
            Aggregator<T> aggregator = new Aggregator<>(comparator);
            Iterator<String> tokens = analyzer.apply(query).iterator();
            if (tokens.hasNext())
            {
                aggregator.addAll(index.get(tokens.next(),searchPayload));

            }
            while (tokens.hasNext())
            {
                if (aggregator.isEmpty())
                {
                    break;
                }
                aggregator.retainAll(index.get(tokens.next(),searchPayload));

            }
            return aggregator.values(searchPayload);
        }
        catch(Exception ex) {
            return null;
        }
        finally
        {
            read.unlock();
        }
    }
    
    /**
     * Builder for constructing {@link SearchEngine} instances.
     */
    public static class Builder<T extends Indexable>
    {
        private Analyzer analyzer;
        private Comparator<ScoredObject<T>> comparator;
        private IndexAdapter<T> index;

        /**
         * Constructs a new {@link SearchEngine.Builder}.
         */
        public Builder()
        {
            this.analyzer = new Analyzer()
            {
                @Override
                public Collection<String> apply(Collection<String> input)
                {
                    return new ArrayList<>(input);
                }
            };
        }

        /**
         * Set the analyzer.
         */
        public Builder<T> setAnalyzer(Analyzer analyzer)
        {
            this.analyzer = analyzer;
            return this;
        }

        /**
         * Set the comparator.
         */
        public Builder<T> setComparator(@Nullable Comparator<ScoredObject<T>> comparator)
        {
            this.comparator = comparator;
            return this;
        }

        /**
         * Set the index.
         *
         * @throws NullPointerException if {@code index} is null;
         */
        public Builder<T> setIndex(final Index<T> index)
        {

            checkPointer(index != null);
            
            return setIndex(new IndexAdapter<T>()
            {
                @Override
                public Collection<ScoredObject<T>> get(String token, SearchPayload json)
                {
                    List<ScoredObject<T>> result = new LinkedList<>();
                    for (T element : index.getAll(token))
                    {
                        result.add(new ScoredObject<>(element, 0));
                    }
                    return result;
                }

                @Override
                public boolean put(String token, T value)
                {
                    return index.put(token, value);
                }

                @Override
                public void setFaultTolerant(Boolean bool) 
                {
          
                }
            });
        }

        /**
         * Set the index.
         */
        public Builder<T> setIndex(IndexAdapter<T> index)
        {
            this.index = index;
            return this;
        }
        

        /**
         * Returns a new {@link SearchEngine} parameterized according to
         * the builder.
         *
         * @throws NullPointerException if {@code analyzer} or {@code index} are null;
         */
        public SearchEngine<T> build()
        {
            checkPointer(analyzer != null);
            checkPointer(index != null);
            return new SearchEngine<>(this);
        }
    }
}

