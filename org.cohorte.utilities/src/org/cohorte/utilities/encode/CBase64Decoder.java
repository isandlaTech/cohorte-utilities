package org.cohorte.utilities.encode;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

/**
 *
 * @author ogattaz
 */
public class CBase64Decoder {

	private byte[] pByteArray = null;

	/**
	 * @param aAscii7bits
	 *                        a Ascii 7 bits string
	 */
	public CBase64Decoder(String aAscii7bits) {

		if (aAscii7bits != null && aAscii7bits.startsWith(IBase64.BASIC_PREFIX)) {
			aAscii7bits = aAscii7bits.substring(IBase64.BASIC_PREFIX.length());
		}
		pByteArray = aAscii7bits == null ? new byte[0] : Base64.getDecoder().decode(aAscii7bits);
	}

	/**
	 * @return
	 */
	public byte[] getBytes() {

		return pByteArray;
	}

	/**
	 * @return
	 */
	public String getString() {

		return new String(pByteArray);
	}

	/**
	 * @param aFile
	 * @throws Exception
	 */
	public void writeFile(File aFile) throws Exception {
		if (pByteArray != null && aFile != null) {
			FileOutputStream wStream = new FileOutputStream(aFile, true);
			wStream.write(pByteArray);
			wStream.close();
		}
	}

	/**
	 * @param aFilePath
	 * @throws Exception
	 */
	public void writeFile(String aFilePath) throws Exception {
		if (pByteArray != null && aFilePath != null) {
			writeFile(new File(aFilePath));
		}
	}
}
