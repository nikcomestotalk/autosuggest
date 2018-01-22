
## Smart Auto Suggestion in Java
Let your customers engage right at the search bar by giving them more userful, user-centric searches with Smart Search 

## Salient features
1. Instant search suggestions and spell check
2. Low Latency  ~ Response within 20 ms for 1M records
3. Order don’t matter “Iphone 6s” or “6s iphone” yield same result
4. Unlimited Filters and Parameters support
5. Support personalization
6. Customizable parameters like Servers port and recover methods[Refer #config.properties].

## Dependancies
1. Apache Maven >= 3.3.9
2. Java ~ 1.8

## Installation
1. Git clone https://github.com/nikcomestotalk/autosuggest.git
2. cd autosuggest
3. mvn install
4. mvn exec:java

## Contributing

1. Fork this repo and clone your fork.
2. Make your desired changes
3. Add tests for your new feature and ensure all tests are passing
4. Commit and push
5. Submit a Pull Request through Github's interface and I'll review your changes to see if they make it to the next release.


## Issues

Use this repo's github issues.

## Request Template
URL    : *127.0.0.1:1081/query*

METHOD : *POST*

POST PARAMS: 
```json
		{
			"query": "iphone",
			"filter": {		
				"param1":1,
				"param2":2
			},
			"bucket": {		
				"p1": {	
					"value" : 7007,
					"weight": 90
					},
				"p2": {
					"value":57,
					"weight":10
					}
				},
			"limit":10
		}
```

*Query*  : Query of which you want suggestion

*Filter* : Filter out suggestions, you can set unlimited filters.

*Bucket* : Order your suggestion on the basis of parameters specified within bucket key, Max 3 allowed

*Limit*  :  Limit the suggestions

## Response Template
```json
[
    {
        "matched": "iphone 6s",
        "parameters": {
            "param1": 1,
	    "param2":2,
            "location": 4321
        },
        "error": ""
    },
    {
        "matched": "Iphone 6s 64gb ",
        "parameters": {
	    "param1": 1,
	    "param2":2,
            "user": 1,
            "location": 4321
        },
        "error": ""
    }
]
```
## Training or Populating Data in engine
`How to put data into Suggest Engine.`

URL    : *127.0.0.1:1081/update*

METHOD : *POST*

POST PARAMS: 
```json
{
	"query": "iphone fc",
	"parameter": {
		"p1": 16,
		"p2": 12,
		"p3": 34
	}
```

*Query*  : Text you want to save.

*parameter* : Tagging the text, which can be used for both filter and bucket parameters.

