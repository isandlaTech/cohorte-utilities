package org.cohorte.utilities.crypto;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.cohorte.utilities.encode.CBase64Decoder;

/**
 * @author ogattaz
 *
 */
public class CX509CertificateUtils {

	/**
	 * Converts a DER formatted certificate String to a {@link X509Certificate}
	 * instance.
	 *
	 * <pre>
	 * openssl x509 -outform der -in 20151122_certificate_recette.kmsuez.com.pem -out 20151122_certificate_recette.kmsuez.com.der
	 *
	 * <pre>
	 *
	 * PEM file
	 *
	 * <pre>
	 * -----BEGIN CERTIFICATE-----
	 * MIIGQjCCBSqgAwIBAgIHBqOC7O8muTANBgkqhkiG9w0BAQsFADCBjDELMAkGA1UE
	 * BhMCSUwxFjAUBgNVBAoTDVN0YXJ0Q29tIEx0ZC4xKzApBgNVBAsTIlNlY3VyZSBE
	 * ...
	 * q9wWu9/rDbCHbm77fJIld8nMrOnoDpwEks0q2FfndPCSg1bGlIH1W51c74atFTr1
	 * FuLYNlpsqSoUo5SNg/BTXrXu3z2CASmi/CE7p79FW9gjKD9TqiBIixY2hZ9e4J2c
	 * ReIcN+Nk1kPFGJhzZDNT2YoaCGqHWg==
	 * -----END CERTIFICATE----
	 * </pre>
	 *
	 * @param aFile
	 * @return
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static X509Certificate convertDerToX509Certificate(final File aFile) throws CertificateException,
			IOException {

		String wTemp = getFileContent(aFile);
		return convertDerToX509Certificate(wTemp);
	}

	/**
	 * @param pem
	 *            PEM formatted String
	 * @return a X509Certificate instance
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static X509Certificate convertDerToX509Certificate(final String aPEMCertificate)
			throws CertificateException, IOException {

		CertificateFactory wFactoryX509 = CertificateFactory.getInstance("X.509");
		InputStream is = new ByteArrayInputStream(aPEMCertificate.getBytes("UTF-8"));
		/*
		 * In the case of a certificate factory for X.509 certificates, the
		 * certificate provided in inStream must be DER-encoded and may be
		 * supplied in binary or printable (Base64) encoding. If the certificate
		 * is provided in Base64 encoding, it must be bounded at the beginning
		 * by -----BEGIN CERTIFICATE-----, and must be bounded at the end by
		 * -----END CERTIFICATE-----.
		 */
		return (X509Certificate) wFactoryX509.generateCertificate(is);
	}

	/**
	 * @param aFormatedKey
	 */
	private static String extractBase64FromFormatedKey(final String aFormatedKey, final String aHeader,
			final String aFooter) {

		// if the formated key or header or the footer is empty => do nothing
		if (isNulOrEmptyStr(aFormatedKey) || isNulOrEmptyStr(aHeader) || isNulOrEmptyStr(aFooter)) {
			return aFormatedKey;
		}
		int wFormatLength = aHeader.length() + aFooter.length();

		// if the length of the formated key is less than the lentgh of the
		// header and the footer => do nothing
		if (aFormatedKey.length() < wFormatLength) {
			return aFormatedKey;
		}

		int wPosHeader = aFormatedKey.indexOf(aHeader);
		// if there's no header => do nothing
		if (wPosHeader < 0) {
			return aFormatedKey;
		}
		int wPosStartB64 = wPosHeader + aHeader.length();

		int wPosFooter = aFormatedKey.indexOf(aFooter, wPosStartB64);
		// if there's no footer => do nothing
		if (wPosFooter < 0) {
			return aFormatedKey;
		}

		return aFormatedKey.substring(wPosStartB64, wPosFooter);
	}

	/**
	 * @param aFile
	 * @return
	 * @throws IOException
	 */
	private static String getFileContent(final File aFile) throws IOException {

		DataInputStream dis = null;
		try {
			dis = new DataInputStream(new FileInputStream(aFile));
			byte[] keyBytes = new byte[(int) aFile.length()];
			dis.readFully(keyBytes);
			return new String(keyBytes);
		} finally {
			if (dis != null) {
				dis.close();
			}
		}
	}

	/**
	 * @param aFile
	 * @param algorithm
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws IOException
	 *
	 * @see http 
	 *      ://stackoverflow.com/questions/11787571/how-to-read-pem-file-to-get
	 *      -private-and-public-key
	 */
	public static PrivateKey getPemPrivateKey(File aFile, String algorithm) throws InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException, IOException {

		String wTemp = getFileContent(aFile);

		return getPemPrivateKey(wTemp, algorithm);
	}

	/**
	 *
	 * @param aDerKey
	 * @param algorithm
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 *
	 * @see http 
	 *      ://stackoverflow.com/questions/11787571/how-to-read-pem-file-to-get
	 *      -private-and-public-key
	 */
	public static PrivateKey getPemPrivateKey(String aFormatedKey, String algorithm)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {

		if (aFormatedKey == null || aFormatedKey.isEmpty()) {
			throw new InvalidKeyException("Can't extract private key from null or empty string");
		}

		String privKeyPEM = null;
		if (aFormatedKey.contains(CConstants.PRIVATE_KEY_BEGIN)) {

			privKeyPEM = extractBase64FromFormatedKey(aFormatedKey, CConstants.PRIVATE_KEY_BEGIN + '\n',
					CConstants.PRIVATE_KEY_END);
		} else {
			privKeyPEM = extractBase64FromFormatedKey(aFormatedKey, CConstants.PRIVATE_RSA_KEY_BEGIN + '\n',
					CConstants.PRIVATE_RSA_KEY_END);

		}

		byte[] decoded = new CBase64Decoder(privKeyPEM).getBytes();

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
		KeyFactory kf = KeyFactory.getInstance(algorithm);
		return kf.generatePrivate(spec);
	}

	/**
	 * @param aFile
	 * @param algorithm
	 * @return
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * 
	 * @see http 
	 *      ://stackoverflow.com/questions/11787571/how-to-read-pem-file-to-get
	 *      -private-and-public-key
	 */
	public static PublicKey getPemPublicKey(File aFile, String algorithm) throws InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException, IOException {

		String wTemp = getFileContent(aFile);

		return getPemPublicKey(wTemp, algorithm);
	}

	/**
	 * @param aDerKey
	 * @param algorithm
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws Exception
	 *
	 * @see http 
	 *      ://stackoverflow.com/questions/11787571/how-to-read-pem-file-to-get
	 *      -private-and-public-key
	 */
	public static PublicKey getPemPublicKey(String aFormatedKey, String algorithm)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {

		if (aFormatedKey == null || aFormatedKey.isEmpty()) {
			throw new InvalidKeyException("Can't extract private key from null or empty string");
		}

		String wPublicKeyPEM = extractBase64FromFormatedKey(aFormatedKey, CConstants.PUBLIC_KEY_BEGIN + '\n',
				CConstants.PUBLIC_KEY_END);

		byte[] decoded = new CBase64Decoder(wPublicKeyPEM).getBytes();

		X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
		KeyFactory kf = KeyFactory.getInstance(algorithm);
		return kf.generatePublic(spec);
	}

	/**
	 * @param aStr
	 * @return
	 */
	private static boolean isNulOrEmptyStr(final String aStr) {
		return (aStr == null || aStr.isEmpty());
	}

}
