package org.cohorte.utilities.sql;

/**
 * represents the parts of the DBResult
 *
 * @author ogattaz
 *
 */
public enum EDBResultPart {
	DATA, KEYS, MESSAGE;

	/**
	 * @param aDescriptionInfos
	 * @param aDescriptionInfo
	 * @return true if aDescriptionInfo is in the table aDescriptionInfos
	 */
	public static boolean wantPart(final EDBResultPart[] aDescriptionInfos,
			final EDBResultPart aDescriptionInfo) {

		for (final EDBResultPart wDescriptionInfo : aDescriptionInfos) {
			if (aDescriptionInfo == wDescriptionInfo) {
				return true;
			}
		}
		return false;
	}

}
