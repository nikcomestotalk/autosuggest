
## Smart Auto Suggestion in Java
Let your customers engage right at the search bar by giving them more useful, user-centric search suggestion

## Salient features
1. Instant search suggestions and spell check
2. Low Latency  ~ Response within 20 ms for 1M records
3. Order doesn't matter “iPhone 6s” or “6s iPhone” yield same result
4. Unlimited Filters and Parameters support
5. Support personalization
6. Customizable parameters like Servers port and recovery methods[Refer #config.properties].

## Dependancies(if no docker)
1. Apache Maven >= 3.3.9
2. Java ~ 1.8

## Installation
### 	Not a docker friend
	1. Git clone https://github.com/nikcomestotalk/autosuggest.git
	2. cd autosuggest
	3. mvn install
	4. mvn exec:java
	
### 	Docker is my childhood friend
	1. docker run -d -p 1082:1082 -p 1081:1081 --name autosuggest autosuggest/autosuggest

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

## DEMO(Click to see video demonstration)
[![Demo of auto suggestion](https://lh3.googleusercontent.com/I7VbhhLJE_qKNW4sunbsxfQsYj8q9ReToamvtaXRFUPShHkxuw23zXbVTCbxiaq_g4EXdwQ8MgREDZVAPQGsQgLvtrYe4-lm__1HK8RyAx9xa824WZ5gtkc84Gdqr_MzNefpIbfcAYTR7OuIkSMR65r4GZAcxDhmpTpevDMnH4dK1Xnu70x3uk7haz83e46HKGqnKO7qN2kmmTU8R02zDMUF2XewBReESYwmnaqJJ4U1mOW_CSRJ0vDcpR72_irNb_BcMGDUSS0G7zGi04acDGHXLPfivGCQwYA_1IzEPlnWcO319R7XbL2kqPe37W8BjWgcndA7AG36-Sfrl1ywNimWt63AB-Xl7qZJwuD2ftpn24y4l0kPNoGs03VQIHKNpjzc8rN1Bb-8xdK2txYGOjDVT6L_D_XISqSaqyq_u6NDw08uXv34VR217zrJUo-Wd9StsMGwmHhBPgaIZalBid3jqJrJB6eo2FmsT3ZyLakFz_HGo2Xk1oM5zLMP8xgkLmjdGu2v1BL0IrJlf_FSQuFl5RG3xkJBwkAxz9-gjGAFvW1bOrmY2cUGtR0-eX41=w1366-h597)](https://youtu.be/Y3vMfKAwWV4)
