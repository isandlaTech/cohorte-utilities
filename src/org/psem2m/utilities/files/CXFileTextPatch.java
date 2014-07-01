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
	public static CXFileTextPatch parse(final Element wPatchElmt,
			final Map<String, String> aReplacements) throws Exception {
		CXFileTextPatch wPatch = new CXFileTextPatch();

		Element wLocationElmt = CXDomUtils.getFirstChildElmtByTag(wPatchElmt,
				"location");
		if (wLocationElmt == null) {
			throw new Exception(
					"There's no [location] element in the [patch] element");
		}
		List<Element> wLocationLineElemts = CXDomUtils.getElementsByTagName(
				wLocationElmt, "line");
		if (wLocationLineElemts.size() < 1) {
			throw new Exception(
					"There's no [line] element as child of the [location] element");
		}

		Element wTextElmt = CXDomUtils.getFirstChildElmtByTag(wPatchElmt,
				"text");
		if (wTextElmt == null) {
			throw new Exception(
					"There's no [text] element in the [text] element");
		}

		List<Element> wTextLineElemts = CXDomUtils.getElementsByTagName(
				wTextElmt, "line");
		if (wTextLineElemts.size() < 1) {
			throw new Exception(
					"There's no [line] element as child of the [text] element");
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
	public static List<CXFileTextPatch> parse(final List<Element> aPatchElmts,
			final Map<String, String> aReplacements) throws Exception {
		List<CXFileTextPatch> wPatches = new ArrayList<CXFileTextPatch>();
		for (Element wPatchElmt : aPatchElmts) {
			wPatches.add(parse(wPatchElmt, aReplacements));
		}
		return wPatches;
	}

	private final List<String> pLocationLines = new ArrayList<String>();
	private final List<String> pTextLines = new ArrayList<String>();
	public EPatchWhere pWhere = EPatchWhere.AFTER;

	/**
	 * @param aWhere
	 */
	public CXFileTextPatch() {
		super();
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
	 * @return
	 */
	public boolean isBefore() {
		return pWhere == EPatchWhere.BEFORE;
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
		CXStringUtils.appendKeyValInBuff(wSB, "LocationLines.size",
				pLocationLines.size());
		CXStringUtils.appendKeyValInBuff(wSB, "TextLines.size",
				pTextLines.size());
		return wSB.toString();
	}
}

/**
 * @author ogattaz
 * 
 */
enum EPatchWhere {
	AFTER, BEFORE;
}