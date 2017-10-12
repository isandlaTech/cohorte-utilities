package org.cohorte.utilities.installer;


/**
 * @author ogattaz
 *
 */
public interface IInstaller {
	
	/**
	 * 
	 */
	void finish();
	
	/**
	 * @param aIzPackInstallData the IzPack InstallData instance
	 */
	void setIzPackInstallData( final Object aIzPackInstallData);
	

}
