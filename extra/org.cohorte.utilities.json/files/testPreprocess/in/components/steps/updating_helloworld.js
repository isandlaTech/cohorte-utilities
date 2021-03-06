[
	{
		"$generator":{
	
			id:"pull$.id",
			component:"$.id",
			state :"starting",
			type:"exec",
			command:{
				"$generator":{
					"interpreter":"docker",
					"args":[
						"pull",
						"$.docker.image:$.docker.version"
					]
				}	
			}
		}
	},{
		"$generator":{
	
			id:"create$.id",
			component:"$.id",
			state :"starting",
			type:"exec",
			command:{
				"$generator":{
					"interpreter":"docker",
					"args":[
						"create"
					],
					"params":[
						{"--name":"$.docker.name"},
						{"-v":"$.docker.volume[0].container:$.docker.volume[0].host"}
						
					],
					"image":"$.docker.image:$.docker.version"
				}	
			}
		}
	}
]