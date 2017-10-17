***CURRENTLY IN DEVELOP MODE***


# Smart Search Suggestor
Let your customers engage right at the search bar by giving them more userful, user-centric searches with Smart Search 

Silent features
1. Instant search suggestions and spell check
2. Low Latency  ~ Response within 20 ms for 1M records
3. Order don’t matter “Iphone 6s” or “6s iphone” yield same result
4. Unlimited Filters and Parameters support
5. Support personalization


Train input data 
T1. {"text": " Iphone 5s in mumbai","parameters":{"user":45432,"category":453,"location":34,"model":1}}
T2. {"text": "Samsung 10L automatic washing machine","parameters":{"category":321,"location":34,"model":14}}
T3. {"text": " Iphone 6s in good condition","parameters":{"user":342,"category":453,"location":12,"model":2}}

<b>Text</b> tag Basically used to create tree and <b>parameters</b> tag is to parameters matched when searching.
1. Search Query with no user tag.

{"matched":"Iphone","parameters":{"location": 34 }}

Return  T1 only

2. Search Query with no location, no user

{"matched":"Iphone","parameters":{}}

Return T1 and T3

3. Search Query with user tag.

{"matched":"Iphone","parameters":{"user":45432}} [Here user tag is used to prioritize search done by user[45432] first and rest later.

Returns in with T1 coming first and T2 next.

3. Auto Correct text feature is also available

{"matched":"Ephone","parameters":{}}

Returns T1 and T2
