package org.cohorte.utilities.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.CXDateTime;
import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.CXTimer;

/**
 * An instance of CRsaKeyContext contains
 * 
 * @author ogattaz
 * 
 */
public class CRsaKeyContext extends CX509Certificate {

	private final CXTimer pCertificatTimer;
	private final KeyPair pKeyPair;
	private final CXTimer pKeyTimer;
	private final long pTimeStamp;

	/**
	 * @param aKeyPair
	 * @param aKeyTimer
	 *            the timer containing the duration of the KeyPair generation
	 * @param aX509Certificate
	 * @param aCertificatTimer
	 *            the timer containing the duration of the certificate
	 *            generation
	 * @throws CertificateEncodingException
	 */
	CRsaKeyContext(final KeyPair aKeyPair, final CXTimer aKeyTimer,
			final X509Certificate aX509Certificate,
			final CXTimer aCertificatTimer) throws CertificateEncodingException {
		super(aX509Certificate);

		pTimeStamp = System.currentTimeMillis();
		pKeyPair = aKeyPair;
		pKeyTimer = aKeyTimer;
		pCertificatTimer = aCertificatTimer;
	}

	/**
	 * @return a formated string ("%6.3f") containing the duration in in
	 *         milliseconds with microesconds
	 */
	public String getCertificatDuration() {
		return pCertificatTimer.getDurationStrMilliSec();
	}

	/**
	 * @return a formated string ("%6.3f") containing the duration in in
	 *         milliseconds with microesconds
	 */
	public String getKeyDuration() {
		return pKeyTimer.getDurationStrMilliSec();
	}

	/**
	 * @return the private Key
	 */
	public PrivateKey getPrivate() {
		return pKeyPair.getPrivate();
	}

	/**
	 * @return
	 */
	public String getPrivateInfos() {
		StringBuilder wSB = new StringBuilder();
		CXStringUtils.appendKeyValInBuff(wSB, getPrivate().getClass()
				.getSimpleName(), getPrivate().getFormat());
		wSB.append('\n');
		CXStringUtils.appendKeyValInBuff(wSB, "  size", getPrivateSize());
		wSB.append('\n');
		CXStringUtils.appendKeyValInBuff(wSB, "  key",
				CXBytesUtils.bytesToHexaString(getPrivate().getEncoded()));

		return wSB.toString();

	}

	/**
	 * @return the number of bits of the private key
	 */
	public int getPrivateSize() {
		return getPrivate().getEncoded().length * 8;
	}

	/**
	 * @return the public Key
	 */
	public PublicKey getPublic() {
		return pKeyPair.getPublic();
	}

	/**
	 * @return
	 */
	public String getPublicInfos() {
		return getPublic().toString();
	}

	/**
	 * @return
	 */
	public long getTimeStamp() {
		return pTimeStamp;
	}

	/**
	 * @return
	 */
	public String getTimeStampIso8601() {
		return CXDateTime.getIso8601TimeStamp(getTimeStamp());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder wSB = new StringBuilder();
		CXStringUtils.appendKeyValInBuff(wSB, "TimeStamp",
				getTimeStampIso8601());
		wSB.append('\n');
		CXStringUtils.appendKeyValInBuff(wSB, "KeyDuration", getKeyDuration());
		wSB.append('\n');
		CXStringUtils.appendKeyValInBuff(wSB, "CertificatDuration",
				getCertificatDuration());
		wSB.append('\n');
		CXStringUtils.appendKeyValInBuff(wSB, "SigAlgName", getCertificate()
				.getSigAlgName());
		wSB.append('\n');
		CXStringUtils.appendKeyValInBuff(wSB, "SigAlgOID", getCertificate()
				.getSigAlgOID());
		wSB.append('\n');
		CXStringUtils.appendKeyValInBuff(wSB, "CertificateStreamSize",
				getCertificateStreamDerSize());
		wSB.append('\n');
		CXStringUtils.appendKeyValInBuff(wSB, "CertificateBase64Size",
				getCertificatePemBase64Size());
		return wSB.toString();
	}
}
