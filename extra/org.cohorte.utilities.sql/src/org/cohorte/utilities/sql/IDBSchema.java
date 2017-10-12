package org.cohorte.utilities.sql;

/**
 *
 * mysql
 *
 * <pre>
 * SELECT SCHEMA_NAME AS `Database` FROM INFORMATION_SCHEMA.SCHEMATA
 * </pre>
 *
 *
 * sqlserver
 *
 * <pre>
 * SELECT name FROM sys.schemas
 * SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA
 * </pre>
 *
 * postgressql
 *
 * <pre>
 * select schema_name from information_schema.schemata
 * </pre>
 *
 *
 * @see https://dev.mysql.com/doc/refman/5.0/en/schemata-table.html
 *
 * @see http
 *      ://stackoverflow.com/questions/3719623/how-do-i-obtain-a-list-of-all-
 *      schemas-in-a-sql-server-database
 *
 * @see http
 *      ://dba.stackexchange.com/questions/40045/how-do-i-list-all-schemas-in
 *      -postgresql
 *
 * @author ogattaz
 */
public interface IDBSchema {

	public static final String GET_SCHEMAS = "select schema_name from information_schema.schemata";

}
