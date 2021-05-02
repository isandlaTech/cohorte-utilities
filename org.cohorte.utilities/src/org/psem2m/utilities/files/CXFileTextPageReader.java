package org.psem2m.utilities.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONObject;

/**
 * 1.4.0
 * 
 * Read large text file by page :
 * 
 * Sample : 8 pages of 10 lines to read a text file havin 73 lines ==>
 * duration=[ 4,743]
 * 
 * 
 * <pre>
 * 2021/05/02; 19:34:10:820;   ...  ==> PAGE 1
 * {
 *   "total": 10,
 *   "offset": 0,
 *   "size": 10,
 *   "lines": [
 *     {"00001": "﻿2021-04-28 10:13:34.524|INFO  |        qtp1895696416-186|              <init>|New trace file : /Users/ogattaz/workspaces/cohorte_iot_pack_git/Deployment/cohorte-data/log/tracker/2021_04_28+10_13_34.523+IMPORT_CHEVALF_TEST_COMPARATOR+cb1e5117_f29f_4785_a10a_46e2bbee6235.log"},
 *     {"00002": "2021-04-28 10:13:34.620|INFO  |asksExecutorShiroAware(1)|             newTask|======================================================================="},
 *     {"00003": "2021-04-28 10:13:34.621|INFO  |asksExecutorShiroAware(1)|             newTask|====BEGIN ===> Execution \"IMPORT_CHEVALF_TEST_COMPARATOR\" at 2021-04-28 10:13:34.186"},
 *     {"00004": "2021-04-28 10:13:34.664|INFO  |asksExecutorShiroAware(1)|             endTask|====END======> duration=[    41,776] task: Execution \"IMPORT_CHEVALF_TEST_COMPARATOR\" at 2021-04-28 10:13:34.186 "},
 *     {"00005": "2021-04-28 10:13:34.664|INFO  |asksExecutorShiroAware(1)|         processTask|task id=[Task_4620] admit [5] subtask(s) to execute"},
 *     {"00006": "2021-04-28 10:13:34.665|INFO  |asksExecutorShiroAware(1)|             newTask|======================================================================="},
 *     {"00007": "2021-04-28 10:13:34.665|INFO  |asksExecutorShiroAware(1)|             newTask|====BEGIN ===> Execution \"Task that watch a directory\" at 2021-04-28 10:13:34.269"},
 *     {"00008": "2021-04-28 10:13:34.759|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_0627] should execute a callable"},
 *     {"00009": "2021-04-28 10:13:34.789|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_0627] wait the end of the task"},
 *     {"00010": "2021-04-28 10:13:34.811|DETAIL|asksExecutorShiroAware(2)|                call|Watching in folder [fake_share_cifs] : OK : no file to process"}
 *   ]
 * }
 * 
 * ...
 * 
 * 
 * 2021/05/02; 19:34:10:823;   ... ==> PAGE 7
 * {
 *   "total": 10,
 *   "offset": 60,
 *   "size": 10,
 *   "lines": [
 *     {"00061": "2021-04-28 10:13:35.498|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9999] finished status=[SUCCESS]"},
 *     {"00062": "2021-04-28 10:13:35.499|INFO  |asksExecutorShiroAware(1)|             endTask|====END======> duration=[   163,196] task: Execution \"Task that rename a file\" at 2021-04-28 10:13:34.439 "},
 *     {"00063": "2021-04-28 10:13:35.690|INFO  |asksExecutorShiroAware(1)|                call|+-------+-----------+--------------------------------------------------------------------------------------------------------+--------------+--------------+"},
 *     {"00064": "                       |      |                         |                   +||  Num  |   TaskId  |                                                  Task                                                  | Milliseconds | hh:mm:ss.SSS |"},
 *     {"00065": "                       |      |                         |                   +|+-------+-----------+--------------------------------------------------------------------------------------------------------+--------------+--------------+"},
 *     {"00066": "                       |      |                         |                   +||   1   | Task_0627 |                    fileWatcher: Task that watch a directory                                            |      162     | 00:00:00.162 |"},
 *     {"00067": "                       |      |                         |                   +||   2   | Task_1849 |                    fileRenamer: Task that rename a file                                                |      93      | 00:00:00.093 |"},
 *     {"00068": "                       |      |                         |                   +||   3   | Task_9639 |                  csvComparator: Comparing the content of the current csv file against the previous one |      115     | 00:00:00.115 |"},
 *     {"00069": "                       |      |                         |                   +||   4   | Task_6405 |                    fileRenamer: Task that rename a file                                                |      117     | 00:00:00.117 |"},
 *     {"00070": "                       |      |                         |                   +||   5   | Task_9999 |                    fileRenamer: Task that rename a file                                                |      116     | 00:00:00.116 |"}
 *   ]
 * }
 * 2021/05/02; 19:34:10:823;  ...  ==> PAGE 8
 * {
 *   "total": 3,
 *   "offset": 70,
 *   "size": 3,
 *   "lines": [
 *     {"00071": "                       |      |                         |                   +|| TOTAL | Task_4620 | IMPORT_CHEVALF_TEST_COMPARATOR: IMPORT_CHEVALF_TEST_COMPARATOR                                         |     1022     | 00:00:01.022 |"},
 *     {"00072": "                       |      |                         |                   +|+-------+-----------+--------------------------------------------------------------------------------------------------------+--------------+--------------+"},
 *     {"00073": "2021-04-28 10:13:35.691|INFO  |asksExecutorShiroAware(1)|                call|====END======> duration=[    1070,719]=>[00:00:01.070] Orchestrator call finished"}
 *   ]
 * }
 * </pre>
 * 
 * @author ogattaz
 *
 */
public class CXFileTextPageReader extends CXFileTextAbstratReader {

	/**
	 * The representtaion of a text page
	 * 
	 * <pre>
	 *     {
	 *         "total":"213",
	 *         "offset":"20",
	 *         "size":5,
	 *     	"lines":[
	 *         	{ "00020":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_1849] finished status=[SUCCESS]"},
	 *         	{ "00021":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             endTask|====END======> duration=[   130,657] task: Execution \"Task that rename a file\" at 2021-04-28 10:13:34.308 "},
	 *         	{ "00022":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             newTask|======================================================================="},
	 *         	{ "00023":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             newTask|====BEGIN ===> Execution \"Comparing the content of the current csv file against the previous one\" at 2021-04-28 10:13:34.353"},
	 *         	{ "00024":"2021-04-28 10:13:35.041|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9639] should execute a callable"},
	 *         	{ "00025":"2021-04-28 10:13:35.062|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9639] wait the end of the task"}
	 *            ]
	 *         }
	 * </pre>
	 * 
	 * @author ogattaz
	 *
	 */
	public class CTextPage {

		public static final String PROP_LINES = "lines";

		public static final String PROP_OFFSET = "offset";

		public static final String PROP_SIZE = "size";

		public static final String PROP_TOTAL = "total";

		private final List<String> pLines;

		private final long pOffset;

		private final long pTotal;

		/**
		 * @param aTotal
		 * @param aLines
		 */
		CTextPage(final long aTotal, final long aOffset, final List<String> aLines) {
			super();
			pTotal = aTotal;
			pOffset = aOffset;
			pLines = aLines;
		}

		/**
		 * @return
		 */
		public List<String> getLines() {
			return pLines;
		}

		/**
		 * @return
		 */
		public long getNbLine() {
			return pLines.size();
		}

		/**
		 * @return
		 */
		public long getOffset() {
			return pOffset;
		}

		/**
		 * @return
		 */
		public long getTotal() {
			return pTotal;
		}

		/**
		 * <pre>
		 * 	    [ 
		 * 	        { "00020":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_1849] finished status=[SUCCESS]"},
		 * 	        { "00021":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             endTask|====END======> duration=[   130,657] task: Execution \"Task that rename a file\" at 2021-04-28 10:13:34.308 "},
		 * 	        { "00022":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             newTask|======================================================================="},
		 * 	        { "00023":"2021-04-28 10:13:34.997|INFO  |asksExecutorShiroAware(1)|             newTask|====BEGIN ===> Execution \"Comparing the content of the current csv file against the previous one\" at 2021-04-28 10:13:34.353"},
		 * 	        { "00024":"2021-04-28 10:13:35.041|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9639] should execute a callable"},
		 * 	        { "00025":"2021-04-28 10:13:35.062|INFO  |asksExecutorShiroAware(1)|         executeTask|task id=[Task_9639] wait the end of the task"}
		 * 	    ]
		 * </pre>
		 * 
		 * @param aLines the list of line
		 * @param aOffset the start offset
		 * @return
		 */
		public JSONArray linesToJson() {
			JSONArray wJsonLines = new JSONArray();
			long wNumLine = getOffset();
			String wKeyLine;
			for (String wLine : pLines) {
				JSONObject wJsonLine = new JSONObject();
				wNumLine++;
				wKeyLine = String.format("%05d", wNumLine);
				wJsonLine.put(wKeyLine, wLine);
				wJsonLines.put(wJsonLine);
			}
			return wJsonLines;
		}

		/**
		 * @return
		 */
		public JSONObject toJson() {
			JSONObject wPage = new JSONObject();
			wPage.put(PROP_TOTAL, getNbLine());
			wPage.put(PROP_OFFSET, getOffset());
			wPage.put(PROP_SIZE, getNbLine());
			wPage.put(PROP_LINES, linesToJson());
			return wPage;
		}
	}

	/**
	 * The filter which get only the line of the page
	 * 
	 * @author ogattaz
	 *
	 */
	private class CTextPager implements Predicate<String> {

		final long pEnd;
		int pNbScannedLine = 0;

		final long pStart;

		/**
		 * @param aOffset
		 * @param aPageSize
		 */
		CTextPager(final int aOffset, final int aPageSize) {
			pEnd = aOffset + aPageSize + 1;
			pStart = aOffset;
		}

		/**
		 * @return
		 */
		long getNbScannedLine() {
			return pNbScannedLine;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.function.Predicate#test(java.lang.Object)
		 */
		@Override
		public boolean test(String t) {
			pNbScannedLine++;
			return pNbScannedLine > pStart && pNbScannedLine < pEnd;
		}
	}

	/**
	 * @param aFile
	 */
	public CXFileTextPageReader(final File aFile) {
		super(aFile);
	}

	/**
	 * @param aFile
	 */
	/**
	 * @param aFile
	 * @param aFileCharset
	 */
	public CXFileTextPageReader(final File aFile, Charset aFileCharset) {
		super(aFile, aFileCharset);

	}

	/**
	 * @param aFile
	 * @param aCharsetName
	 */
	public CXFileTextPageReader(final File aFile, final String aCharsetName) {
		super(aFile, aCharsetName);

	}

	/**
	 * @param aFilePath
	 */
	public CXFileTextPageReader(final String aFilePath) {
		super(aFilePath);
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTextPageReader(final String aFilePath, final Charset aFileCharset) {
		super(aFilePath, aFileCharset);
	}

	/**
	 * @param aFilePath
	 * @param aFileCharset
	 */
	public CXFileTextPageReader(final String aFilePath, final String aCharsetName) {
		super(aFilePath, aCharsetName);
	}

	/**
	 * @param aOffset
	 * @param aPageSize
	 * @return
	 * @throws IOException
	 */
	public CTextPage readPage(final int aOffset, final int aPageSize) throws IOException {

		CTextPager wPager = new CTextPager(aOffset, aPageSize);

		List<String> wLines = Files.lines(getPath(), getFileCharset())
		//
				.filter(wPager)
				//
				.collect(Collectors.toList());

		return new CTextPage(wPager.getNbScannedLine(), aOffset, wLines);
	}

	/**
	 * 
	 * @param aOffset
	 * @param aPageSize
	 * @return
	 * @throws IOException
	 */
	public JSONObject redPageAsJson(final int aOffset, final int aPageSize) throws IOException {

		return readPage(aOffset, aPageSize).toJson();
	}

}
