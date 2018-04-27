package org.psem2m.utilities.rsrc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.psem2m.utilities.scripting.CXJsObjectBase;

/**
 * Path d'un URL qui pointe vers une ressource
 *
 */
public class CXRsrcUriPath extends CXJsObjectBase implements Cloneable {

	// Force l'extension de la ressource si pas d'extension dans aPath
	public static CXRsrcUriPath newInstanceWithExt(
			final CXRsrcUriDir aParentDir, final String aPath,
			final String aDefExt) {
		CXRsrcUriPath wPath = new CXRsrcUriPath(aParentDir, aPath);
		if (wPath.isValid() && !wPath.hasExtension() && aDefExt != null) {
			wPath.setExt(aDefExt);
		}
		return wPath;
	}

	private String pExt = null;
	private String pFullPath = null;
	private CXMimeType pMimeType = null;
	private String pName = null;
	private String pNameNoExt = null;
	private CXRsrcUriDir pParent = null;

	private String[] pPartArray;

	public CXRsrcUriPath(final CXRsrcUriDir aParentDir,
			final CXRsrcUriPath aPath) {
		if (aPath != null && aPath.isValid()) {
			pName = aPath.getName();
			pParent = aParentDir == null ? aPath.getParent() : aParentDir
					.concat(aPath.getParent());
			pFullPath = pParent.getUrlPath(pName);
		}
	}

	public CXRsrcUriPath(final CXRsrcUriDir aParentDir, final String aPath) {
		this(aParentDir, new CXRsrcUriPath(aPath));
	}

	private CXRsrcUriPath(final CXRsrcUriPath aPath) {
		if (aPath != null && isValid()) {
			pParent = aPath.pParent;
			pName = aPath.pName;
			pFullPath = aPath.pFullPath;
			pExt = aPath.pExt;
			pMimeType = aPath.pMimeType;
		}
	}

	public CXRsrcUriPath(final String aPath) {
		if (aPath != null) {
			String wPath = aPath.trim();
			if (wPath.indexOf(CXRsrcUriDir.BAD_SEPARATOR) != -1) {
				wPath = wPath.replace(CXRsrcUriDir.BAD_SEPARATOR,
						CXRsrcUriDir.SEPARATOR);
			}
			int wIdx = wPath.lastIndexOf(CXRsrcUriDir.SEPARATOR);
			if (wIdx == -1) {
				pName = wPath;
				pParent = new CXRsrcUriDir();
			} else {
				wIdx++;
				if (wIdx < wPath.length()) {
					pName = wPath.substring(wIdx);
				}
				pParent = new CXRsrcUriDir(wPath.substring(0, wIdx));
			}
			pFullPath = pParent.getUrlPath(pName);
		}
	}

	public CXRsrcUriPath(final String aParentDir, final String aPath) {
		this(aParentDir == null ? null : new CXRsrcUriDir(aParentDir),
				new CXRsrcUriPath(aPath));
	}

	@Override
	public Appendable addDescriptionInBuffer(Appendable aSB) {
		aSB = super.addDescriptionInBuffer(aSB);
		descrAddLine(aSB, "Path", pFullPath == null ? DESCR_NONE : pFullPath);
		return aSB;
	}

	@Override
	public CXRsrcUriPath clone() {
		return new CXRsrcUriPath(this);
	}

	// Get and Set

	// Converti en DIrectory sans l'extension
	public CXRsrcUriDir clone2Dir() {
		return pParent.concat(getNameNoExt());
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof CXRsrcUriPath)) {
			return false;
		}
		CXRsrcUriPath oMod = (CXRsrcUriPath) o;
		if (!oMod.isValid()) {
			return false;
		}
		return pFullPath.equals(oMod.pFullPath);
	}

	public boolean equalsIgnoreCase(final Object o) {
		if (o == null || !(o instanceof CXRsrcUriPath)) {
			return false;
		}
		CXRsrcUriPath oMod = (CXRsrcUriPath) o;
		if (!oMod.isValid()) {
			return false;
		}
		return pFullPath.equalsIgnoreCase(oMod.pFullPath);
	}

	public String getExtension() {
		if (pNameNoExt == null) {
			splitExt();
		}
		return pExt;
	}

	public String getFullPath() {
		return pFullPath;
	}

	// Si a/b/c/rsrc.ext -> Renvoie c - null sinon
	public String getLastDirName() {
		int wLastIdx = pParent != null ? pParent.getNbPart() - 1 : -1;
		return wLastIdx > 0 ? getPathPart(wLastIdx) : null;
	}

	public CXMimeType getMimeType() {
		if (pMimeType == null) {
			pMimeType = CXMimeType.getMimeTypeFromExt(getExtension());
		}
		return pMimeType;
	}

	// Avec extension
	public String getName() {
		return pName;
	}

	public String getNameNoExt() {
		if (pNameNoExt == null) {
			splitExt();
		}
		return pNameNoExt;
	}

	// Renvoie le nombre d'�l�ments du path
	public int getNbPart() {
		return getPartsArray().length;
	}

	public CXRsrcUriDir getParent() {
		return pParent;
	}

	// renvoie l'url sous la forme d'un tableau de strings
	public String[] getPartsArray() {
		if (pPartArray != null) {
			return pPartArray;
		}
		String[] wArray = pParent != null ? pParent.getPartsArray(false) : null;
		pPartArray = wArray != null ? Arrays.copyOf(wArray, wArray.length + 1)
				: new String[1];
		pPartArray[pPartArray.length - 1] = pName;
		return pPartArray;
	}

	// Renvoie le ni�me �l�ment du path
	public String getPathPart(final int aIdx) {
		int wLen = getNbPart();
		return aIdx >= 0 && aIdx < wLen ? getPartsArray()[aIdx] : null;
	}

	// Renvoie le path de la ressource � partir de la aIdx i�me partir inclue
	public CXRsrcUriPath getRsrcPathFromPart(final int aIdx) {
		if (aIdx < 0) {
			return null;
		}
		if (aIdx >= getNbPart()) {
			return new CXRsrcUriPath(pName);
		}
		return new CXRsrcUriPath(pParent.getPathFromPart(aIdx), pName);
	}

	// Renvoie le path de la ressource � partir de la 1ere partie d'url aUrlPart
	// non inclue
	public CXRsrcUriPath getRsrcPathFromPart(final String aUrlPart) {
		return getRsrcPathFromPart(aUrlPart, 0);
	}

	// Renvoie le path de la ressource � partir de la 1ere partie d'url aUrlPart
	// non inclue
	public CXRsrcUriPath getRsrcPathFromPart(final String aUrlPart,
			final int aOffset) {
		int wIdx = pParent.getFirstPartIdx(aUrlPart);
		if (wIdx == -1) {
			return null;
		}
		return getRsrcPathFromPart(wIdx + 1 + aOffset);
	}

	public CXRsrcUriPath getRsrcPathFromPartNotInclude(final String aUrlPart) {
		int wIdx = pParent.getFirstPartIdx(aUrlPart);
		if (wIdx == -1) {
			return null;
		}
		return getRsrcPathFromPart(wIdx + 1);
	}

	public URI getURI() throws URISyntaxException {
		return new URI(pFullPath);
	}

	public String getUrlStr(final String aAddress) {
		if (aAddress == null || aAddress.length() == 0) {
			return pFullPath;
		}
		StringBuilder wSB = new StringBuilder().append(aAddress);
		if (pParent != null) {
			wSB.append(pParent.getPath(true, true));
		}
		return wSB.append(pName).toString();
	}

	// Private car m�thode doit rester immutable

	public boolean hasExtension() {
		return getExtension() != null;
	}

	public boolean hasMimeType() {
		return getMimeType() != null;
	}

	public boolean hasName() {
		return pName != null && !pName.isEmpty();
	}

	public boolean isValid() {
		return pFullPath != null && pParent != null && pParent.isValid()
				&& pName != null;
	}

	private void setExt(final String aDefExt) {
		pExt = aDefExt;
		pName = new StringBuilder(pName).append('.').append(aDefExt).toString();
		pFullPath = new StringBuilder(pFullPath).append('.').append(aDefExt)
				.toString();
	}

	private void splitExt() {
		if (pName != null) {
			int wIdx = pName.lastIndexOf('.');
			if (wIdx != -1) {
				pExt = pName.substring(wIdx + 1);
				if (pExt.isEmpty()) {
					pExt = null;
				}
				pNameNoExt = pName.substring(0, wIdx);
			} else {
				pExt = null;
				pNameNoExt = pName;
			}
		}
	}

	@Override
	public String toString() {
		return getFullPath();
	}
}
