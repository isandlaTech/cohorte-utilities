package org.cohorte.utilities.picosoc;

import java.io.File;
import java.util.logging.Level;

import org.psem2m.utilities.logging.EActivityLogColumn;
import org.psem2m.utilities.logging.IActivityFormater;

/**
 * @author ogattaz
 * 
 * 
 */
public abstract class CAbstractActivityLoggerConfigurator extends
		CAbstractComponentBase implements ISvcActivityLoggerConfigurator {

	protected File pDirLogs = null;
	protected int pFileLimit = 10 * 1024 * 1024;
	protected boolean pIsMultiline = false;
	protected Level pLevel = Level.OFF;
	protected EActivityLogColumn[] pLineDef = IActivityFormater.LINE_SHORT;
	protected String pLoggerName = null;
	protected int pNbFile = 10;

	/**
	 * 
	 */
	public CAbstractActivityLoggerConfigurator() {
		super();

		// register this instance as two services
		registerMeAsService(ISvcLoggerConfigurator.class);
		registerMeAsService(ISvcActivityLoggerConfigurator.class);
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
		this();
		pDirLogs = aDirLogs;
		pLevel = aLevel;
		pLoggerName = aLoggerName;
		pFileLimit = aFileLimit;
		pNbFile = aNbFile;
		pLineDef = aLineDef;
		pIsMultiline = aIsMultiline;
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

}
