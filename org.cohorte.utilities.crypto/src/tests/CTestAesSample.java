package tests;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.cohorte.utilities.crypto.CAesKeyContext;
import org.cohorte.utilities.crypto.CDecoderAES;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 * 
 * @see http://stackoverflow.com/questions/11707976/cryptography-in-java
 * 
 */
public class CTestAesSample {

	public static void main(String[] args) {
		int wExitCode = 0;
		CTestAesSample wTest = null;
		try {
			wTest = new CTestAesSample();
			wTest.doTest();
		} catch (Exception e) {
			e.printStackTrace();
			wExitCode = 1;
		} finally {

			wTest.destroy();
		}
		System.exit(wExitCode);
	}

	private final IActivityLogger pLogger = CActivityLoggerBasicConsole
			.getInstance();

	/**
	 * 
	 */
	public CTestAesSample() {
		super();
	}

	/**
	 * Turns array of bytes into string
	 * 
	 * @param buf
	 *            Array of bytes to convert to hex string
	 * @return Generated hex string
	 */
	private String asHex(byte buf[]) {
		StringBuilder strbuf = new StringBuilder(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if ((buf[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Long.toString(buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	/**
	 * 
	 */
	private void destroy() {
		pLogger.logInfo(this, "destroy", "close the logger");
		pLogger.close();
	}

	/**
	 * @throws ...Exception
	 * 
	 */
	private void doTest() throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException,
			IOException {
		pLogger.logInfo(this, "doTest", "BEGIN");

		testStackOverflow("This string is a secret message.");

		testCryptDecrypt("This string is a secret message.");

		pLogger.logInfo(this, "doTest", "END");
	}

	/**
	 * @param aPlainText
	 * @throws ...Exception
	 */
	private void testCryptDecrypt(String aPlainText)
			throws NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException,
			IOException {

		pLogger.logInfo(this, "testCryptDecrypt", "Plaintext=[%s]", aPlainText);

		CAesKeyContext wCAesGenerator = new CAesKeyContext(pLogger);

		CDecoderAES wCDecoderAES = new CDecoderAES(pLogger);

		byte[] wCiphertext = wCDecoderAES.encryptAES(aPlainText,
				wCAesGenerator.getAesKey(), wCAesGenerator.getAesIv());

		pLogger.logInfo(this, "testStackOverflow", "Ciphertext=[%s]",
				asHex(wCiphertext));

		String wDecrypted = wCDecoderAES.decryptAES(wCiphertext,
				wCAesGenerator.getAesKey(), wCAesGenerator.getAesIv());

		pLogger.logInfo(this, "testCryptDecrypt", "Plaintext=[%s]", wDecrypted);
	}

	/**
	 * Encrypt a sample message using AES in CBC mode with a random IV genrated
	 * using SecyreRandom.
	 * 
	 * @see http://stackoverflow.com/questions/11707976/cryptography-in-java
	 */
	private void testStackOverflow(String aPlainText) {
		try {

			pLogger.logInfo(this, "testStackOverflow", "Plaintext=[%s]",
					aPlainText);

			// generate a key
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128); // To use 256 bit keys, you need the
								// "unlimited strength" encryption policy files
								// from Sun.
			byte[] wAesKey = keygen.generateKey().getEncoded();
			SecretKeySpec wAesKeySpec = new SecretKeySpec(wAesKey, "AES");

			pLogger.logInfo(this, "testStackOverflow", "AesKey=[%s] size=[%d]",
					asHex(wAesKey), wAesKey.length);

			// build the initialization vector (randomly).
			SecureRandom random = new SecureRandom();
			byte[] wAesIV = new byte[16];// generate random 16 byte IV AES is
											// always 16bytes
			random.nextBytes(wAesIV);
			IvParameterSpec wAesIvSpec = new IvParameterSpec(wAesIV);

			pLogger.logInfo(this, "testStackOverflow", " AesIV=[%s] size=[%d]",
					asHex(wAesIV), wAesIV.length);

			// initialize the cipher for encrypt mode
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, wAesKeySpec, wAesIvSpec);

			// encrypt the message
			byte[] encrypted = cipher.doFinal(aPlainText.getBytes());

			pLogger.logInfo(this, "testStackOverflow", "Ciphertext=[%s]",
					asHex(encrypted));

			// reinitialize the cipher for decryption
			cipher.init(Cipher.DECRYPT_MODE, wAesKeySpec, wAesIvSpec);

			// decrypt the message
			byte[] decrypted = cipher.doFinal(encrypted);

			pLogger.logInfo(this, "testCryptDecrypt", "Plaintext=[%s]",
					new String(decrypted));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
