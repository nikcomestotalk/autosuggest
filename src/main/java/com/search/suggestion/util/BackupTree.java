package com.search.suggestion.util;

import java.io.*;
import java.util.Properties;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.search.suggestion.adaptor.IndexAdapter;
import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.text.index.FuzzyIndex;
import com.search.suggestion.text.index.PatriciaTrie;

public class BackupTree implements Runnable {
	private SearchEngine<SuggestPayload> searchEngine;;
	private FuzzyIndex<SuggestPayload> indexdata;
	private int timegap = 5000;
	public BackupTree(SearchEngine<SuggestPayload> engine) {
		this.searchEngine = engine;
	}
	public void run() {
		Kryo kryo = new Kryo();
		while(true) {
			SuggestAdapter adapter = (SuggestAdapter) (searchEngine.indexUsed());
			indexdata = adapter.getIndex();
	    	 FileOutputStream fos = null;
	         ObjectOutputStream out = null;
	         try {
				String contentPath = System.getProperties().get("user.dir") + "/suggest/content/search.bin";
				 Output output = new Output(new FileOutputStream(contentPath));
				 kryo.writeObject(output, indexdata);
				 output.close();
                 System.out.println("Search Object Persisted");
				/* File f = new File(contentPath+"/content/search.bin");
				 if(!f.exists())
					 f.createNewFile();
	             fos = new FileOutputStream(f);
	             out = new ObjectOutputStream(fos);
	             out.writeObject(indexdata);
	             out.close();
	             System.out.println("Search Object Persisted");*/
	         } catch (Exception ex) {
	             ex.printStackTrace();
	         }

	         finally {
				 try {
					 Thread.sleep(5000);
				 } catch (InterruptedException e) {
					 e.printStackTrace();
				 }
			 }
		}
	}

	public void updateIndexer() throws FileNotFoundException {
		Kryo kryo = new Kryo();

        SuggestAdapter adapter = (SuggestAdapter)(searchEngine.indexUsed());
        //PatriciaTrie<SuggestPayload> index = (PatriciaTrie<SuggestPayload>) adapter.getIndex();
		String contentPath = System.getProperties().get("user.dir") + "/suggest/content/search.bin";
        File f = new File(contentPath);
        if(f.isFile()) {
            try {
                Input input = new Input(new FileInputStream(contentPath));
                PatriciaTrie index = kryo.readObject(input, PatriciaTrie.class);
                input.close();

                adapter.setIndex(index);
                searchEngine.setIndexAdapter(adapter);
                //searchEngine.setIndexAdapter((IndexAdapter)someObject);
                System.out.println("Search Object Restored");
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        /*FileInputStream fos = null;
        ObjectInputStream out = null;
        IndexAdapter indexdata = (IndexAdapter)(searchEngine.indexUsed());
        String contentPath = System.getProperties().get("user.dir") + "/suggest";
        File f = new File(contentPath+"/content/search.bin");
        if(f.isFile() && f.canRead())  {
            try {
                fos = new FileInputStream(f);
                out = new ObjectInputStream(fos);
                searchEngine.setIndexAdapter((IndexAdapter)out.readObject());
                out.close();
            }
            catch (Exception ex) {
                System.out.println("Unable to set indexer, can't proceed further");
                System.exit(0);
            } finally {
                try {
                    out.close();
                } catch (Exception e) {

                }
            }
        }*/
		/*File f = new File(this.getClass().getResource("/content/search.bin").getPath());
		if(f.exists()) {
			//fos = new FileInputStream(f);

		}*/
	}
}