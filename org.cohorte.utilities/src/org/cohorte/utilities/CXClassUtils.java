package org.cohorte.utilities;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;

/**
 * Usage:
 * 
 * <pre>
		// Usage example:
		 class FooBar
		 {
		    static final Logger LOGGER = LoggerFactory.getLogger(CallerClassGetter.getCallerClass())
		 }
 * </pre>
 * 
 * @author ogattaz
 * 
 * @see Getting the class name from a static method in Java
 *      https://stackoverflow.com/questions/936684/getting-the-class-name-from-a-static-method-in-java
 */
public final class CXClassUtils extends SecurityManager {

	private static final CXClassUtils INSTANCE = new CXClassUtils();

	/**
	 * @param aSB
	 * @return
	 */
	public static StringBuilder addCallersClassesInSB(final StringBuilder aSB, final char aSeparator) {

		int wIdx = 0;
		for (Class<?> wClass : getCallersClasses()) {
			wIdx++;
			if (aSB.length() > 0) {
				aSB.append(aSeparator);
			}
			CXStringUtils.appendFormatStrInBuff(aSB, "(%2d):%s", wIdx, wClass.getName());
		}

		return aSB;
	}

	/**
	 * @return
	 */
	public static String dumpCallersClasses() {
		return dumpCallersClasses('\n');
	}

	/**
	 * @param aSeparator
	 * @return
	 */
	public static String dumpCallersClasses(final char aSeparator) {

		return addCallersClassesInSB(new StringBuilder(), aSeparator).toString();
	}

	/**
	 * return the first class of the list of callers which is :
	 * <lu>
	 * <li>outside the package ofthe class "org.cohorte.utilities.CXClassUtils"</li>
	 * <li>not the aAssignableClass</li>
	 * <li>assignable to the aAssignableClass</li>
	 * </ul>

	 * <pre>
		   [class org.cohorte.utilities.CXClassUtils, 
			class org.cohorte.utilities.CXClassUtils, 
			class org.cohorte.utilities.junit.CAbstractJunitTest, 
			class test.org.cohorte.utilities.picosoc.CTestComponentsLogger, 
			...
			]
	 * </pre>
	 * 
	 * @return the first class of the list of callers which is ...
	 */
	@SuppressWarnings("unchecked")
	public static <R> Class<R> findClass(final Class<R> aAssignableClass) {

		Package wCurrentPackage = CXClassUtils.class.getPackage();

		for (Class<?> wClass : getCallersClasses()) {

			// is this class outside the package "fr.agilium.dimensions.core.junit"
			if (!wCurrentPackage.equals(wClass.getPackage())) {

				// is this class extends CAbstractBaseTest?
				if (aAssignableClass!= wClass && aAssignableClass.isAssignableFrom(wClass)) {
					return (Class<R>)wClass;
				}
			}
		}
		String wMessage = String.format(
				"Unable to find a class<? extends %s> outside of the package [%s]. Callers:%s",aAssignableClass.getSimpleName(),
				wCurrentPackage.toString(), dumpCallersClasses(','));

		CActivityLoggerBasicConsole.getInstance().logSevere(aAssignableClass, "getCallerClass", wMessage);

		throw new RuntimeException(wMessage);
	}

	/**
	 * @return
	 */
	public static Class<?>[] getCallersClasses() {
		return INSTANCE.getClassContext();
	}

	/**
	 * 
	 */
	private CXClassUtils() {
		super();
	}
}
