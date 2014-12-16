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

		pLogger.logDebug(this, "<init>", "instanciated. RsaKeyContext.TimeStamp[%s]", pRsaKeyContext.getTimeStampIso8601());
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

		Cipher wCipherRsa = Cipher.getInstance(ALGORITHM_RSA);

		wCipherRsa.init(Cipher.DECRYPT_MODE, decryptionKey);

		byte[] utf8 = wCipherRsa.doFinal(buffer);

		return new String(utf8, CXBytesUtils.ENCODING_UTF_8);
	}

	/**
	 * @param aBase64Data
	 * @return
	 * @throws ...Exception
	 */
	public String decryptRSABase64(final String aBase64Data)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException {

		byte[] wData = DatatypeConverter.parseBase64Binary(aBase64Data);

		return decryptRSA(pRsaKeyContext.getPrivate(), wData);
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

		byte[] wData = DatatypeConverter.parseHexBinary(aHexBinaryData);

		return decryptRSA(pRsaKeyContext.getPrivate(), wData);
	}

	/**
	 * @param pubkey
	 * @param text
	 * @return
	 * @throws ...Exception
	 * @see http 
	 *      ://stackoverflow.com/questions/10517930/how-to-encrypt-decrypt-with
	 *      -rsa-keys-in-java
	 */
	private byte[] encryptRSA(Key pubkey, String text)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

		Cipher wRSA = Cipher.getInstance("RSA");

		wRSA.init(Cipher.ENCRYPT_MODE, pubkey);

		return wRSA.doFinal(text.getBytes());
	}

	/**
	 * @param aData
	 * @return
	 * @throws ...Exception
	 */
	public String encryptRSABase64(String aData) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {

		String wData = DatatypeConverter.printBase64Binary(encryptRSA(
				pRsaKeyContext.getPublic(), aData));

		pLogger.logDebug(this, "encryptRSABase64", "Data=[%s]", wData);

		return wData;
	}

	/**
	 * @param aData
	 * @return
	 * @throws ...Exception
	 */
	public String encryptRSAHexBinary(String aData) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {

		String wData = DatatypeConverter.printHexBinary(encryptRSA(
				pRsaKeyContext.getPublic(), aData));

		pLogger.logDebug(this, "encryptRSAHexBinary", "Data=[%s]", wData);

		return wData;
	}
}
