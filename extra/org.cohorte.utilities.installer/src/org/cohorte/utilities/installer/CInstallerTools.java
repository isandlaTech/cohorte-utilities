package org.cohorte.utilities.installer;

import java.util.logging.Level;

import org.cohorte.utilities.picosoc.CServicReference;
import org.cohorte.utilities.picosoc.CServicesRegistry;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public class CInstallerTools {

	/**
	 * @param aGuiId
	 * @param aSuffixe
	 * @return
	 */
	public static String buildJComponentName(Object aGuiId, String aSuffixe) {
		return String.format("%s.%s", aGuiId.toString(), aSuffixe);

	}

	/**
	 * @param aWho
	 * @param aSuffixe
	 * @return
	 */
	public static String buildMessageId(Object aWho, String aSuffixe) {
		return String
				.format("%s.%s", aWho.getClass().getSimpleName(), aSuffixe);
	}

	/**
	 * @param aSpecification
	 * @return
	 */
	public static <T> T getService(Class<? extends T> aSpecification) throws Exception {
		try {
			return CServicesRegistry.getRegistry()
					.getServiceRef(aSpecification).getService();
		} catch (Exception e) {
			CInstallerLogger.logInFile(Level.SEVERE, CInstallerTools.class,
					"getService", "unable to get service [‰s] : %s",
					IActivityLogger.class.getSimpleName(), e);
			throw e;
		}
	}

	/**
	 * @return
	 */
	public static IInstallerData getServiceInstallerData(){
		
		IInstallerData wInstallerData=null;
		try {
			wInstallerData = getService(IInstallerData.class);
		} catch (Exception e) {
			CInstallerLogger.logInFile(Level.SEVERE, CInstallerTools.class,
					"getServiceInstallerdata",
					"unable to retreive the service IInstallerData: %s", e);
			}
		return wInstallerData;
	}
	
	/**
	 * @return the registered Logger service or an instance of
	 *         CActivityLoggerNull
	 */
	public static IActivityLogger getServiceLogger() {
		IActivityLogger wLogger = CActivityLoggerNull.getInstance();
		try {
			wLogger = getService(IActivityLogger.class);

		} catch (Exception e) {
			CInstallerLogger.logInFile(Level.SEVERE, CInstallerTools.class,
					"getServiceLogger",
					"unable to retreive the service IActivityLogger: %s", e);
		}
		return wLogger;
	}
	
	
	
	

	/**
	 * @param aSpecification
	 * @param aService
	 */
	public static <T> CServicReference<T> registerService(
			Class<? extends T> aSpecification, T aService) throws Exception {

		return CServicesRegistry.getRegistry().registerService(aSpecification,
				aService);
	}

	/**
	 * 
	 */
	private CInstallerTools() {
	}

}
