[{
		// check container created
		"$generator":{
			"id":"writeshellcheck$.id",
			"component":"$.id",
			"state":"creating",
			"type":"exec",
			"command":{
				"$generator":{
					"interpreter":"file",
					"location":"/root/${deploy.subdomain}/shells/check$(..).docker.name",
					"lines":[// list of line to write in the file 
						"#!/bin/sh",
						'res=`ps -a | grep $(..).docker.name | wc -l`',
						'echo res'
					]
				}	
			}
		}
	},{
		"$generator":{

			"id":"check$.id",
			"component":"$.id",
			"state":"creating",
			"type":"exec",
			"command":{
				"$generator":{
					"interpreter":"shell",
					"args":[
						"sh",
						"/root/${deploy.subdomain}/shells/check$(..).docker.name"
					],
					"expected":"1"// describe for the command the expected result
				}	
			}
		}
	}]