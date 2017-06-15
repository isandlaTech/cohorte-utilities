package org.cohorte.utilities.tests.impl;

import java.nio.charset.Charset;

import javax.script.ScriptContext;

import org.cohorte.utilities.tests.CAppConsoleBase;
import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXOSUtils;
import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.rsrc.CXRsrcProviderFile;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;
import org.psem2m.utilities.scripting.CXJsEngine;
import org.psem2m.utilities.scripting.CXJsExcepRhino;
import org.psem2m.utilities.scripting.CXJsManager;
import org.psem2m.utilities.scripting.CXJsScriptContext;
import org.psem2m.utilities.scripting.CXJsSourceMain;
import org.psem2m.utilities.scripting.IXjsTracer;

/**
 * @author ogattaz
 *
 */
public class CTestJavascript extends CAppConsoleBase {

	/**
	 * @author ogattaz
	 *
	 */
	class CJsTracer implements IXjsTracer {

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

		final CXJsManager wXJsManager = new CXJsManager("JavaScript");

		final CXJsEngine wXJsEngine = wXJsManager.getScriptEngineFactory()
				.getScriptEngine();

		// dossier dans lequel on trouve les sources
		final CXFileDir wDir = new CXFileDir(CXFileDir.getUserDir(),
				"testcases/scripts/");
		final CXRsrcProviderFile wProvider = new CXRsrcProviderFile(wDir,
				Charset.forName(CXBytesUtils.ENCODING_UTF_8));

		pLogger.logInfo(this, "initOneProvider", "new RsrcProvider  for [%s]",
				wDir.getAbsolutePath());

		final IXjsTracer wXjsTracer = new CJsTracer();

		final CXJsSourceMain wMain = wXJsManager.getMainSource(wProvider,
				new CXRsrcUriPath("test.js"), wXjsTracer);

		final CXJsScriptContext wCtx = new CXJsScriptContext(1024);
		wCtx.setAttribute("ENGSCOP", "ENGINE_1", ScriptContext.ENGINE_SCOPE);
		wCtx.setAttribute("GLOSCOP", "GLOBAL_1", ScriptContext.GLOBAL_SCOPE);

		try {
			final Object wResult = wXJsEngine.eval(wMain, wCtx, wXjsTracer);

			pLogger.logInfo(this, "doCommandRun", "Result=[%s]", wResult);

		} catch (final CXJsExcepRhino wE) {

			// get the partial source around the line where the error

			String wPartialSource = wMain.getText(wE.getLineNumber(), 5);
			wPartialSource = "\n\t>> "
					+ wPartialSource.replace("\n", "\n\t>> ");

			pLogger.logSevere(this, "doCommandRun", "ERROR message=[%s] %s",
					wE.getMessage(), wPartialSource);

			pLogger.logSevere(this, "doCommandRun", wMain.toDescription());

		}
		pLogger.logInfo(this, "doCommandRun", "End");
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
