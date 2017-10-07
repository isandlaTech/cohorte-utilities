package org.cohorte.utilities.security;

/**
 * MOD_355
 * 
 * 
 * @author ogattaz
 * 
 */
public class CXPassphraseRDM extends CXAbstractPassphrase {


	/**
	 * @see https://tools.ietf.org/html/rfc3548
	 */
	public static final String RDM_PREFIX = "RDM:";

	/**
	 * @param aNested
	 * @throws CXPassphraseSchemeException
	 */
	public CXPassphraseRDM(final IXPassphrase aNested) throws CXPassphraseSchemeException {
		super(CXPassphraseType.RDM, aNested);
	}

	/**
	 * <pre>
	 * RDM:JnqKUc\oEj4V1Jbxo<8NQsJl)K"g\oN>WSIKuW1p1//PYxH1L;N62K6y1La;nxphc>$O)H\Ln2]\J+tJV$p-/5Is$Htpxb*bD(L)J]tpHZA"V\oOs7,1L3}T
	 * notencoded
	 * </pre>
	 * 
	 * @param aValue
	 *            the value prefixed or not by "RDM:"
	 * @param aValue
	 *            a encoded or not passphrase
	 * @throws CXPassphraseSchemeException
	 *             if the schem is not suppported by this class
	 */
	public CXPassphraseRDM(final String aValue) throws CXPassphraseSchemeException,InstantiationException {
		super(CXPassphraseType.RDM, aValue);
	}

	/**
	 * @param aValue
	 * @return
	 */
	protected String decode(final String aValue) {
		return  CXObfuscatorRDM.getInstance().deobfuscate(aValue);
	}

	/**
	 * @param aValue
	 * @return
	 */
	protected String encode(final String aValue) {
		return  CXObfuscatorRDM.getInstance().obfuscate(aValue);
	}

	/**
	 * <pre>
	 * RDM:JnqKUc\oEj4V1Jbxo<8NQsJl)K"g\oN>WSIKuW1p1//PYxH1L;N62K6y1La;nxphc>$O)H\Ln2]\J+tJV$p-/5Is$Htpxb*bD(L)J]tpHZA"V\oOs7,1L3}T
	 * notencoded
	 * </pre>
	 * 
	 * @param aValue
	 *            the value prefixed or not by "RDM:"
	 * @return the encoded value if there
	 * 
	 * @see org.cohorte.utilities.security.CXAbstractPassphrase#extractEncodedData(java.lang.String)
	 */
	@Override
	protected String extractEncodedData(final String aValue) throws CXPassphraseSchemeException {

		if (aValue == null || aValue.length() < RDM_PREFIX.length()) {
			return null;
		}
		int wPosColumn = aValue.indexOf(':');

		if (wPosColumn < 0) {
			return null;
		}

		String wSheme = aValue.substring(0, wPosColumn + 1);

		if (RDM_PREFIX.equalsIgnoreCase(wSheme)) {
			return aValue.substring(RDM_PREFIX.length());
		}

		throw new CXPassphraseSchemeException("Not a RDM scheme [%s]", wSheme);
	}

}
