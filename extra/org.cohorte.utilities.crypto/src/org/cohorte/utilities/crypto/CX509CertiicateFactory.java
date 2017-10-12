package org.cohorte.utilities.crypto;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Date;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class CX509CertiicateFactory {

	public static CX509Certificate generateCertificate(final String aDn,
			final KeyPair aKeyPair, final int aNbDays) throws IOException,
			CertificateException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException {
		return generateCertificate(aDn, aKeyPair, aNbDays, null);
	}

	/*
	 * create a certificate without bouncy castle
	 *
	 * @see :
	 * https://stackoverflow.com/questions/1615871/creating-an-x509-certificate
	 * -in-java-without-bouncycastle
	 */
	/**
	 * Create a self-signed X.509 Example
	 *
	 * @param dn
	 *            the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
	 * @param pair
	 *            the KeyPair
	 * @param days
	 *            how many days from now the Example is valid for
	 * @param algorithm
	 *            the signing algorithm, eg "SHA1withRSA"
	 */
	public static CX509Certificate generateCertificate(final String aDn,
			final KeyPair aKeyPair, final int aNbDays, String aAlgorithm)
			throws IOException, CertificateException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			SignatureException {
		if (aAlgorithm == null) {
			aAlgorithm = "SHA1withRSA";
		}
		PrivateKey privkey = aKeyPair.getPrivate();
		X509CertInfo wInfo = new X509CertInfo();
		Date from = new Date();
		Date to = new Date(from.getTime() + aNbDays * 86400000l);
		CertificateValidity interval = new CertificateValidity(from, to); // compute
																			// certificate
																			// validatity
		BigInteger sn = new BigInteger(64, new SecureRandom());
		X500Name owner = new X500Name(aDn);

		wInfo.set(X509CertInfo.VALIDITY, interval);
		wInfo.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(sn));
		wInfo.set(X509CertInfo.SUBJECT, owner);
		wInfo.set(X509CertInfo.ISSUER, owner);
		wInfo.set(X509CertInfo.KEY,
				new CertificateX509Key(aKeyPair.getPublic()));
		wInfo.set(X509CertInfo.VERSION, new CertificateVersion(
				CertificateVersion.V3));
		AlgorithmId wAlgo = new AlgorithmId(
				AlgorithmId.md5WithRSAEncryption_oid);
		wInfo.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(wAlgo));

		// Sign the cert to identify the algorithm that's used.
		X509CertImpl wCert = new X509CertImpl(wInfo);
		wCert.sign(privkey, aAlgorithm);

		// Update the algorith, and resign.
		wAlgo = (AlgorithmId) wCert.get(X509CertImpl.SIG_ALG);
		wInfo.set(CertificateAlgorithmId.NAME + "."
				+ CertificateAlgorithmId.ALGORITHM, wAlgo);
		wCert = new X509CertImpl(wInfo);
		wCert.sign(privkey, aAlgorithm);
		return new CX509Certificate(wCert);

	}
}
