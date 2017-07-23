package com.search.suggestion.text.analyze;


import com.search.suggestion.text.analyze.tokenize.WordTokenizer;
import com.search.suggestion.text.analyze.transform.LowerCaseTransformer;

import java.io.Serializable;
import java.util.Collection;

public class SuggestAnalyzer extends Analyzer implements Serializable
{
    private Analyzer tokenizer = new WordTokenizer();
    private Analyzer transformer = new LowerCaseTransformer();

    @Override
    public Collection<String> apply(Collection<String> input)
    {
        return tokenizer.apply(transformer.apply(input));
    }
}
