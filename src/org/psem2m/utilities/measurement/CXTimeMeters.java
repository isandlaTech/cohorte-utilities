package org.psem2m.utilities.measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;

/**
 * 
 * 
 * TEXT:
 * 
 * <pre>
 * Id=[TimeMeterName1] Main=[429,455] Timer1B=[251,226] Timer1C=[176,954] Info00=[ResultOfSomethingB:0.4310281000591685] Info01=[ResultOfSomethingC:0.46446065255875946]
 * </pre>
 * 
 * CVS (separated by a tab character)
 * 
 * <pre>
 * Id	TimeMeterName1	Main	429,455	Timer1B	251,226	Timer1C	176,954	Info00	ResultOfSomethingB:0.4310281000591685	Info01	ResultOfSomethingC:0.46446065255875946
 * </pre>
 * 
 * JSON:
 * 
 * <pre>
 * {"Main":"429.455","Info00":"ResultOfSomethingB:0.4310281000591685","Id":"TimeMeterName1","Timer1B":"251.226","Info01":"ResultOfSomethingC:0.46446065255875946","Timer1C":"176.954"}
 * </pre>
 * 
 * XML :
 * 
 * <pre>
 * <TimeMeterName1><Main>429,455</Main><Timer1B>251,226</Timer1B><Timer1C>176,954</Timer1C><Info00>ResultOfSomethingB:0.4310281000591685</Info00><Info01>ResultOfSomethingC:0.46446065255875946</Info01></TimeMeterName1>
 * </pre>
 * 
 * @author ogattaz
 * 
 */
public class CXTimeMeters {

	private static final String COL_NAME_FREE = "FreeMem";

	private static final String COL_NAME_ID = "Id";

	private static final List<String> NO_INFO = new ArrayList<String>();

	public static final String SEPARATOR_TAB = "\t";

	private static final String TIMER_NAME_MAIN = "Main";

	public static final boolean WITH_FREEMEM = true;

	private long pFreeMemory = -1;

	private List<String> pInfos = NO_INFO;

	private final CXNamedTimer pMainTimer;

	/**
	 * teh flag to return or not the amount of free memory in the Java Virtual
	 * Machine.
	 */
	private final boolean pMustMeasureFreeMem;

	private final CXNamedTimers pNamedTimers = new CXNamedTimers();

	private String pTimeMetersId;

	/**
	 * @param aTimeMetersId
	 *            the id of this TimeMeters instance
	 */
	public CXTimeMeters(final String aId) {
		this(aId, !WITH_FREEMEM);
	}

	/**
	 * @param aTimeMetersId
	 *            the id of this TimeMeters instance
	 * @param aMustMeasureFreeMem
	 *            measure the free memory when the timer will be stopped
	 */
	public CXTimeMeters(final String aTimeMetersId,
			final boolean aMustMeasureFreeMem) {
		this(aTimeMetersId, aMustMeasureFreeMem, TIMER_NAME_MAIN);
	}

	/**
	 * @param aTimeMetersId
	 *            the id of this TimeMeters instance
	 * @param aMustMeasureFreeMem
	 *            measure the free memory when the timer will be stopped
	 * @param aMainTimerName
	 *            the id of the main timer of this TimeMeters
	 */
	public CXTimeMeters(final String aTimeMetersId,
			final boolean aMustMeasureFreeMem, final String aMainTimerName) {
		super();
		pTimeMetersId = aTimeMetersId;
		pMustMeasureFreeMem = aMustMeasureFreeMem;
		if (mustMeasureFreeMem()) {
			Runtime.getRuntime().gc();
			pFreeMemory = Runtime.getRuntime().freeMemory();
		}
		pMainTimer = addNewTimer(aMainTimerName);
	}

	/**
	 * @param aTimeMetersId
	 *            the id of this TimeMeters instance
	 * @param aMainTimerName
	 *            the id of the main timer of this TimeMeters
	 */
	public CXTimeMeters(final String aTimeMetersId, final String aMainTimerName) {
		this(aTimeMetersId, !WITH_FREEMEM, aMainTimerName);
	}

	/**
	 * Adds information to this TimeMeters instance. These informations will be
	 * add to the final report
	 * 
	 * @param aFormat
	 *            the format used by the java string formatter
	 * @param aInfos
	 *            an array of object used by the java string formatter
	 */
	public void addInfos(final String aFormat, final Object... aInfos) {
		if (NO_INFO.equals(pInfos)) {
			pInfos = new ArrayList<String>();
		}
		pInfos.add(String.format(aFormat, aInfos));
	}

	/**
	 * @param aName
	 * @return a new instance of named timer
	 */
	public CXNamedTimer addNewTimer(final String aName) {
		CXNamedTimer wNamedTimer = new CXNamedTimer(aName);
		pNamedTimers.add(wNamedTimer);
		return wNamedTimer;
	}

	/**
	 * @return
	 */
	public long getFreeMemory() {
		return pFreeMemory;
	}

	/**
	 * @return the identifier of the instance
	 */
	public String getId() {
		return pTimeMetersId;
	}

	/**
	 * @return
	 */
	public List<String> getInfos() {
		return pInfos;
	}

	/**
	 * @return a formated string ("%6.3f") containing the duration in in
	 *         milliseconds with microesconds (eg. "175,044" milliseconds)
	 */
	public String getMainDurationStrMicroSec() {
		return getMainTimer().getDurationStrMicroSec();
	}

	/**
	 * @return a formated string ("%06d") containing the duration in
	 *         milliseconds (eg. "000256" milliseconds)
	 */
	public String getMainDurationStrMilliSec() {
		return getMainTimer().getDurationStrMilliSec();
	}

	/**
	 * @return the instance of CXTimer
	 */
	public CXTimer getMainTimer() {
		return pMainTimer.getTimer();
	}

	/**
	 * @return the list of timers
	 */
	public List<CXNamedTimer> getNamedTimers() {
		return pNamedTimers;
	}

	/**
	 * @return a string containing the set of the names of the timeMeters
	 *         sperarated by a "tab" character
	 */
	public String getNamesInStr() {
		return getNamesInStr(CXTimeMeters.SEPARATOR_TAB);
	}

	/**
	 * @param aSeparator
	 * @return a string containing the set of the names of this TimeMeters
	 *         sperarated by "aSeparator"
	 */
	public String getNamesInStr(final String aSeparator) {
		return String.format("%s%s%s%s", COL_NAME_ID, aSeparator,
				COL_NAME_FREE, pNamedTimers.getNamesInStr(aSeparator),
				aSeparator);
	}

	/**
	 * @return true if some info is stored in this TimeMeters instance
	 */
	public boolean hasInfos() {
		return getInfos().size() > 0;
	}

	/**
	 * eg.
	 * 
	 * @return true id the amount of the free memory will be add in the report
	 *         of this TimeMeters
	 */
	protected boolean mustMeasureFreeMem() {
		return pMustMeasureFreeMem;
	}

	/**
	 * @param aId
	 *            the new identifier of the instance
	 */
	public void setId(final String aId) {
		pTimeMetersId = aId;
	}

	/**
	 * Stops all the named timers and get free memory amount if wanted
	 */
	public void stopAll() {
		pNamedTimers.stopAll();
		if (mustMeasureFreeMem()) {
			Runtime.getRuntime().gc();
			pFreeMemory = Runtime.getRuntime().freeMemory();
		}
	}

	/**
	 * @return
	 */
	public String toCvs() {
		return toCvs(CXTimeMeters.SEPARATOR_TAB);
	}

	/**
	 * @param aSeparator
	 * @return
	 */
	public String toCvs(final String aSeparator) {
		stopAll();

		StringBuilder wSB = new StringBuilder();

		wSB.append(String.format("%2$s%1$s%3$s", aSeparator, COL_NAME_ID,
				getId()));

		if (mustMeasureFreeMem()) {
			wSB.append(String.format("%1$s%2$s%1$s%3$s", aSeparator,
					COL_NAME_FREE, pFreeMemory));
		}

		wSB.append(pNamedTimers.toCsv(aSeparator));

		if (hasInfos()) {
			int wMax = getInfos().size();
			for (int wIdx = 0; wIdx < wMax; wIdx++) {
				wSB.append(String.format("%1$sInfo%2$02d%1$s%3$s", aSeparator,
						wIdx, getInfos().get(wIdx)));
			}
		}

		return wSB.toString();
	}

	/**
	 * @return
	 * @throws JSONException
	 */
	public JSONObject toJson() throws JSONException {
		stopAll();
		Map<String, Object> wJsonAttributesHashMap = pNamedTimers
				.toJsonAttributesHashMap();
		JSONObject wJsonObj = new JSONObject(wJsonAttributesHashMap);
		wJsonObj.accumulate(COL_NAME_ID, getId());
		if (mustMeasureFreeMem()) {
			wJsonObj.accumulate(COL_NAME_FREE, pFreeMemory);
		}
		if (hasInfos()) {
			int wMax = getInfos().size();
			for (int wIdx = 0; wIdx < wMax; wIdx++) {
				wJsonObj.accumulate(String.format("Info%02d", wIdx), getInfos()
						.get(wIdx));
			}
		}
		return wJsonObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		stopAll();
		StringBuilder wSB = new StringBuilder();

		wSB.append(String.format("%s=[%s]", COL_NAME_ID, getId()));

		if (mustMeasureFreeMem()) {
			wSB.append(String.format(" %s=[%d]", COL_NAME_FREE, pFreeMemory));
		}

		wSB.append(pNamedTimers.toString());

		if (hasInfos()) {
			int wMax = getInfos().size();
			for (int wIdx = 0; wIdx < wMax; wIdx++) {
				wSB.append(String.format(" Info%02d=[%s]", wIdx, getInfos()
						.get(wIdx)));
			}
		}

		return wSB.toString();
	}

	/**
	 * @return
	 */
	public String toXml() {
		StringBuilder wSB = new StringBuilder();

		wSB.append(String.format("<%s>", getId()));

		if (mustMeasureFreeMem()) {
			wSB.append(String.format("<%1$s>%2$s</%1$s>", COL_NAME_FREE,
					pFreeMemory));
		}

		wSB.append(pNamedTimers.toXml());

		if (hasInfos()) {
			int wMax = getInfos().size();
			for (int wIdx = 0; wIdx < wMax; wIdx++) {
				wSB.append(String.format("<Info%1$02d>%2$s</Info%1$02d>", wIdx,
						getInfos().get(wIdx)));
			}
		}

		wSB.append(String.format("</%s>", getId()));

		return wSB.toString();
	}

}
