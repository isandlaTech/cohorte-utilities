package org.psem2m.utilities.files;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * 1.4.0
 * 
 * @author ogattaz
 *
 */
abstract class CXFileTextAbstratReader {

	private static final String DEFAULT_CHARSET = "UTF-8";

	private final File pFile;

	private final Charset pFileCharset;

	/**
	 * @param aFile
	 */
	public CXFileTextAbstratReader(final File aFile) {
		this(aFile, Charset.forName(DEFAULT_CHARSET));
	}

	/**
	 * @param aFile
	 */
	/**
	 * @param aFile
	 * @param aFileCharset
	 */
	public CXFileTextAbstratReader(final File aFile, Charset aFileCharset) {
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
	public CXFileTextAbstratReader(final File aFile, final String aCharsetName) {
		this(aFile, Charset.forName(aCharsetName));

	}

	/**
	 * @param aFilePath
	 */
	public CXFileTextAbstratReader(final String aFilePath) {
		this(new File(aFilePath));
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTextAbstratReader(final String aFilePath, final Charset aFileCharset) {
		this(new File(aFilePath), aFileCharset);
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTextAbstratReader(final String aFilePath, final String aCharsetName) {
		this(new File(aFilePath), Charset.forName(aCharsetName));
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
