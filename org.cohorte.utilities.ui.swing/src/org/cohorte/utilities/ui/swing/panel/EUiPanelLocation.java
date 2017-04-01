package org.cohorte.utilities.ui.swing.panel;

/**
 * @author ogattaz
 *
 */
public enum EUiPanelLocation {
	ASCENDING, DESCENDING, FIRST, LAST;

	/**
	 * @param aLocation
	 * @return
	 */
	public boolean is(final EUiPanelLocation aLocation) {

		return this == aLocation;
	}

	/**
	 * @return
	 */
	public boolean isFIRST() {

		return is(FIRST);
	}

	/**
	 * @return
	 */
	public boolean isLAST() {

		return is(LAST);
	}
}
