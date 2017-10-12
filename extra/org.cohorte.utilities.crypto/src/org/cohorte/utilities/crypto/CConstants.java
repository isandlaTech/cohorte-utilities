package org.cohorte.utilities.crypto;

/**
 * @author ogattaz
 *
 */
public class CConstants {

	/**
	 * <pre>
	 * -----BEGIN CERTIFICATE-----
	 * MIIGQjCCBSqgAwIBAgIHBqOC7O8muTANBgkqhkiG9w0BAQsFADCBjDELMAkGA1UE
	 * BhMCSUwxFjAUBgNVBAoTDVN0YXJ0Q29tIEx0ZC4xKzApBgNVBAsTIlNlY3VyZSBE
	 * ...
	 * FuLYNlpsqSoUo5SNg/BTXrXu3z2CASmi/CE7p79FW9gjKD9TqiBIixY2hZ9e4J2c
	 * ReIcN+Nk1kPFGJhzZDNT2YoaCGqHWg==
	 * -----END CERTIFICATE----
	 * </pre>
	 */
	public final static String CERTIFICATE_BEGIN = "-----BEGIN CERTIFICATE-----";
	public final static String CERTIFICATE_END = "-----END CERTIFICATE-----";
	public final static String CERTIFICATE_FORMAT = CERTIFICATE_BEGIN
			+ "\n%s\n" + CERTIFICATE_END;
	public final static String CERTIFICATE_FORMAT_DUMP = "Dump certificate:\n"
			+ CERTIFICATE_FORMAT;

	/**
	 * <pre>
	 * -----BEGIN TRUSTED CERTIFICATE-----
	 * MIIEEjCCAvoCCQCGkepcXyP91zANBgkqhkiG9w0BAQUFADCByjEVMBMGA1UEAxMM
	 * cGF0cmljayBlbWluMScwJQYJKoZIhvcNAQkBFhhwYXRyaWNrIGVtaW5AYWdpbGl1
	 * ...
	 * r43hVd60TuKkiAMjpsMtU9Ud4+/pKEwaBpmZiSHnZTK3aiIsRaxR6tp1/y3QVPS6
	 * VOmSKWunqs2jrh6irM+toVUQTEt17SrRcOE2OHyEif6eh+W799k=
	 * -----END TRUSTED CERTIFICATE-----
	 * </pre>
	 */
	
	public final static String CERTIFICATE_TRUSTED_BEGIN = "-----BEGIN TRUSTED CERTIFICATE-----";
	public final static String CERTIFICATE_TRUSTED_END = "-----END TRUSTED CERTIFICATE-----";
	public final static String CERTIFICATE_TRUSTED_FORMAT = CERTIFICATE_TRUSTED_BEGIN
			+ "\n%s\n" + CERTIFICATE_TRUSTED_END;
	public final static String CERTIFICATE_TRUSTED_FORMAT_DUMP = "Dump trusted certificate:\n"
			+ CERTIFICATE_TRUSTED_END;

	/**
	 * <pre>
	 * </pre>
	 */
	public final static String PRIVATE_KEY_BEGIN = "-----BEGIN PRIVATE KEY-----";
	public final static String PRIVATE_KEY_END = "-----END PRIVATE KEY-----";
	public final static String PRIVATE_KEY_FORMAT = PRIVATE_KEY_BEGIN
			+ "\n%s\n" + PRIVATE_KEY_END;
	public final static String PRIVATE_KEY_FORMAT_DUMP = "Dump private key:\n"
			+ PRIVATE_KEY_FORMAT;

	/**
	 * <pre>
	 * </pre>
	 */

	public final static String PRIVATE_RSA_KEY_BEGIN = "-----BEGIN RSA PRIVATE KEY-----";
	public final static String PRIVATE_RSA_KEY_END = "-----END RSA PRIVATE KEY-----";
	public final static String PRIVATE_RSA_KEY_FORMAT = PRIVATE_RSA_KEY_BEGIN
			+ "\n%s\n" + PRIVATE_RSA_KEY_END;
	public final static String PRIVATE_RSA_KEY_FORMAT_DUMP = "Dump private RSA key:\n"
			+ PRIVATE_RSA_KEY_FORMAT;

	/**
	 * <pre>
	 * </pre>
	 */

	public final static String PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----";
	public final static String PUBLIC_KEY_END = "-----END PUBLIC KEY-----";

	public final static String PUBLIC_KEY_FORMAT = PUBLIC_KEY_BEGIN + "\n%s\n"
			+ PUBLIC_KEY_END;
	public final static String PUBLIC_KEY_FORMAT_DUMP = "Dump public key:\n"
			+ PUBLIC_KEY_FORMAT;

}
