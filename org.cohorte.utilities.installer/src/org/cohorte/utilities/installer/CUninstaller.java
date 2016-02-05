package org.cohorte.utilities.installer;

public class CUninstaller extends CInstaller {
	
	/**
	 * Constructor.
	 * 
	 * @throws Exception
	 */
	public CUninstaller() throws Exception {
		this("Uninstaller");
	}
	
	public CUninstaller(String aProductName) throws Exception {		
		// Create Uninstaller singleton instance with logger.
		super(aProductName, String.format("%s_%s", (aProductName!=null?aProductName:"Uninstaller"),
				CUninstaller.class.getSimpleName()));
		
	}

}
