package test.org.cohorte.utilities.picosoc.loggers;

import java.io.File;
import java.util.logging.Level;

import org.cohorte.utilities.picosoc.CAbstractActivityLoggerConfigurator;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.EActivityLogColumn;
import org.psem2m.utilities.logging.IActivityFormater;

/**
 * #48
 * 
 * @author ogattaz
 *
 */
public class CLoggerConfigurator extends CAbstractActivityLoggerConfigurator {

	/**
	 * @param aLoggerAlias
	 * @throws Exception
	 */
	CLoggerConfigurator(final String aLoggerAlias) throws Exception {
		super(aLoggerAlias);

		// for the test, use the Alias as the logger name
		pLoggerName = (aLoggerAlias != null) ? aLoggerAlias : "LOGGER";

		// init
		if (!getDirLogs().exists()) {
			((CXFileDir) getDirLogs()).createHierarchy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.ISvcLoggerConfigurator#getDirLogs()
	 */
	@Override
	public File getDirLogs() throws Exception {
		return new CXFileDir("./files", pLoggerName);
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
		return 20 * 1024 * 1024;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.picosoc.ISvcLoggerConfigurator#getLevel()
	 */
	@Override
	public Level getLevel() {
		return Level.FINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.ISvcActivityLoggerConfigurator#getLineDef()
	 */
	@Override
	public EActivityLogColumn[] getLineDef() {
		return IActivityFormater.LINE_SHORT;
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
		return 15;
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
		return IActivityFormater.MULTILINES_TEXT;
	}
}
