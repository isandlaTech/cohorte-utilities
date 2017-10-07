package org.cohorte.utilities.security;

import java.nio.charset.StandardCharsets;

public class CXObfuscatorOBF {

	public static final CXObfuscatorOBF sObfuscatorOBF = new CXObfuscatorOBF();

	/**
	 * @return
	 */
	public static CXObfuscatorOBF getInstance() {
		return sObfuscatorOBF;
	}

	/**
	 * 
	 */
	public CXObfuscatorOBF() {
		super();
	}

	/**
	 * @param s
	 * @return
	 * 
	 * @see http 
	 *      ://www.atetric.com/atetric/javadoc/org.eclipse.jetty/jetty-util/9.2
	 *      .4.v20141103/src-html/org/eclipse/jetty/util/security/Password.html
	 */
	public String deobfuscate(final String aValue) {

		byte[] wBytes = new byte[aValue.length() / 2];
		int l = 0;
		for (int i = 0; i < aValue.length(); i += 4) {
			if (aValue.charAt(i) == 'U') {
				i++;
				String x = aValue.substring(i, i + 4);
				int i0 = Integer.parseInt(x, 36);
				byte bx = (byte) (i0 >>> 8);
				wBytes[l++] = bx;
			} else {
				String x = aValue.substring(i, i + 4);
				int i0 = Integer.parseInt(x, 36);
				int i1 = (i0 / 256);
				int i2 = (i0 % 256);
				byte bx = (byte) ((i1 + i2 - 254) / 2);
				wBytes[l++] = bx;
			}
		}

		return new String(wBytes, 0, l, StandardCharsets.UTF_8);
	}

	/**
	 * @param aValue
	 * @return the obfuscate
	 * 
	 * @see http 
	 *      ://www.atetric.com/atetric/javadoc/org.eclipse.jetty/jetty-util/9.2
	 *      .4.v20141103/src-html/org/eclipse/jetty/util/security/Password.html
	 */
	public String obfuscate(final String aValue) {

		byte[] wBytes = aValue.getBytes(StandardCharsets.UTF_8);

		StringBuilder wSB = new StringBuilder();

		for (int i = 0; i < wBytes.length; i++) {
			byte b1 = wBytes[i];
			byte b2 = wBytes[wBytes.length - (i + 1)];
			if (b1 < 0 || b2 < 0) {
				int i0 = (0xff & b1) * 256 + (0xff & b2);
				String x = Integer.toString(i0, 36).toLowerCase();
				wSB.append("U0000", 0, 5 - x.length());
				wSB.append(x);
			} else {
				int i1 = 127 + b1 + b2;
				int i2 = 127 + b1 - b2;
				int i0 = i1 * 256 + i2;
				String x = Integer.toString(i0, 36).toLowerCase();

				@SuppressWarnings("unused")
				int j0 = Integer.parseInt(x, 36);
				int j1 = (i0 / 256);
				int j2 = (i0 % 256);
				@SuppressWarnings("unused")
				byte bx = (byte) ((j1 + j2 - 254) / 2);

				wSB.append("000", 0, 4 - x.length());
				wSB.append(x);
			}

		}
		return wSB.toString();
	}

}
