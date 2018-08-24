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
			"$generator":{
				"$file":{
					"path":"file://subdir/test_replace_vars_include1.js?test=$.article.ged&test2=$.article.ged",
					"cond":"'${var}' == '$.article.test'"
				}
			},
			
		},
	"test":{
		"$generator":{
			"test":"$1"
		}
	}

}