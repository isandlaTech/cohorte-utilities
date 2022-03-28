{
	"properties": {
		"_iot": {
			"id": "/properties/_iot",
			"properties": {
				"tid": {
					"type": "string"
				},
				"mat": {
					"type": "number"
				},
				"cat": {
					"type": "number"
				},
				"mby": {
					"type": "string"
				},
				"cby": {
					"type": "string"
				},
				"item_id": {
					"type": "string"
				},
				"hash": {
					"type": "string"
				}
			},
			"type": "object"
		},
		"transcripterId": {
			"type": "string"
		},
		"notSynchronizable": {
			"type": "boolean"
		},
		"cmt": {
			"id": "/properties/cmt",
			"type": "string"
		},
		"_id": {
			"id": "/properties/id",
			"type": "string",
			"pattern": "^(\\w|-|_|.)*§"
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
		"scope": {
			"id": "/properties/scope",
			"type": "string"
		},
		"permission": {
			"id": "/properties/permission",
			"type": "string"
		}
	}
}