package test.cohorte.utilities.testapps.impl;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptContext;

import org.cohorte.utilities.tests.CAppConsoleBase;
import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.rsrc.CXRsrcProvider;
import org.psem2m.utilities.rsrc.CXRsrcProviderFile;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;
import org.psem2m.utilities.scripting.CXJsEngine;
import org.psem2m.utilities.scripting.CXJsExcepRhino;
import org.psem2m.utilities.scripting.CXJsManager;
import org.psem2m.utilities.scripting.CXJsRuningContext;
import org.psem2m.utilities.scripting.CXJsSourceMain;
import org.psem2m.utilities.scripting.IXjsTracer;

/**
 * #12 Manage chains of resource providers
 * 
 * @author ogattaz
 *
 */
public class CTestJavascript extends CAppConsoleBase {

	/**
	 * @author ogattaz
	 *
	 */
	class CTestJsTracerWrapper implements IXjsTracer {

		/**
		 * @param e
		 * @return
		 */
		private String getCauseMessagesList(final Throwable e) {
			return "\t- "
					+ CXException.eCauseMessagesInString(e).replace(" , ",
							"\n\t- ");
		}

		@Override
		public boolean isTraceDebugOn() {
			return true;
		}

		@Override
		public boolean isTraceInfosOn() {
			return true;
		}

		@Override
		public void trace(final CharSequence aSB) {
			pLogger.logInfo(this, "jsTrace", aSB);

		}

		@Override
		public void trace(final Object aObj, final CharSequence aSB) {
			pLogger.logInfo(aObj, "jsTrace", "%s", aSB);

		}

		@Override
		public void trace(final Object aObj, final CharSequence aSB,
				final Throwable e) {
			pLogger.logInfo(aObj, "jsTrace", "%s\n%s", aSB,
					getCauseMessagesList(e));

		}

		@Override
		public void trace(final Object aObj, final Throwable e) {
			pLogger.logInfo(aObj, "jsTrace", "%s", getCauseMessagesList(e));

		}
	}

	private final static String CMD_RUN = "run";

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			final CTestJavascript wTest = new CTestJavascript(args);
			wTest.runApp();
			wTest.destroy();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public CTestJavascript(final String[] args) {
		super(args);
		addOneCommand(CMD_RUN, "r", new String[] { "run script" });
		pLogger.logInfo(this, "<init>", "instanciated");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doCommandClose(java.lang.String)
	 */
	@Override
	protected void doCommandClose() throws Exception {
		pLogger.logInfo(this, "doCommandClose", "begin");
		// ...
		pLogger.logInfo(this, "doCommandClose", "end");
	}

	/**
	 * @param aCmdeLine
	 * @throws Exception
	 */
	private void doCommandRun(final String aCmdeLine) throws Exception {
		pLogger.logInfo(this, "doCommandRun", "begin");

		// #12 Manage chains of resource providers

		// build the resource provider chain : "scripts2" => "scripts1" =>
		// "scripts0"
		CXRsrcProviderFile wProviderChain = newRsrcProviderFile(
				"testcases/scripts0/", null);
		wProviderChain = newRsrcProviderFile("testcases/scripts1/",
				wProviderChain);
		wProviderChain = newRsrcProviderFile("testcases/scripts2/",
				wProviderChain);

		// prepare the manager
		final CXJsManager wXJsManager = new CXJsManager(pLogger, "JavaScript");

		// set the resource provider chain
		wXJsManager.setRsrcProviderChain(wProviderChain);

		pLogger.logInfo(this, "doCommandRun", "new wXJsManager:\n%s",
				wXJsManager.toDescription());

		// prepare a tracer
		final IXjsTracer wTestjsTracer = new CTestJsTracerWrapper();

		// -------------------- STEP BY STEP CALL --------------------

		final CXJsSourceMain wJsSourceMain = wXJsManager.getMainSource(
				new CXRsrcUriPath("test.js"), wTestjsTracer);

		final CXJsRuningContext wCtx = new CXJsRuningContext(1024);
		wCtx.setAttribute("gTestEngineScope", "ENGINE_SCOPE",
				ScriptContext.ENGINE_SCOPE);
		wCtx.setAttribute("gTestClobalScope", "GLOBAL_SCOPE",
				ScriptContext.GLOBAL_SCOPE);

		try {
			final CXJsEngine wXJsEngine = wXJsManager.getScriptEngineFactory()
					.getScriptEngine();

			final Object wResult = wXJsEngine.eval(wJsSourceMain, wCtx,
					wTestjsTracer);

			pLogger.logInfo(this, "doCommandRun", "Result=[%s]", wResult);

		} catch (final CXJsExcepRhino wE) {

			// get the partial source around the line where the error
			String wPartialSource = wJsSourceMain.getText(wE.getLineNumber(),
					5, "\n\t>> ");
			// wPartialSource = "\n\t>> "
			// + wPartialSource.replace("\n", "\n\t>> ");

			pLogger.logSevere(this, "doCommandRun", "ERROR message=[%s] %s",
					wE.getMessage(), wPartialSource);

			pLogger.logSevere(this, "doCommandRun",
					wJsSourceMain.toDescription());

		}

		// -------------------- CALL IN ONCE --------------------

		try {
			Map<String, Object> wVariablesMap = new HashMap<>();
			wVariablesMap.put("gTestEngineScope", "ENGINE_SCOPE");
			wVariablesMap.put("gTestClobalScope", "GLOBAL_SCOPE");

			wXJsManager.runScript("test1.js", wVariablesMap);

		} catch (final CXJsExcepRhino wE) {
			// get the partial source around the line where the error
			String wPartialSource = wE.getModuleMain().getText(
					wE.getLineNumber(), 5, "\n\t>> ");
			pLogger.logSevere(this, "doCommandRun", "ERROR message=[%s] %s",
					wE.getMessage(), wPartialSource);

		}
		pLogger.logInfo(this, "doCommandRun", "End Command Run");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doCommandUser(java.lang.String)
	 */
	@Override
	protected void doCommandUser(final String aCmdeLine) throws Exception {
		if (isCommandX(CMD_RUN)) {
			doCommandRun(aCmdeLine);
		}
	}

	/**
	 * @param aPath
	 * @param aNext
	 * @return
	 * @throws Exception
	 */
	private CXRsrcProviderFile newRsrcProviderFile(final String aPath,
			final CXRsrcProvider aNext) throws Exception {
		final CXFileDir wDir0 = new CXFileDir(CXFileDir.getUserDir(), aPath);
		final CXRsrcProviderFile wProvider = new CXRsrcProviderFile(wDir0,
				Charset.forName(CXBytesUtils.ENCODING_UTF_8));
		if (aNext != null) {
			wProvider.setNext(aNext);
		}
		pLogger.logInfo(this, "newRsrcProviderFile", "RsrcProviderFile: %s",
				wProvider.toDescription());
		return wProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tests.CAbstractTest#doTest()
	 */
	@Override
	protected void runApp() throws Exception {
		pLogger.logInfo(this, "doTest", "begin");
		pLogger.logInfo(this, "doTest", CXOSUtils.getEnvContext());
		pLogger.logInfo(this, "doTest", CXJvmUtils.getJavaContext());

		waitForCommand();

		pLogger.logInfo(this, "doTest", "end");
	}

}
