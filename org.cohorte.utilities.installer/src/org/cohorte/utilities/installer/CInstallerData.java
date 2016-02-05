package org.cohorte.utilities.installer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.psem2m.utilities.CXSortListProperties;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * The "data" service of the installer.
 * 
 * Used to store the content of the config.ini file of the patched web server
 * 
 * @author ogattaz
 * 
 */
public class CInstallerData extends HashMap<String, Object> implements
		IInstallerData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 236834263427191825L;

	private final IActivityLogger pLogger;

	/**
	 * @throws
	 * 
	 */
	public CInstallerData() {
		super();

		pLogger = CInstallerTools.getServiceLogger();

		try {
			CInstallerTools.registerService(IInstallerData.class, this);
		} catch (Exception e) {
			pLogger.logSevere(this, "<init>", "ERROR: %s", e);
		}

		pLogger.logInfo(this, "<init>", "instanciated");
	}

	/**
	 * @return
	 */
	public String  dump(){
		return super.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.addins.cyptedechange.panels.IInstallerData#getData
	 * (java.lang.String)
	 */
	//@Override
	public Object getData(String aId) {
		Object wValue = get(aId);
		pLogger.logInfo(this, "get", "id=[%s] value=[%s]",
				aId, wValue);
		return wValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.addins.cyptedechange.panels.IInstallerData#getDataBoolean
	 * (java.lang.String)
	 */
	//@Override
	public boolean getDataBoolean(String aId) {
		return Boolean.parseBoolean(getDataString(aId));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.addins.cyptedechange.panels.IInstallerData#getDataString
	 * (java.lang.String)
	 */
	//@Override
	public String getDataString(String aId) {
		return String.valueOf(getData(aId));
	}

	/* (non-Javadoc)
	 * @see com.isandlatech.x3.addins.cyptedechange.panels.IInstallerData#putAll(java.util.Properties)
	 */
	//@Override
	public void putAll(Properties aProperties) {
		CXSortListProperties wSortProperties = new CXSortListProperties(aProperties,CXSortListProperties.ASCENDING);

		int wSizeBefore = size();
		for (Map.Entry<Object, Object> wEntry : wSortProperties.getTreeSet()) {
			putData(wEntry.getKey().toString(), wEntry.getValue());
		}
		int wSizeAfter = size();
		pLogger.logInfo(this, "putAll", "SizeBefore=[%d] SizeAfter=[%d]",
				wSizeBefore, wSizeAfter);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.addins.cyptedechange.panels.IInstallerData#putData
	 * (java.lang.String, java.lang.Object)
	 */
	//@Override
	public void putData(String aId, Object aValue) {
		put(aId, aValue);
	}
	
	/* (non-Javadoc)
	 * @see java.util.AbstractMap#toString()
	 */
	@Override
	public String toString(){
		return String.format("%s@%d",getClass().getName(), hashCode());
	}

}
