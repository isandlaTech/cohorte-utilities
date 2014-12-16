package org.cohorte.utilities.crypto;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.xml.bind.DatatypeConverter;

/**
 * X509Certificate extensions
 * 
 * @author ogattaz
 * 
 */
public class CX509Certificate {

	public final static String CERTIFICATE_BEGIN = "-----BEGIN CERTIFICATE-----";

	public final static String CERTIFICATE_END = "-----END CERTIFICATE-----";

	/*
	 * .pem – (Privacy-enhanced Electronic Mail) Base64 encoded DER certificate,
	 * enclosed between "-----BEGIN CERTIFICATE-----" and
	 * "-----END CERTIFICATE-----"
	 */
	private String pCertificatePemBase64 = null;

	/*
	 * the DER encoded form of this certificate.
	 * 
	 * It is assumed that each certificate type would have only a single form of
	 * encoding; for example, X.509 certificates would be encoded as ASN.1 DER.
	 */
	private final byte[] pCertificateStream;

	/* the original X509 certificate */
	private final X509Certificate pX509Certificate;

	/**
	 * @param aX509Certificate
	 * @throws CertificateEncodingException
	 */
	public CX509Certificate(final X509Certificate aX509Certificate)
			throws CertificateEncodingException {
		super();

		if (aX509Certificate == null) {
			throw new NullPointerException("aX509Certificate must be not null");
		}

		// store the X509 certificate
		pX509Certificate = aX509Certificate;

		// gets the DER encoded form of this certificate.
		pCertificateStream = getCertificate().getEncoded();
	}

	/**
	 * @return
	 */
	public X509Certificate getCertificate() {
		return pX509Certificate;
	}

	/**
	 * 
	 * generate the Privacy-enhanced Electronic Mail Base64 encoded DER
	 * certificate, enclosed between "-----BEGIN CERTIFICATE-----" and
	 * "-----END CERTIFICATE-----"
	 * 
	 * <pre>
	 * -----BEGIN CERTIFICATE-----
	 * MIIB6DCCAVGgAwIBAgIJAKbiF2qeIrO+MA0GCSqGSIb3DQEBBQUAMDYxCzAJBgNVBAYTAkZSMREw
	 * DwYDVQQHEwhHcmVub2JsZTEUMBIGA1UEAxMLaXNhbmRsYVRlY2gwHhcNMTQwNDMwMTMxMjIyWhcN
	 * MTUwNDMwMTMxMjIyWjA2MQswCQYDVQQGEwJGUjERMA8GA1UEBxMIR3Jlbm9ibGUxFDASBgNVBAMT
	 * C2lzYW5kbGFUZWNoMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZTgmoTvb0oEyu5Fm0s0fV
	 * VMGpDavLCYgrYW7q6a25nrlC0nkkFXiwgG3QduuOmJcgBCkLEy40GnAN9m+OZeoB4uzBXIclVXO1
	 * q0WcqN3Ulf4AAoizfN0Dpz/oOiyWKgJR/QHzDACDztaTimiwkG6t7uV7UCHXpRjxRanKKoGllQID
	 * AQABMA0GCSqGSIb3DQEBBQUAA4GBAEJ8f3MHINOhOz0AbOaKP35Qx9qGBow0jKqmDoFVI1HAlQoB
	 * rL+E867ibjPPljiI0deinbqz8TGlp3j9UFH8Q0TSGS9zcsbYfadus+edG+sk9AuZc3bkMyx9l82N
	 * 1qvgGc3nHU+lzj4CrX2gtgIG1xMitLSw3sm0DAimxEiO0GR7
	 * -----END CERTIFICATE-----
	 * </pre>
	 * 
	 * @return a Privacy-enhanced Electronic Mail Base64 encoded DER certificate
	 * @throws CertificateEncodingException
	 * 
	 * @see http://en.wikipedia.org/wiki/X.509#
	 *      Extensions_informing_a_specific_usage_of_a_certificate
	 */
	public String getCertificatePemBase64() {
		if (pCertificatePemBase64 == null) {
			StringBuilder wSB = new StringBuilder();
			wSB.append(CERTIFICATE_BEGIN);
			String wCSB64 = getCertificateStreamBase64();
			int wMax = wCSB64.length();
			int wEnd;
			for (int wStart = 0; wStart < wMax; wStart += 76) {
				wEnd = wStart + 76;
				if (wEnd > wMax) {
					wEnd = wMax;
				}
				wSB.append('\n');
				wSB.append(wCSB64.substring(wStart, wEnd));
			}
			wSB.append('\n');
			wSB.append(CERTIFICATE_END);
			pCertificatePemBase64 = wSB.toString();
		}
		return pCertificatePemBase64;
	}

	/**
	 * @return the size of the Privacy-enhanced Electronic Mail Base64 encoded
	 *         DER certificate
	 * 
	 */
	public int getCertificatePemBase64Size() {
		return getCertificatePemBase64().length();
	}

	/**
	 * @return the base64 encoded stream of the certificate
	 * 
	 * @see http
	 *      ://java-performance.info/base64-encoding-and-decoding-performance/
	 */
	String getCertificateStreamBase64() {
		return DatatypeConverter.printBase64Binary(getCertificateStreamDer());
	}

	/**
	 * @return the DER encoded form of this certificate.
	 * 
	 * @see http
	 *      ://en.wikipedia.org/wiki/Distinguished_Encoding_Rules#DER_encoding
	 */
	public byte[] getCertificateStreamDer() {
		return pCertificateStream;
	}

	/**
	 * @return the size of the DER encoded form of this certificate.
	 */
	public int getCertificateStreamDerSize() {
		return getCertificateStreamDer().length;
	}

}
