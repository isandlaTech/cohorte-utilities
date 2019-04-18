package org.cohorte.utilities.junit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.junit.Test;
import org.psem2m.utilities.CXException;

/**
 * @author ogattaz
 * @since 1.1.0 (#32)
 *
 */
class CTestDefinition {

	private final Annotation pAnnotation;

	private final Map<String, CTestRunning> pMapTestRunnings = new LinkedHashMap<>();
	private final Method pMethod;
	private int pNbFinishedOK = 0;
	private int pNbStarted = 0;
	private final int pTestNumber;

	/**
	 * @param aMethod
	 */
	/**
	 * @param aMethod
	 * @param aAnnotation
	 * @param aTestNumber
	 *            the number of the test [1..n]
	 */
	CTestDefinition(final Method aMethod, final Annotation aAnnotation, final int aTestNumber) {
		super();
		pMethod = aMethod;
		pAnnotation = aAnnotation;
		pTestNumber = aTestNumber;
	}

	/**
	 * @return
	 */
	public String getAnnotationName() {
		return pAnnotation.annotationType().getSimpleName();
	}

	/**
	 * @return
	 */
	public String getName() {
		return pMethod.getName();
	}

	int getNbFinishedOK() {
		return pNbFinishedOK;
	}

	/**
	 * @return
	 */
	int getNbStarted() {
		return pNbStarted;
	}

	/**
	 * @return
	 */
	public int getTestNumber() {
		return pTestNumber;
	}

	/**
	 * @param aOK
	 */
	public CTestRunning setTestBegin(final String aRunningId, final boolean aBegin) {
		pNbStarted++;

		CTestRunning wTestRunning = new CTestRunning(aRunningId);
		CTestRunning wOldTestRunning = pMapTestRunnings.put(wTestRunning.getRunningId(), wTestRunning);
		if (wOldTestRunning != null) {
			throw new CTestException("The CTestException [%s] already exists", aRunningId);
		}
		wTestRunning.setTestBegin(aBegin);
		return wTestRunning;
	}

	/**
	 * @param aException
	 */
	public CTestRunning setTestKO(final String aRunningId, final String aMessage, final Throwable aException) {
		pNbFinishedOK++;

		CTestRunning wTestRunning = setTestOK(aRunningId, false);

		wTestRunning.setTestKO(aMessage, aException);
		return wTestRunning;
	}

	/**
	 * @param aOK
	 */
	public CTestRunning setTestOK(final String aRunningId, final boolean aOK) {
		pNbFinishedOK++;

		CTestRunning wTestRunning = pMapTestRunnings.get(CTestRunning.validRunningId(aRunningId));
		if (wTestRunning == null) {
			throw new CTestException("The CTestException [%s] doesn't exist", aRunningId);
		}
		wTestRunning.setTestOK(aOK);
		return wTestRunning;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder wSB = new StringBuilder();

		wSB.append(String.format("%-50s :", getName()));
		int wIdx = 0;
		for (Entry<String, CTestRunning> wEntry : pMapTestRunnings.entrySet()) {
			wSB.append(String.format("\n   - running(%03d) : %s", wIdx, wEntry.getValue().toString()));
			wIdx++;
		}

		return wSB.toString();
	}

}

/**
 * @author ogattaz
 *
 */
class CTestRunning {

	/**
	 * @param aRunningId
	 * @return
	 */
	static String validRunningId(final String aRunningId) {
		return (aRunningId != null && !aRunningId.isEmpty()) ? aRunningId : "run once";
	}

	private String pMessage;
	private final String pRunningId;
	private Throwable pTestException;
	private boolean pTestFinishedOK = false;
	private boolean pTestStarted = false;

	CTestRunning(final String aRunningId) {
		super();
		pRunningId = validRunningId(aRunningId);
	}

	/**
	 * @return
	 */
	public String getErrorMessage() {
		return hasErrorMessage() ? pMessage : "";
	}

	/**
	 * @return
	 */
	public String getExceptionMessages() {

		return (hasException()) ? CXException.eCauseMessagesInString(pTestException).replace("\n", " | ") : "";
	}

	/**
	 * @return
	 */
	public String getFinishedOkState() {
		return isFinishedOK() ? "finished OK"
				: (isStarted() ? "error : " + getErrorMessage() + " " + getExceptionMessages() : "");
	}

	/**
	 * @return
	 */
	String getRunningId() {
		return pRunningId;
	}

	public String getStartedState() {
		return isStarted() ? "started" : "";
	}

	/**
	 * @return
	 */
	public boolean hasErrorMessage() {
		return pMessage != null;
	}

	/**
	 * @return
	 */
	public boolean hasException() {
		return pTestException != null;
	}

	/**
	 * @return
	 */
	public boolean isFinishedOK() {
		return pTestFinishedOK;
	}

	/**
	 * @return
	 */
	public boolean isStarted() {
		return pTestStarted;
	}

	/**
	 * @param aBegin
	 */
	public void setTestBegin(final boolean aBegin) {

		pTestStarted = aBegin;
	}

	/**
	 * @param aException
	 */
	public void setTestKO(final String aMessage, final Throwable aException) {
		pTestException = aException;
		pMessage = aMessage;
	}

	public void setTestOK(final boolean aOK) {
		pTestFinishedOK = aOK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%-80s : %s%s%s", getRunningId(), getStartedState(), (isStarted() ? ", " : ""),
				getFinishedOkState());
	}
}

/**
 * @author ogattaz
 *
 */
public class CTestsRegistry {

	private final Map<String, CTestDefinition> pMapTests = new TreeMap<>();

	private final Class<? extends CAbstractJunitTest> pTestClass;

	/**
	 * @param aTesClass
	 */
	public CTestsRegistry(final Class<? extends CAbstractJunitTest> aTestClass) {
		super();
		pTestClass = aTestClass;
		initialize(aTestClass);
	}

	/**
	 * @return
	 */
	public String dump() {

		return dumpIn(new StringBuilder()).toString();
	}

	/**
	 * @param wSB
	 * @return
	 */
	public StringBuilder dumpIn(final StringBuilder wSB) {

		wSB.append(String.format(" +++ Tests : %s", pTestClass.getName()));

		int wIdx = 0;
		int wNbOK = 0;
		int wNbStarted = 0;
		for (Entry<String, CTestDefinition> wEntry : pMapTests.entrySet()) {
			wIdx++;
			wSB.append(String.format("\n - [%2d] %s", wIdx, wEntry.getValue().toString()));

			wNbStarted += wEntry.getValue().getNbStarted();
			wNbOK += wEntry.getValue().getNbFinishedOK();
		}

		wSB.append(String.format("\n +++  nbTests=[%2d] result: started=[%2d] / isOk=[%2d] => is valid : %s", wIdx,
				wNbStarted, wNbOK, (wNbStarted == wNbOK)));

		return wSB;
	}

	/**
	 * @return
	 */
	public int getNbtest() {
		return pMapTests.size();
	}

	/**
	 * @param aTesClass
	 */
	@SuppressWarnings("unchecked")
	private void initialize(final Class<? extends CAbstractJunitTest> aTesClass) {
		for (Method wMethod : aTesClass.getDeclaredMethods()) {

			// compliance with Java 7
			Annotation wAnnotation = wMethod.getAnnotation(Test.class);
			if (wAnnotation == null) {
				// issue with tychos to compilation with Theory. this class cannot be found
				Class<? extends Annotation> wTheoryClass;
				try {
					wTheoryClass = (Class<? extends Annotation>) Class
							.forName("org.junit.experimental.theories.Theory");
					if (wTheoryClass != null) {
						wAnnotation = wMethod.getAnnotation(wTheoryClass);
					}
				} catch (ClassNotFoundException e) {
					wAnnotation = null;
				}

			}

			if (wAnnotation != null) {
				// the number of the test [1..n]
				int wTestNumber = pMapTests.size() + 1;
				pMapTests.put(wMethod.getName(), new CTestDefinition(wMethod, wAnnotation, wTestNumber));
			}
		}
	}

	/**
	 * @param aMethodName
	 * @return the found CTestBean instance.
	 * @throws Exception
	 */
	public CTestDefinition setTestBegin(final String aMethodName, final String aRunningId) throws Exception {

		CTestDefinition wTestBean = pMapTests.get(aMethodName);
		if (wTestBean == null) {
			throw new Exception("Unable to fin the test bean to store the result of the test");
		}
		wTestBean.setTestBegin(aRunningId, true);
		return wTestBean;
	}

	public void setTestKO(final String aMethodName, final String aRunningId, final String aMessage,
			final Throwable aException) throws Exception {

		CTestDefinition wTestBean = pMapTests.get(aMethodName);
		if (wTestBean == null) {
			throw new Exception("Unable to fin the test bean to store the result of the test");
		}
		wTestBean.setTestKO(aRunningId, aMessage, aException);
	}

	/**
	 * @param aMethodName
	 */
	public void setTestOK(final String aMethodName, final String aRunningId) throws Exception {

		CTestDefinition wTestBean = pMapTests.get(aMethodName);
		if (wTestBean == null) {
			throw new Exception("Unable to fin the test bean to store the result of the test");
		}
		wTestBean.setTestOK(aRunningId, true);
	}

	/**
	 * @return
	 */
	public int size() {
		return pMapTests.size();
	}

}
