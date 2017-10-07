package org.cohorte.utilities.security;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ogattaz
 *
 */
public class CXPassphraseParser {

	static List<Class<? extends CXAbstractPassphrase>> wPassphraseClasses = new ArrayList<Class<? extends CXAbstractPassphrase>>();

	static {
		addPassphraseClasses(CXPassphraseB64.class);
		addPassphraseClasses(CXPassphraseOBF.class);
		addPassphraseClasses(CXPassphraseRDM.class);
	}

	/**
	 * to be able to extends the parser
	 *
	 * @param aCXAbstractPassphraseClass
	 */
	public static void addPassphraseClasses(
			final Class<? extends CXAbstractPassphrase> aCXAbstractPassphraseClass) {
		wPassphraseClasses.add(0, aCXAbstractPassphraseClass);
	}

	/**
	 * @param aPassphrase
	 *            a instance of passphrase
	 * @return true if the decoded passphrase contains a semi column
	 */
	private static boolean hasSubPassphrase(final IXPassphrase aPassphrase) {
		// at least a profix of one character
		return aPassphrase != null && aPassphrase.getDecoded().indexOf(':') > 0;
	}

	/**
	 * @param aValue
	 *            a string value to cenvert into a passphrase
	 * @return an instance of passphrase
	 * @throws CXPassphraseSchemeException
	 */
	public static IXPassphrase parse(final String aValue)
			throws CXPassphraseSchemeException {

		CXAbstractPassphrase wPP = null;
		int wIdx = 0;
		while (wPP == null && wIdx < wPassphraseClasses.size()) {

			try {
				// try to instanciate a PassPhrase
				final Class<?> wPassphraseClass = wPassphraseClasses.get(wIdx);
				// System.err.println("try to instanciate:" +
				// wPassphraseClass.getSimpleName());

				final Constructor<?> constructeur = wPassphraseClass
						.getConstructor(new Class[] { String.class });
				wPP = (CXAbstractPassphrase) constructeur
						.newInstance(new Object[] { aValue });
				if (hasSubPassphrase(wPP)) {
					wPP.setNested(parse(wPP.getDecoded()));
				}
				// System.err.println("Has instanciated :" + wPP);
				return wPP;
			} catch (final InvocationTargetException e) {
				// System.err.println("InvocationTargetException:" +
				// CXException.eCauseMessagesInString( e.getTargetException()));
				// nothing try to an other
			} catch (final Exception e) {
				throw new CXPassphraseSchemeException(e,
						"Unable to instanciate IXPassphrase using [%s]", aValue);

			}
			wIdx++;
		}
		throw new CXPassphraseSchemeException("Not a parsable Passphrase [%s]",
				aValue);
	}

	/**
	 *
	 */
	private CXPassphraseParser() {
	}

}
