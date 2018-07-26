{
	"properties": {
		"_iot": {
			"id": "/properties/md",
			"anyOf": [{
				"type": "integer"
			}, {
				"type": "number"
			}, {
				"type": "string"
			}, {
				"type": "array"
			}, {
				"type": "boolean"
			}, {
				"type": "object"
			}, {
				"type": "null"
			}],
			"type": "object"
		},
		"cmt": {
			"id": "/properties/cmt",
			"type": "string"
		},
		"_id": {
			"id": "/properties/id",
			"type": "string",
			"minLength": 5,
			"pattern": "^(\\w|-|_)*$"
		},
		"kwd": {
			"id": "/properties/kwd",
			"items": {
				"id": "/properties/kwd/items",
				"type": "string"
			},
			"type": "array"
		},
		"lbl": {
			"id": "/properties/lbl",
			"type": "string"
		},
		"md": {
			"id": "/properties/md",
			"anyOf": [{
				"type": "integer"
			}, {
				"type": "number"
			}, {
				"type": "string"
			}, {
				"type": "array"
			}, {
				"type": "boolean"
			}, {
				"type": "object"
			}, {
				"type": "null"
			}],
			"type": "object"
		},
		"item_id": {
			"id": "/properties/reference/",
			"properties": {
				"ref": {
					"type": "string"
				}
			},
			"type": "object"
		},
		"script_action_id": {
			"id": "/properties/reference/system/script/0",
			"properties": {
				"ref": {
					"type": "string"
				}
			},
			"type": "object"
		},
		"script_condition_id": {
			"id": "/properties/reference/system/script/0",
			"properties": {
				"ref": {
					"type": "string"
				}
			},
			"type": "object"
		},
		"asynchronous": {
			"type": "boolean"
		},
		"operatonName": {
			"type": "string"
		},
		"actionSchedule": {
			"type": "string"
		},
		"active": {
			"type": "boolean"
		}
	}
}