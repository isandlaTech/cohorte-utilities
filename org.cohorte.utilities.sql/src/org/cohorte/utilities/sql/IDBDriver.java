package org.cohorte.utilities.sql;

import java.sql.Connection;

/**
 * <pre>
 * jdbc:mysql://server:port/world?useUnicode=true&characterEncoding=UTF-8
 * jdbc:sqlserver://server:port;databaseName=CGP;user=myUser;password=myPass
 * jdbc:postgresql://host:port/database?user=userName&password=pass&postgres&charSet=LATIN1&compatible=7.2
 * </pre>
 *
 * @author ogattaz
 *
 */
public interface IDBDriver {

	String MYSQL = EDBType.MYSQL.name().toLowerCase();
	String POSTGRESQL = EDBType.POSTGRESQL.name().toLowerCase();
	String SQLSERVER = EDBType.SQLSERVER.name().toLowerCase();

	/**
	 * @return
	 */
	String getClassName();

	/**
	 * <pre>
	 * mysql:
	 * jdbc:mysql://192.168.2.200:3306/myDababase?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
	 * sqlserver < V4
	 * jdbc:microsoft:sqlserver://192.168.2.200:1433;DatabaseName=CGP,sa,sqlserver
	 * sqlserver >= V4:
	 * jdbc:sqlserver://192.168.2.200:1433;databaseName=CGP;user=sa;password=sqlserver
	 * </pre>
	 *
	 *
	 * @param aHost
	 * @param aPort
	 * @param aDbName
	 * @param aUserId
	 * @param aPassword
	 * @return
	 */
	String getConnectUrl(final String aHost, final int aPort,
			final String aDbName, final String aUserId, final String aPassword,
			final String aUrlExtend);

	/**
	 * @return
	 */
	String getVersion();

	/**
	 * @param aHostNamePort
	 * @param aDbName
	 * @param aDbUserId
	 * @param aDbPassword
	 * @return
	 * @throws Exception
	 */
	Connection newDbConnection(final String aHost, final int aPort,
			final String aDbName, final String aDbUserId,
			final String aDbPassword, final String aUrlExtend) throws Exception;

	/**
	 * @param aUrl
	 * @return
	 */
	String retrieveDbName(final String aUrl);

}
