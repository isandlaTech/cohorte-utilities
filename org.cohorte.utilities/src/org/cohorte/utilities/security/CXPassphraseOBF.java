package org.cohorte.utilities.security;

/**
 *
 * Compliant with Jetty password utility used to generate all varieties of
 * passwords.
 *
 *
 * @see https://wiki.eclipse.org/Jetty/Howto/Secure_Passwords
 *
 * @author ogattaz
 *
 */
public class CXPassphraseOBF extends CXAbstractPassphrase {

	/**
	 * @see https://tools.ietf.org/html/rfc3548
	 */
	public static final String OBF_PREFIX = "OBF:";

	/**
	 * @param aNested
	 * @throws CXPassphraseSchemeException
	 */
	public CXPassphraseOBF(final IXPassphrase aNested)
			throws CXPassphraseSchemeException {
		super(CXPassphraseType.OBF, aNested);
	}

	/**
	 * @param aValue
	 *            a encoded or not passphrase
	 * @throws CXPassphraseSchemeException
	 */
	public CXPassphraseOBF(final String aValue)
			throws CXPassphraseSchemeException, InstantiationException {
		super(CXPassphraseType.OBF, aValue);
	}

	/**
	 * @param aValue
	 * @return
	 */
	@Override
	protected String decode(final String aValue) {
		return CXObfuscatorOBF.getInstance().deobfuscate(aValue);
	}

	/**
	 * @param aValue
	 * @return
	 */
	@Override
	protected String encode(final String aValue) {
		return CXObfuscatorOBF.getInstance().obfuscate(aValue);
	}

	/**
	 * <pre>
	 * OBF:bW90ZGVwYXNzZQ0K
	 * notencoded
	 * </pre>
	 *
	 * @param aValue
	 *            the value prefixed or not by "OBF:"
	 * @return the encoded value if there
	 */
	@Override
	protected String extractEncodedData(final String aValue)
			throws CXPassphraseSchemeException {

		if (aValue == null || aValue.length() < OBF_PREFIX.length()) {
			return null;
		}
		final int wPosColumn = aValue.indexOf(':');

		if (wPosColumn < 0) {
			return null;
		}

		final String wSheme = aValue.substring(0, wPosColumn + 1);

		if (OBF_PREFIX.equalsIgnoreCase(wSheme)) {
			return aValue.substring(OBF_PREFIX.length());
		}

		throw new CXPassphraseSchemeException("Not a OBF scheme [%s]", wSheme);
	}
}
