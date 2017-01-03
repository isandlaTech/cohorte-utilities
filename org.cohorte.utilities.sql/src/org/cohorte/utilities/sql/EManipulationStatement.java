package org.cohorte.utilities.sql;

/**
 * <pre>
 * 14.2.1 CALL Syntax
 * 14.2.2 DELETE Syntax
 * 14.2.3 DO Syntax
 * 14.2.4 HANDLER Syntax
 * 14.2.5 INSERT Syntax
 * 14.2.6 LOAD DATA INFILE Syntax
 * 14.2.7 LOAD XML Syntax
 * 14.2.8 REPLACE Syntax
 * 14.2.9 SELECT Syntax
 * 14.2.10 Subquery Syntax
 * 14.2.11 UPDATE Syntax
 * </pre>
 *
 * @author ogattaz
 *
 * @see http://dev.mysql.com/doc/refman/5.7/en/sql-syntax-data-manipulation.html
 */
public enum EManipulationStatement {
	COUNT, DELETE, INSERT, OTHER, REPLACE, SELECT, UPDATE;

	/**
	 * @return
	 */
	boolean isCount() {
		return this == COUNT;
	}
}
