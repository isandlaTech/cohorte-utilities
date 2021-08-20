package org.cohorte.utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;

/**
 * 
 * MOD_OG_20210427 Sharing utilities developped in Dimensions core
 * 
 * 
 * Usage:
 * 
 * <pre>
 * 		// Usage example:
 * 		 class FooBar
 * 		 {
 * 		    static final Logger LOGGER = LoggerFactory.getLogger(CallerClassGetter.getCallerClass())
 * 		 }
 * </pre>
 * 
 * @author ogattaz
 * 
 * @see Getting the class name from a static method in Java
 *      https://stackoverflow
 *      .com/questions/936684/getting-the-class-name-from-a-
 *      static-method-in-java
 */
public final class CXClassUtils extends SecurityManager {

	private static final CXClassUtils INSTANCE = new CXClassUtils();

	public static final boolean WITH_SUB_PACKAGES = true;

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
	 * @param e
	 * @return
	 */
	private static String dumpThrowable(final Throwable e) {
		StringWriter wSW = new StringWriter();
		e.printStackTrace(new PrintWriter(wSW));
		return wSW.toString();
	}

	/**
	 * return the first class of the list of callers which is : <lu> <li>outside
	 * the package ofthe class "org.cohorte.utilities.CXClassUtils"</li> <li>not
	 * the aAssignableClass</li> <li>assignable to the aAssignableClass</li>
	 * </ul>
	 * 
	 * <pre>
	 * 		   [class org.cohorte.utilities.CXClassUtils, 
	 * 			class org.cohorte.utilities.CXClassUtils, 
	 * 			class org.cohorte.utilities.junit.CAbstractJunitTest, 
	 * 			class test.org.cohorte.utilities.picosoc.CTestComponentsLogger, 
	 * 			...
	 * 			]
	 * </pre>
	 * 
	 * @return the first class of the list of callers which is ...
	 */
	@SuppressWarnings("unchecked")
	public static <R> Class<R> findClass(final Class<R> aAssignableClass) {

		Package wCurrentPackage = CXClassUtils.class.getPackage();

		for (Class<?> wClass : getCallersClasses()) {

			// is this class outside the package
			// "fr.agilium.dimensions.core.junit"
			if (!wCurrentPackage.equals(wClass.getPackage())) {

				// is this class extends CAbstractBaseTest?
				if (aAssignableClass != wClass && aAssignableClass.isAssignableFrom(wClass)) {
					return (Class<R>) wClass;
				}
			}
		}
		String wMessage = String.format("Unable to find a class<? extends %s> outside of the package [%s]. Callers:%s",
				aAssignableClass.getSimpleName(), wCurrentPackage.toString(), dumpCallersClasses(','));

		CActivityLoggerBasicConsole.getInstance().logSevere(aAssignableClass, "getCallerClass", wMessage);

		throw new RuntimeException(wMessage);
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 *
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName, final IXClassFilter aClassFilter)
			throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory() && aClassFilter.isWithSubPackages()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName(), aClassFilter));
			}
			//
			else if (file.getName().endsWith(".class")) {

				String wSimpleClassName = file.getName().substring(0, file.getName().length() - 6);

				if (!aClassFilter.isSimpleClassNameMatches(wSimpleClassName)) {
					continue;
				}

				Class<?> wClass = Class.forName(packageName + '.' + wSimpleClassName);

				if (aClassFilter.hasExtendedClass()) {

					// Determines if the class or interface represented by this
					// Class object is
					// either the same as, or is a superclass or superinterface
					// of, the class or
					// interface represented by the specified Class parameter.
					// It returns true if
					// so;
					if (!aClassFilter.getExtendedClass().isAssignableFrom(wClass)) {
						continue;
					}

				}
				classes.add(wClass);
			}
		}
		return classes;
	}

	/**
	 * @return
	 */
	public static Class<?>[] getCallersClasses() {
		return INSTANCE.getClassContext();
	}

	/**
	 * @param aClassFilter
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static List<Class<?>> getClasses(final IXClassFilter aClassFilter) throws ClassNotFoundException,
			IOException {

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		assert classLoader != null;

		ArrayList<Class<?>> wClasses = new ArrayList<>();

		for (String wPackageName : aClassFilter.getPackageNames()) {

			String path = packageNameToPath(wPackageName);

			Enumeration<URL> wResources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<>();
			while (wResources.hasMoreElements()) {
				URL resource = wResources.nextElement();
				dirs.add(new File(resource.getFile()));
			}

			for (File directory : dirs) {
				wClasses.addAll(findClasses(directory, wPackageName, aClassFilter));
			}
		}
		return wClasses;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 *
	 * @param aPackageName
	 *            The base package
	 * @param aWithSubpackage
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static List<Class<?>> getClasses(String aPackageName) throws ClassNotFoundException, IOException {

		return getClasses(new CXClassFilter(aPackageName));

	}

	/**
	 * MOD_OG 1.4.3
	 * 
	 * @param aClass
	 * @param aFieldName
	 * @return
	 * @throws Exception
	 * 
	 */
	public static String getPrivateStaticFinalString(Class<?> aClass, final String aFieldName) throws Exception {

		if (!CXJvmUtils.isJava8()) {
			return String.format(
					"Can't retrieve the value of the field [%s:%s], the jvm version [%s] is greater than [1.8]",
					aClass.getSimpleName(), aFieldName, CXJvmUtils.getJavaVersion());
		}

		try {

			Field wTargetField = aClass.getDeclaredField(aFieldName);

			final boolean wHasToRemovePrivate = !wTargetField.isAccessible();

			// @see
			// https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection

			int wOriginalModifiers = wTargetField.getModifiers();
			final boolean wHasToRemoveFinal = (wOriginalModifiers & ~Modifier.FINAL) != wOriginalModifiers;

			if (wHasToRemoveFinal || wHasToRemovePrivate) {
				// wrapping setAccessible
				Exception wException = AccessController.doPrivileged(new PrivilegedAction<Exception>() {
					@Override
					public Exception run() {

						try {
							// remove "final"
							if (wHasToRemoveFinal) {
								Field wModifiersField = Field.class.getDeclaredField("modifiers");
								wModifiersField.setAccessible(true);
								int wNewModifier = wOriginalModifiers & ~Modifier.FINAL;
								wModifiersField.setInt(wTargetField, wNewModifier);
							}

							// remove "private"
							if (wHasToRemovePrivate) {
								wTargetField.setAccessible(true);
							}

							return null;
						}
						//
						catch (Exception e) {
							return e;
						}
					}
				});
				if (wException != null) {
					throw wException;
				}
			}

			String wValue = String.valueOf(wTargetField.get(null));

			return wValue;
		}
		//
		catch (Exception e) {
			throw new Exception(String.format("ERROR: Unable to get the value of the private field [%s.%s]",
			//
					aClass.getSimpleName(),
					//
					aFieldName), e);
		}
	}

	/**
	 * @param aPackageName
	 * @return
	 */
	public static String packageNameToPath(final String aPackageName) {
		return aPackageName.replace('.', '/');
	}

	/**
	 * MOD_OG 1.4.3
	 * 
	 * 
	 * @param aClass
	 * @param aFieldName
	 * @param aValue
	 * @return the report of the setting
	 * @throws Exception
	 * 
	 * @see https 
	 *      ://stackoverflow.com/questions/3301635/change-private-static-final
	 *      -field-using-java-reflection
	 */
	public static String setPrivateStaticFinalString(Class<?> aClass, final String aFieldName, final String aValue)
			throws Exception {

		if (!CXJvmUtils.isJava8()) {
			return String.format(
					"Can't retrieve the value of the field [%s:%s], the jvm version [%s] is greater than [1.8]",
					aClass.getSimpleName(), aFieldName, CXJvmUtils.getJavaVersion());
		}

		try {

			Field wTargetField = aClass.getDeclaredField(aFieldName);

			final boolean wHasToRemovePrivate = !wTargetField.isAccessible();

			// @see
			// https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection

			int wOriginalModifiers = wTargetField.getModifiers();
			final boolean wHasToRemoveFinal = (wOriginalModifiers & ~Modifier.FINAL) != wOriginalModifiers;

			if (wHasToRemoveFinal || wHasToRemovePrivate) {
				// wrapping setAccessible
				Exception wException = AccessController.doPrivileged(new PrivilegedAction<Exception>() {
					@Override
					public Exception run() {

						try {
							// remove "final"
							if (wHasToRemoveFinal) {
								Field wModifiersField = Field.class.getDeclaredField("modifiers");
								wModifiersField.setAccessible(true);
								int wNewModifier = wOriginalModifiers & ~Modifier.FINAL;
								wModifiersField.setInt(wTargetField, wNewModifier);
							}

							// remove "private"
							if (wHasToRemovePrivate) {
								wTargetField.setAccessible(true);
							}

							return null;
						}
						//
						catch (Exception e) {
							return e;
						}
					}
				});
				if (wException != null) {
					throw wException;
				}
			}

			// verif
			String wOldValue = String.valueOf(wTargetField.get(null));

			// set static field
			wTargetField.set(null, aValue);

			// verif
			String wNewValue = String.valueOf(wTargetField.get(null));

			// verif
			boolean wModified = aValue.equals(wNewValue);

			return String.format("Modified static fied: [%s.%s]\n - Modified=[%b]\n - NewValue=[%s]\n - OldValue=[%s]",
			//
					aClass.getSimpleName(),
					//
					wTargetField.getName(),
					//
					wModified,
					//
					wNewValue,
					//
					wOldValue);

		} catch (Exception e) {
			throw new Exception(String.format("ERROR: Unable to set the final field [%s.%s]",
			//
					aClass.getSimpleName(),
					//
					aFieldName), e);
		}
	}

	/**
	 * 
	 */
	private CXClassUtils() {
		super();
	}
}
