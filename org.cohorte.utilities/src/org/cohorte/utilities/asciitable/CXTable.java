package org.cohorte.utilities.asciitable;

import java.util.ArrayList;
import java.util.List;

import org.cohorte.utilities.asciitable.spec.IASCIITable;
import org.psem2m.utilities.CXStringUtils;

/**
 * 
 * <pre>
 * </pre>
 * 
 * @author ogattaz
 *
 */
public class CXTable {

	public static final String NUMBER_FORMAT = "%3d";

	public static final String NUMBER_TITLE = "Num.";

	public static final boolean WITH_NUMBER = true;

	/**
	 * <pre>
	 *     [ --- A  --- ][ --- B  --- ][ --- C  --- ][ --- D  --- ][ --- E  --- ][ --- F  --- ]
	 * ( 0)[01          ][Ram         ][2000        ][Manager     ][#99, Silk bo][1111        ]
	 * ( 1)[02          ][Sri         ][12000       ][Developer   ][BTM Layout  ][22222       ]
	 * ( 2)[03          ][Prasad      ][42000       ][Lead        ][#66, Viaya B][333333      ]
	 * ( 3)[04          ][Anu         ][132000      ][QA          ][#22, Vizag  ][4444444     ]
	 * ( 4)[05          ][Sai         ][62000       ][Developer   ][#3-3, Kakina]
	 * ( 5)[06          ][Venkat      ][2000        ][Manager     ]
	 * ( 6)[07          ][Raj         ][62000       ]
	 * ( 7)[08          ][BTC         ]
	 * </pre>
	 * 
	 * @param aMatrix
	 * @return
	 */
	public static String dumpMatrix(final String[][] aMatrix, int aCellWidth) {
		if (aCellWidth < 12) {
			aCellWidth = 12;
		}
		StringBuilder wSB = new StringBuilder();
		// load Matrix
		int wIdx = 0;
		for (String[] wLine : aMatrix) {
			if (wIdx == 0) {
				wSB.append("    ");
				int wTitleWidht = aCellWidth - 10;
				int wMax = wLine.length;
				for (int wIdy = 0; wIdy < wMax; wIdy++) {
					String wTitle = String.valueOf(new Character((char) ('A' + wIdy)));
					String wValue = CXStringUtils.strAdjustLeft(wTitle, wTitleWidht, ' ');
					wSB.append(String.format("[ --- %s --- ]", wValue));
				}
				wSB.append('\n');
			}
			wSB.append(String.format("(%2d)", wIdx));

			for (String wColunm : wLine) {
				String wValue = CXStringUtils.strAdjustLeft(wColunm, aCellWidth, ' ');
				wSB.append(String.format("[%s]", wValue));
			}
			wSB.append('\n');
			wIdx++;
		}

		return wSB.toString();

	}

	private final List<CXTBodyRow> pBodyRows = new ArrayList<>();

	private final CXTHeaderRow pHeaderRow = new CXTHeaderRow();

	private final String pNumberFormat;

	private final String pNumberTitle;

	private final ASCIITableHeader pTH;

	private final boolean pWithNumber;

	/**
	 * 
	 * <pre>
	 * +-----------+--------+-------------+------------------------+---------+
	 * | User Name | Salary | Designation |         Address        |  Lucky# |
	 * +-----------+--------+-------------+------------------------+---------+
	 * | Ram       |   2000 |   Manager   | #99, Silk board        |    1111 |
	 * | Sri       |  12000 |  Developer  | BTM Layout             |   22222 |
	 * | Prasad    |  42000 |     Lead    | #66, Viaya Bank Layout |  333333 |
	 * | Anu       | 132000 |      QA     | #22, Vizag             | 4444444 |
	 * | Sai       |  62000 |  Developer  | #3-3, Kakinada         |         |
	 * | Venkat    |   2000 |   Manager   |                        |         |
	 * | Raj       |  62000 |             |                        |         |
	 * | BTC       |        |             |                        |         |
	 * +-----------+--------+-------------+------------------------+---------+
	 * 
	 * nbTest=[100] duratio=[47,471] average=[ 0,475]
	 * </pre>
	 * 
	 * @see
	 * /org.cohorte.utilities/src/org/cohorte/utilities/asciitable/Test.java
	 */
	public CXTable() {
		this(!WITH_NUMBER);
	}

	/**
	 * <pre>
	 * +--------+-----------+--------+-------------+------------------------+---------+
	 * | Numéro | User Name | Salary | Designation |         Address        |  Lucky# |
	 * +--------+-----------+--------+-------------+------------------------+---------+
	 * |     01 | Ram       |   2000 |   Manager   | #99, Silk board        |    1111 |
	 * |     02 | Sri       |  12000 |  Developer  | BTM Layout             |   22222 |
	 * |     03 | Prasad    |  42000 |     Lead    | #66, Viaya Bank Layout |  333333 |
	 * |     04 | Anu       | 132000 |      QA     | #22, Vizag             | 4444444 |
	 * |     05 | Sai       |  62000 |  Developer  | #3-3, Kakinada         |         |
	 * |     06 | Venkat    |   2000 |   Manager   |                        |         |
	 * |     07 | Raj       |  62000 |             |                        |         |
	 * |     08 | BTC       |        |             |                        |         |
	 * +--------+-----------+--------+-------------+------------------------+---------+
	 * 
	 * nbTest=[100] duratio=[92,188] average=[ 0,922]
	 * </pre>
	 * 
	 * @param aWithIndex
	 * 
	 * @see
	 * /org.cohorte.utilities/src/org/cohorte/utilities/asciitable/Test.java
	 */
	public CXTable(final boolean aWithIndex) {
		this(aWithIndex, NUMBER_FORMAT, NUMBER_TITLE);
	}

	/**
	 * @param aWithIndex
	 * @param aNumberFormat
	 */
	public CXTable(final boolean aWithNumber, final String aNumberFormat, final String aNumberTitle) {
		super();

		pWithNumber = aWithNumber;

		pNumberFormat = aNumberFormat;

		pNumberTitle = aNumberTitle;

		pTH = (pWithNumber) ? new ASCIITableHeader(pNumberTitle, ASCIITable.ALIGN_RIGHT) : null;
	}

	/**
	 * add a set of TBody rows
	 * 
	 * @param aTBodyRows
	 * @return
	 */
	public CXTable addAllTBodyRows(List<CXTBodyRow> aTBodyRows) {
		pBodyRows.addAll(aTBodyRows);
		return this;
	}

	/**
	 * add a set of TH to the Header
	 * 
	 * @param aTHeaders
	 * @return
	 */
	public CXTable addAllTHeaders(final CXTHeaderRow aTHeaders) {

		pHeaderRow.addAllTH(aTHeaders);
		return this;
	}

	/**
	 * @return
	 */
	public List<CXTBodyRow> getTBodyRows() {
		return pBodyRows;
	}

	/**
	 * @return
	 */
	public String[][] getCellMatrix() {
		List<String[]> wLines = new ArrayList<>();
		int wNumRow = 0;
		for (CXTBodyRow wBodyRow : pBodyRows) {

			if (pWithNumber) {
				wNumRow++;
				wBodyRow.addTD(0, String.format(pNumberFormat, wNumRow));
			}

			wLines.add(wBodyRow.toStringArray());

			if (pWithNumber) {
				wBodyRow.remove(0);
			}
		}
		return wLines.toArray(new String[wLines.size()][]);
	}

	/**
	 * @return
	 */
	public CXTHeaderRow getTHeaderRow() {
		return pHeaderRow;
	}

	/**
	 * @return
	 */
	public CXTBodyRow newTBodyRow() {
		CXTBodyRow wRow = new CXTBodyRow();
		pBodyRows.add(wRow);
		return wRow;
	}

	/**
	 * @param aMatrix
	 * @return
	 */
	public CXTable setCellMatrix(String[][] aMatrix) {

		pBodyRows.clear();

		// load Matrix
		for (String[] wLine : aMatrix) {
			CXTBodyRow wTBodyRow = newTBodyRow();
			for (String wColunm : wLine) {
				wTBodyRow.addTD(wColunm);
			}
		}
		return this;
	}

	/**
	 * @return
	 */
	public CXTable setTBodyRows(List<CXTBodyRow> aTBodyRows) {
		pBodyRows.clear();
		return addAllTBodyRows(aTBodyRows);
	}

	/**
	 * @param aLineIdx
	 * @param aColIdx
	 * @param aCellValue
	 * @return
	 */
	public CXTable setTCell(final int aLineIdx, final int aColIdx, final String aCellValue) {

		while (aLineIdx >= pBodyRows.size()) {
			pBodyRows.add(new CXTBodyRow());
		}
		CXTBodyRow wTBodyRow = pBodyRows.get(aLineIdx);

		wTBodyRow.setTD(aColIdx, aCellValue);

		pHeaderRow.setTH(aColIdx, null, IASCIITable.ALIGN_CENTER);

		return this;
	}

	/**
	 * @param aTHeaderRow
	 * @return
	 */
	public CXTable setTHeaderRow(final CXTHeaderRow aTHeaderRow) {

		pHeaderRow.clear();
		return addAllTHeaders(aTHeaderRow);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		ASCIITableHeader[] wHeaders = pHeaderRow.toHeaderArray(pTH);

		String[][] wMatrix = getCellMatrix();

		return ASCIITable.getInstance().getTable(wHeaders, wMatrix);
	}
}
