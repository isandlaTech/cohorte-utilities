package org.cohorte.utilities.encode;

/**
 * @author ogattaz
 *
 */
public interface IBase64 {

	 String BASIC_PREFIX = "basic:";
	 
	 String BASE64_PREFIX = "base64:";
	
	 String[] PREFIXES = {BASIC_PREFIX,BASE64_PREFIX};
}
