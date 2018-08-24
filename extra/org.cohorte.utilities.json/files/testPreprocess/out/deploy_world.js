{
	"article": {
		"HelloWorldDimensions": [{
			"id": "helloWorld",
			"type": "docker",
			"states": {
				"creating": {
					"dependencies": [{
						"tomcat": "created"
					}],
					"steps": [{
						"id": "pullhelloWorld",
						"component": "helloWorld",
						"state": "starting",
						"type": "exec",
						"command": {
							"interpreter": "docker",
							"args": ["pull", "dimensions/helloWorld:1.0.0"]
						}
					}, {
						"id": "createhelloWorld",
						"component": "helloWorld",
						"state": "starting",
						"type": "exec",
						"command": {
							"interpreter": "docker",
							"args": ["create"],
							"params": [{
								"--name": "helloworld"
							}, {
								"-v": "/opt/webapp:/root/grandest/tomcat/webapps/"
							}, {
								"-v": "/opt/webapp:/root/grandest/tomcat/webapps/"
							}],
							"image": "dimensions/helloWorld:1.0.0"
						}
					}, {
						"id": "createhelloWorld",
						"component": "helloWorld",
						"state": "starting",
						"type": "exec",
						"command": {
							"interpreter": "docker",
							"args": ["ps -a | grep helloworld | wc -l"],
							"expected": "1"
						}
					}]
				},
				"starting": {
					"dependencies": [{
						"tomcat": "started"
					}],
					"steps": [{
						"id": "pullhelloWorld",
						"component": "helloWorld",
						"state": "starting",
						"type": "exec",
						"command": {
							"interpreter": "docker",
							"args": ["start", "helloworld"]
						}
					}]
				},
				"validating": {
					"dependencies": [{
						"tomcat": "started"
					}],
					"steps": [{
						"id": "pullhelloWorld",
						"component": "helloWorld",
						"state ": "starting",
						"type": "exec",
						"command": {
							"interpreter": "curl",
							"args": ["-Is", ".agilium.cloud"]
						}
					}]
				},
				"updating": {
					"dependencies": [{
						"tomcat": "stopped"
					}],
					"steps": [{
						"id": "pullhelloWorld",
						"component": "helloWorld",
						"state ": "starting",
						"type": "exec",
						"command": {
							"interpreter": "docker",
							"args": ["pull", "dimensions/helloWorld:1.0.0"]
						}
					}, {
						"id": "createhelloWorld",
						"component": "helloWorld",
						"state ": "starting",
						"type": "exec",
						"command": {
							"interpreter": "docker",
							"args": ["create"],
							"params": [{
								"--name": "helloworld"
							}, {
								"-v": "/opt/webapp:/root/grandest/tomcat/webapps/"
							}],
							"image": "dimensions/helloWorld:1.0.0"
						}
					}]
				}
			},
			"docker": {
				"image": "dimensions/helloWorld",
				"name": "helloworld",
				"version": "1.0.0",
				"tyoe": "it",
				"volume": [{
					"container": "/opt/webapp",
					"host": "/root/grandest/tomcat/webapps/"
				}],
				"options": "--rm"
			}
		}]
	},
	"deploy": {
		"subdomain": "",
		"fqdn": ".agilium.cloud",
		"ip": ""
	},
	"components": [{
		"id": "http",
		"type": "docker",
		"states": {
			"creating": {
				"steps": [{
					"id": "pullhttp",
					"component": "http",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["pull", "dimensions/httpd:1.0.0"]
					}
				}, {
					"id": "createhttp",
					"component": "http",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["create"],
						"params": [{
							"--name": "httpd"
						}, {
							"-p": "80:80"
						}, {
							"-p": "443:443"
						}, {
							"-v": "/opt/conf:/root/grandest/httpd/conf/"
						}, {
							"-v": "/opt/conf:/root/grandest/httpd/conf/"
						}],
						"image": "dimensions/httpd:1.0.0"
					}
				}, {
					"id": "createhttp",
					"component": "http",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["ps -a | grep httpd | wc -l"],
						"expected": "1"
					}
				}]
			},
			"starting": {
				"steps": [{
					"id": "pullhttp",
					"component": "http",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["start", "httpd"]
					}
				}]
			},
			"validating": {
				"steps": [{
					"id": "pullhttp",
					"component": "http",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "curl",
						"args": ["-Is", ".agilium.cloud"]
					}
				}]
			},
			"updating": {
				"steps": [{
					"id": "pullhttp",
					"component": "http",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["pull", "dimensions/httpd:1.0.0"]
					}
				}, {
					"id": "createhttp",
					"component": "http",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["create"],
						"params": [{
							"--name": "httpd"
						}, {
							"-p": "80:80"
						}, {
							"-p": "443:443"
						}, {
							"-v": "/opt/conf:/root/grandest/httpd/conf/"
						}],
						"image": "dimensions/httpd:1.0.0"
					}
				}]
			}
		},
		"docker": {
			"image": "dimensions/httpd",
			"version": "1.0.0",
			"name": "httpd",
			"tyoe": "d",
			"port": ["80:80", "443:443"],
			"volume": [{
				"container": "/opt/conf",
				"host": "/root/grandest/httpd/conf/"
			}]
		}
	}, {
		"id": "tomcat",
		"type": "docker",
		"states": {
			"creating": {
				"dependencies": [{
					"mysql": "created"
				}, {
					"httpd": "created"
				}],
				"steps": [{
					"id": "pulltomcat",
					"component": "tomcat",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["pull", "dimensions/tomcat:1.0.0"]
					}
				}, {
					"id": "createtomcat",
					"component": "tomcat",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["create"],
						"params": [{
							"--name": "tomcat"
						}, {
							"-v": "/opt/tomcat/base/webapps:/root/grandest/tomcat/webapps/"
						}, {
							"-v": "/opt/tomcat/base/webapps:/root/grandest/tomcat/webapps/"
						}],
						"image": "dimensions/tomcat:1.0.0"
					}
				}, {
					"id": "createtomcat",
					"component": "tomcat",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["ps -a | grep tomcat | wc -l"],
						"expected": "1"
					}
				}]
			},
			"starting": {
				"dependencies": [{
					"mysql": "started"
				}, {
					"httpd": "started"
				}],
				"steps": [{
					"id": "pulltomcat",
					"component": "tomcat",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["start", "tomcat"]
					}
				}]
			},
			"validating": {
				"dependencies": [{
					"mysql": "started"
				}, {
					"httpd": "started"
				}],
				"steps": [{
					"id": "pulltomcat",
					"component": "tomcat",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "curl",
						"args": ["-Is", ".agilium.cloud"]
					}
				}]
			},
			"updating": {
				"steps": [{
					"id": "pulltomcat",
					"component": "tomcat",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["pull", "dimensions/tomcat:1.0.0"]
					}
				}, {
					"id": "createtomcat",
					"component": "tomcat",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["create"],
						"params": [{
							"--name": "tomcat"
						}, {
							"-v": "/opt/tomcat/base/webapps:/root/grandest/tomcat/webapps/"
						}],
						"image": "dimensions/tomcat:1.0.0"
					}
				}]
			}
		},
		"docker": {
			"image": "dimensions/tomcat",
			"version": "1.0.0",
			"name": "tomcat",
			"tyoe": "d",
			"volume": [{
				"container": "/opt/tomcat/base/webapps",
				"host": "/root/grandest/tomcat/webapps/"
			}, {
				"container": "/opt/tomcat/base/conf",
				"host": "/root/grandest/tomcat/conf/"
			}],
			"options": "--rm"
		}
	}, {
		"id": "helloWorld",
		"type": "docker",
		"states": {
			"creating": {
				"dependencies": [{
					"tomcat": "created"
				}],
				"steps": [{
					"id": "pullhelloWorld",
					"component": "helloWorld",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["pull", "dimensions/helloWorld:1.0.0"]
					}
				}, {
					"id": "createhelloWorld",
					"component": "helloWorld",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["create"],
						"params": [{
							"--name": "helloworld"
						}, {
							"-v": "/opt/webapp:/root/grandest/tomcat/webapps/"
						}, {
							"-v": "/opt/webapp:/root/grandest/tomcat/webapps/"
						}],
						"image": "dimensions/helloWorld:1.0.0"
					}
				}, {
					"id": "createhelloWorld",
					"component": "helloWorld",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["ps -a | grep helloworld | wc -l"],
						"expected": "1"
					}
				}]
			},
			"starting": {
				"dependencies": [{
					"tomcat": "started"
				}],
				"steps": [{
					"id": "pullhelloWorld",
					"component": "helloWorld",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["start", "helloworld"]
					}
				}]
			},
			"validating": {
				"dependencies": [{
					"tomcat": "started"
				}],
				"steps": [{
					"id": "pullhelloWorld",
					"component": "helloWorld",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "curl",
						"args": ["-Is", ".agilium.cloud"]
					}
				}]
			},
			"updating": {
				"dependencies": [{
					"tomcat": "stopped"
				}],
				"steps": [{
					"id": "pullhelloWorld",
					"component": "helloWorld",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["pull", "dimensions/helloWorld:1.0.0"]
					}
				}, {
					"id": "createhelloWorld",
					"component": "helloWorld",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["create"],
						"params": [{
							"--name": "helloworld"
						}, {
							"-v": "/opt/webapp:/root/grandest/tomcat/webapps/"
						}],
						"image": "dimensions/helloWorld:1.0.0"
					}
				}]
			}
		},
		"docker": {
			"image": "dimensions/helloWorld",
			"name": "helloworld",
			"version": "1.0.0",
			"tyoe": "it",
			"volume": [{
				"container": "/opt/webapp",
				"host": "/root/grandest/tomcat/webapps/"
			}],
			"options": "--rm"
		}
	}, {
		"id": "mysql",
		"type": "docker",
		"states": {
			"creating": {
				"steps": [{
					"id": "pullmysql",
					"component": "mysql",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["pull", "dimensions/mysql:1.0.0"]
					}
				}, {
					"id": "createmysql",
					"component": "mysql",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["create"],
						"params": [{
							"--name": "mysql"
						}, {
							"-v": "/opt/conf:/root/grandest/httpd/conf/"
						}, {
							"-v": "/opt/conf:/root/grandest/httpd/conf/"
						}],
						"image": "dimensions/mysql:1.0.0"
					}
				}, {
					"id": "createmysql",
					"component": "mysql",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["ps -a | grep mysql | wc -l"],
						"expected": "1"
					}
				}]
			},
			"starting": {
				"steps": [{
					"id": "pullmysql",
					"component": "mysql",
					"state": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["start", "mysql"]
					}
				}]
			},
			"validating": {
				"steps": [{
					"id": "pullmysql",
					"component": "mysql",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "curl",
						"args": ["-Is", ".agilium.cloud"]
					}
				}]
			},
			"updating": {
				"steps": [{
					"id": "pullmysql",
					"component": "mysql",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["pull", "dimensions/mysql:1.0.0"]
					}
				}, {
					"id": "createmysql",
					"component": "mysql",
					"state ": "starting",
					"type": "exec",
					"command": {
						"interpreter": "docker",
						"args": ["create"],
						"params": [{
							"--name": "mysql"
						}, {
							"-v": "/opt/conf:/root/grandest/httpd/conf/"
						}],
						"image": "dimensions/mysql:1.0.0"
					}
				}]
			}
		},
		"docker": {
			"image": "dimensions/mysql",
			"version": "1.0.0",
			"name": "mysql",
			"tyoe": "d",
			"volume": [{
				"container": "/opt/conf",
				"host": "/root/grandest/httpd/conf/"
			}]
		}
	}]
}