package org.cohorte.utilities.crypto;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 */
public class CAesKeyContext {

	private final byte[] pIV;

	private final byte[] pKey;

	public final IActivityLogger pLogger;

	/**
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * 
	 */
	public CAesKeyContext() throws UnsupportedEncodingException,
			NoSuchAlgorithmException {
		this(CActivityLoggerNull.getInstance());
	}

	/**
	 * @param aLogger
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public CAesKeyContext(final IActivityLogger aLogger)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		super();
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
		pIV = initIV();
		pKey = initKey();

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "<init>", "instanciated OK");
		}
	}

	/**
	 * @return
	 */
	public byte[] getAesIv() {
		return pIV;
	}

	/**
	 * @return
	 */
	public String getAesIvHexBinary() {
		return DatatypeConverter.printHexBinary(getAesIv());
	}

	/**
	 * @return
	 */
	public IvParameterSpec getAesIvSpec() {
		return new IvParameterSpec(getAesIv());
	}

	/**
	 * @return
	 */
	public byte[] getAesKey() {
		return pKey;

	}

	/**
	 * @return
	 */
	public String getAesKeyHexBinary() {
		return DatatypeConverter.printHexBinary(getAesKey());

	}

	/**
	 * @return
	 */
	public SecretKeySpec getAesKeySpec() {
		return new SecretKeySpec(getAesKey(), "AES");
	}

	/**
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] initIV() throws UnsupportedEncodingException,
			NoSuchAlgorithmException {

		SecureRandom random = new SecureRandom();
		// generate random 16 byte IV AES is always 16bytes
		byte[] wIV = new byte[16];
		random.nextBytes(wIV);
		return wIV;
	}

	/**
	 * generate a key
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] initKey() throws NoSuchAlgorithmException {

		KeyGenerator keygen = KeyGenerator.getInstance("AES");
		// To use 256 bit keys, you need the "unlimited strength" encryption
		// policy files from Sun.
		keygen.init(128);
		byte[] wKey = keygen.generateKey().getEncoded();

		return wKey;
	}

}
