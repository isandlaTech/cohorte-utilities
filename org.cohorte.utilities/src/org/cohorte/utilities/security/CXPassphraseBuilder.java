package org.cohorte.utilities.security;

/**
 * @author ogattaz
 *
 */
public class CXPassphraseBuilder {

	/**
	 * @param aValue
	 * @return
	 * @throws InstantiationException
	 * @throws CXPassphraseSchemeException
	 */
	public static IXPassphrase buildB64(final IXPassphrase aPassphrase)
			throws InstantiationException, CXPassphraseSchemeException {

		return new CXPassphraseB64(aPassphrase);
	}

	/**
	 * @param aValue
	 * @return
	 * @throws InstantiationException
	 * @throws CXPassphraseSchemeException
	 */
	public static IXPassphrase buildB64(final String aValue)
			throws InstantiationException, CXPassphraseSchemeException {

		return new CXPassphraseB64(aValue);
	}

	/**
	 * @param aValue
	 * @return
	 * @throws InstantiationException
	 * @throws CXPassphraseSchemeException
	 */
	public static IXPassphrase buildB64OBFRDM(final String aValue)
			throws InstantiationException, CXPassphraseSchemeException {

		return new CXPassphraseB64(new CXPassphraseOBF(new CXPassphraseRDM(
				aValue)));
	}

	/**
	 * @param aValue
	 * @return
	 * @throws InstantiationException
	 * @throws CXPassphraseSchemeException
	 */
	public static IXPassphrase buildOBF(final IXPassphrase aPassphrase)
			throws InstantiationException, CXPassphraseSchemeException {

		return new CXPassphraseOBF(aPassphrase);
	}

	/**
	 * @param aValue
	 * @return
	 * @throws InstantiationException
	 * @throws CXPassphraseSchemeException
	 */
	public static IXPassphrase buildOBF(final String aValue)
			throws InstantiationException, CXPassphraseSchemeException {

		return new CXPassphraseOBF(aValue);
	}

	/**
	 * @param aPassphrase
	 * @return
	 * @throws InstantiationException
	 * @throws CXPassphraseSchemeException
	 */
	public static IXPassphrase buildRDM(final IXPassphrase aPassphrase)
			throws InstantiationException, CXPassphraseSchemeException {

		return new CXPassphraseRDM(aPassphrase);
	}

	/**
	 * @param aValue
	 * @return
	 * @throws InstantiationException
	 * @throws CXPassphraseSchemeException
	 */
	public static IXPassphrase buildRDM(final String aValue)
			throws InstantiationException, CXPassphraseSchemeException {

		return new CXPassphraseRDM(aValue);
	}
}
