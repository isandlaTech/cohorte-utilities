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
				"path":"file://subsubdir/test_replace_vars_include2.js?var=test2",
				"cond":"'${var}' == 'test'"
			}
		}

}