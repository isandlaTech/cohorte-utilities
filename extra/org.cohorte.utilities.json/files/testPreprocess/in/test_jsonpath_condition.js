{
	

	"article" : [
		{
			"id":"id1",
			"test2":"test1",
			"test3":"test1"

		},{
			"id":"id2",
			"test2":"test2",
			"test3":"test2"
		},{
			"id":"id3",
			"test2":"test1",
			"test3":"test1",
			"$generator":{
				"id" : "value",
				"valueId2":"$.article[id=id2].test3",
				"test":"$.article[id=id3].$generator.id"
			}
		}
	]
	
		

}