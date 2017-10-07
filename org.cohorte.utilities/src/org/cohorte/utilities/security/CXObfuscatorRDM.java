package org.cohorte.utilities.security;

import java.util.Random;

import org.psem2m.utilities.CXException;

/**
 * Obfuscator "random"
 *
 * @author ogattaz
 *
 */
public class CXObfuscatorRDM {

	private static final String CHARS1 = " !\"#$'()*+,-./0123456789:;<>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^`abcdefghijklmnopqrstuvwxyz{}~£";

	private static final int CHARS1_LEN = CHARS1.length();

	private static final String CHARS2 = ":DE;<89>?@AyCvHJKLopNP!QST,-./UVXYZ{O}a Mb'cGdefghRijkl\"£m`nFqrsWtu^wxz#$(\\)*+0I1234B567[~]";

	private static final int CHARS2_LEN = CHARS2.length();
	/**
	 * @see https://tools.ietf.org/html/rfc3548
	 */
	public static final String OBF_PREFIX = "RDM:";

	private static final int SALT_MIN = 0;
	private static final int SALT_PLUS = 3;

	public static final CXObfuscatorRDM sObfuscatorRDM = new CXObfuscatorRDM();
	private static final Random sRandomizer = new Random();

	static {
		validConstants();
	}

	/**
	 * @param aChar
	 * @return
	 */
	private static char char1To2(final char aChar) {

		final int wPos = CHARS1.indexOf(aChar);
		return (wPos > -1) ? CHARS2.charAt(wPos) : (char) (aChar - 2);
	}

	/**
	 * @param aChar
	 * @return
	 */
	private static char char2To1(final char aChar) {
		final int wPos = CHARS2.indexOf(aChar);
		return (wPos > -1) ? CHARS1.charAt(wPos) : (char) (aChar + 2);
	}

	/**
	 * @return
	 */
	public static CXObfuscatorRDM getInstance() {
		return sObfuscatorRDM;
	}

	/**
	 * @return true if the constants are coherent.
	 */
	public static boolean validConstants() {

		try {
			if (CHARS1_LEN != CHARS2_LEN) {
				throw new Exception(
						String.format(
								"%s: The constants CHARS1 and CHARS2 doen't have the same length : [%s!=%s]",
								CXObfuscatorRDM.class.getSimpleName(),
								CHARS1_LEN, CHARS2_LEN));
			}
			final int wIdx = 0;
			for (final char wChar : CHARS1.toCharArray()) {
				final char wChar2 = char1To2(wChar);
				final char wChar3 = char2To1(wChar2);
				if (wChar3 != wChar) {
					throw new Exception(
							String.format(
									"%s: The test of the reversibility of the character [%s] is wrong (idx %d)",
									CXObfuscatorRDM.class.getSimpleName(),
									wChar, wIdx));
				}
			}
			return true;

		} catch (final Exception e) {
			System.err.println(CXException.eInString(e));
			return false;
		}
	}

	/**
	 * instanciation not allowed
	 */
	private CXObfuscatorRDM() {
		super();
	}

	/**
	 * @param aBuffer
	 * @return
	 */
	public String deobfuscate(final String aBuffer) {

		final StringBuilder wSB = new StringBuilder();
		int wSalt;
		final int wMax = aBuffer.length() - 1;
		int wIdx = 0;
		while (true) {
			wSalt = Integer.parseInt(String.valueOf(char2To1(aBuffer
					.charAt(wIdx))));
			wIdx += wSalt + 1;
			if (wIdx > wMax) {
				break;
			}
			wSB.append(char2To1(aBuffer.charAt(wIdx)));
			wIdx++;
		}

		// return
		// String.format("%s %s %s",wSB.toString(),wSaltPart1,wSaltPart2);
		return wSB.toString();
	}

	/**
	 * @param aValue
	 * @return
	 */
	public String obfuscate(final String aValue) {

		final char[] wPassChars = String.valueOf(aValue).toCharArray();
		// 1-5
		int wSalt;
		final StringBuilder wSB = new StringBuilder();
		for (final char wChar : wPassChars) {
			wSalt = SALT_MIN + sRandomizer.nextInt(SALT_PLUS);
			saltInSB(wSB, wSalt);
			randomCharInSB(wSB, wSalt);
			wSB.append(char1To2(wChar));
		}
		wSalt = SALT_MIN + sRandomizer.nextInt(SALT_PLUS);
		saltInSB(wSB, wSalt);
		randomCharInSB(wSB, wSalt);

		return wSB.toString();
	}

	/**
	 * @return random char from CHARS1
	 */
	private char randomChar() {
		final int wIdx = sRandomizer.nextInt(CHARS1_LEN);
		return CHARS1.charAt(wIdx);
	}

	/**
	 * @param aSB
	 * @param aNb
	 * @return
	 */
	private StringBuilder randomCharInSB(final StringBuilder aSB, final int aNb) {
		for (int wIdx = 0; wIdx < aNb; wIdx++) {
			aSB.append(randomChar());
		}
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aSalt
	 * @return
	 */
	private StringBuilder saltInSB(final StringBuilder aSB, final int aSalt) {
		aSB.append(char1To2(String.valueOf(aSalt).charAt(0)));
		return aSB;
	}
}
