package com.search.suggestion.util;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.search.suggestion.adaptor.IndexAdapter;
import com.search.suggestion.adaptor.SuggestAdapter;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.text.index.FuzzyIndex;
import com.search.suggestion.text.index.PatriciaTrie;
import org.apache.http.client.utils.DateUtils;

public class BackupTree implements Runnable {
    private SearchEngine<SuggestPayload> futureEngine;
    private SearchEngine<SuggestPayload> searchEngine;
	private FuzzyIndex<SuggestPayload> indexdata;
	private int timegap = 5000;
	public BackupTree(SearchEngine<SuggestPayload> engine) {
		this.searchEngine = engine;
	}

    public BackupTree(SearchEngine<SuggestPayload> suggestCurrent, SearchEngine<SuggestPayload> suggestFuture) {
	    this.searchEngine = suggestCurrent;
	    this.futureEngine = suggestFuture;
    }

    public void run() {
		Kryo kryo = new Kryo();
		try {
			callReIndexer();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/*while(true) {
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
		}*/
	}

    private void callReIndexer() throws IOException, InterruptedException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		CountDownLatch latch = new CountDownLatch(3);

		ExecutorService es = Executors.newCachedThreadPool();
		for(int i=0;i<3;i++) {
			es.execute(new ReIndexer(dateFormat.format(getDate(-i)), futureEngine ));
		}
		es.shutdown();
		es.awaitTermination(1, TimeUnit.DAYS );
		System.out.println("done with syncing up");
		searchEngine = futureEngine;

    }

    private Date getDate(int days) {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, days);
		return cal.getTime();
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
        //Start my new searchEngine to update

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