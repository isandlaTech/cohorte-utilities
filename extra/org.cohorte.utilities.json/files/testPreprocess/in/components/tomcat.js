// description of the hello world component that correspond to a webapp hello world
{
	"id":"helloWorld",
	"type":"docker",
	"states":[
		{
			"creating":{
				"dependencies":{
					{"mysql":"created"},
					{"httpd":"created"}

				},
				"steps":{"$include":"steps/creating_deamon.js"}
			},
			
			"starting":{
				"dependencies":{
					{"mysql":"started"}
					{"httpd":"started"}
					"steps":{"$include":"steps/starting_deamon.js"}

				}
			},
			
			"validating":{
				"dependencies":{
					{"mysql":"started"}
					{"httpd":"started"}
					"steps":{"$include":"steps/validating_deamon.js"}

				}
			},
			"updating":{
				"steps":{"$include":"steps/updating_deamon.js"}

			}
			
		}
	],
	"docker":{
		"image":"dimensions/tomcat",
		"version":"1.0.0",
		"tyoe":"d",
		"volume":[
			{
				"container":"/opt/tomcat/base/webapps",
				"host":"/root/grandest/tomcat/webapps/"
			},
			{
				"container":"/opt/tomcat/base/conf",
				"host":"/root/grandest/tomcat/conf/"
			}
		],
		"options":"--rm"
	}
}