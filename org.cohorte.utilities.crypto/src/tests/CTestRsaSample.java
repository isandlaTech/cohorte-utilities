package tests;

import java.util.logging.Level;

import javax.xml.bind.DatatypeConverter;

import org.cohorte.utilities.crypto.CDecoderRSA;
import org.cohorte.utilities.crypto.CRsaGenerator;
import org.cohorte.utilities.crypto.CRsaKeyContext;

/**
 * This sample application demonstrates how to :
 * <ul>
 * <li>generate an RSA key
 * <li>get the X509 certificat containing the public key
 * <li>crypt and decrypt (cipher) using first the public or the private key
 * </ul>
 *
 * @author ogattaz
 *
 */
public class CTestRsaSample extends CTest {

	public static final String DISTINGUISEDNAME = "CN=isandlaTech,L=Meylan,C=FR";

	// Data must not be longer than 117 bytes
	static final String ORIGINAL = "This string is a secret information.";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int wExitCode = 0;
		CTest wTest = null;
		try {
			wTest = new CTestRsaSample();
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
	public CTestRsaSample() {
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

		final CRsaGenerator wRsaGenerator = new CRsaGenerator(pLogger,
				DISTINGUISEDNAME, 2048);

		final CRsaKeyContext wRsaKeyContext = wRsaGenerator
				.generateRsaKeyContext();

		pLogger.logInfo(this, "doTest", "CertificatePemBase64:\n%s",
				wRsaKeyContext.getCertificatePemBase64());

		pLogger.logInfo(this, "doTest", "Private:\n%s", DatatypeConverter
				.printBase64Binary(wRsaKeyContext.getPrivate().getEncoded()));

		pLogger.logInfo(this, "doTest", "Public:\n%s", DatatypeConverter
				.printBase64Binary(wRsaKeyContext.getPublic().getEncoded()));

		pLogger.logInfo(this, "doTest",
				"========== Encrypt with Public and Decrypt with Private using CRsaKeyContext");

		doTestPublicPrivate(wRsaKeyContext);

		pLogger.logInfo(this, "doTest",
				"========== Encrypt with Private and Decrypt with Public  using CRsaKeyContext");

		doTestPrivatePublic(wRsaKeyContext);

		pLogger.logInfo(this, "doTest", "END");
	}

	/**
	 * @param wCRsaKeyContext
	 * @throws Exception
	 */
	void doTestPrivatePublic(final CRsaKeyContext wCRsaKeyContext)
			throws Exception {

		final CDecoderRSA wRsaDecoder = new CDecoderRSA(pLogger,
				wCRsaKeyContext);

		pLogger.logDebug(this, "doTest", "RsaDecoder: %s", wRsaDecoder);

		pLogger.logDebug(this, "doTest", "Data: %s", ORIGINAL);

		final String wBase64Data = wRsaDecoder.encryptRSABase64(
				wCRsaKeyContext.getPrivate(), ORIGINAL);

		final String wDecrypted = wRsaDecoder.decryptRSABase64(
				wCRsaKeyContext.getPublic(), wBase64Data);

		pLogger.logDebug(this, "doTest", "Data: %s", wDecrypted);

	}

	/**
	 * @throws Exception
	 */
	/**
	 * @param wCRsaKeyContext
	 * @throws Exception
	 */
	void doTestPublicPrivate(final CRsaKeyContext wCRsaKeyContext)
			throws Exception {

		final CDecoderRSA wRsaDecoder = new CDecoderRSA(pLogger,
				wCRsaKeyContext);

		pLogger.logDebug(this, "doTest", "RsaDecoder: %s", wRsaDecoder);

		pLogger.logDebug(this, "doTest", "Data: %s", ORIGINAL);

		final String wBase64Data = wRsaDecoder.encryptRSABase64(
				wCRsaKeyContext.getPublic(), ORIGINAL);

		final String wDecrypted = wRsaDecoder.decryptRSABase64(
				wCRsaKeyContext.getPrivate(), wBase64Data);

		pLogger.logDebug(this, "doTest", "Data: %s", wDecrypted);

	}

}
