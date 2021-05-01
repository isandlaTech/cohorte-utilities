package org.cohorte.utilities.asciitable;

import java.util.ArrayList;
import java.util.List;

import org.psem2m.utilities.CXStringUtils;

/**
 * @author ogattaz
 *
 */
public class CXTHeaderRow {

	private final List<ASCIITableHeader> pHeaders = new ArrayList<>();

	/**
	 * 
	 */
	public CXTHeaderRow() {
		super();
	}

	/**
	 * @param aTHeaderRow
	 * @return
	 */
	public CXTHeaderRow addAllTH(final CXTHeaderRow aTHeaderRow) {

		pHeaders.addAll(aTHeaderRow.getTHs());
		return this;
	}

	/**
	 * @param aHeader
	 * @return
	 */
	public CXTHeaderRow addTH(final ASCIITableHeader aHeader) {

		pHeaders.add(aHeader);
		return this;
	}

	/**
	 * @param aTitle
	 * @param aDataAlign
	 * @return
	 */
	public CXTHeaderRow addTH(final String aTitle) {

		return addTH(new ASCIITableHeader(aTitle));
	}

	/**
	 * @param aTitle
	 * @param aDataAlign
	 * @return
	 */
	public CXTHeaderRow addTH(final String aTitle, final int aDataAlign) {

		return addTH(new ASCIITableHeader(aTitle, aDataAlign));
	}

	/**
	 * @param aTitle
	 * @param aDataAlign
	 * @param aHeaderAlign
	 * @return
	 */
	public CXTHeaderRow addTH(final String aTitle, final int aDataAlign, final int aHeaderAlign) {

		return addTH(new ASCIITableHeader(aTitle, aDataAlign, aHeaderAlign));
	}

	/**
	 * 
	 */
	public void clear() {

		pHeaders.clear();
	}

	/**
	 * @param aColIdx
	 * @return
	 */
	private String generateTitle(final int aColIdx) {
		return String.valueOf(new Character((char) ('A' + aColIdx)));
	}

	/**
	 * @return
	 */
	public List<ASCIITableHeader> getTHs() {
		return pHeaders;
	}

	/**
	 * @param aColIdx
	 * @param aTitle
	 * @param aDataAlign
	 * @return
	 */
	public CXTHeaderRow setTH(final int aColIdx, final String aTitle, final int aDataAlign) {

		while (aColIdx >= pHeaders.size()) {
			addTH(new ASCIITableHeader(generateTitle(pHeaders.size()), aDataAlign));
		}

		ASCIITableHeader wTH = pHeaders.get(aColIdx);
		if (aTitle != null) {
			wTH.setHeaderName(aTitle);
		}
		wTH.setDataAlign(aDataAlign);

		return this;
	}

	/**
	 * @return
	 */
	public ASCIITableHeader[] toHeaderArray() {
		return pHeaders.toArray(new ASCIITableHeader[pHeaders.size()]);
	}

	/**
	 * @param aFirstTHeader
	 * @return
	 */
	public ASCIITableHeader[] toHeaderArray(final ASCIITableHeader aFirstTHeader) {

		if (aFirstTHeader == null) {
			return toHeaderArray();
		}
		//
		else {
			pHeaders.add(0, aFirstTHeader);
			ASCIITableHeader[] wArray = toHeaderArray();
			pHeaders.remove(0);
			return wArray;
		}
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

		String[] wArray = new String[pHeaders.size()];
		int wIdx = 0;
		for (ASCIITableHeader wHeader : pHeaders) {
			wArray[wIdx] = wHeader.getHeaderName();
		}
		return wArray;
	}
}
