package org.psem2m.utilities.files;

import static java.nio.file.StandardOpenOption.READ;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Tail a Text file
 * 
 * Correction if the file ended by one or more \n
 * 
 * @author ogattaz
 * @author Lucas Do (Aug 26, 2018)
 * 
 * @see https://medium.com/@dotronglong/tail-in-java-8-9114a62eb88b
 *
 */
public class CXFileTailReader {

	private static final int DEFAULT_BUFFER_SIZE = 512;

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static final char NEW_LINE = 10;

	private final File pFile;

	private final Charset pFileCharset;

	/**
	 * @param aFile
	 */
	public CXFileTailReader(final File aFile) {
		this(aFile, Charset.forName(DEFAULT_CHARSET));
	}

	/**
	 * @param aFile
	 */
	/**
	 * @param aFile
	 * @param aFileCharset
	 */
	public CXFileTailReader(final File aFile, Charset aFileCharset) {
		super();

		if (!aFile.exists()) {
			throw new InvalidPathException(aFile.getAbsolutePath(), "File does not exist");
		}

		pFile = aFile;
		pFileCharset = aFileCharset;
	}

	/**
	 * @param aFile
	 * @param aCharsetName
	 */
	public CXFileTailReader(final File aFile, final String aCharsetName) {
		this(aFile, Charset.forName(aCharsetName));

	}

	/**
	 * @param aFilePath
	 */
	public CXFileTailReader(final String aFilePath) {
		this(new File(aFilePath));
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTailReader(final String aFilePath, final Charset aFileCharset) {
		this(new File(aFilePath), aFileCharset);
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTailReader(final String aFilePath, final String aCharsetName) {
		this(new File(aFilePath), Charset.forName(aCharsetName));
	}

	/**
	 * @param aLines
	 * @param aOutput
	 */
	private void flushLine(LinkedList<String> aLines, ByteArrayOutputStream aOutput) {

		try {
			aLines.addFirst(aOutput.toString(getFileCharsetName()));
		} catch (UnsupportedEncodingException e) {
			aLines.addFirst(aOutput.toString());
		}
		aOutput.reset();
	}

	/**
	 * @return
	 */
	public File getFile() {
		return pFile;
	}

	/**
	 * @return
	 */
	Charset getFileCharset() {
		return pFileCharset;
	}

	/**
	 * @return
	 */
	String getFileCharsetName() {
		return getFileCharset().name();
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return pFile.getName();
	}

	/**
	 * @return
	 */
	public Path getPath() {
		return getFile().toPath();
	}

	/**
	 * @param out
	 * @param bytes
	 * @param offset
	 * @param limit
	 * @throws IOException
	 */
	private void prependBuffer(ByteArrayOutputStream out, byte[] bytes, int offset, int limit) throws IOException {
		ByteArrayOutputStream tmp = new ByteArrayOutputStream();
		tmp.write(bytes, offset, limit);
		out.writeTo(tmp);
		out.reset();
		tmp.writeTo(out);
	}

	/**
	 * @param aBytes
	 * @param aOutput
	 * @param aLines
	 * @param aNumberOfLines
	 * @throws IOException
	 */
	private void readChunk(byte[] aBytes, ByteArrayOutputStream aOutput, LinkedList<String> aLines, int aNumberOfLines)
			throws IOException {

		int wLimit = 0;
		int wOffset = aBytes.length - 1;
		boolean wHasToFlush = false;
		for (int i = wOffset; i >= 0 && aLines.size() < aNumberOfLines; i--) {
			byte b = aBytes[i];
			if (b == NEW_LINE) {
				wHasToFlush = true;
			} else {
				wOffset = i;
				wLimit++;
			}
			if (wHasToFlush) {
				prependBuffer(aOutput, aBytes, wOffset, wLimit);
				flushLine(aLines, aOutput);
				wLimit = 0;
				wHasToFlush = false;
			}
		}

		if (wLimit > 0) {
			// in case we have some characters left without in one line
			// we add them to buffer in front of the others
			prependBuffer(aOutput, aBytes, wOffset, wLimit);
		}
	}

	/**
	 * Read a number of lines from bottom of file
	 * 
	 * @see CXFileTailReader https://en.wikipedia.org/wiki/Tail_(Unix)
	 * @param path a string represents for path to file
	 * @param numberOfLines an integer number indicates number of lines to be
	 * retrieved
	 * @return a list of string represents for lines
	 *
	 * @throws InvalidPathException if file does not exist
	 * @throws IOException If an I/O error occurs
	 *
	 * @param aNumberOfLines
	 * @return
	 * @throws IOException
	 */
	public List<String> tail(final int aNumberOfLines) throws IOException {

		LinkedList<String> wLines = new LinkedList<>();
		FileChannel wFileChannel = null;
		ByteArrayOutputStream wOut = null;
		try {
			wFileChannel = FileChannel.open(getPath(), READ);

			long wFileSize = wFileChannel.size();
			if (wFileSize == 0) {
				return wLines;
			}

			int wBufferSize = DEFAULT_BUFFER_SIZE;
			long wReadBytes = 0;
			long wPosition = 0;
			ByteBuffer wByteBuffer;
			wOut = new ByteArrayOutputStream();
			do {
				if (wBufferSize > wFileSize) {
					wBufferSize = (int) wFileSize;
					wPosition = 0;
				} else {
					wPosition = wFileSize - (wReadBytes + wBufferSize);
					if (wPosition < 0) {
						wPosition = 0;
						wBufferSize = (int) (wFileSize - wReadBytes);
					}
				}

				wByteBuffer = ByteBuffer.allocate(wBufferSize);
				wReadBytes += wFileChannel.read(wByteBuffer, wPosition);

				readChunk(wByteBuffer.array(), wOut, wLines, aNumberOfLines);
			} while (wLines.size() < aNumberOfLines && wPosition > 0);

			if (wOut.size() > 0) {
				flushLine(wLines, wOut);
			}

			return wLines;

		} catch (Exception e) {
			throw new IOException(String.format("Unable to tail [%s] lines from file [%s]", aNumberOfLines,
					getFileName()), e);
		} finally {

			if (wOut != null) {
				wOut.close();
			}
			if (wFileChannel != null) {
				wFileChannel.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("file name=[%s] charset=[%s]", getFileName(), getFileCharsetName());
	}
}
