{"type":"object",
	"$schema": "http://json-schema.org/draft-03/schema",
	"id": "#",
	"required":false,
	"properties":{
		"OrgId": {
			"type":"string",
			"id": "OrgId",
			"required":false
		},
		"metrics": {
			"type":"array",
			"id": "metrics",
			"required":false,
			"items": {
				"type":"object",
				"id": "0",
				"required":false,
				"properties":{
					"name": {
						"type":"string",
						"id": "name",
						"required":false
					},
					"valueTrend": {
						"type":"array",
						"id": "valueTrend",
						"required":false,
						"items": {
							"type":"object",
							"id": "0",
							"required":false,
							"properties":{
								"date": {
									"type":"string",
									"id": "date",
									"required":false
								},
								"val": {
									"type":"string",
									"id": "val",
									"required":false
								}
							}
						}
					}
				}
			}
		}
	}
}