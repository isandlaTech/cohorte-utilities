{
	"name" : "spellchecker-demo-app",
	"root" : {
		"name" : "spellchecker-demo",
		"components" : [ {
			/**
			 * EN Dictionary
			 */
			"name" : "dictionary_en_python",
			"factory" : "spell_dictionary_en_factory",
			"language" : "python"
		}, {
			/**
			 * FR Dictionary
			 */
			"name" : "dictionary_fr_python",
			"factory" : "spell_dictionary_fr_factory",
			"language" : "python"
		}, {
			/**
			 * Spell Checker
			 */
			"name" : "spell_checker_python",
			"factory" : "spell_checker_factory",
			"language" : "python"
		}, {
			/**
			 * Spell Client
			 */
			"name" : "spell_client_python",
			"factory" : "spell_client_factory",
			"language" : "python",
			"isolate" : "client-isolate"
		} ]
	}
}