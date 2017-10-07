package test.cohorte.utilities.security;

import junit.framework.TestCase;

import org.cohorte.utilities.security.CXPassphraseB64;
import org.cohorte.utilities.security.CXPassphraseBuilder;
import org.cohorte.utilities.security.CXPassphraseOBF;
import org.cohorte.utilities.security.CXPassphraseParser;
import org.cohorte.utilities.security.IXPassphrase;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.logging.CActivityLoggerBasicConsole;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class TestCXPassphrase extends TestCase {

	IActivityLogger pLogger = CActivityLoggerBasicConsole.getInstance();

	/**
	 * @param aPasphraseId
	 * @param aDecoded
	 */
	void log(final String aPasphraseId, final String aMethod,
			final String aEncodend) {
		pLogger.logInfo(this, "testIs", "%s.%s=[%3d][%s]", aPasphraseId,
				aMethod, aEncodend.length(), aEncodend);
	}

	/**
	 * @param aPasphraseId
	 * @param aDecoded
	 */
	void logDecoded(final String aPasphraseId, final String aDecoded) {
		log(aPasphraseId, "getDecoded", aDecoded);
	}

	/**
	 * @param aPasphraseId
	 * @param aDecoded
	 */
	void logEncoded(final String aPasphraseId, final String aEncodend) {
		log(aPasphraseId, "getEncoded", aEncodend);
	}

	void logSeparator(final String aTitle) {
		pLogger.logInfo(
				this,
				"testIs",
				"-----------------------------------------------------------\n %63s %s",
				"=====>", aTitle);
	}

	/**
	 *
	 */
	public void testIs() {

		// encode
		final String wInitialValue = "anticonstitutionnellement";

		logSeparator("Inital value");

		pLogger.logInfo(this, "testIs", "string=[%s]", wInitialValue);

		try {

			/*
			 * ------------------------------------------------------------
			 */
			logSeparator("encode wPPStr1 with B64 using CXPassphraseBuilder (simple)");

			final IXPassphrase wB64x = CXPassphraseBuilder.buildB64(wInitialValue);
			assertNotNull(wB64x);

			logDecoded("wB64x", wB64x.getDecoded());
			logEncoded("wB64x", wB64x.getEncoded());

			assertEquals(wB64x.getDecoded(), wInitialValue);

			// test reverse
			final IXPassphrase wB64y = CXPassphraseParser.parse(wB64x
					.getEncoded());
			logDecoded("wB64y", wB64y.getDecoded());
			logEncoded("wB64y", wB64y.getEncoded());
			assertEquals(wB64y.getDecoded(), wInitialValue);

			/*
			 * ------------------------------------------------------------
			 */
			logSeparator("encode wPPStr1 with OBF using CXPassphraseBuilder (simple)");

			final IXPassphrase wOBFx = CXPassphraseBuilder.buildOBF(wInitialValue);
			assertNotNull(wOBFx);

			logDecoded("wOBFx", wOBFx.getDecoded());
			logEncoded("wOBFx", wOBFx.getEncoded());

			assertEquals(wOBFx.getDecoded(), wInitialValue);

			// test reverse
			final IXPassphrase wOBFy = CXPassphraseParser.parse(wOBFx
					.getEncoded());
			logDecoded("wOBFy", wOBFy.getDecoded());
			logEncoded("wOBFy", wOBFy.getEncoded());
			assertEquals(wOBFy.getDecoded(), wInitialValue);

			/*
			 * ------------------------------------------------------------
			 */
			logSeparator("encode wPPStr1 with RDM using CXPassphraseBuilder (simple)");

			final IXPassphrase wRDMa = CXPassphraseBuilder.buildRDM(wInitialValue);
			assertNotNull(wRDMa);

			logDecoded("wRDMa", wRDMa.getDecoded());
			logEncoded("wRDMa", wRDMa.getEncoded());

			assertEquals(wRDMa.getDecoded(), wInitialValue);

			// test reverse
			final IXPassphrase wRDMb = CXPassphraseParser.parse(wRDMa
					.getEncoded());
			logDecoded("wRDMb", wRDMb.getDecoded());
			logEncoded("wRDMb", wRDMb.getEncoded());
			assertEquals(wRDMb.getDecoded(), wInitialValue);

			/*
			 * ------------------------------------------------------------
			 */
			logSeparator("encode wPPStr1 with RDM with OBF (double)");

			final IXPassphrase wOBFa = new CXPassphraseOBF(wRDMa);
			assertNotNull(wOBFa);

			logDecoded("wOBFa", wOBFa.getDecoded());
			logEncoded("wOBFa", wOBFa.getEncoded());
			log("wOBFa", "getNested().getEncoded", wOBFa.getNested()
					.getEncoded());

			assertEquals(wOBFa.getDecoded(), wInitialValue);

			// test reverse
			final IXPassphrase wOBFb = CXPassphraseParser.parse(wOBFa
					.getEncoded());
			logDecoded("wOBFb", wOBFb.getDecoded());
			logEncoded("wOBFb", wOBFb.getEncoded());
			assertEquals(wOBFb.getDecoded(), wInitialValue);

			/*
			 * ------------------------------------------------------------
			 */
			logSeparator("encode wPPStr1 with RDM with OBF with B64 (triple)");

			final IXPassphrase wB64a = new CXPassphraseB64(wOBFa);
			assertNotNull(wB64a);

			logDecoded("wB64a", wB64a.getDecoded());
			logEncoded("wB64a", wB64a.getEncoded());
			log("wB64a", "getNested().getEncoded", wB64a.getNested()
					.getEncoded());
			log("wB64a", "getNested().getNested().getEncoded", wB64a
					.getNested().getNested().getEncoded());

			assertEquals(wB64a.getDecoded(), wInitialValue);

			// test reverse
			final IXPassphrase wB64b = CXPassphraseParser.parse(wB64a
					.getEncoded());
			logDecoded("wB64b", wB64b.getDecoded());
			logEncoded("wB64b", wB64b.getEncoded());
			assertEquals(wB64b.getDecoded(), wInitialValue);

			/*
			 * ------------------------------------------------------------
			 */
			logSeparator("parse triple encoded PP with B64 with OBF with RDM to retreive wPPStr1");

			final String wPPStr2 = wB64a.getEncoded();
			pLogger.logInfo(this, "testIs", "from=[%s]", wPPStr2);

			final IXPassphrase wPPb = CXPassphraseParser.parse(wPPStr2);
			assertNotNull(wPPb);

			logDecoded("wPPb", wPPb.getDecoded());
			logEncoded("wPPb", wPPb.getEncoded());
			log("wPPb", "getNested().getEncoded", wPPb.getNested().getEncoded());
			log("wPPb", "getNested().getNested().getEncoded", wPPb.getNested()
					.getNested().getEncoded());

			assertEquals(wPPb.getDecoded(), wInitialValue);

			/*
			 * ------------------------------------------------------------
			 */
			logSeparator("build and parse triple encoded PP using CXPassphraseBuilder");

			final IXPassphrase wTripleA = CXPassphraseBuilder
					.buildB64OBFRDM(wInitialValue);
			assertNotNull(wTripleA);

			logDecoded("wTripleA", wTripleA.getDecoded());
			logEncoded("wTripleA", wTripleA.getEncoded());

			final IXPassphrase wwTripleB = CXPassphraseParser.parse(wTripleA
					.getEncoded());
			logDecoded("wwTripleB", wwTripleB.getDecoded());
			logEncoded("wwTripleB", wwTripleB.getEncoded());

			assertEquals(wwTripleB.getDecoded(), wInitialValue);

			/*
			 * ------------------------------------------------------------
			 */
			logSeparator("TESTS OK");

		} catch (final Throwable e) {

			pLogger.logSevere(this, "testIs", "ERROR: %s", e);

			fail(CXException.eMiniInString(e));
		}

	}
}
