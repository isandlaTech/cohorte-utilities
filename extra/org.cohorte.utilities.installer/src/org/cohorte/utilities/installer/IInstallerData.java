package org.cohorte.utilities.installer;

import java.util.Properties;

public interface IInstallerData {
	
	
	/**
	 * @param aId
	 * @return
	 */
	Object getData(String aId);
	
	
	/**
	 * @param aId
	 * @return
	 */
	boolean getDataBoolean(String aId);
	

	/**
	 * @param aId
	 * @return
	 */
	String getDataString(String aId);
	

	
	/**
	 * @param aProperties
	 */
	void putAll(Properties aProperties);
	
	/**
	 * @param aId
	 * @param aValue
	 */
	void putData(String aId,Object aValue);

}
