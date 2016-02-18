package org.cohorte.utilities.crypto;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import org.psem2m.utilities.files.CXFile;

/**
 * Classe d'utilitaire MD5.
 * 
 * @see http://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java
 * 
 * @author debbabi
 *
 */
public class CMD5Utils {

	public static final String MD5 = "MD5";

	/**
	 * Creates Checksum array.
	 *  
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static byte[] createChecksum(String filename) throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance(MD5);
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}


	/**
	 * Gets MD5 hash of the file provided as argument.
	 * 
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	public static String getMD5Checksum(String filename) throws Exception {
		byte[] b = createChecksum(filename);
		String result = "";

		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
	 * Gets MD5 hash of the file provided as argument.
	 * 
	 * @param aFile
	 * @return MD5 hash
	 * @throws Exception
	 */
	public static String getMD5Checksum(CXFile aFile) throws Exception {
		if (aFile == null) {
			throw new Exception(String.format("No argument is given"));
		} else if (!aFile.exists()) {
			throw new Exception(String.format("File [%s] does not exists!",
					aFile.getAbsolutePath()));
		} else {
			return getMD5Checksum(aFile.getAbsolutePath());
		}
	}
}
