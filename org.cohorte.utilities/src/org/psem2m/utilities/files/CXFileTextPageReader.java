package org.psem2m.utilities.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 1.4.0
 * 
 * Read page of lines in a Text file
 * 
 * @author ogattaz
 *
 */
public class CXFileTextPageReader extends CXFileTextAbstratReader {

	/**
	 * @param aFile
	 */
	public CXFileTextPageReader(final File aFile) {
		super(aFile);
	}

	/**
	 * @param aFile
	 */
	/**
	 * @param aFile
	 * @param aFileCharset
	 */
	public CXFileTextPageReader(final File aFile, Charset aFileCharset) {
		super(aFile, aFileCharset);

	}

	/**
	 * @param aFile
	 * @param aCharsetName
	 */
	public CXFileTextPageReader(final File aFile, final String aCharsetName) {
		super(aFile, aCharsetName);

	}

	/**
	 * @param aFilePath
	 */
	public CXFileTextPageReader(final String aFilePath) {
		super(aFilePath);
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTextPageReader(final String aFilePath, final Charset aFileCharset) {
		super(aFilePath, aFileCharset);
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTextPageReader(final String aFilePath, final String aCharsetName) {
		super(aFilePath, aCharsetName);
	}

	/**
	 * @param aOffset
	 * @param aPageSize
	 * @return
	 * @throws IOException
	 */
	public List<String> readPage(final int aOffset, final int aPageSize) throws IOException {

		List<String> alist = Files.lines(getPath(), getFileCharset()).filter(new Predicate<String>() {

			final int pEnd = aOffset + aPageSize;
			final int pStart = aOffset;

			int wNumLine = 0;

			@Override
			public boolean test(String t) {
				wNumLine++;
				return wNumLine >= pStart && wNumLine < pEnd;
			}
		}).collect(Collectors.toList());

		return alist;
	}

}
