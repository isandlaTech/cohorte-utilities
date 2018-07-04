// description of the hello world component that correspond to a webapp hello world
{
	"id":"helloWorld",
	"type":"docker",
	"states":[
		{
			"creating":{
				"dependencies":{
					{"tomcat":"created"}
				},
				"steps":{"$include":"steps/creating_interactive.js"}

			},
			
			"starting":{
				"dependencies":{
					{"tomcat":"started"}
				},
				"steps":{"$include":"steps/starting_interactive.js"}

			},
			
			"validating":{
				"dependencies":{
					{"tomcat":"started"}

				},
				"steps":{"$include":"steps/validating_interactive.js"}

			},
			"updating":{
				"dependencies":{
					{"tomcat":"stopped"}
				},
				"steps":{"$include":"steps/updating_interactive.js"}

			}
			
		}
	],
	"docker":{
		"image":"dimensions/helloWorld",
		"version":"1.0.0",
		"tyoe":"it",
		"volume":[
			{
				"container":"/opt/webapp",
				"host":"/root/grandest/tomcat/webapps/"
			}
		],
		"options":"--rm"
	}
}