package org.psem2m.utilities.rsrc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

/**
 * Classe - Lecture d''octets
 * 
 * @author ogattaz
 * 
 */
public class CXRsrcByteReader {
	private static final int READ_BUF_SIZE = 0x8000;

	/**
	 * @param aInputStream
	 * @return
	 * @throws IOException
	 */
	private static byte[] readAll(InputStream aInputStream) throws IOException {
		if (aInputStream == null) {
			return new byte[0];
		}
		CXRsrcByteArray wReadBytes = new CXRsrcByteArray();
		byte[] wReadBuffer = new byte[READ_BUF_SIZE];
		boolean wEof = false;
		do {
			int wReadSize = aInputStream.read(wReadBuffer, 0, READ_BUF_SIZE);
			wEof = wReadSize <= 0;
			if (!wEof) {
				wReadBytes.add(wReadBuffer, wReadSize);
			}
		} while (!wEof);
		return wReadBytes.toArray();
	}

	/**
	 * @param aUrlConnection
	 * @return
	 * @throws IOException
	 */
	public static byte[] readAll(URLConnection aUrlConnection) throws IOException {
		InputStream wInputStream = null;
		try {
			wInputStream = aUrlConnection.getInputStream();
			return readAll(wInputStream);
		} catch (IOException e) {
			throw (e);
		} finally {
			if (wInputStream != null) {
				wInputStream.close();
			}
		}
	}
}
