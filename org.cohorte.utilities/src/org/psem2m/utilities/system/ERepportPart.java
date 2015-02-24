package org.psem2m.utilities.system;

/**
 * @author ogattaz
 * 
 */
public enum ERepportPart {
	GENERAL, STDERR, STDOUT;

	public static final ERepportPart[] ALL_PARTS = new ERepportPart[] {
			GENERAL, STDERR, STDOUT };
	public static final ERepportPart[] WITHOUT_STDERR = new ERepportPart[] {
			GENERAL, STDOUT };

	/**
	 * @param aParts
	 *            a array of repport parts
	 * @param aPart
	 *            a repport part
	 * @return
	 */
	public static boolean isInReportParts(final ERepportPart[] aParts,
			final ERepportPart aPart) {
		for (ERepportPart wPart : aParts) {
			if (wPart == aPart) {
				return true;
			}
		}
		return false;
	}

}
