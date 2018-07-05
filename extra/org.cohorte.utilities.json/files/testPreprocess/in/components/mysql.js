// tomcat components
{
	"id":"mysql",
	"type":"docker",
	"states":{
		"creating":{
			"steps":{"$file":"file://steps/creating_mysql.js"}

		},
		
		"starting":{
			"steps":{"$file":"file://steps/starting_mysql.js"}

		},
		
		"validating":{
			"steps":{"$file":"file://steps/validating_mysql.js"}

		},
		
		"updating":{
			"steps":{"$file":"file://steps/updating_mysql.js"}

		},
		
	},
	"docker":{
		"image":"dimensions/mysql",
		"version":"1.0.0",
		"name":"mysql",
		"tyoe":"d",
		
		"volume":[
			{
				"container":"/opt/conf",
				"host":"/root/grandest/httpd/conf/"
			}
		],
	}
}