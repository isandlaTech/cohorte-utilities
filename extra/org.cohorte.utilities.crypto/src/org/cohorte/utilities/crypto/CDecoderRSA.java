package org.cohorte.utilities.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CDecoderRSA extends CDecoderAES {

	private final static String ALGORITHM_RSA = CRsaGenerator.ALGORITHM_GENERATE;

	private final CRsaKeyContext pRsaKeyContext;

	/**
	 *
	 */
	public CDecoderRSA(final CRsaKeyContext aRsaKeyContext) {
		this(CActivityLoggerNull.getInstance(), aRsaKeyContext);
	}

	/**
	 * @param aRsaKeyContext
	 */
	public CDecoderRSA(final IActivityLogger aLogger,
			final CRsaKeyContext aRsaKeyContext) {
		super(aLogger);
		pRsaKeyContext = aRsaKeyContext;

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "<init>",
					"instanciated. RsaKeyContext.TimeStamp[%s]",
					pRsaKeyContext.getTimeStampIso8601());
		}
	}

	/**
	 * @param decryptionKey
	 * @param buffer
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 * @see http
	 *      ://stackoverflow.com/questions/10517930/how-to-encrypt-decrypt-with
	 *      -rsa-keys-in-java
	 */
	private String decryptRSA(Key decryptionKey, byte[] buffer)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {

		final Cipher wCipherRsa = Cipher.getInstance(ALGORITHM_RSA);

		wCipherRsa.init(Cipher.DECRYPT_MODE, decryptionKey);

		final byte[] utf8 = wCipherRsa.doFinal(buffer);

		return new String(utf8, CXBytesUtils.ENCODING_UTF_8);
	}

	/**
	 * MOD_OG_20150717
	 *
	 * Decrypt (Cipher) using the given key (default)
	 *
	 * @param decryptionKey
	 * @param aBase64Data
	 * @return
	 * @throws ...Exception
	 */
	public String decryptRSABase64(final Key aDecryptionKey,
			final String aBase64Data) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException,
			UnsupportedEncodingException {

		final byte[] wData = DatatypeConverter.parseBase64Binary(aBase64Data);

		return decryptRSA(aDecryptionKey, wData);
	}

	/**
	 * Decrypt (Cipher) using private key (default)
	 *
	 * @param aBase64Data
	 * @return
	 * @throws ...Exception
	 */
	public String decryptRSABase64(final String aBase64Data)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {

		// MOD_OG_20150717
		return decryptRSABase64(pRsaKeyContext.getPrivate(), aBase64Data);
	}

	/**
	 * @param aHexBinaryData
	 * @return
	 * @throws ...Exception
	 */
	public String decryptRSAHexBinary(final String aHexBinaryData)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {

		final byte[] wData = DatatypeConverter.parseHexBinary(aHexBinaryData);

		return decryptRSA(pRsaKeyContext.getPrivate(), wData);
	}

	/**
	 * Encrypt (Cipher) using the given key
	 *
	 * @param aEncryptionKey
	 * @param aData
	 * @return
	 * @throws ...Exception
	 * @see http
	 *      ://stackoverflow.com/questions/10517930/how-to-encrypt-decrypt-with
	 *      -rsa-keys-in-java
	 */
	private byte[] encryptRSA(Key aEncryptionKey, String aData)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		final Cipher wRSA = Cipher.getInstance("RSA");

		wRSA.init(Cipher.ENCRYPT_MODE, aEncryptionKey);

		return wRSA.doFinal(aData.getBytes());
	}

	/**
	 * MOD_OG_20150717
	 *
	 * Encrypt (Cipher) using the given key
	 *
	 *
	 * @param aEncodingKey
	 * @param aData
	 *            a Base64 stream
	 * @return
	 * @throws ...Exception
	 */
	public String encryptRSABase64(final Key aEncodingKey, String aData)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {

		final String wData = DatatypeConverter.printBase64Binary(encryptRSA(
				aEncodingKey, aData));

		pLogger.logDebug(this, "encryptRSABase64", "Data=[%s]", wData);

		return wData;
	}

	/**
	 * Encrypt (Cipher) using the public key ogf the RSA key of the context
	 *
	 * @param aRawData
	 *            Data must not be longer than 117 bytes
	 * @return the base 64 representtaion
	 * @throws ...Exception
	 */
	public String encryptRSABase64(final String aRawData)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException {

		// MOD_OG_20150717
		return encryptRSABase64(pRsaKeyContext.getPublic(), aRawData);
	}

	/**
	 * @param aData
	 * @return
	 * @throws ...Exception
	 */
	public String encryptRSAHexBinary(String aData) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {

		final String wData = DatatypeConverter.printHexBinary(encryptRSA(
				pRsaKeyContext.getPublic(), aData));

		pLogger.logDebug(this, "encryptRSAHexBinary", "Data=[%s]", wData);

		return wData;
	}
}
