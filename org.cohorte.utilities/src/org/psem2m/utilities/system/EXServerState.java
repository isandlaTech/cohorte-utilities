package org.psem2m.utilities.system;

/**
 * @author ogattaz
 * 
 */
public enum EXServerState {
	EXCEPTION, INSTANCIATED, STARTED, STARTING, STOPPED, STOPPING, TIMEOUT;

	/**
	 * @param aText
	 * @return
	 */
	public static EXServerState stdoutToState(final String aText) {

		if (aText != null && !aText.isEmpty()) {

			String wUpperText = aText.toUpperCase();

			for (EXServerState wState : EXServerState.values()) {
				if (wUpperText.startsWith(wState.name())) {
					return wState;
				}
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	boolean isOnError() {
		return this == EXCEPTION;
	}

	/**
	 * @return
	 */
	boolean isStarted() {
		return this == STARTED;
	}

	/**
	 * @return
	 */
	boolean isStopped() {
		return this == STOPPED;
	}

}
