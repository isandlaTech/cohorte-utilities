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
				"path": "('${var}' == 'test')?test_replace_vars_include1.js:test_replace_vars_include2;",
				"cond":"'${var}' == 'test'"
			}
		}

}