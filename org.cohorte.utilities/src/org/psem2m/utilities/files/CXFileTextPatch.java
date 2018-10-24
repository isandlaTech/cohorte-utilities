package org.psem2m.utilities.files;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.psem2m.utilities.CXDomUtils;
import org.psem2m.utilities.CXStringUtils;
import org.w3c.dom.Element;

/**
 * <pre>
 * 	<patch>
 * 		<location where="before">
 * 				<line>})</line>
 * 				<line>$(window).resize(function(){</line>
 * 				<line>X3.Frames.resize()</line>
 * 				<line>})</line>
 * 		</location>
 * 		<text>
 * 			<line>// X3 crypted exchange : Patch : Begin</line>
 * 			<line>// X509 certificate instanciation</line>
 * 			<line>// reformat the X509 certificate stream replacing all the '§' characater with a "new line"</line>
 * 			<line>gX509StreamBase64 = gX509StreamBase64.replace(/§/g, "\n");</line>
 * 			<line>// instanciates the x509 certificate with the received stream</line>
 * 			<line>gX509 = x3c_newCertificateX509(gX509StreamBase64);</line>
 * 			<line>// X3 crypted exchange : Patch : End</line>
 * 		</text>
 * 	</patch>
 * </pre>
 * 
 * @author ogattaz
 * 
 */
public class CXFileTextPatch {

	/**
	 * @param wPatchElmt
	 * @param aReplacements
	 * @return
	 * @throws Exception
	 */
	public static CXFileTextPatch parse(final Element wPatchElmt, final Map<String, String> aReplacements)
			throws Exception {
		CXFileTextPatch wPatch = new CXFileTextPatch();

		Element wLocationElmt = CXDomUtils.getFirstChildElmtByTag(wPatchElmt, "location");
		if (wLocationElmt == null) {
			throw new Exception("There's no [location] element in the [patch] element");
		}
		List<Element> wLocationLineElemts = CXDomUtils.getElementsByTagName(wLocationElmt, "line");
		if (wLocationLineElemts.size() < 1) {
			throw new Exception("There's no [line] element as child of the [location] element");
		}

		Element wTextElmt = CXDomUtils.getFirstChildElmtByTag(wPatchElmt, "text");
		if (wTextElmt == null) {
			throw new Exception("There's no [text] element in the [text] element");
		}

		List<Element> wAlterLine = CXDomUtils.getElementsByTagName(wLocationElmt, "alterLine");
		if (wAlterLine.size() == 1) {
			Element wStartAlterLine = CXDomUtils.getFirstChildElmtByTag(wAlterLine.get(0), "start");
			wPatch.setAlterLinStart(CXDomUtils.getTextNode(wStartAlterLine));

			Element wEndAlterLine = CXDomUtils.getFirstChildElmtByTag(wAlterLine.get(0), "end");
			wPatch.setAlterLineEnd(CXDomUtils.getTextNode(wEndAlterLine));

			Element wEndAlterSep = CXDomUtils.getFirstChildElmtByTag(wAlterLine.get(0), "sep");
			wPatch.setAlterLineSeparator(CXDomUtils.getTextNode(wEndAlterSep));
		} else {
			wPatch.setAlterLinStart("");
			wPatch.setAlterLineSeparator(",");
			wPatch.setAlterLineEnd("");

		}

		List<Element> wTextLineElemts = CXDomUtils.getElementsByTagName(wTextElmt, "line");
		if (wTextLineElemts.size() < 1) {
			throw new Exception("There's no [line] element as child of the [text] element");
		}

		String wWhere = wLocationElmt.getAttribute("where");
		if (wWhere == null || wWhere.isEmpty()) {
			wWhere = "after";
		}

		wPatch.setWhere(EPatchWhere.valueOf(wWhere.toUpperCase()));

		for (Element wLine : wLocationLineElemts) {
			wPatch.pLocationLines.add(CXDomUtils.getTextNode(wLine));
		}

		for (Element wLine : wTextLineElemts) {
			wPatch.pTextLines.add(CXDomUtils.getTextNode(wLine));
		}
		return wPatch;
	}

	/**
	 * @param aPatchElmts
	 * @param aReplacements
	 * @return
	 * @throws Exception
	 */
	public static List<CXFileTextPatch> parse(final List<Element> aPatchElmts, final Map<String, String> aReplacements)
			throws Exception {
		List<CXFileTextPatch> wPatches = new ArrayList<>();
		for (Element wPatchElmt : aPatchElmts) {
			wPatches.add(parse(wPatchElmt, aReplacements));
		}
		return wPatches;
	}

	private String pAlterLineEnd = null;
	private String pAlterLineSep = null;
	private String pAlterLineStart = null;

	private final List<String> pLocationLines = new ArrayList<>();

	private final List<String> pTextLines = new ArrayList<>();
	public EPatchWhere pWhere = EPatchWhere.AFTER;

	/**
	 * @param aWhere
	 */
	public CXFileTextPatch() {
		super();
	}

	/**
	 * return the character that identify the first character of the string that
	 * den
	 * 
	 * @return
	 */
	public String getAlterLineEnd() {
		return pAlterLineEnd;
	}

	/**
	 * return on alter line a separator between multiple value if it's necessary
	 */
	public String getAlterLineSeparator() {
		return pAlterLineSep;
	}

	public String getAlterLinStart() {
		return pAlterLineStart;
	}

	/**
	 * @return
	 */
	public String getLocationLine(final int aIdx) {
		return pLocationLines.get(aIdx);
	}

	/**
	 * @return
	 */
	public List<String> getLocationLines() {
		return pLocationLines;
	}

	/**
	 * @param aIdx
	 * @return
	 */
	public String getLoweredTrimedLocationLine(final int aIdx) {
		String wLine = null;
		if (aIdx < getNbLocationLines()) {
			wLine = getLocationLine(aIdx);
			if (wLine != null) {
				wLine = wLine.trim().toLowerCase();
			}
		}
		return wLine;
	}

	/**
	 * @return
	 */
	public int getNbLocationLines() {
		return (pLocationLines != null) ? getLocationLines().size() : -1;
	}

	/**
	 * @return
	 */
	public List<String> getTextLines() {
		return pTextLines;
	}

	/**
	 * @return
	 */
	public boolean isAfter() {
		return pWhere == EPatchWhere.AFTER;
	}

	/**
	 * alter the current line after the last character expressed in line to to
	 * be patch
	 * 
	 * @return
	 */
	public boolean isAlterLineAfter() {
		return pWhere == EPatchWhere.ALTERLINEAFTER;
	}

	/**
	 * @return
	 */
	public boolean isBefore() {
		return pWhere == EPatchWhere.BEFORE;
	}

	public void setAlterLineEnd(String aStr) {
		pAlterLineEnd = aStr;
	}

	/**
	 * return on alter line a separator between multiple value if it's necessary
	 */
	public void setAlterLineSeparator(String aStr) {
		pAlterLineSep = aStr;
	}

	public void setAlterLinStart(String aStr) {
		pAlterLineStart = aStr;
	}

	/**
	 * @param aLocationLines
	 */
	public void setLocationLines(final List<String> aLocationLines) {
		pLocationLines.clear();
		pLocationLines.addAll(aLocationLines);
	}

	/**
	 * @param aTextLines
	 */
	public void setTextLines(final List<String> aTextLines) {
		pTextLines.clear();
		pTextLines.addAll(aTextLines);
	}

	/**
	 * @param aWhere
	 */
	public void setWhere(final EPatchWhere aWhere) {
		pWhere = aWhere;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder wSB = new StringBuilder();
		CXStringUtils.appendKeyValInBuff(wSB, "Where", pWhere.name());
		CXStringUtils.appendKeyValInBuff(wSB, "LocationLines.size", pLocationLines.size());
		CXStringUtils.appendKeyValInBuff(wSB, "TextLines.size", pTextLines.size());
		return wSB.toString();
	}
}

/**
 * @author ogattaz
 * 
 */
enum EPatchWhere {
	AFTER, ALTERLINEAFTER, BEFORE;
}