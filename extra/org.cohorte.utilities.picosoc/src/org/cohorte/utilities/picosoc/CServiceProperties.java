package org.cohorte.utilities.picosoc;

import java.util.LinkedHashMap;

/**
 * @author ogattaz
 *
 */
public class CServiceProperties extends LinkedHashMap<String,String>{
	
	
	/**
	 * @param aId
	 * @param aValue
	 * @return
	 */
	public static CServiceProperties newProps(final String aId, final String aValue) {

		return newProps().addPair(aId, aValue);
	}
	
	/**
	 * @return
	 */
	public static CServiceProperties newProps() {
		return new CServiceProperties();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1003977384952392793L;

	/**
	 * 
	 */
	public CServiceProperties() {
		super();
	}
	
	/**
	 * @param aKey
	 * @param aValue
	 * @return
	 */
	public CServiceProperties addPair(final String aKey,final String aValue) {
		this.put(aKey, aValue);
		return this;
	}

}
