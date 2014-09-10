package org.psem2m.utilities.measurement;

import org.psem2m.utilities.CXTimer;

/**
 * instrumentation tool
 * 
 * @author ogattaz
 * 
 */
public class CXNamedTimer {

	/** the name of the timer */
	private final String pName;

	private final CXTimer pTimer = CXTimer.newStartedTimer();

	/**
	 * @param aName
	 *            the name of the timer
	 */
	CXNamedTimer(final String aName) {
		super();
		pName = aName.replace(' ', '_');
	}

	/**
	 * @return a formated string ("%6.3f") containing the duration in in
	 *         milliseconds with microesconds (eg. "175,044" milliseconds)
	 */
	public String getDurationStrMicroSec() {
		return getTimer().getDurationStrMicroSec();
	}

	/**
	 * @return the name of the timer
	 */
	public String getName() {
		return pName;
	}

	/**
	 * @return the instance of CXTimer
	 */
	public CXTimer getTimer() {
		return pTimer;
	}

	/**
	 * @return
	 */
	public boolean isStopped() {
		return pTimer.isStopped();
	}

	/**
	 * stop the curent timer
	 */
	public void stop() {
		if (!isStopped()) {
			pTimer.stop();
		}
	}

	/**
	 * @return
	 */
	public String toCsv(final String aSeparator) {
		stop();
		return String.format("%s%s%s", pName, aSeparator,
				getDurationStrMicroSec());
	}

	/**
	 * @return
	 */
	public String toJsonAttribute() {
		stop();
		return String.format("%s:%s", pName,
				getDurationStrMicroSec().replace(',', '.'));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		stop();
		return String.format("%s=[%s]", pName, getDurationStrMicroSec());
	}

	/**
	 * @return
	 */
	public String toXml() {
		stop();
		return String.format("<%1$s>%2$s</%1$s>", pName,
				getDurationStrMicroSec());
	}

	/**
	 * @return
	 */
	public String toXmlAttribute() {
		stop();
		return String.format("%s=\"%s\"", pName, getDurationStrMicroSec());
	}
}