package com.search.suggestion.text.analyze.tokenize;


import com.search.suggestion.text.analyze.Analyzer;

import java.io.Serializable;
import java.text.BreakIterator;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.search.suggestion.common.Precondition.checkPointer;
/**
 * Break text into words.
 */
public class WordTokenizer extends Analyzer implements Serializable
{
    private final BreakIterator boundary;

    /**
     * Constructs a new {@link WordTokenizer}.
     */
    public WordTokenizer()
    {
        this.boundary = BreakIterator.getWordInstance();
    }

    @Override
    public Collection<String> apply(Collection<String> input)
    {
        checkPointer(input != null);
        List<String> result = new LinkedList<>();
        for (String text : input)
        {
            checkPointer(text != null);
            boundary.setText(text.toString());
            for (
                int start = boundary.first(), end = boundary.next();
                end != BreakIterator.DONE;
                start = end, end = boundary.next()
            )
            {
                String word = text.substring(start, end);
                if (Character.isLetterOrDigit(word.charAt(0)))
                {
                    result.add(word);
                }
            }
        }
        return result;
    }
}
