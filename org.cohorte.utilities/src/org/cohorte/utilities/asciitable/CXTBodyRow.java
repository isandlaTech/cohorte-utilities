package org.cohorte.utilities.asciitable;

import java.util.ArrayList;
import java.util.List;

import org.psem2m.utilities.CXStringUtils;

/**
 * @author ogattaz
 *
 */
public class CXTBodyRow {

	private List<CXTCell> pCells = new ArrayList<>();

	/**
	 * 
	 */
	public CXTBodyRow() {
		super();
	}

	/**
	 * only used to remove the column "number"
	 * 
	 * @param aColIdx
	 * @param aCellValue
	 * @return
	 */
	CXTBodyRow addTD(final int aColIdx, final String aCellValue) {
		pCells.add(aColIdx, new CXTCell(aCellValue));
		return this;
	}

	/**
	 * @param aCellValue
	 * @return
	 */
	public CXTBodyRow addTD(final String aCellValue) {

		pCells.add(new CXTCell(aCellValue));
		return this;
	}

	/**
	 * @param aLongValue
	 * @return
	 */
	public CXTBodyRow addTDInt(final int aLongValue) {

		return addTD(String.valueOf(aLongValue));
	}

	/**
	 * @param aLongValue
	 * @param aNumericFormat
	 * @return
	 */
	public CXTBodyRow addTDInt(final long aIntValue, final String aNumericFormat) {

		return addTD(String.format(aNumericFormat, aIntValue));
	}

	/**
	 * @param aLongValue
	 * @return
	 */
	public CXTBodyRow addTDLong(final long aLongValue) {

		return addTD(String.valueOf(aLongValue));
	}

	/**
	 * @param aLongValue
	 * @param aNumericFormat
	 * @return
	 */
	public CXTBodyRow addTDLong(final long aLongValue, final String aNumericFormat) {

		return addTD(String.format(aNumericFormat, aLongValue));
	}

	/**
	 * @param aColumnValue
	 * @return
	 */
	public CXTBodyRow addTDValue(Object aColumnValue) {

		return addTD(String.valueOf(aColumnValue));
	}

	/**
	 * only used to remove the column "number"
	 * 
	 * @param aIndex
	 */
	void remove(final int aIndex) {
		pCells.remove(aIndex);
	}

	/**
	 * @param aIndex
	 * @param aCellValue
	 * @return
	 */
	public CXTBodyRow setTD(final int aColIdx, final String aCellValue) {

		while (aColIdx >= pCells.size()) {
			pCells.add(new CXTCell());
		}
		pCells.get(aColIdx).setValue(aCellValue);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return CXStringUtils.stringTableToString(toStringArray());
	}

	/**
	 * @return
	 */
	public String[] toStringArray() {

		String[] wArray = new String[pCells.size()];
		int wIdx = 0;
		for (CXTCell wCell : pCells) {
			wArray[wIdx] = wCell.toString();
			wIdx++;
		}
		return wArray;
	}
}
