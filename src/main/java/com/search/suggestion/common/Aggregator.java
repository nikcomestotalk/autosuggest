package com.search.suggestion.common;

import com.search.suggestion.data.ScoredObject;
import com.search.suggestion.data.SearchPayload;
import com.search.suggestion.data.SuggestPayload;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import static com.search.suggestion.common.Precondition.checkPointer;

/**
 * Aggregator to collect, merge and transform {@link ScoredObject} elements.
 */
@SuppressWarnings("unchecked")
public class Aggregator<T>
{
    private final Map<T, Double> scores;
    private final Comparator<ScoredObject<T>> comparator;

    /**
     * Constructs a new {@link Aggregator}.
     */
    public Aggregator()
    {
        this(null);
    }

    /**
     * Constructs a new {@link Aggregator}.
     */
    public Aggregator(@Nullable Comparator<ScoredObject<T>> comparator)
    {
        this.scores = new HashMap<>();
        this.comparator = comparator;
    }

    /**
     * Adds a single element, if not present already.
     *
     * @throws NullPointerException if {@code element} is null;
     */
    public boolean add(ScoredObject<T> element)
    {
        return addAll(Arrays.asList(element));
    }

    /**
     * Adds a collection of elements, if not present already.
     *
     * @throws NullPointerException if {@code elements} is null or contains a null element;
     */
    public boolean addAll(Collection<ScoredObject<T>> elements)
    {
        checkPointer(elements != null);
        boolean result = false;
        for (ScoredObject<T> element : elements)
        {
            checkPointer(element != null);
            Double score = scores.get(element.getObject());

            if (score == null || element.getScore()==0)
            {
                scores.put(element.getObject(), element.getScore());
                result = true;
            }
            else if (element.getScore() != 0)
            {

                scores.put(element.getObject(), score + element.getScore());
                result = true;
            }
            //System.out.println("Score is " + 	scores.get(element.getObject()));
        }
        return result;
    }

    /**
     * Returns {@code true} if no elements exist.
     */
    public boolean isEmpty()
    {
        return scores.isEmpty();
    }

    /**
     * Retain the elements in common, compared according to the objects scored.
     *
     * @throws NullPointerException if {@code element} is null;
     */
    public boolean retain(ScoredObject<T> element)
    {
        return retainAll(Arrays.asList(element));
    }

    /**
     * Retains the elements in common, compared according to the objects scored.
     *
     * @throws NullPointerException if {@code elements} is null or contains a null element;
     */
    public boolean retainAll(Collection<ScoredObject<T>> elements)
    {
        checkPointer(elements != null);
        // Intersect
        Collection<T> set = new HashSet<>();
        for (ScoredObject<T> element : elements)
        {
            checkPointer(element != null);
            set.add(element.getObject());
        }
        boolean result = scores.keySet().retainAll(set);
        // Combine scores
        for (ScoredObject<T> element : elements)
        {
            if (element.getScore() == 0)
            {
                continue;
            }
            Double score = scores.get(element.getObject());
            if (score != null)
            {
                scores.put(element.getObject(), score + element.getScore());
                result = true;
            }
        }
        return result;
    }

    /**
     * Returns the number of elements.
     */
    public int size()
    {
        return scores.size();
    }

    /**
     * Returns a {@link List} of all objects scored, sorted according to the default comparator.
     */
    public List<T> values()
    {
        List<ScoredObject<T>> list = new ArrayList<>();
        for (Entry<T, Double> entry : scores.entrySet())
        {

        	//System.out.println("Actual score is "+entry.getValue()+" For "+entry.getKey().getSearch());
            list.add(new ScoredObject<>(entry.getKey(), entry.getValue()));
        }
        Collections.sort(list, comparator);
        List<T> result = new ArrayList<>();
        for (ScoredObject<T> element : list)
        {
            result.add(element.getObject());
        }
        return result;
    }
    /**
     * Returns a {@link List} of all objects scored, sorted according to the default comparator.
     */
    public TreeMap <Double,List<SuggestPayload>> values(SearchPayload json, boolean bool)
    {
        String bucketKey = json.getFirstBucket();
        int bucketValue = json.getBucket(bucketKey).get("value");
    	//Double allowedScore = (Double) (json.getRealText().split(" ").length -.5);
    	/*String query = json.getString("q");*/
    	Double value;
        TreeMap<Double,List<SuggestPayload>> tmap = new TreeMap<Double,List<SuggestPayload>>(Collections.reverseOrder());
        
        for (Entry<T, Double> entry : scores.entrySet())
        {
        	SuggestPayload oldsr = (SuggestPayload) entry.getKey();
        	SuggestPayload sr = new SuggestPayload(oldsr.getSearch(), new HashMap<String,Integer>());
        	sr.copy(oldsr);
        	
        	value = entry.getValue();
        	value = (double)Math.round(value * 100) / 100;
        	
        	//System.out.println(" Before sorting for name "+sr.getSearch()+" count is "+sr.getCount()+" value is "+value);
        	//if(value>=allowedScore){
        		UpdateMap(tmap,sr,value);
        	//}
        	
        	//System.out.println("LIST"+tmap);
        	
            //list.add(new ScoredObject<>(entry.getKey(), value));
        }
        return tmap;
    }
    /**
     * Returns a {@link List} of all objects scored, sorted according to the default comparator.
     */
    public List<T> values(SearchPayload json)
    {
        int maxBucketSize = 3;
        ArrayList<ArrayList<T>> bucketList = new ArrayList<>();

        Map<String, Map<String, Integer>> bucket = json.getBucket();


        int bucketSize = bucket.size();
        String bucketName[];
        int bucketValue[];
        int bucketWeight[];
        int size=1;
        if(bucketSize == 1) {
            size=2;
        }
        if(bucketSize == 2) {
            size=4;
        }
        if(bucketSize == 3) {
            size=8;
        }
        bucketName = new String[maxBucketSize];
        bucketValue = new int[maxBucketSize];
        bucketWeight = new int[maxBucketSize];
        for(int i =0; i<maxBucketSize;i++) {
            bucketName[i]   = "";
            bucketValue[i]  = 0;
            bucketWeight[i] = 0;
        }
        for(int i=0; i<size;i++) {
            //Map<Integer, List<SuggestPayload>> tMapWithBucket = new HashMap<>();
            ArrayList<T> arrayListWithBucket = new ArrayList<>();
            bucketList.add(arrayListWithBucket);
        }
        int start = 0;
        if (bucket.size() > 0) {
            for (Entry<String, Map<String, Integer>> entry : bucket.entrySet()) {
                bucketName[start]   = entry.getKey();
                bucketValue[start]  = entry.getValue().get("value");
                bucketWeight[start] = entry.getValue().get("weight");
                start++;
            }
        }
        //Double allowedScore = (Double) (json.getRealText().split(" ").length -.5);
    	/*String query = json.getString("q");*/
        Double value;
        TreeMap<Double, List<SuggestPayload>> tmap = new TreeMap<Double, List<SuggestPayload>>(Collections.reverseOrder());

        for (Entry<T, Double> entry : scores.entrySet()) {
            SuggestPayload oldsr = (SuggestPayload) entry.getKey();
            SuggestPayload sr = new SuggestPayload(oldsr.getSearch(), new HashMap<>());
            sr.copy(oldsr);

            value = entry.getValue();
            value = (double) Math.round(value * 100) / 100;

            //System.out.println(" Before sorting for name "+sr.getSearch()+" count is "+sr.getCount()+" value is "+value);
            //if(value>=allowedScore){
            UpdateMap(tmap, sr, value);
            //}

            //System.out.println("LIST"+tmap);

            //list.add(new ScoredObject<>(entry.getKey(), value));
        }
        Set set = tmap.entrySet();
        Iterator iterator = set.iterator();
        List<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            Entry mentry = (Entry) iterator.next();
            ArrayList<SuggestPayload> al = (ArrayList<SuggestPayload>) mentry.getValue();

            Map<Integer,TreeMap<Integer, List<SuggestPayload>>> tempBucket = new HashMap<>();
            for(int i=0; i<size;i++) {
                TreeMap<Integer, List<SuggestPayload>> bucketZero = new TreeMap<Integer, List<SuggestPayload>>(Collections.reverseOrder());
                tempBucket.put(i, bucketZero);
            }
            /*TreeMap<Integer, List<SuggestPayload>> bucketZero = new TreeMap<Integer, List<SuggestPayload>>(Collections.reverseOrder());
            TreeMap<Integer, List<SuggestPayload>> bucketOne = new TreeMap<Integer, List<SuggestPayload>>(Collections.reverseOrder());
            TreeMap<Integer, List<SuggestPayload>> bucketTwo = new TreeMap<Integer, List<SuggestPayload>>(Collections.reverseOrder());
            TreeMap<Integer, List<SuggestPayload>> bucketThree = new TreeMap<Integer, List<SuggestPayload>>(Collections.reverseOrder());*/
            for (int i = 0; i < al.size(); i++) {

                SuggestPayload sr = al.get(i);

                boolean first = same(sr, json, bucketName[0]);
                boolean second = same(sr, json, bucketName[1]);
                boolean third = same(sr, json, bucketName[2]);

                if(bucketSize == 0) {
                    UpdateMap(tempBucket.get(0), sr, sr.getCount());
                }
                if (bucketSize == 1) {
                    if (first) {
                        UpdateMap(tempBucket.get(0), sr, sr.getCount());
                    }
                    else {
                        UpdateMap(tempBucket.get(1), sr, sr.getCount());
                    }
                }
                if (bucketSize == 2) {
                    if (first && second) {
                        UpdateMap(tempBucket.get(0), sr, sr.getCount());
                    }
                    else if ((first && !second) || (!first && second)) {
                        int wgt1 = bucketWeight[0];
                        int wgt2 = bucketWeight[1];
                        if(wgt1 > wgt2 && first) {
                            UpdateMap(tempBucket.get(1), sr, sr.getCount());
                        }
                        else if(wgt2 > wgt1 && second) {
                            UpdateMap(tempBucket.get(1), sr, sr.getCount());
                        }
                        else {
                            UpdateMap(tempBucket.get(2), sr, sr.getCount());
                        }
                    }
                    else {
                        UpdateMap(tempBucket.get(3), sr, sr.getCount());
                    }
                }
                if (bucketSize == 3) {
                    List<Integer> weights = new ArrayList<Integer>();

                    weights.add(0);
                    weights.add(bucketWeight[2]);
                    weights.add(bucketWeight[1]);
                    weights.add(bucketWeight[2]+bucketWeight[1]);
                    weights.add(bucketWeight[0]);
                    weights.add(bucketWeight[0]+bucketWeight[2]);
                    weights.add(bucketWeight[0]+bucketWeight[1]);
                    weights.add(bucketWeight[0]+bucketWeight[1] + bucketWeight[2]);
                    Collections.reverse(weights);

                    int totalWeight = 0;
                    if(first)
                        totalWeight+=bucketWeight[0];
                    if(second)
                        totalWeight+=bucketWeight[1];
                    if(third)
                        totalWeight+=bucketWeight[2];

                    int find = 0;
                    for(int weight : weights) {
                        if(weight == totalWeight)
                            break;
                        find++;
                    }

                        UpdateMap(tempBucket.get(find), sr, sr.getCount());
                }
            }
            for(int i = 0; i < size; i++) {
                if(tempBucket.get(i).size()>0)
                    UpdateResults(bucketList.get(i), tempBucket.get(i));
            }
            /*if(bucketZero.size()>=1)
                bucketList.get(0).putAll(bucketZero);
            if(bucketOne.size()>=1)
                bucketList.get(1).putAll(bucketOne);
            if(bucketTwo.size()>=1)
                bucketList.get(2).putAll(bucketTwo);
            if(bucketThree.size()>=1)
                bucketList.get(3).putAll(bucketThree);*/
           /* if(tempBucket.get(0).size()>=1)
                UpdateResults((List<T>) bucketList.get(0),tempBucket.get(0));
            if(tempBucket.get(1).size()>=1)
                UpdateResults((List<T>) bucketList.get(1),tempBucket.get(1));
            if(tempBucket.get(2).size()>=1)
                UpdateResults((List<T>) bucketList.get(2),tempBucket.get(2));
            if(tempBucket.get(3).size()>=1)
                UpdateResults((List<T>) bucketList.get(3),tempBucket.get(3));*/

        }
        //System.out.println("user wlaa "+tMapWithBucket);
        //System.out.println("Bina user wala "+tMapNoBucket);
        for (int i = 0; i < bucketList.size(); i++) {
            UpdateResultsList(result, bucketList.get(i));
        }
        //System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
        // System.out.println(mentry.getValue());

    //System.out.println(result);
    //System.out.println(tmap);
    //System.out.println(list);
    // Collections.sort(list, comparator);

       /* for (ScoredObject<T> element : list)
        {
            result.add(element.getObject());
        }*/
        return result;
    }

    private void UpdateResultsList(List<T> result, List<T> t) {
        for(T sp : t) {
            result.add(sp);
        }
    }

    private boolean same(SuggestPayload sr, SearchPayload json, String key) {

        if (!key.equals("") && sr.getFilter(key) != null && json.getBucket(key) !=null) {
            if(Integer.parseInt(sr.getFilter(key).toString()) == Integer.parseInt(json.getBucket(key).get("value").toString())) {
                return true;
            }
        }
        return false;
    }

    public void UpdateResults(List<T> result,Map<Integer,List<SuggestPayload>> tmap) {
    	Set set = tmap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
        	Entry mentry = (Entry)iterator.next();
        	ArrayList<SuggestPayload> al = (ArrayList<SuggestPayload>)mentry.getValue();
        	for(int i=0;i<al.size();i++) {
        		result.add((T)al.get(i));
        	}
        }
    }

    public <T>void UpdateMap(TreeMap<T,List<SuggestPayload>> tmap, SuggestPayload sr, T key) {
    	//System.out.println("coming here for "+key);
    	if(tmap.containsKey(key)) {
		    List<SuggestPayload> templist =  tmap.get(key);
		    boolean norecord = true;
		    T newkey = key;
		    //System.out.println("-------- For loop started -------");
		    //System.out.println("Searching for --->"+sr.getRealText());
		    for (Object srt : templist.toArray()) {
		    	SuggestPayload srs = (SuggestPayload)srt;
		    	String name = sr.getSearch();
		    	String name1 = srs.getSearch();
		    	String rn = sr.getRealText();
		    	String rn1 = srs.getRealText();
		    	//System.out.println(rn);
		    	//System.out.println(rn1);
		    	
		    	//System.out.println("start of this -->"+sr.getSearch()+" "+srs.getSearch()+" "+sr.getRealText()+" "+srs.getRealText()+" end of this");

                if(sr.getSearch().equals(srs.getSearch()) || sr.getRealText().equals(srs.getRealText())) {
		    		norecord = false;
		    		//System.out.println("ONe found"+sr.getRealText());
		    		srs.setCount(srs.getCount()+sr.getCount());
		    		break;
		    	}
		    }
		    if(norecord) {
		    	//System.out.println(templist);
		    	//System.out.println("not found in  list -->"+ sr.getRealText());
	       		templist.add(sr);
	       		tmap.remove(key);
	       		tmap.put(key, templist);
		    }
	   }
	   else
	   {
		   List<SuggestPayload> templist = new ArrayList<SuggestPayload>();
		   templist.add(sr);
		   tmap.put(key, templist);
	   }
    }
}
