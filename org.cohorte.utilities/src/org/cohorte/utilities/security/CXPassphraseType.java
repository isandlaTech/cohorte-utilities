package org.cohorte.utilities.security;

import java.util.ArrayList;
import java.util.List;

/**
 * Not an Enum to be extensible !
 *
 * @author ogattaz
 *
 */
public class CXPassphraseType {

	public static CXPassphraseType B64 = new CXPassphraseType(
			CXPassphraseB64.B64_PREFIX_BASE64);

	public static CXPassphraseType OBF = new CXPassphraseType(
			CXPassphraseOBF.OBF_PREFIX);

	public static CXPassphraseType RDM = new CXPassphraseType(
			CXPassphraseRDM.RDM_PREFIX);
	public static List<CXPassphraseType> sPassphraseTypes = new ArrayList<CXPassphraseType>();

	static {
		addPassphraseType(B64);
		addPassphraseType(OBF);
		addPassphraseType(RDM);
	}

	/**
	 * @param aCXAbstractPassphraseClass
	 */
	public static void addPassphraseType(final CXPassphraseType aPassphraseType) {
		sPassphraseTypes.add(0, aPassphraseType);
	}

	private final String pScheme;

	/**
	 * @param aScheme
	 */
	protected CXPassphraseType(final String aScheme) {
		pScheme = aScheme;
	}

	/**
	 * @return
	 */
	public String getScheme() {
		return pScheme;
	}

}
