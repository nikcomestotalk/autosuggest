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
        //InputStream inputStream = ClassLoader.getSystemResourceAsStream(System.getProperties().get("user.dir")+"/content/stopwords.txt");
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
                //System.out.println("coming here for "+word+" done here");
                wordsList.add(word);
            }

        }
        for (String ss : wordsList)
        {
            sb.append(ss);
            sb.append(" ");
        }
        //System.out.println("coming ehre"+sb.toString()+"ending here");
        if(sb.toString().equals(""))
            return s;

        return sb.toString();

    }
}
