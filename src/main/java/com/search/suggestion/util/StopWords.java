package com.search.suggestion.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by nikhil on 30/7/17.
 */
public class StopWords {
    Set<String> stopWordsSet = new HashSet<String>();
    public StopWords() throws IOException {

        File file = new File(System.getProperties().get("user.dir")+ApplicationProperties.getProperty("stopwords.path"));

        BufferedReader br = new BufferedReader(
                new FileReader(file));
        String line = "";
        while ((line = br.readLine()) != null) {
            stopWordsSet.add(line.toUpperCase());
        }
    }

    public String removeStopWords(String s) {
        String[] words = s.split(" ");
        ArrayList<String> wordsList = new ArrayList<String>();
        StringBuilder sb = new StringBuilder("");
        for(String word : words)
        {
            String wordCompare = word.toUpperCase();
            if(!stopWordsSet.contains(wordCompare))
            {
                wordsList.add(word);
            }

        }
        for (String ss : wordsList)
        {
            sb.append(ss);
            sb.append(" ");
        }
        if(sb.toString().equals(""))
            return s;

        return sb.toString();

    }
}
