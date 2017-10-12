{
    "$schema": "http://json-schema.org/draft-04/schema#",
    "definitions": {},
    "id": "http://example.com/example.json",
    "additionalProperties":false,
    "properties": {
        "cmt": {
            "id": "/properties/cmt",
            "type": "string"
        },
        "id": {
            "id": "/properties/id",
            "type": "string"
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
            "properties": {
                "test": {
                    "id": "/properties/md/properties/test",
                    "properties": {
                        "min": {
                            "id": "/properties/md/properties/test/properties/min",
                            "type": "integer"
                        }
                    },
                    "type": "object"
                },
                "v": {
                    "id": "/properties/md/properties/v",
                    "type": "number"
                }
            },
            "type": "object"
        },
        "org_id": {
            "id": "/properties/org_id",
            "properties": {},
            "type": "object"
        },
       
    },
    "type": "object"
}