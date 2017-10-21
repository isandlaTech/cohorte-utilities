package org.cohorte.utilities.sql;

/**
 * Th managed DataBase Type
 *
 * <pre>
 * jdbc:mysql://server:port/world?useUnicode=true&characterEncoding=UTF-8
 * jdbc:sqlserver://server:port;databaseName=CGP;user=myUser;password=myPass
 * jdbc:postgresql://host:port/database?user=userName&password=pass&postgres&charSet=LATIN1&compatible=7.2
 * </pre>
 *
 * @author ogattaz
 *
 */
public enum EDBType {

	MYSQL, POSTGRESQL, SQLSERVER;

	/**
	 * @param aModuleId
	 * @return
	 */
	public static EDBType fromName(final String aModuleId) {
		if (aModuleId != null && !aModuleId.isEmpty()) {

			for (EDBType wType : EDBType.values()) {
				if (wType.name().equalsIgnoreCase(aModuleId)) {
					return wType;
				}
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public boolean isMySql() {
		return this == MYSQL;
	}

	/**
	 * @return
	 */
	public boolean isPostgreSql() {
		return this == POSTGRESQL;
	}

	/**
	 * @return
	 */
	public boolean isSqlServer() {
		return this == SQLSERVER;
	}
}
