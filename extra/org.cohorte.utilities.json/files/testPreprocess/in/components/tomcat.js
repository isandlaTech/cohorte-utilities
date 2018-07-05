// description of the hello world component that correspond to a webapp hello world
{
	"id":"tomcat",
	"type":"docker",
	"states":{
			"creating":{
				"dependencies":[
					{"mysql":"created"},
					{"httpd":"created"}

					],
				"steps":{"$file":"file://steps/creating_tomcat.js"}
			},
			
			"starting":{
				"dependencies":[
					{"mysql":"started"},
					{"httpd":"started"}
					],
					"steps":{"$file":"file://steps/starting_tomcat.js"}

			},
			
			"validating":{
				"dependencies":[
					{"mysql":"started"},
					{"httpd":"started"}
					],
					"steps":{"$file":"file://steps/validating_tomcat.js"}

					
			},
			"updating":{
				"steps":{"$file":"file://steps/updating_tomcat.js"}

			}
			
	},
	"docker":{
		"image":"dimensions/tomcat",
		"version":"1.0.0",
		"name":"tomcat",
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