package org.cohorte.utilities.crypto;

import java.io.File;

import org.psem2m.utilities.files.CXFile;

/**
 * the type of keystore.
 * 
 * See the KeyStore section in the Java Cryptography Architecture Standard
 * Algorithm Name Documentation for information about standard keystore types.
 * 
 * <ul>
 * <li>jceks The proprietary keystore implementation provided by the SunJCE
 * provider.
 * <li>jks The proprietary keystore implementation provided by the SUN provider.
 * <li>dks A domain keystore is a collection of keystores presented as a single
 * logical keystore. It is specified by configuration data whose syntax is
 * described in DomainLoadStoreParameter.
 * <li>pkcs11 A keystore backed by a PKCS #11 token.
 * <li>pkcs12 The transfer syntax for personal identity information as defined
 * in PKCS #12.
 * </ul>
 * 
 * @see http
 *      ://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames
 *      .html
 * 
 * @author ogattaz
 *
 */
public enum EKeystoreType {
	JKS(new String[] { "jks" }), DKS(new String[] { "dks" }), PKCS11(new String[] { "pkcs11", "p11" }), PKCS12(new String[] { "pkcs12", "p12" });
	
	// ATTENTION : the extension array MUST have ONE or more element

	private final String[] pExtensions;

	/**
	 * @param aExtensions
	 */
	EKeystoreType(String[] aExtensions) {
		pExtensions = aExtensions;
	}
	
	public String getExtension(){
		return pExtensions[0];
	}

	/**
	 * @param aExtension
	 * @return
	 */
	public static EKeystoreType fromFileExtension(final String aExtension) {
		if (aExtension != null && !aExtension.isEmpty()) {
			for (EKeystoreType wType : EKeystoreType.values()) {
				for (String wExtension : wType.pExtensions) {
					if (wExtension.equalsIgnoreCase(aExtension)) {
						return wType;
					}
				}
			}
		}
		return JKS;
	}

	/**
	 * @param aFile
	 * @return
	 */
	public static EKeystoreType fromFileExtension(final File aFile) {

		if (aFile != null) {
			return fromFileExtension(new CXFile(aFile).getExtension());
		}
		return JKS;
	}

}
