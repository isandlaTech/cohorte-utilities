// tomcat components
{
	"id":"tomcat",
	"type":"docker",
	"states":{
		"creating":{
			"steps":{"$include":"steps/creating_deamon.js"}

		},
		
		"starting":{
			"steps":{"$include":"steps/starting_deamon.js"}

		},
		
		"validating":{
			"steps":{"$include":"steps/validating_deamon.js"}

		},
		
		"updating":{
			"steps":{"$include":"steps/updating_deamon.js"}

		},
		
	}
}