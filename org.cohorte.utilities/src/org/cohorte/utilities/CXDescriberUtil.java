package org.cohorte.utilities;

import java.util.StringTokenizer;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.IXDescriber;

/**
 * @author ogattaz
 * 
 */
public class CXDescriberUtil implements IXDescriber {

	// Constantes
	protected final static String DESCR_CHAR_TITLE = "*";
	public final static char DESCR_NEWLINE = '\n';
	public final static String DESCR_NEWLINE_STR = "\n";
	public final static String DESCR_NONE = "None";
	public final static String DESCR_NULL = "Null";
	public final static char DESCR_SPACE = ' ';
	public final static String DESCR_STD_INDENT = "   ";
	protected final static String DESCR_STR_SUBTITLE1 = CXStringUtils.strFromChar('-', 20);
	protected final static String DESCR_STR_SUBTITLE2 = CXStringUtils.strFromChar('-', 20).concat("\n");
	protected final static String DESCR_STR_TITLE = CXStringUtils.strFromChar('*', 100).concat("\n");
	public final static String DESCR_VALUE_BEGIN = "=[";
	public final static char DESCR_VALUE_END = ']';
	public final static String DESCR_VALUE_SEP = " - ";
	public final static String EMPTY_STR = "";
	public final static String TOKEN_NEWLINE = "\r\n";

	/**
	 * @param aToIndent
	 * @return
	 */
	public static StringBuilder descrAddIndent(String aToIndent) {

		StringBuilder wSb = new StringBuilder();
		return aToIndent == null ? wSb : descrAddIndent(wSb, aToIndent, null);
	}

	/**
	 * @param aToBuff
	 * @param aToIndent
	 * @return
	 */
	public static StringBuilder descrAddIndent(StringBuilder aToBuff, String aToIndent) {

		return aToIndent == null ? aToBuff : descrAddIndent(aToBuff, aToIndent, null);
	}

	/**
	 * @param aToBuff
	 * @param aToIndentBuff
	 * @param aIndent
	 * @return
	 */
	public static StringBuilder descrAddIndent(StringBuilder aToBuff, String aToIndentBuff, String aIndent) {

		String wIndent = aIndent == null ? DESCR_STD_INDENT : aIndent;
		StringBuilder wResult = aToBuff == null ? new StringBuilder() : aToBuff;
		if (aToIndentBuff != null && aToIndentBuff.length() != 0) {
			StringTokenizer wTok = new StringTokenizer(aToIndentBuff, TOKEN_NEWLINE, false);
			while (wTok.hasMoreTokens())
				wResult.append(wIndent).append(wTok.nextToken()).append(DESCR_NEWLINE);
		}
		return wResult;
	}

	/**
	 * @param aToBuff
	 * @param aToIndentBuff
	 * @return
	 */
	public static StringBuilder descrAddIndent(StringBuilder aToBuff, StringBuilder aToIndentBuff) {

		return descrAddIndent(aToBuff, aToIndentBuff.toString(), null);
	}

	/**
	 * @param aToBuff
	 * @param aToIndentBuff
	 * @param aIndent
	 * @return
	 */
	public static StringBuilder descrAddIndent(StringBuilder aToBuff, StringBuilder aToIndentBuff, String aIndent) {

		return descrAddIndent(aToBuff, aToIndentBuff.toString(), aIndent);
	}

	/**
	 * @param aBuff
	 * @return
	 */
	public static StringBuilder descrAddLine(StringBuilder aBuff) {

		return descrCheckBuffer(aBuff).append(DESCR_NEWLINE);
	}

	/**
	 * @param aBuff
	 * @param aLine
	 * @return
	 */
	public static StringBuilder descrAddLine(StringBuilder aBuff, String aLine) {

		return descrCheckBuffer(aBuff).append(aLine).append(DESCR_NEWLINE);
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddLine(StringBuilder aBuff, String aLib, boolean aValue) {

		return descrAddLine(aBuff, aLib, String.valueOf(aValue));
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddLine(StringBuilder aBuff, String aLib, double aValue) {

		return descrAddLine(aBuff, aLib, String.valueOf(aValue));
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddLine(StringBuilder aBuff, String aLib, int aValue) {

		return descrAddLine(aBuff, aLib, String.valueOf(aValue));
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddLine(StringBuilder aBuff, String aLib, long aValue) {

		return descrAddLine(aBuff, aLib, String.valueOf(aValue));
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddLine(StringBuilder aBuff, String aLib, String aValue) {

		return descrAddProp(aBuff, aLib, aValue).append(DESCR_NEWLINE);
	}

	/**
	 * @param aBuff
	 * @param aLine
	 * @return
	 */
	public static StringBuilder descrAddLine(StringBuilder aBuff, StringBuilder aLine) {

		return descrCheckBuffer(aBuff).append(aLine).append(DESCR_NEWLINE);
	}

	/**
	 * @param aBuff
	 * @param aLine
	 * @param aIndent
	 * @return
	 */
	public static StringBuilder descrAddLineIndent(StringBuilder aBuff, String aLine, String aIndent) {

		return descrAddLine(aBuff.append(aIndent), aLine);
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @param aIndent
	 * @return
	 */
	public static StringBuilder descrAddLineIndent(StringBuilder aBuff, String aLib, String aValue, String aIndent) {

		return descrAddLine(aBuff.append(aIndent), aLib, aValue);
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddProp(StringBuilder aBuff, String aLib, boolean aValue) {

		return descrAddProp(aBuff, aLib, String.valueOf(aValue));
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddProp(StringBuilder aBuff, String aLib, char aValue) {

		return descrAddProp(aBuff, aLib, String.valueOf(aValue));
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddProp(StringBuilder aBuff, String aLib, double aValue) {

		return descrAddProp(aBuff, aLib, String.valueOf(aValue));
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddProp(StringBuilder aBuff, String aLib, long aValue) {

		return descrAddProp(aBuff, aLib, String.valueOf(aValue));
	}

	/**
	 * @param aBuff
	 * @param aLib
	 * @param aValue
	 * @return
	 */
	public static StringBuilder descrAddProp(StringBuilder aBuff, String aLib, String aValue) {

		StringBuilder wBuff = descrCheckBuffer(aBuff);
		boolean wSep = wBuff.length() != 0;
		if (wSep) {
			wSep = wBuff.charAt(wBuff.length() - 1) != DESCR_NEWLINE;
		}
		if (wSep)
			wBuff.append(DESCR_VALUE_SEP);
		if (aLib == null || aLib.isEmpty())
			wBuff.append('[').append(aValue == null ? EMPTY_STR : aValue).append(']');
		else
			wBuff.append(aLib).append(DESCR_VALUE_BEGIN).append(aValue == null ? EMPTY_STR : aValue)
					.append(DESCR_VALUE_END);
		return wBuff;
	}

	/**
	 * @param aBuff
	 * @param aSubTitle
	 * @return
	 */
	public static StringBuilder descrAddSubTitle(StringBuilder aBuff, String aSubTitle) {

		StringBuilder wBuff = descrCheckBuffer(aBuff).append(DESCR_STR_SUBTITLE1);
		if (aSubTitle != null && aSubTitle.length() != 0)
			wBuff.append(DESCR_SPACE).append(aSubTitle).append(DESCR_SPACE).append(DESCR_STR_SUBTITLE2);
		else
			wBuff.append(DESCR_STR_SUBTITLE2);
		return wBuff;
	}

	public static StringBuilder descrAddSubTitleLine(StringBuilder aBuff) {

		return descrAddSubTitle(aBuff, null);
	}

	public static StringBuilder descrAddText(StringBuilder aBuff, String atext) {

		return descrCheckBuffer(aBuff).append(atext);
	}

	public static StringBuilder descrAddText(StringBuilder aBuff, StringBuilder atext) {

		return descrCheckBuffer(aBuff).append(atext);
	}

	public static StringBuilder descrAddTitle(String aTitle) {

		return descrAddTitle(null, aTitle);
	}

	// aTitle=null -> trace une lignes -> utilisé pour marquer la fin du
	// paragraphe
	public static StringBuilder descrAddTitle(StringBuilder aBuff, String aTitle) {

		StringBuilder wBuff = descrCheckBuffer(aBuff).append(DESCR_STR_TITLE);
		if (aTitle != null && aTitle.length() != 0)
			wBuff.append(DESCR_CHAR_TITLE).append(DESCR_SPACE).append(aTitle.toUpperCase()).append(DESCR_NEWLINE)
					.append(DESCR_STR_TITLE);
		return wBuff;
	}

	public static StringBuilder descrAddTitleLine(StringBuilder aBuff) {

		return descrAddTitle(aBuff, null);
	}

	// Utils description

	public static StringBuilder descrCheckBuffer(StringBuilder aSB) {

		return aSB == null ? new StringBuilder() : aSB;
	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aBuffer) {

		return aBuffer;
	}

	public String descrClassName() {

		return getClass().getSimpleName();
	}

	public StringBuilder descrToBuilder(StringBuilder aSB) {

		return aSB == null ? new StringBuilder(1024) : aSB;
	}

	// Interface IXtdRsrcDescriber

	public String descrToString() {

		return descrToBuilder(null).toString();
	}

	@Override
	public String toDescription() {

		return descrToString();
	}

	@Override
	public String toString() {

		return descrToString();
	}
}
