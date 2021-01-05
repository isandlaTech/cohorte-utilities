package org.cohorte.utilities.picosoc;

import java.io.File;
import java.util.logging.Level;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.IXDescriber;
import org.psem2m.utilities.logging.EActivityLogColumn;
import org.psem2m.utilities.logging.IActivityFormater;

/**
 * @author ogattaz
 * 
 * 
 */
public abstract class CAbstractActivityLoggerConfigurator extends
		CAbstractComponentBase implements ISvcActivityLoggerConfigurator ,IXDescriber{

	protected File pDirLogs = null;
	protected int pFileLimit = 10 * 1024 * 1024;
	protected boolean pIsMultiline = false;
	protected Level pLevel = Level.OFF;
	protected EActivityLogColumn[] pLineDef = IActivityFormater.LINE_SHORT;
	protected String pLoggerName = null;
	protected final String pLoggerAlias;
	protected int pNbFile = 10;

	
	/**
	 * 
	 */
	public CAbstractActivityLoggerConfigurator(  ) {
		this(CComponentLogger.NO_LOGGER_ALIAS);
	}
	
	/**
	 * @param aDirLogs
	 * @param aLevel
	 * @param aLoggerName
	 * @param aFileLimit
	 * @param aNbFile
	 * @param aLineDef
	 * @param aIsMultiline
	 */
	public CAbstractActivityLoggerConfigurator(final File aDirLogs,
			final Level aLevel, final String aLoggerName, final int aFileLimit,
			final int aNbFile, final EActivityLogColumn[] aLineDef,
			final boolean aIsMultiline) {
		this(aLoggerName);
		pDirLogs = aDirLogs;
		pLevel = aLevel;
		pFileLimit = aFileLimit;
		pNbFile = aNbFile;
		pLineDef = aLineDef;
		pIsMultiline = aIsMultiline;
	}

	/**
	 * @param aLoggerAlias
	 */
	public CAbstractActivityLoggerConfigurator(final String aLoggerAlias) {
		super();
		pLoggerAlias = aLoggerAlias;
		
		CServiceProperties wProps= (aLoggerAlias==null)?null:CServiceProperties.newProps(CComponentLogger.LOGGER_ALIAS,aLoggerAlias);

		// register this instance as two services
		registerMeAsService(ISvcLoggerConfigurator.class,wProps);
		registerMeAsService(ISvcActivityLoggerConfigurator.class,wProps);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.IXDescriber#addDescriptionInBuffer(java.lang.Appendable
	 * )
	 */
	@Override
	public Appendable addDescriptionInBuffer(final Appendable aBuffer) {
		
		return CXStringUtils.appendFormatStrInBuff(aBuffer, "name=[%s] FileSizeLimit=[%s] NbFile=[%s] isMultiline=[%s] DirLogs=[%s]",
				//
				getLoggerName(),
				//
				getFileLimit(),
				//
				getNbFile(),
				//
				isMultiline(),
				//
				pDirLogs);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.ISvcLoggerConfigurator#getDirLogs()
	 */
	@Override
	public File getDirLogs() throws Exception {
		return pDirLogs;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.ISvcActivityLoggerConfigurator#getFileLimit
	 * ()
	 */
	@Override
	public int getFileLimit() {
		return pFileLimit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.ISvcLoggerConfigurator#getLevel()
	 */
	@Override
	public Level getLevel() {
		return pLevel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.ISvcActivityLoggerConfigurator#getLineDef()
	 */
	@Override
	public EActivityLogColumn[] getLineDef() {
		return pLineDef;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.ISvcLoggerConfigurator#getLoggerName()
	 */
	@Override
	public String getLoggerName() {
		return pLoggerName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.ISvcActivityLoggerConfigurator#getNbFile()
	 */
	@Override
	public int getNbFile() {
		return pNbFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.ISvcActivityLoggerConfigurator#isMultiline
	 * ()
	 */
	@Override
	public boolean isMultiline() {
		return pIsMultiline;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.IXDescriber#toDescription()
	 */
	@Override
	public String toDescription() {
		return addDescriptionInBuffer(
				new StringBuilder(128)).toString();
	}
	

	

}
