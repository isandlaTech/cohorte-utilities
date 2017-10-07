package org.cohorte.utilities.security;

import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.CXException;

import javax.xml.bind.DatatypeConverter;

/**
 * MOD_355
 * 
 * Base64 encoding and decoding performance =>
 * 
 * Sun has added another Base64 implementation in Java 6 (thanks to Thomas
 * Darimont for his remainder!): it was hidden in javax.xml.bind package and was
 * unknown to many developers.
 * 
 * javax.xml.bind.DatatypeConverter;
 * 
 * <pre>
 *  Encode         Decode       Encode       Decode       Encode       Encode           Decode
 *  Name           100 bytes    100 bytes    1000 bytes   1000 bytes   200000000 bytes  200000000 bytes
 *  ---------------------------------------------------------------------------------------------
 *  JavaXmlImpl    0.721 sec    1.067 sec    0.664 sec    0.947 sec    0.689 sec        0.885 sec
 *  Java8Impl      0.808 sec    1.101 sec    0.712 sec    0.913 sec    0.699 sec        0.887 sec
 *  MiGBase64Impl  0.887 sec    2.113 sec    0.788 sec    1.978 sec    0.812 sec        1.928 sec
 *  IHarderImpl    1.179 sec    2.645 sec    1.012 sec    2.439 sec    0.976 sec        2.431 sec
 *  ApacheImpl     4.113 sec    4.733 sec    2.308 sec    2.667 sec    2.205 sec        2.552 sec
 *  GuavaImpl      3.305 sec    4.178 sec    3.12 sec     3.755 sec    3.102 sec        3.744 sec
 *  SunImpl       11.153 sec    8.281 sec    3.992 sec    4.533 sec    3.289 sec        4.096 sec
 * </pre>
 * 
 * @see http ://java-performance.info/base64-encoding-and-decoding-performance/
 * 
 * @author ogattaz
 * 
 */
public class CXPassphraseB64 extends CXAbstractPassphrase {

	/**
	 * @see https://tools.ietf.org/html/rfc3548
	 */
	public static final String B64_PREFIX_B64 = "b64:";
	public static final String B64_PREFIX_BASE64 = "base64:";
	public static final String B64_PREFIX_BASIC = "basic:";

	// "b64:" or "basic:" or "base64:"
	public static final String[] B64_PREFIXES = { B64_PREFIX_B64, B64_PREFIX_BASIC, B64_PREFIX_BASE64 };

	/**
	 * @param aNested
	 * @throws CXPassphraseSchemeException
	 */
	public CXPassphraseB64(final IXPassphrase aNested) throws CXPassphraseSchemeException {
		super(CXPassphraseType.B64, aNested);
	}

	/**
	 * <pre>
	 * b64:bW90ZGVwYXNzZQ0K
	 * basic:bW90ZGVwYXNzZQ0K
	 * base64:bW90ZGVwYXNzZQ0K
	 * OBF:1ri71v1r1v2n1ri71shq1ri71shs1ri71v1r1v2n1ri7
	 * notencoded
	 * </pre>
	 * 
	 * @param aValue
	 *            the value prefixed or not by "b64:" or "base64:" or "basic:"
	 * @param aValue
	 *            a encoded or not passphrase
	 * @throws CXPassphraseSchemeException
	 *             if the schem is not suppported by this class
	 */
	public CXPassphraseB64(final String aValue) throws CXPassphraseSchemeException,InstantiationException {
		super(CXPassphraseType.B64, aValue);
	}

	/**
	 * 
	 * 
	 * 
	 * @param aValue
	 *            A string containing lexical representation of
	 *            xsd:base64Binary. (lexicalXSDBase64Binary)
	 * @return
	 */
	protected String decode(final String aValue) {
		try {
			// Converts the string argument into an array of bytes.
			return new String(DatatypeConverter.parseBase64Binary(aValue));
		}
		// IllegalArgumentException - if string parameter does not conform to
		// lexical value space defined in XML Schema Part 2: Datatypes for
		// xsd:base64Binary
		catch (Exception e) {
			return String.format("Decoding error. Value=[%s1] %s", aValue,
					CXException.eCauseMessagesInString(e));
		}
	}

	/**
	 * @param aValue
	 * @return A string containing a lexical representation of xsd:base64Binary
	 */
	protected String encode(final String aValue) {
		try {
			// Converts an array of bytes into a string.
			return DatatypeConverter.printBase64Binary(aValue.getBytes(CXBytesUtils.ENCODING_UTF_8));
		}
		// IllegalArgumentException - if val is null.
		catch (Exception e) {
			return String.format("Encoding error. Value=[%s1] %s", aValue,
					CXException.eCauseMessagesInString(e));
		}
	}

	/**
	 * <pre>
	 * b64:bW90ZGVwYXNzZQ0K
	 * basic:bW90ZGVwYXNzZQ0K
	 * base64:bW90ZGVwYXNzZQ0K
	 * notencoded
	 * </pre>
	 * 
	 * @param aValue
	 *            the value prefixed or not by "b64:" or "base64:" or "basic:"
	 * @return the encoded value if there
	 * 
	 * @see org.cohorte.utilities.security.CXAbstractPassphrase#extractEncodedData(java.lang.String)
	 */
	@Override
	protected String extractEncodedData(final String aValue) throws CXPassphraseSchemeException {

		if (aValue == null || aValue.length() < B64_PREFIX_B64.length()) {
			return null;
		}
		int wPosColumn = aValue.indexOf(':');

		if (wPosColumn < 0) {
			return null;
		}

		String wSheme = aValue.substring(0, wPosColumn + 1);

		for (String wKnownPrefix : B64_PREFIXES) {
			if (wKnownPrefix.equalsIgnoreCase(wSheme)) {
				return aValue.substring(wKnownPrefix.length());
			}
		}
		throw new CXPassphraseSchemeException("Not a Base64 scheme [%s]", wSheme);
	}

}
