package tests;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.psem2m.utilities.CXSortList;
import org.psem2m.utilities.CXSortedMapString;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public class CTestSortedMapString {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			CTestSortedMapString wTest = new CTestSortedMapString();
			wTest.doTest();
			wTest.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final IActivityLogger pLogger;

	/**
	 * 
	 */
	private CTestSortedMapString() {
		super();
		pLogger = CActivityLoggerBasicConsole.getInstance();
		pLogger.logInfo(this, "<init>", "Instanciated");
	}

	/**
	 * 
	 */
	private void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
		pLogger.close();
	}

	/**
	 * 
	 */
	private void doTest() {
		pLogger.logInfo(this, "doTest", "Begin");

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		List<Object> wList = new ArrayList<Object>();
		wList.add("xxx");
		wList.add("yyy");
		properties.put("zzz", wList);
		properties.put("ddd", new Double(3.141592));
		properties.put("osgi.command.scope", "scope");
		properties.put("aaa", "value aaaa");
		properties.put("osgi.command.function", new String[] { "start",
				"startldap", "startxml", "stop", "verify" });
		properties.put("bbb", new Boolean(false));
		properties.put("ccc", new Double(3.141592));

		pLogger.logInfo(this, "doTest", properties.toString());

		CXSortedMapString wSM = CXSortedMapString.convert(properties,
				CXSortList.ASCENDING, CXSortList.SORTBYKEY);

		pLogger.logInfo(this, "doTest", wSM.toString());

		pLogger.logInfo(this, "doTest", "End");
	}
}
