package org.psem2m.utilities.files;

/**
 * Lecture/Ecriture de fichiers XML
 * Encodage UTF-8 par d�faut sans l'entête Bom
 */
public class CXFileUtf8WithoutBom extends CXFileUtf8
{
	
	/**
	 * @param aFile
	 */
	public CXFileUtf8WithoutBom(CXFile aFile)
	{
		super(aFile);
	}
	
	/**
	 * @param aFullPath
	 */
	public CXFileUtf8WithoutBom(String aFullPath)
	{
		super(aFullPath);
	}
	

	/**
	 * @param aParentDir
	 * @param aFileName
	 */
	public CXFileUtf8WithoutBom(String aParentDir, String aFileName)
	{
		super(aParentDir, aFileName);
	}
	

	/**
	 * @param aParentDir
	 * @param aFileName
	 */
	public CXFileUtf8WithoutBom(CXFileDir aParentDir, String aFileName)
	{
		super(aParentDir, aFileName);
	}
	
	/**
	 *  Override  to prevent the writing of the BOM
	 *  
	 * (non-Javadoc)
	 * @see org.psem2m.utilities.files.CXFileText#writeEncoding()
	 */
	//@Override
	protected void writeEncoding() throws Exception {
		// nothing
	}

}
