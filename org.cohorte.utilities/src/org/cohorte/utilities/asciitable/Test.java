/**
 * Copyright (C) 2011 K Venkata Sudhakar <kvenkatasudhakar@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cohorte.utilities.asciitable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.cohorte.utilities.asciitable.impl.CollectionASCIITableAware;
import org.cohorte.utilities.asciitable.impl.JDBCASCIITableAware;
import org.cohorte.utilities.asciitable.spec.IASCIITableAware;
import org.psem2m.utilities.CXTimer;

/**
 * Tests of ASCII Table and CXTable.
 * 
 * CXTable
 * 
 * @author ogattaz
 * 
 * ASCII Table
 * 
 * @author K Venkata Sudhakar (kvenkatasudhakar@gmail.com)
 * @version 1.0
 *
 */
public class Test {

	/**
	 * @author ogattaz
	 *
	 */
	public static class Employee {

		private int age;
		private String hobby;
		private boolean married;
		private String name;
		private double salary;

		public Employee(String name, int age, String hobby, boolean married, double salary) {
			super();
			this.name = name;
			this.age = age;
			this.hobby = hobby;
			this.married = married;
			this.salary = salary;
		}

		public int getAge() {
			return age;
		}

		public String getHobby() {
			return hobby;
		}

		public String getName() {
			return name;
		}

		public double getSalary() {
			return salary;
		}

		public boolean isMarried() {
			return married;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public void setHobby(String hobby) {
			this.hobby = hobby;
		}

		public void setMarried(boolean married) {
			this.married = married;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setSalary(double salary) {
			this.salary = salary;
		}
	}

	static String[][] data = { { "Ram", "2000", "Manager", "#99, Silk board", "1111" },
			{ "Sri", "12000", "Developer", "BTM Layout", "22222" },
			{ "Prasad", "42000", "Lead", "#66, Viaya Bank Layout", "333333" },
			{ "Anu", "132000", "QA", "#22, Vizag", "4444444" }, { "Sai", "62000", "Developer", "#3-3, Kakinada" },
			{ "Venkat", "2000", "Manager" }, { "Raj", "62000" }, { "BTC" }, };

	private static void basicTests() {

		// ###########################
		printTestSeparator();

		CXTimer wTimer;
		long wToTalDuration = 0;
		int wMax = 2;
		for (int wTestIdx = 0; wTestIdx < wMax; wTestIdx++) {
			wTimer = CXTimer.newStartedTimer();

			String[] header = { "User Name", "Salary", "Designation", "Address", "Lucky#" };

			wTimer = CXTimer.newStartedTimer();

			ASCIITableHeader[] headerObjs = { new ASCIITableHeader("User Name", ASCIITable.ALIGN_LEFT),
					new ASCIITableHeader("Salary"), new ASCIITableHeader("Designation", ASCIITable.ALIGN_CENTER),
					new ASCIITableHeader("Address", ASCIITable.ALIGN_LEFT),
					new ASCIITableHeader("Lucky#", ASCIITable.ALIGN_RIGHT), };

			ASCIITable.getInstance().printTable(headerObjs, data);
			ASCIITable.getInstance().printTable(header, data);

			System.out.println(ASCIITable.getInstance().getTable(headerObjs, data));

			wToTalDuration += wTimer.getDurationNs();
		}
		printDuration(wMax, wToTalDuration);

	}

	/**
	 * 
	 */
	private static void collectionTests() {

		// ###########################
		printTestSeparator();

		CXTimer wTimer;
		long wToTalDuration = 0;
		int wMax = 2;
		for (int wTestIdx = 0; wTestIdx < wMax; wTestIdx++) {
			wTimer = CXTimer.newStartedTimer();
			Employee stud = new Employee("Sriram", 2, "Chess", false, 987654321.21d);
			Employee stud2 = new Employee("Sudhakar", 29, "Painting", true, 123456789.12d);
			List<Employee> students = Arrays.asList(stud, stud2);

			IASCIITableAware asciiTableAware = new CollectionASCIITableAware<>(students, "name", "age", "married",
					"hobby", "salary"); // properties to read

			ASCIITable.getInstance().printTable(asciiTableAware);

			asciiTableAware = new CollectionASCIITableAware<>(students,
			//
					Arrays.asList("name", "age", "married", "hobby", "salary"),
					// properties to read
					Arrays.asList("STUDENT_NAME", "HIS_AGE")); // custom headers

			ASCIITable.getInstance().printTable(asciiTableAware);
			wToTalDuration += wTimer.getDurationNs();
		}
		printDuration(wMax, wToTalDuration);
	}

	/**
	 * 
	 */
	private static void cxtableMatrixTests() {

		// ###########################
		printTestSeparator();

		CXTimer wTimer;
		CXTable wTable = null;
		long wToTalDuration = 0;
		int wMax = 100;
		for (int wTestIdx = 0; wTestIdx < wMax; wTestIdx++) {
			wTimer = CXTimer.newStartedTimer();
			// new table with first colum "numéro"
			wTable = new CXTable(CXTable.WITH_NUMBER, "%02d", "Numéro");

			// build header
			wTable.getHeader()
			//
					.addTH("Zéro", ASCIITable.ALIGN_CENTER)
					//
					.addTH("Un", ASCIITable.ALIGN_CENTER)
					//
					.addTH("Deux", ASCIITable.ALIGN_CENTER)
					//
					.addTH("Trois", ASCIITable.ALIGN_CENTER)
					//
					.addTH("Quatre", ASCIITable.ALIGN_CENTER);

			// load Matrix
			for (String[] wLine : data) {
				CXTBodyRow wTBodyRow = wTable.newTBodyRow();
				for (String wColunm : wLine) {
					wTBodyRow.addTD(wColunm);
				}
			}

			wTable.setTCell(8, 4, "aaaaa");
			wTable.setTCell(8, 4, "bbbbb");
			wTable.setTCell(9, 5, "ccccc");
			wTable.setTCell(10, 6, "ddddd");
			wTable.setTCell(11, 7, "eeeee");
			wTable.setTCell(10, 6, "DDDDD");

			for (int wIdx = 7; wIdx < 12; wIdx++) {
				wTable.setTCell(0, wIdx, "AAA");
				wTable.setTCell(2, wIdx, "BBB");
			}
			for (int wIdx = 3; wIdx < 12; wIdx++) {
				wTable.setTCell(wIdx, 0, "CCC");
				wTable.setTCell(wIdx, 1, "DDD");
			}
			for (int wIdx = 0; wIdx < 12; wIdx++) {
				wTable.setTCell(wIdx, wIdx, "000000");
			}

			wTable.toString();

			wToTalDuration += wTimer.getDurationNs();
		}
		System.out.println("Print table:");
		System.out.println(wTable.toString());
		System.out.println("Print dump matrix:");
		System.out.println(CXTable.dumpMatrix(wTable.getCellMatrix(), 15));

		printDuration(wMax, wToTalDuration);
	}

	/**
	 * 
	 */
	private static void cxtableTests() {

		// ###########################
		printTestSeparator();

		CXTimer wTimer;
		CXTable wTable = null;
		CXTHeaderRow wTHeaderRow = null;
		long wToTalDuration = 0;
		int wMax = 100;
		for (int wTestIdx = 0; wTestIdx < wMax; wTestIdx++) {
			wTimer = CXTimer.newStartedTimer();
			// new table with first colum "numéro"
			wTable = new CXTable(CXTable.WITH_NUMBER, "%02d", "Numéro");

			// build header
			wTHeaderRow = wTable.getHeader()
			//
					.addTH("User Name", ASCIITable.ALIGN_LEFT)
					//
					.addTH("Salary").addTH("Designation", ASCIITable.ALIGN_CENTER)
					//
					.addTH("Address", ASCIITable.ALIGN_LEFT)
					//
					.addTH("Lucky#", ASCIITable.ALIGN_RIGHT);

			// load Matrix
			for (String[] wLine : data) {
				CXTBodyRow wTBodyRow = wTable.newTBodyRow();
				for (String wColunm : wLine) {
					wTBodyRow.addTD(wColunm);
				}
			}
			wTable.toString();
			wToTalDuration += wTimer.getDurationNs();
		}
		System.out.println(wTable.toString());

		System.out.println(CXTable.dumpMatrix(wTable.getCellMatrix(), 8));

		printDuration(wMax, wToTalDuration);

		// ###########################
		printTestSeparator();

		List<CXTBodyRow> wRows = wTable.getBodyRows();
		for (int wTestIdx = 0; wTestIdx < wMax; wTestIdx++) {

			wTimer = CXTimer.newStartedTimer();
			// new table
			wTable = new CXTable();

			wTable.setTHeaderRow(wTHeaderRow);

			wTable.setBodyRows(wRows);

			wTable.toString();

			wToTalDuration += wTimer.getDurationNs();
		}
		System.out.println(wTable.toString());
		printDuration(wMax, wToTalDuration);

		// ###########################
		printTestSeparator();

		wRows = wTable.getBodyRows();
		for (int wTestIdx = 0; wTestIdx < wMax; wTestIdx++) {

			wTimer = CXTimer.newStartedTimer();
			// new table with first colum "number"
			wTable = new CXTable(CXTable.WITH_NUMBER, "%10d", "Number");

			wTable.setTHeaderRow(wTHeaderRow);

			wTable.setBodyRows(wRows);

			wTable.toString();

			wToTalDuration += wTimer.getDurationNs();
		}
		System.out.println(wTable.toString());
		printDuration(wMax, wToTalDuration);

	}

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void h2JDBCTests() throws ClassNotFoundException, SQLException {

		// ###########################
		printTestSeparator();
		CXTimer wTimer = null;
		long wToTalDuration = 0;
		int wMax = 100;
		for (int wTestIdx = 0; wTestIdx < wMax; wTestIdx++) {
			wTimer = CXTimer.newStartedTimer();

			if (false) {
				// Need to have h2-1.3.160.jar in classpath.
				Class.forName("org.h2.Driver");
				Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");

				// Print BUG_STAT table
				IASCIITableAware asciiTableAware = new JDBCASCIITableAware(conn, "select STATUS, COUNT from BUG_STAT");
				ASCIITable.getInstance().printTable(asciiTableAware);

				// Print USER table
				asciiTableAware = new JDBCASCIITableAware(conn, "select * from USER");
				ASCIITable.getInstance().printTable(asciiTableAware);
			}
			wToTalDuration += wTimer.getDurationNs();
		}
		printDuration(wMax, wToTalDuration);

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		basicTests();

		h2JDBCTests();

		oracleJDBCTests();

		collectionTests();

		cxtableTests();

		cxtableMatrixTests();
	}

	/**
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void oracleJDBCTests() throws ClassNotFoundException, SQLException {

		// ###########################
		printTestSeparator();

		CXTimer wTimer = null;
		long wToTalDuration = 0;
		int wMax = 100;
		for (int wTestIdx = 0; wTestIdx < wMax; wTestIdx++) {
			wTimer = CXTimer.newStartedTimer();

			if (false) {

				// Need to have ojdbc6.jar in classpath.
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORADBVENKAT",
						"digital_transformation", "digital_transformation");

				// Print BUG_STAT table
				IASCIITableAware asciiTableAware = new JDBCASCIITableAware(conn, "select * from CONTACTINFO");
				ASCIITable.getInstance().printTable(asciiTableAware);
			}
			wToTalDuration += wTimer.getDurationNs();
		}
		printDuration(wMax, wToTalDuration);

	}

	/**
	 * @param aNbTest
	 * @param aToTalDuration
	 */
	private static void printDuration(final int aNbTest, final long aToTalDuration) {
		System.out.println(String.format("nbTest=[%d] duratio=[%s] average=[%s]\n", aNbTest,
				CXTimer.nanoSecToMicroSecStr(aToTalDuration), CXTimer.nanoSecToMicroSecStr(aToTalDuration / aNbTest)));
	}

	/**
	 * 
	 */
	private static void printTestSeparator() {
		System.out
				.println("################################################################################################################\n");
	}

}
