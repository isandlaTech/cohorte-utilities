package tests;

import java.io.InputStream;
import java.util.Map;

import org.cohorte.utilities.tests.CAppConsoleBase;
import org.psem2m.utilities.CXArray;
import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.CXDomAndJson;
import org.psem2m.utilities.CXDomUtils;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;
import org.psem2m.utilities.json.JSONObject;
import org.w3c.dom.Element;

/**
 * @author ogattaz
 * 
 */
public class CTestDomAndJson extends CAppConsoleBase {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestDomAndJson wTest = new CTestDomAndJson(args);
			wTest.runApp();
			wTest.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public CTestDomAndJson(final String[] args) {
		super(args);
		addOneCommand(CMD_TEST, new String[] { "test the array tools" });
		pLogger.logInfo(this, "<init>", "instanciated");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.tests.CAppConsoleBase#destroy()
	 */
	@Override
	protected void destroy() {
		super.destroy();
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandTest(final String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandTest", "begin");
		doCommandTestConvert();
		doCommandTestConvertNoArray();
		doCommandTestUpdateFull();
		doCommandTestUpdatePartial();
		pLogger.logInfo(this, "doCommandTest", "end");

	}

	/**
	 * @throws Exception
	 */
	private void doCommandTestConvert() throws Exception {
		pLogger.logInfo(this, "doCommandTestConvert", "begin -------------");

		CXDomUtils wDomUtils = getXmlResource("convertToJson");

		CXTimer wTimer = CXTimer.newStartedTimer();
		JSONObject wJsonObject = new CXDomAndJson().convertXmlToJson(wDomUtils
				.getRootElmt());

		pLogger.logInfo(this, "doCommandTestConvert",
				"wJsonObject1: duration=[%s]\n%s",
				wTimer.getDurationStrMicroSec(), wJsonObject.toString(2));

		wTimer = CXTimer.newStartedTimer();
		wJsonObject = new CXDomAndJson().convertXmlToJson(wDomUtils
				.getFirstDescElmtByTag("DataToSend"));

		pLogger.logInfo(this, "doCommandTestConvert",
				"wJsonObject2: duration=[%s]\n%s",
				wTimer.getDurationStrMicroSec(), wJsonObject.toString(2));

		// <HeadersToSend/ ==> array ==> [{ ... },{ ... }]
		wTimer = CXTimer.newStartedTimer();
		wJsonObject = new CXDomAndJson().convertXmlToJson(wDomUtils
				.getFirstDescElmtByTag("HeadersToSend"));

		pLogger.logInfo(this, "doCommandTestConvert",
				"wJsonObject: duration=[%s]\n%s",
				wTimer.getDurationStrMicroSec(), wJsonObject.toString(2));

		// <ResponseHeaders/ ==> empty ==> {}
		wTimer = CXTimer.newStartedTimer();
		wJsonObject = new CXDomAndJson().convertXmlToJson(wDomUtils
				.getFirstDescElmtByTag("ResponseHeaders"));

		pLogger.logInfo(this, "doCommandTestConvert",
				"wJsonObject: duration=[%s]\n%s",
				wTimer.getDurationStrMicroSec(), wJsonObject.toString(2));

		pLogger.logInfo(this, "doCommandTestConvert", "end");
	}

	/**
	 * @throws Exception
	 */
	private void doCommandTestConvertNoArray() throws Exception {
		pLogger.logInfo(this, "doCommandTestConvertNoArray",
				"begin -------------");

		CXDomUtils wDomUtils = getXmlResource("convertToJson-no-array");

		CXTimer wTimer = CXTimer.newStartedTimer();

		JSONObject wJsonObject = new CXDomAndJson().convertXmlToJson(wDomUtils
				.getRootElmt());

		pLogger.logInfo(this, "doCommandTestConvert",
				"wJsonObject1: duration=[%s]\n%s",
				wTimer.getDurationStrMicroSec(), wJsonObject.toString(2));

		Map<String, String> wMap = new CXDomAndJson()
				.convertElementToMap(wDomUtils
						.getFirstDescElmtByTag("HeadersToSend"));

		pLogger.logInfo(this, "doCommandTestConvert",
				"wJsonObject: duration=[%s]\n%s",
				wTimer.getDurationStrMicroSec(),
				CXStringUtils.stringMapToString(wMap));

		pLogger.logInfo(this, "doCommandTestConvertNoArray", "end");

	}

	/**
	 * @throws Exception
	 */
	private void doCommandTestUpdateFull() throws Exception {
		pLogger.logInfo(this, "doCommandTestUpdateFull", "begin -------------");

		CXDomUtils wDomUtils = getXmlResource("updateXmlFromJson");

		JSONObject wJsonObject = getJsonResource("updateXmlFromJson-full");

		CXTimer wTimer = CXTimer.newStartedTimer();

		boolean wUpdated = new CXDomAndJson().updateXmlFromJson(
				wDomUtils.getRootElmt(), wJsonObject);

		pLogger.logInfo(this, "doCommandTest",
				"Dom AFTER : wUpdated=[%s] duration=[%s]\n%s", wUpdated,
				wTimer.getDurationStrMicroSec(), wDomUtils.toXml(2));

		pLogger.logInfo(this, "doCommandTestUpdateFull", "end");
	}

	/**
	 * @throws Exception
	 */
	private void doCommandTestUpdatePartial() throws Exception {
		pLogger.logInfo(this, "doCommandTestUpdatePartial",
				"begin -------------");

		CXDomUtils wDomUtils = getXmlResource("updateXmlFromJson");
		Element wResponseDataElmt = wDomUtils
				.getFirstDescElmtByTag("ResponseData");

		JSONObject wJsonObject = getJsonResource("updateXmlFromJson-partial");

		CXTimer wTimer = CXTimer.newStartedTimer();

		boolean wUpdated = new CXDomAndJson().updateXmlFromJson(
				wResponseDataElmt, wJsonObject);

		pLogger.logInfo(this, "doCommandTest",
				"Dom AFTER : wUpdated=[%s] duration=[%s]\n%s", wUpdated,
				wTimer.getDurationStrMicroSec(),
				CXDomUtils.toXml(wResponseDataElmt, 2));

		pLogger.logInfo(this, "doCommandTestUpdatePartial", "end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.tests.CAppConsoleBase#doCommandUser(java.lang.String
	 * )
	 */
	@Override
	protected void doCommandUser(String aCmdeLine) throws Exception {
		if (isCommandX(CMD_TEST)) {
			doCommandTest(aCmdeLine);
		} else {
			wrongCommandUser(aCmdeLine);
		}
	}

	/**
	 * @param aResourceId
	 * @return
	 * @throws Exception
	 */
	private JSONObject getJsonResource(final String aResourceId)
			throws Exception {

		String wJsonResource = getResourceData(aResourceId, "json");

		pLogger.logInfo(this, "getJsonResource", "wJsonResource:\n%s",
				wJsonResource);

		return new JSONObject(wJsonResource);
	}

	/**
	 * @param aResourceId
	 * @return
	 * @throws Exception
	 */
	private String getResourceData(final String aResourceId,
			final String aResourceExtension) throws Exception {

		String wResourcePath = getResourcePath(aResourceId, aResourceExtension);

		pLogger.logInfo(this, "getResourceData", "wResourcePath=[%s]",
				wResourcePath);

		InputStream wIS = getClass().getClassLoader().getResourceAsStream(
				wResourcePath);

		pLogger.logInfo(this, "getResourceData", "wIS=[%s] available=[%s]",
				wIS, wIS.available());

		return CXStringUtils.strFromInputStream(wIS,
				CXBytesUtils.ENCODING_UTF_8);
	}

	/**
	 * @param aResourceId
	 * @return
	 */
	private String getResourcePath(final String aResourceId,
			final String aResourceExtension) {
		return String.format("%s_%s.%s",
				getClass().getName().replace('.', '/'), aResourceId,
				aResourceExtension);
	}

	/**
	 * @param aResourceId
	 * @return
	 * @throws Exception
	 */
	private CXDomUtils getXmlResource(final String aResourceId)
			throws Exception {

		String wXmlresource = getResourceData(aResourceId, "xml");

		pLogger.logInfo(this, "getXmlResource", "wXmlresource:\n%s",
				wXmlresource);

		return new CXDomUtils(wXmlresource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.tests.CAppConsoleBase#runApp()
	 */
	@Override
	protected void runApp() throws Exception {

		pLogger.logInfo(this, "doTest", "begin");

		if (CXArray.contains(pAppArgs, APPLICATION_PARAM_AUTO)) {
			doCommandTest(null);
		} else {
			waitForCommand();
		}
		pLogger.logInfo(this, "doTest", "end");
	}
}
