package org.cohorte.utilities.sql;

/**
 * <pre>
 * 14.1.1 ALTER DATABASE Syntax
 * 14.1.2 ALTER EVENT Syntax
 * 14.1.3 ALTER FUNCTION Syntax
 * 14.1.4 ALTER INSTANCE Syntax
 * 14.1.5 ALTER LOGFILE GROUP Syntax
 * 14.1.6 ALTER PROCEDURE Syntax
 * 14.1.7 ALTER SERVER Syntax
 * 14.1.8 ALTER TABLE Syntax
 * 14.1.9 ALTER TABLESPACE Syntax
 * 14.1.10 ALTER VIEW Syntax
 * 14.1.11 CREATE DATABASE Syntax
 * 14.1.12 CREATE EVENT Syntax
 * 14.1.13 CREATE FUNCTION Syntax
 * 14.1.14 CREATE INDEX Syntax
 * 14.1.15 CREATE LOGFILE GROUP Syntax
 * 14.1.16 CREATE PROCEDURE and CREATE FUNCTION Syntax
 * 14.1.17 CREATE SERVER Syntax
 * 14.1.18 CREATE TABLE Syntax
 * 14.1.19 CREATE TABLESPACE Syntax
 * 14.1.20 CREATE TRIGGER Syntax
 * 14.1.21 CREATE VIEW Syntax
 * 14.1.22 DROP DATABASE Syntax
 * 14.1.23 DROP EVENT Syntax
 * 14.1.24 DROP FUNCTION Syntax
 * 14.1.25 DROP INDEX Syntax
 * 14.1.26 DROP LOGFILE GROUP Syntax
 * 14.1.27 DROP PROCEDURE and DROP FUNCTION Syntax
 * 14.1.28 DROP SERVER Syntax
 * 14.1.29 DROP TABLE Syntax
 * 14.1.30 DROP TABLESPACE Syntax
 * 14.1.31 DROP TRIGGER Syntax
 * 14.1.32 DROP VIEW Syntax
 * 14.1.33 RENAME TABLE Syntax
 * 14.1.34 TRUNCATE TABLE Syntax
 * </pre>
 *
 * @author ogattaz
 * @see http://dev.mysql.com/doc/refman/5.7/en/sql-syntax-data-definition.html
 */
public enum EDefinitionStatement {
	ALTER, CREATE, DROP, RENAME, TRUNCATE;
}
