package org.cohorte.utilities.installer;

import org.cohorte.utilities.installer.CInstallerBase;
import org.cohorte.utilities.installer.IInstaller;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXStringUtils;

import com.izforge.izpack.api.data.Info;
import com.izforge.izpack.api.data.InstallData;

/**
 * Installer Singleton object.
 *
 * @authors olivier gattaz, Bassem Debbabi
 * 
 */
public class CInstaller extends CInstallerBase implements IInstaller,
		IConstants {

	protected String pProductName = "Installer";
	
	/**
	 * Installer installData.
	 */
	private InstallData pIzPackInstallData;

	/**
	 * Constructor.
	 *
	 * @throws Exception
	 */
	public CInstaller(String aProductName) throws Exception {		
		this(aProductName, String.format("%s_%s", aProductName,
				CInstaller.class.getSimpleName()));
	}
	
	public CInstaller(String aProductName, String aLoggerName) throws Exception {		
		super(aLoggerName);
		if (aProductName != null) {
			pProductName = aProductName;
		}
		logRegistery();
		pLogger.logInfo(this, "<ini>", "JAVA CONTEXT \n%s",
				CXJvmUtils.getJavaContext());
	}
	
	/**
	 * Constructor.
	 *
	 * @throws Exception
	 */
	public CInstaller() throws Exception {
		this("Installer");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.installer.CInstallerBase#dumpInstallDataInfos()
	 */
	@Override
	public String dumpInstallDataInfos() {
		boolean wIsInstallDataAvailable = pIzPackInstallData != null;
		boolean wIsInstallInfosvailable = wIsInstallDataAvailable
				&& pIzPackInstallData.getInfo() != null;

		if (!wIsInstallInfosvailable) {
			String wMess = String.format(
					"IsInstallDataAvailable=[%b] IsInstallInfosvailable=[%b]",
					wIsInstallDataAvailable, wIsInstallInfosvailable);
			pLogger.logInfo(this, "dumpInstallDataInfos", wMess);
			return wMess;
		}
		StringBuilder wSB = new StringBuilder();

		Info wInfos = pIzPackInstallData.getInfo();

		CXStringUtils.appendFormatStrInBuff(wSB, "\nAppName=[%s]",
				wInfos.getAppName());
		CXStringUtils.appendFormatStrInBuff(wSB, "\nAppVersion=[%s]",
				wInfos.getAppVersion());
		CXStringUtils.appendFormatStrInBuff(wSB, "\nAuthors=[%s]",
				wInfos.getAuthors());
		CXStringUtils.appendFormatStrInBuff(wSB, "\nInstallationSubPath=[%s]",
				wInfos.getInstallationSubPath());
		CXStringUtils.appendFormatStrInBuff(wSB, "\nInstallerBase=[%s]",
				wInfos.getInstallerBase());
		CXStringUtils.appendFormatStrInBuff(wSB, "\nJavaVersion=[%s]",
				wInfos.getJavaVersion());
		return wSB.toString();
	}

	/**
	 * When the installer stops, we move the log file created in the initial
	 * user dir of the installer into the target dir
	 *
	 * @see org.cohorte.utilities.installer.IInstaller#finishInstallation
	 *      (com.izforge.izpack.installer.data.GUIInstallData)
	 */
	@Override
	public void finish() {

		if (pIzPackInstallData != null) {
			pLogger.logInfo(this, "finish", "InstallPath=[%s]",
					pIzPackInstallData.getInstallPath());

			pLogger.logInfo(this, "finish", "isInstallSuccess=[%s]",
					pIzPackInstallData.isInstallSuccess());
		} else {
			pLogger.logInfo(this, "finish", "no InstallData available");

		}

		super.finish();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.cohorte.utilities.installer.CInstallerBase#getInstalledAppName()
	 */
	@Override
	public String getInstalledAppName() {
		
		//MOD_OG_20160712  Protection against null pointer exception during exit hook
		return (pIzPackInstallData!=null)?pIzPackInstallData.getInfo().getAppName():null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.installer.CInstallerBase#getIzPackInstallPath()
	 */
	@Override
	public String getIzPackInstallPath() {
		
		//MOD_OG_20160712  Protection against null pointer exception during exit hook
		return (pIzPackInstallData!=null)?pIzPackInstallData.getInstallPath():null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.installer.CInstallerBase#getUninstallerClassName()
	 */
	@Override
	public String getUninstallerClassName() {
		return CUninstaller.class.getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.cohorte.utilities.installer.CInstallerBase#setIzPackInstallData()
	 */
	//@Override
	public void setIzPackInstallData(final Object aInstallData) {
		try {
			pIzPackInstallData = (InstallData) aInstallData;
			for (String wLineDump : dumpInstallDataInfos().split("\n")) {
				pLogger.logInfo(this, "setIzPackInstallData", "Infos: %s",
						wLineDump);
			}
		} catch (Exception e) {
			pLogger.logSevere(this, "setIzPackInstallData", "ERROR: %s",
					CXException.eInString(e));
		}
	}

}
