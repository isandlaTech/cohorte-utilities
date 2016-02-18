package org.cohorte.utilities.installer.system;

import org.psem2m.utilities.logging.IActivityLoggerBase;
import org.psem2m.utilities.system.CXOSCommand;
import org.psem2m.utilities.system.EXCommandState;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

/**
 * Overrides CXOSCommand class.
 * It allows writing Std Output (err) to ProcessPanel handler.
 * 
 * @author bdebbabi
 *
 */
public class CXOSInstallerCommand extends CXOSCommand {

	/**
	 * izPack Process Handler.
	 * Allows among others to write to standard izpack output (or emit error messages, etc).
	 */
	private AbstractUIProcessHandler pHandler;
	
	/**
	 * @param aTracer
	 * @param aCommandLine
	 */
	public CXOSInstallerCommand(final IActivityLoggerBase aTracer, AbstractUIProcessHandler aHandler, 
			final String... aCommandLine) {
		super(aTracer, EXCommandState.CMD_RUN_OK, aCommandLine);
		this.pHandler = aHandler;
	}
	
	@Override
	public int consumeStdOutputErrLine(String aLine) {
		if (pHandler != null) {
			pHandler.logOutput(aLine, true);
		}
		return super.consumeStdOutputErrLine(aLine);
	}
	
	@Override
	public int consumeStdOutputLine(String aLine) {
		if (pHandler != null) {
			pHandler.logOutput(aLine, false);
		}
		return super.consumeStdOutputLine(aLine);
	}
}
