package org.cohorte.utilities.tests.impl;

import java.util.ArrayList;
import java.util.List;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.measurement.CXNamedTimer;
import org.psem2m.utilities.measurement.CXTimeMeters;

public class CTestMeasurment {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestMeasurment wTestMeasurment = new CTestMeasurment();
			wTestMeasurment.doTest();
			wTestMeasurment.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final IActivityLogger pLogger = CActivityLoggerBasicConsole
			.getInstance();

	/**
	 * 
	 */
	CTestMeasurment() {
		super();
	}

	/**
	 * 
	 */
	void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
		pLogger.close();
	}

	/**
	 * @throws JSONException
	 * 
	 */
	void doTest() throws JSONException {
		pLogger.logInfo(this, "doTest", "BEGIN");

		testCXTimer();
		testCXTimeMeters1();
		testCXTimeMeters2();

		pLogger.logInfo(this, "doTest", "END");
	}

	/**
	 * @param aSimulateDuration
	 * @return
	 */
	private Object simulateSomething(final long aSimulateDuration) {

		pLogger.logInfo(this, "simulateSomething", "SimulateDuration=[%d]",
				aSimulateDuration);

		try {
			Thread.sleep(aSimulateDuration);
		} catch (InterruptedException e) {
			// nothing...
		}
		return String.valueOf(Math.random());

	}

	/**
	 * Création d'un timeMeters avec deux "sub-timers" et ajout de deux
	 * informations
	 * 
	 * Dumps:
	 * 
	 * <pre>
	 * TEXT: Id=[TimeMeterName1] Main=[428,394] Timer1B=[250,967] Timer1C=[176,069] Info00=[ResultOfSomethingB:0.9509717801967676] Info01=[ResultOfSomethingC:0.40979726814154205]
	 * 
	 * CVS : Id	TimeMeterName1	Main	428,394	Timer1B	250,967	Timer1C	176,069	Info00	ResultOfSomethingB:0.9509717801967676	Info01	ResultOfSomethingC:0.40979726814154205
	 * 
	 * JSON: {"Main":"428.394","Info00":"ResultOfSomethingB:0.9509717801967676","Id":"TimeMeterName1","Timer1B":"250.967","Info01":"ResultOfSomethingC:0.40979726814154205","Timer1C":"176.069"}
	 * 
	 * XML : <TimeMeterName1><Main>428,394</Main><Timer1B>250,967</Timer1B><Timer1C>176,069</Timer1C><Info00>ResultOfSomethingB:0.9509717801967676</Info00><Info01>ResultOfSomethingC:0.40979726814154205</Info01></TimeMeterName1>
	 * </pre>
	 * 
	 * @throws JSONException
	 */
	void testCXTimeMeters1() throws JSONException {

		// ============ creation du CXTimeMeters (timer implicite "main"
		// démarré)

		String wTimeMeters1Name = "TimeMeterName1";
		pLogger.logInfo(this, "testCXTimeMeters1", "create TimeMeters=[%s]",
				wTimeMeters1Name);
		CXTimeMeters wTimeMeters1 = new CXTimeMeters(wTimeMeters1Name);

		// ============ ajout d'un premier sub-timer (eg. Timer1B)

		String wTimeMeters1Timer1BName = "Timer1B";
		pLogger.logInfo(this, "testCXTimeMeters1",
				"add NamedTimer=[%s] to the TimeMeters=[%s]",
				wTimeMeters1Timer1BName, wTimeMeters1Name);

		CXNamedTimer wTimeMeters1Timer1B = wTimeMeters1
				.addNewTimer(wTimeMeters1Timer1BName);

		// do smothing during 250 milliseconds...
		Object wResultB = simulateSomething(250);

		pLogger.logInfo(this, "testCXTimeMeters1", "stop the NamedTimer=[%s]",
				wTimeMeters1Timer1BName);
		wTimeMeters1Timer1B.stop();

		// ============ Ajout d'une première information

		wTimeMeters1.addInfos("ResultOfSomethingB:%s", wResultB);

		// ============ ajout d'un deuxième sub-timer (eg. Timer1C)

		String wTimeMeters1Timer1CName = "Timer1C";
		pLogger.logInfo(this, "testCXTimeMeters1",
				"add NamedTimer=[%s] to the TimeMeters=[%s]",
				wTimeMeters1Timer1CName, wTimeMeters1Name);

		CXNamedTimer wTimeMeters1Timer1C = wTimeMeters1
				.addNewTimer(wTimeMeters1Timer1CName);

		// do smothing during 175 milliseconds...
		Object wResultC = simulateSomething(175);

		pLogger.logInfo(this, "testCXTimeMeters1", "stop the NamedTimer=[%s]",
				wTimeMeters1Timer1CName);
		wTimeMeters1Timer1C.stop();

		// ============ Ajout d'une deuxième information

		wTimeMeters1.addInfos("ResultOfSomethingC:%s", wResultC);

		// ============ récupération des infos du CXTimeMeters (arrêt implicite
		// de tous timers)

		pLogger.logInfo(this, "testCXTimeMeters1", "TEXT: %s",
				wTimeMeters1.toString());
		pLogger.logInfo(this, "testCXTimeMeters1", "CVS : %s",
				wTimeMeters1.toCvs());
		pLogger.logInfo(this, "testCXTimeMeters1", "JSON: %s",
				wTimeMeters1.toJson());

		pLogger.logInfo(this, "testCXTimeMeters1", "XML : %s",
				wTimeMeters1.toXml());

	}

	/**
	 * @throws JSONException
	 */
	void testCXTimeMeters2() throws JSONException {
		String wTimeMeters2Name = "TimeMeterName2";

		List<String> wMemoryBuffList = new ArrayList<String>();

		for (int wIdx = 0; wIdx < 10; wIdx++) {

			pLogger.logInfo(this, "testCXTimeMeters2",
					"create TimeMeters=[%s]", wTimeMeters2Name);
			CXTimeMeters wTimeMeters2 = new CXTimeMeters(wTimeMeters2Name,
					CXTimeMeters.WITH_FREEMEM, "myMain");

			wTimeMeters2.addInfos("cycle %d", wIdx);

			// do smothing during 175 milliseconds...
			simulateSomething(175);

			String wMemoryBuff = CXStringUtils.strFromChar('.',
					1024 * 512 * wIdx);
			wMemoryBuffList.add(wMemoryBuff);
			wTimeMeters2.addInfos("MemoryBuff size %d", wMemoryBuff.length());

			pLogger.logInfo(this, "testCXTimeMeters2", "TEXT: %s",
					wTimeMeters2.toString());
		}
	}

	/**
	 * 
	 */
	void testCXTimer() {

		testCXTimerFormat(75);
		testCXTimerFormat(175);
		testCXTimerFormat(500);
		testCXTimerFormat(200);

		CXTimer wTimer = CXTimer.newStartedTimer();

		// do smothing during 250 milliseconds...
		simulateSomething(250);

		pLogger.logInfo(this, "testCXTimer",
				"CXTimer DurationStrMicroSec : [%s]",
				wTimer.getDurationStrMicroSec());

		pLogger.logInfo(this, "testCXTimer",
				"CXTimer DurationStrMilliSec : [%s]",
				wTimer.getDurationStrMilliSec());
	}

	/**
	 * @param aDuration
	 */
	void testCXTimerFormat(final long aDuration) {

		Long wNanoStart = System.nanoTime();

		simulateSomething(aDuration);

		Long wNanoDelta = System.nanoTime() - wNanoStart;

		pLogger.logInfo(this, "testCXTimerFormat",
				"[%5d] milliseconds : ToMicroSec : [%s]", aDuration,
				CXTimer.nanoSecToMicroSecStr(wNanoDelta));
		pLogger.logInfo(this, "testCXTimerFormat",
				"[%5d] milliseconds : ToMilliSec : [%s]", aDuration,
				CXTimer.nanoSecToMilliSecStr(wNanoDelta));
	}
}
