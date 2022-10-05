package org.cohorte.utilities.encode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.psem2m.utilities.files.CXFile;

/**
 * @author ogattaz
 *
 */
public class CBase64Encoder {

	private String pBase64Str = null;

	/**
	 * @param aByteArray
	 */
	public CBase64Encoder(final byte[] aByteArray) {

		pBase64Str = aByteArray == null ? "" : Base64.getEncoder().encodeToString(aByteArray);
	}

	/**
	 * @param aFile
	 */
	public CBase64Encoder(final File aFile) {
		pBase64Str = "";
		if (aFile != null) {
			CXFile wFile = new CXFile(aFile);
			byte[] wByteArray;
			try {
				wByteArray = wFile.readAllBytes();
				pBase64Str = Base64.getEncoder().encodeToString(wByteArray);
			} catch (IOException e) {
				System.err.println("Error encoding from file " + aFile);

			}

		}
	}

	/**
	 * @param aSource
	 */
	public CBase64Encoder(final String aSource) {

		pBase64Str = aSource == null ? "" : Base64.getEncoder().encodeToString(aSource.getBytes());
	}

	/**
	 * @return
	 */
	public String getPrefixedString() {

		return IBase64.BASIC_PREFIX.concat(getString());
	}

	/**
	 * @return
	 */
	public String getString() {

		return pBase64Str;
	}

	/**
	 * @param aFile
	 * @throws Exception
	 */
	public void writeFile(File aFile) throws Exception {
		if (pBase64Str != null && aFile != null) {
			FileOutputStream wStream = new FileOutputStream(aFile, true);
			wStream.write(pBase64Str.getBytes());
			wStream.close();
		}
	}

	/**
	 * @param aFilePath
	 * @throws Exception
	 */
	public void writeFile(String aFilePath) throws Exception {
		if (pBase64Str != null && aFilePath != null) {
			writeFile(new File(aFilePath));
		}
	}
}
