/**
 * test include file with comment
 */
{// test include file with comment
	"allOf": [{
		"$ref": "#/definitions/device"
	}, {
		"properties": {
			"test": {
				"type": "${testVal}"
			}
		}
	}],
	"type": "object"
}