// description of the hello world component that correspond to a webapp hello world
{
	"id":"helloWorld",
	"type":"docker",
	"states":{
			"creating":{
				"dependencies":[
					{"tomcat":"created"}
				],
				"steps":{"$file":"file://steps/creating_helloworld.js"}

			},
			
			"starting":{
				"dependencies":[
					{"tomcat":"started"}
				],
				"steps":{"$file":"file://steps/starting_helloworld.js"}

			},
			
			"validating":{
				"dependencies":[
					{"tomcat":"started"}
				],
				"steps":{"$file":"file://steps/validating_helloworld.js"}

			},
			"updating":{
				"dependencies":[
					{"tomcat":"stopped"}
				],
				"steps":{"$file":"file://steps/updating_helloworld.js"}

			}
			
	},
	"docker":{
		"image":"dimensions/helloWorld",
		"name":"helloworld",
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