package org.psem2m.utilities.files;

import java.io.File;
import java.io.IOException;

/**
 *
 * Encodage UTF-8 without the bom header to be able to read and write UTF8 XML
 * file
 *
 * @author ogattaz
 *
 */
public class CXFileUtf8WithoutBom extends CXFileUtf8 {

	private static final long serialVersionUID = 5864325852345069724L;

	/**
	 * @param aFile
	 */
	public CXFileUtf8WithoutBom(CXFile aFile) {
		super(aFile);
	}

	/**
	 * @param aParentDir
	 * @param aFileName
	 */
	public CXFileUtf8WithoutBom(CXFileDir aParentDir, String aFileName) {
		super(aParentDir, aFileName);
	}

	/**
	 * @param aFile
	 */
	public CXFileUtf8WithoutBom(File aFile) {
		super(aFile);
	}

	/**
	 * @param aParentDir
	 * @param aFileName
	 */
	public CXFileUtf8WithoutBom(File aParentDir, String aFileName) {
		super(aParentDir, aFileName);
	}

	/**
	 * @param aFullPath
	 */
	public CXFileUtf8WithoutBom(String aFullPath) {
		super(aFullPath);
	}

	/**
	 * @param aParentDir
	 * @param aFileName
	 */
	public CXFileUtf8WithoutBom(String aParentDir, String aFileName) {
		super(aParentDir, aFileName);
	}

	/**
	 * Override to prevent the writing of the BOM
	 *
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.files.CXFileText#writeEncoding()
	 */
	@Override
	protected void writeEncoding() throws IOException {
		// nothing => no BOM !
	}

}
