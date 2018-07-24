{
	

	"article" : {
		"test": "${var}",
		"GED" :"testGED",
		"Panoramap" :"testPanoramap",
		"Planning" :{
			"backend":true,
			"version":1.2
		}
	},
	"detail" : 
		{
			"$file":{
				"path":"test_jsonpath_son.js",
				"cond":"'${var}' == 'test'"
			}
		}

}