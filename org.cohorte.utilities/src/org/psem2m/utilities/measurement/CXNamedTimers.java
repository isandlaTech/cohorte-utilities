package org.psem2m.utilities.measurement;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author ogattaz
 * 
 */
public class CXNamedTimers extends LinkedList<CXNamedTimer> {

	private static final long serialVersionUID = -8458740441596449508L;

	/**
	 * 
	 */
	CXNamedTimers() {
		super();
	}

	/**
	 * @return
	 */
	public String getNamesInStr() {
		return getNamesInStr(CXTimeMeters.SEPARATOR_TAB);
	}

	/**
	 * @param aSeparator
	 * @return
	 */
	public String getNamesInStr(final String aSeparator) {
		StringBuilder wSB = new StringBuilder();
		for (CXNamedTimer wNamedTimer : this) {
			wSB.append(aSeparator);
			wSB.append(wNamedTimer.getName());
		}
		return wSB.toString();
	}

	/**
	 * Stops all the timers
	 */
	public void stopAll() {
		for (CXNamedTimer wNamedTimer : this) {
			wNamedTimer.stop();
		}
	}

	/**
	 * @param aSeparator
	 * @return
	 */
	public String toCsv(final String aSeparator) {

		StringBuilder wSB = new StringBuilder();
		for (CXNamedTimer wNamedTimer : this) {
			wSB.append(aSeparator);
			wSB.append(wNamedTimer.toCsv(aSeparator));
		}
		return wSB.toString();
	}

	/**
	 * @return
	 */
	public String toCvs() {
		return toCsv(CXTimeMeters.SEPARATOR_TAB);
	}

	public String toJsonAttributes() {
		StringBuilder wSB = new StringBuilder();
		for (CXNamedTimer wNamedTimer : this) {
			wSB.append(' ');
			wSB.append(wNamedTimer.toJsonAttribute());
		}
		return wSB.toString();
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<String, Object> toJsonAttributesHashMap() {
		HashMap<String, Object> wJsonAttributesHashMap = new HashMap<String, Object>();
		for (CXNamedTimer wNamedTimer : this) {
			wNamedTimer.stop();
			wJsonAttributesHashMap.put(wNamedTimer.getName(), wNamedTimer
					.getDurationStrMicroSec().replace(',', '.'));
		}

		return wJsonAttributesHashMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder wSB = new StringBuilder();
		for (CXNamedTimer wNamedTimer : this) {
			wSB.append(' ');
			wSB.append(wNamedTimer.toString());
		}
		return wSB.toString();
	}

	/**
	 * @return a string containing the set of timers as xml attributes
	 */
	public String toXml() {
		StringBuilder wSB = new StringBuilder();
		for (CXNamedTimer wNamedTimer : this) {

			wSB.append(wNamedTimer.toXml());
		}
		return wSB.toString();
	}

	/**
	 * @return a string containing the set of timers as xml attributes
	 */
	public String toXmlAttributes() {
		StringBuilder wSB = new StringBuilder();
		for (CXNamedTimer wNamedTimer : this) {
			wSB.append(' ');
			wSB.append(wNamedTimer.toXmlAttribute());
		}
		return wSB.toString();
	}

}