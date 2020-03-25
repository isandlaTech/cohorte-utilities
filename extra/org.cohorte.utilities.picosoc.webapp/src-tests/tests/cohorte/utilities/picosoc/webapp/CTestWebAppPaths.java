package tests.cohorte.utilities.picosoc.webapp;

import java.util.logging.Level;

import org.cohorte.utilities.picosoc.CComponentLoggerFile;
import org.cohorte.utilities.picosoc.CServicesRegistry;
import org.cohorte.utilities.picosoc.webapp.CWebAppPathsBase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.psem2m.utilities.logging.CXLoggerUtils;
import org.junit.Test;

/**
 * <pre>
	-ea
	
	-Djava.util.logging.SimpleFormatter.format="%1$tY/%1$tm/%1$td; %1$tH:%1$tM:%1$tS:%1$tL; %4$7.7s; %3$16.016s; %2$54.54s; %5$s%6$s%n"
	
	-Dcatalina.base="/Users/ogattaz/workspaces/dimensions_quasar/catalina_base_9_quasar/" 
	-Dcatalina.home="/Applications/apache-tomcat-9.0.12" 
 * </pre>
 * @author ogattaz
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CTestWebAppPaths {
	
/**
 * @author ogattaz
 *
 */
class CMyWebAppPaths extends CWebAppPathsBase {

	/**
	 * @param aPathCatalinaHome
	 * @param aPathCatalinaBase
	 * @throws Exception
	 */
	protected CMyWebAppPaths(String aPathCatalinaHome, String aPathCatalinaBase) throws Exception {
		super(aPathCatalinaHome, aPathCatalinaBase);
	}
	
}
private static final String CATALINA_BASE	 = "./files-tests/catalina_base";
	
	private static final String	CATALINA_HOME	 = "./files-tests/catalina_home";

	@AfterClass
	public static void destroy() {

		CComponentLoggerFile.logInMain(Level.INFO, CTestWebAppPaths.class, "initialize", CXLoggerUtils.buildBanner('#', true, String.format("JUnit [%s] END", CTestWebAppPaths.class.getSimpleName())));
	}
	
	/**
	 * 
	 */
	@BeforeClass
	public static void initialize() {
		
		CComponentLoggerFile.logInMain(Level.INFO, CTestWebAppPaths.class, "initialize", CXLoggerUtils.buildBanner('#', true, String.format("JUnit [%s] BEGIN", CTestWebAppPaths.class.getSimpleName())));

		try {
			CServicesRegistry.newRegistry();
		} catch (Exception e) {
			CComponentLoggerFile.logInMain(Level.SEVERE, CTestWebAppPaths.class, "initialize", "ERROR: %s", e);
		}
	}
	
	/**
	 * 
	 */
	public CTestWebAppPaths() {
		super();
	}
	/**
	 * @param aMethodName
	 * @param aFormat
	 * @param aArgs
	 */
	private void logEndTest(final String aMethodName,final String aFormat,final Object ... aArgs) {
		
		CComponentLoggerFile.logInMain(Level.INFO,this, aMethodName, CXLoggerUtils.buildBanner('-', false, String.format("END: "+aFormat, aArgs)));

	}
	
	/**
	 * @throws Exception
	 */
	@Test(expected = Exception.class)
	public void test01() throws Exception {
		
		String wOK="OK";
		try {
			// throw and exception because the sys property "catalina.home" isn't set
			new CWebAppPathsBase();
			 
			wOK="KO";			
		}
		finally {
			logEndTest("test01","new CWebAppPathsBase throw execption %s",wOK);
		}

	}
	/**
	 * @throws Exception
	 */
	@Test
	public void test02() throws Exception {
		String wOK="KO";
		try {
				
			System.setProperty("catalina.home", CATALINA_HOME);
			System.setProperty("catalina.base", CATALINA_BASE);
			
			CWebAppPathsBase wWebAppPathsBase = new CWebAppPathsBase();
			
			String wDump=wWebAppPathsBase.dumpPaths();
			
			CComponentLoggerFile.logInMain(Level.INFO, this, "test02", "WebAppPath: %s", wDump);
			
			wOK="OK";
		}
		finally {
			logEndTest("test02","new CWebAppPathsBase %s",wOK);
		}

	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void test10() throws Exception {
		String wOK="KO";
		try {
			
			// 
			CMyWebAppPaths wMyWebAppPaths = new CMyWebAppPaths(CATALINA_HOME,CATALINA_BASE);
			
			String wDump=wMyWebAppPaths.dumpPaths();
			
			CComponentLoggerFile.logInMain(Level.INFO, this, "test01", "WebAppPath: %s", wDump);
			
			wOK="OK";
		}
		finally {
			logEndTest("test10","new CMyWebAppPaths %s",wOK);
		}
	}

}
