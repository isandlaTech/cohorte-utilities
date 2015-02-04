package org.cohorte.utilities.crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * Every implementation of the Java platform is required to support the
 * following standard Cipher transformations with the keysizes in parentheses:
 * <ul>
 * <li>AES/CBC/NoPadding (128)</li>
 * <li>AES/CBC/PKCS5Padding (128)</li>
 * <li>AES/ECB/NoPadding (128)</li>
 * <li>AES/ECB/PKCS5Padding (128)</li>
 * </ul>
 * 
 * @see http://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
 * 
 * 
 * @see http://stackoverflow.com/questions/10759392/java-aes-encryption-and-
 *      decryption
 * 
 * @author ogattaz
 * 
 */
public class CDecoderAES {

	public final static String AES_ALGORITHM = "AES";

	public final static String AES_ALGORITHM_NOPADDED = "AES/CBC/NoPadding";

	public final static int AES_SIZE = 128;

	/**
	 * CryptoJS supports by default the mode : CBC CryptoJS supports by default
	 * the padding schemes: : Pkcs7
	 * 
	 * @see https://code.google.com/p/crypto-js/#Block_Modes_and_Padding
	 */

	public final static String CIPHER_TRANSPHOMRATION = "AES/CBC/PKCS5Padding";

	private final CAesKeyContext pAesKeyContext;

	public final IActivityLogger pLogger;

	/**
	 * 
	 */
	public CDecoderAES() {
		this(CActivityLoggerNull.getInstance());
	}

	/**
	 * @param aLogger
	 */
	public CDecoderAES(final IActivityLogger aLogger) {
		this(aLogger, null);
	}

	/**
	 * @param aLogger
	 * @param aCAesKeyContext
	 */
	public CDecoderAES(final IActivityLogger aLogger,
			CAesKeyContext aAesKeyContext) {
		super();
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();;
		pAesKeyContext = aAesKeyContext;

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "<init>", "instanciated OK",
					haspAesKeyContext());
		}
	}

	/**
	 * @param aEncryptedBytes
	 * @return
	 * @throws Exception
	 */
	public String decryptAES(byte[] aEncryptedBytes) throws Exception {

		if (!haspAesKeyContext()) {
			throw new Exception("can't decrypt, no AesKeyContext available");
		}
		return decryptAES(aEncryptedBytes, pAesKeyContext.getAesKey(),
				pAesKeyContext.getAesIv());

	}

	/**
	 * @param aEncryptedBytes
	 * @param aAesKey
	 * @param aAesIv
	 * @return
	 * @throws ...Exception
	 */
	public String decryptAES(byte[] aEncryptedBytes, byte[] aAesKey,
			byte[] aAesIv) throws NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, IOException,
			InvalidAlgorithmParameterException {

		Key wKey = new SecretKeySpec(aAesKey, AES_ALGORITHM);

		Cipher wCipher = Cipher.getInstance(CIPHER_TRANSPHOMRATION);

		wCipher.init(Cipher.DECRYPT_MODE, wKey, new IvParameterSpec(aAesIv));

		byte[] wDecryptedBytes = wCipher.doFinal(aEncryptedBytes);

		String wData = new String(wDecryptedBytes, "UTF-8");

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(
					this,
					"decryptAES",
					"EncryptedBytes.len=[%s] DecryptedBytes.len=[%s] Data=[%s]",
					aEncryptedBytes.length, wDecryptedBytes.length, wData);
		}

		return wData;
	}

	/**
	 * @param aEncryptedBytes
	 * @param aAesKeyHexBinary
	 * @return
	 * @throws ...Exception
	 */
	public String decryptAES(byte[] aEncryptedBytes, String aAesKeyHexBinary,
			String aAesIvHexBinary) throws NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, IOException,
			InvalidAlgorithmParameterException {

		byte[] wKeyBytes = DatatypeConverter.parseHexBinary(aAesKeyHexBinary);
		byte[] wIvBytes = DatatypeConverter.parseHexBinary(aAesIvHexBinary);

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "decryptAES",
					"Key len=[%s] raw=[%s] Bytes=[%s]", wKeyBytes.length,
					aAesKeyHexBinary, CXBytesUtils.bytesToHexaString(wKeyBytes));
			pLogger.logDebug(this, "decryptAES",
					"Iv  len=[%s] raw=[%s] Bytes=[%s]", wIvBytes.length,
					aAesIvHexBinary, CXBytesUtils.bytesToHexaString(wIvBytes));
		}
		return decryptAES(aEncryptedBytes, wKeyBytes, wIvBytes);

	}

	/**
	 * @param aBase64BinaryData
	 * @return
	 * @throws ...Exception
	 */
	public String decryptAESBase64(String aBase64BinaryData)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, IOException,
			InvalidAlgorithmParameterException, Exception {

		if (!haspAesKeyContext()) {
			throw new Exception("can't decrypt, no AesKeyContext available");
		}
		return decryptAES(
				DatatypeConverter.parseBase64Binary(aBase64BinaryData),
				pAesKeyContext.getAesKey(), pAesKeyContext.getAesIv());

	}

	/**
	 * @param aBase64BinaryData
	 * @param aAesKeyHexBinary
	 * @param aAesIvHexBinary
	 * @return
	 * @throws ...Exception
	 * 
	 */
	public String decryptAESBase64(String aBase64BinaryData,
			String aAesKeyHexBinary, String aAesIvHexBinary)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, IOException,
			InvalidAlgorithmParameterException {

		return decryptAES(
				DatatypeConverter.parseBase64Binary(aBase64BinaryData),
				aAesKeyHexBinary, aAesIvHexBinary);
	}

	/**
	 * @param aHexBinaryData
	 * @return
	 * @throws ...Exception
	 * 
	 */
	public String decryptAESHexBinary(String aHexBinaryData)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, IOException,
			InvalidAlgorithmParameterException, Exception {

		if (!haspAesKeyContext()) {
			throw new Exception("can't decrypt, no AesKeyContext available");
		}
		return decryptAES(DatatypeConverter.parseHexBinary(aHexBinaryData),
				pAesKeyContext.getAesKey(), pAesKeyContext.getAesIv());
	}

	/**
	 * @param aHexBinaryData
	 * @param aAesKeyHexBinary
	 * @param aAesIvHexBinary
	 * @return
	 * @throws ...Exception
	 * 
	 */
	public String decryptAESHexBinary(String aHexBinaryData,
			String aAesKeyHexBinary, String aAesIvHexBinary)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, IOException,
			InvalidAlgorithmParameterException {

		return decryptAES(DatatypeConverter.parseHexBinary(aHexBinaryData),
				aAesKeyHexBinary, aAesIvHexBinary);
	}

	/**
	 * @param aData
	 * @return
	 * @throws ...Exception
	 */
	public byte[] encryptAES(String aData) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, InvalidAlgorithmParameterException,
			Exception {

		if (!haspAesKeyContext()) {
			throw new Exception("can't crypt, no AesKeyContext available");
		}
		return encryptAES(aData, pAesKeyContext.getAesKey(),
				pAesKeyContext.getAesIv());
	}

	/**
	 * @param aData
	 * @param aAesKey
	 * @param aIV
	 * @return
	 * @throws ...Exception
	 */
	public byte[] encryptAES(String aData, byte[] aAesKey, byte[] aIV)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException {

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "encryptAES", "Key.len=[%s] Key.bytes=[%s]",
					aAesKey.length, CXBytesUtils.bytesToHexaString(aAesKey));

			pLogger.logDebug(this, "encryptAES", "Iv.len=[%s] Iv.bytes=[%s]",
					aIV.length, CXBytesUtils.bytesToHexaString(aIV));
		}

		Key wKey = new SecretKeySpec(aAesKey, AES_ALGORITHM);

		Cipher wCipher = Cipher.getInstance(CIPHER_TRANSPHOMRATION);

		wCipher.init(Cipher.ENCRYPT_MODE, wKey, new IvParameterSpec(aIV));

		byte[] wDecryptedBytes = aData.getBytes("UTF-8");

		byte[] wEncryptedBytes = wCipher.doFinal(wDecryptedBytes);

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "encryptAES",
					"DecryptedBytes.len=[%s] EncryptedBytes.len=[%s] ",
					wEncryptedBytes.length, wDecryptedBytes.length);
		}
		return wEncryptedBytes;
	}

	/**
	 * @param aData
	 * @param aAesKey
	 * @return
	 * @throws ...Exception
	 */
	public byte[] encryptAES(String aData, String aAesKey, String aIV)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException {

		byte[] wKeyBytes = DatatypeConverter.parseHexBinary(aAesKey);
		byte[] wIvBytes = DatatypeConverter.parseHexBinary(aIV);

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "encryptAES",
					"Key len=[%s] raw=[%s] Bytes=[%s]", wKeyBytes.length,
					aAesKey, CXBytesUtils.bytesToHexaString(wKeyBytes));

			pLogger.logDebug(this, "encryptAES",
					"Iv  len=[%s] raw=[%s] Bytes=[%s]", wIvBytes.length, aIV,
					CXBytesUtils.bytesToHexaString(wIvBytes));
		}

		return encryptAES(aData, wKeyBytes, wIvBytes);
	}

	/**
	 * @param aData
	 * @return
	 * @throws ...Exception
	 */
	public String encryptAESBase64(String aData) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, InvalidAlgorithmParameterException,
			Exception {

		if (!haspAesKeyContext()) {
			throw new Exception("can't crypt, no AesKeyContext available");
		}
		return encryptAESBase64(aData, pAesKeyContext.getAesKey(),
				pAesKeyContext.getAesIv());
	}

	/**
	 * @param aData
	 * @param aAesKey
	 * @param aAesIv
	 * @return
	 * @throws ...Exception
	 */
	public String encryptAESBase64(String aData, byte[] aAesKey, byte[] aAesIv)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException {

		String wData = DatatypeConverter.printBase64Binary(encryptAES(aData,
				aAesKey, aAesIv));

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "encryptAESBase64", "EncryptedData=[%s]",
					wData);
		}
		return wData;
	}

	/**
	 * @param aData
	 * @param aAesKeyHexBinary
	 * @param aAesIvHexBinary
	 * @return
	 * @throws ...Exception
	 */
	public String encryptAESBase64(String aData, String aAesKeyHexBinary,
			String aAesIvHexBinary) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, InvalidAlgorithmParameterException {

		String wData = DatatypeConverter.printBase64Binary(encryptAES(aData,
				aAesKeyHexBinary, aAesIvHexBinary));

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "encryptAESBase64", "EncryptedData=[%s]",
					wData);
		}
		return wData;
	}

	/**
	 * @param aData
	 * @param aAesKey
	 * @param aIV
	 * @return
	 * @throws ...Exception
	 */
	public String encryptAESHexBinary(String aData, byte[] aAesKey, byte[] aIV)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, UnsupportedEncodingException,
			InvalidAlgorithmParameterException {

		String wData = DatatypeConverter.printHexBinary(encryptAES(aData,
				aAesKey, aIV));

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "encryptAESHexBinary",
					"encryptAESBase64=[%s]", wData);
		}
		return wData;
	}

	/**
	 * @param aData
	 * @param aAesKeyHexBinary
	 * @param aAesIvHexBinary
	 * @return
	 * @throws ...Exception
	 */
	public String encryptAESHexBinary(String aData, String aAesKeyHexBinary,
			String aAesIvHexBinary) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, InvalidAlgorithmParameterException {

		String wData = DatatypeConverter.printHexBinary(encryptAES(aData,
				aAesKeyHexBinary, aAesIvHexBinary));

		if (pLogger.isLogDebugOn()) {
			pLogger.logDebug(this, "encryptAESBase64", "EncryptedData=[%s]",
					wData);
		}
		return wData;
	}

	/**
	 * @return
	 */
	public boolean haspAesKeyContext() {
		return pAesKeyContext != null;
	}
}
