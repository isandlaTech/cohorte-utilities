package org.cohorte.utilities.asciitable;

/**
 * @author ogattaz
 *
 */
public class CXTCell {

	public static final String EMPTY = "";

	private String pCellValue = EMPTY;

	/**
	 * 
	 */
	public CXTCell() {
		super();
	}

	/**
	 * @param aCellValue
	 */
	public CXTCell(final String aCellValue) {
		this();
		setValue(aCellValue);
	}

	/**
	 * @param aCellValue
	 */
	public void setValue(final String aCellValue) {
		pCellValue = aCellValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return pCellValue;
	}
}
