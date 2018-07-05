[
	{
		"$generator":{

			id:"pull$.id",
			component:"$.id",
			state :"starting",
			type:"exec",
			command:{
				"$generator":{
					"interpreter":"curl",
					"args":[
						"-Is",
						"$(^.).deploy.fqdn"
					]
				}	
			}
		}
	}
]