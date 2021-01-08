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
				"path":"file://test_jsonpath_son.js",
				"cond":"'${var}' == 'test'"
			}
		}

}