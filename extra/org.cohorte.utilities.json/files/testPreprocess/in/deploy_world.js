{
	// list of all article with link component
	"article":{
		"HelloWorldDimensions":[
			{"$file":"file://components/helloWorld.js"}
		]
	},
	
	"deploy":{
		"subdomain":"${deploy.subdomain}", // will be replace while loading against property send by Gui
		"fqdn":"${deploy.subdomain}.agilium.cloud", // will be replace while loading against property send by Gui
		"ip":"${deploy.ip}" // will be replace while loading against property send by Gui

	},
	
	// list all available component
	"components":{"$file":"file://components/*.js"}

}