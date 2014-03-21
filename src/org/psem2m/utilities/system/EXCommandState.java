package org.psem2m.utilities.system;

/**
 * <ul>
 * <li>CMD_RUN_OK 0 Exit standard si launch OK</li>
 * <li>CMD_RUN_KO 1 si Exit !=0</li>
 * <li>CMD_RUN_NO -1 Not launched - init value</li>
 * <li>CMD_RUN_TIMEOUT -2 Exit en TimeOut</li>
 * <li>CMD_RUN_STOPPED -3 Exit en arret programme ou utilisateur (sur callback)</li>
 * <li>CMD_RUN_EXCEPTION -4 Exception before lanching</li>
 * </ul>
 * 
 * @author ogattaz
 * 
 */
public enum EXCommandState {

	CMD_RUN_EXCEPTION(-4), CMD_RUN_KO(1), CMD_RUN_NO(-1), CMD_RUN_OK(0), CMD_RUN_STOPED(
			-3), CMD_RUN_TIMEOUT(-2);

	/**
	 * @param aExitValue
	 * @return
	 */
	public static EXCommandState exitValToState(final int aExitValue) {
		return (aExitValue == 0) ? CMD_RUN_OK : CMD_RUN_KO;

	}

	private final int pVal;

	/**
	 * @param aVal
	 */
	EXCommandState(final int aVal) {
		pVal = aVal;
	}

	/**
	 * @return
	 */
	public int getVal() {
		return pVal;
	}

}
