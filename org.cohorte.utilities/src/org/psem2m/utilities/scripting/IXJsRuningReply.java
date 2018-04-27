package org.psem2m.utilities.scripting;

import org.psem2m.utilities.IXDescriber;

/**
 * @author ogattaz
 *
 */
public interface IXJsRuningReply extends IXDescriber {

	/**
	 * ogat - v1.4 - return handle duration and eval duration
	 *
	 * @return
	 */
	public String getEvalDuration();

	public Object getScriptResult();

	/**
	 * @return
	 */
	public String getTimerInfo();

	/**
	 * @return
	 */
	public boolean isEndOK();

}
