package tests;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.cohorte.utilities.crypto.CAesKeyContext;
import org.cohorte.utilities.crypto.CDecoderAES;
import org.psem2m.utilities.CXTimer;

/**
 * MOD_OG_20150717 : CTestAesSample extends CTest
 *
 * @author ogattaz
 *
 * @see http://stackoverflow.com/questions/11707976/cryptography-in-java
 *
 */
public class CTestAesSample extends CTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int wExitCode = 0;
		CTestAesSample wTest = null;
		try {
			wTest = new CTestAesSample();
			wTest.doTest();
		} catch (final Exception e) {
			e.printStackTrace();
			wExitCode = 1;
		} finally {

			wTest.destroy();
		}
		System.exit(wExitCode);
	}

	/**
	 *
	 */
	public CTestAesSample() {
		super();
		pLogger.setLevel(Level.ALL);
		pLogger.logInfo(this, "<init>", "Logger: %s", pLogger.toDescription());
	}

	/**
	 * @throws ...Exception
	 */
	@Override
	void doTest() throws Exception {

		pLogger.logInfo(this, "doTest", "BEGIN");
		final int wNbCopy = 20;

		pLogger.logInfo(this, "doTest",
				"========== Encrypt and Decrypt using AES in CBC mode");

		testStackOverflow(buildText("This string is the first secret message.",
				wNbCopy));

		pLogger.logInfo(this, "doTest",
				"========== Encrypt and Decrypt using CAesKeyContext");

		testCryptDecrypt(buildText("This string is the second secret message.",
				wNbCopy));

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
		pLogger.logInfo(this, "testCryptDecrypt", "Input Plaintext=[%s]",
				aPlainText);

		final CXTimer wTimer = CXTimer.newStartedTimer();

		final CAesKeyContext wCAesContext = new CAesKeyContext(pLogger);

		final CDecoderAES wCDecoderAES = new CDecoderAES(pLogger, wCAesContext);

		final byte[] wCiphertext = wCDecoderAES.encryptAES(aPlainText,
				wCAesContext.getAesKey(), wCAesContext.getAesIv());

		pLogger.logInfo(this, "testStackOverflow", "Ciphertext=[%s]",
				asHex(wCiphertext));

		final String wDecrypted = wCDecoderAES.decryptAES(wCiphertext,
				wCAesContext.getAesKey(), wCAesContext.getAesIv());

		wTimer.stop();

		pLogger.logInfo(this, "testCryptDecrypt", "Output Plaintext=[%s]",
				wDecrypted);
		pLogger.logInfo(this, "testCryptDecrypt", "Duration=[%s]",
				wTimer.getDurationStrMicroSec());

	}

	/**
	 * Encrypt a sample message using AES in CBC mode with a random IV genrated
	 * using SecyreRandom.
	 *
	 * @see http://stackoverflow.com/questions/11707976/cryptography-in-java
	 */
	private void testStackOverflow(String aPlainText) {
		try {

			pLogger.logInfo(this, "testStackOverflow", "Input Plaintext=[%s]",
					aPlainText);

			final CXTimer wTimer = CXTimer.newStartedTimer();

			// generate a key AES. To use 256 bit keys, you need the
			// "unlimited strength" encryption policy files from Sun.
			final KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);

			final byte[] wAesKey = keygen.generateKey().getEncoded();
			final SecretKeySpec wAesKeySpec = new SecretKeySpec(wAesKey, "AES");

			pLogger.logInfo(this, "testStackOverflow", "AesKey=[%s] size=[%d]",
					asHex(wAesKey), wAesKey.length);

			// build the initialization vector (randomly).
			final SecureRandom random = new SecureRandom();
			// generate random 16 byte IV AES is always 16bytes
			final byte[] wAesIV = new byte[16];
			random.nextBytes(wAesIV);
			final IvParameterSpec wAesIvSpec = new IvParameterSpec(wAesIV);

			pLogger.logInfo(this, "testStackOverflow", "AesIV =[%s] size=[%d]",
					asHex(wAesIV), wAesIV.length);

			// initialize the cipher for encrypt mode
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, wAesKeySpec, wAesIvSpec);

			// encrypt the message
			final byte[] encrypted = cipher.doFinal(aPlainText.getBytes());

			pLogger.logInfo(this, "testStackOverflow", "Ciphertext=[%s]",
					asHex(encrypted));

			// reinitialize the cipher for decryption
			cipher.init(Cipher.DECRYPT_MODE, wAesKeySpec, wAesIvSpec);

			// decrypt the message
			final byte[] decrypted = cipher.doFinal(encrypted);

			final String wDecrypted = new String(decrypted);
			wTimer.stop();

			pLogger.logInfo(this, "testStackOverflow", "Output Plaintext=[%s]",
					wDecrypted);
			pLogger.logInfo(this, "testStackOverflow", "Duration=[%s]",
					wTimer.getDurationStrMicroSec());

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

}
