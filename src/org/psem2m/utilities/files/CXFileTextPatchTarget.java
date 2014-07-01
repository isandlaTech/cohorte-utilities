package org.psem2m.utilities.files;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.psem2m.utilities.CXDomUtils;
import org.psem2m.utilities.CXStringUtils;
import org.w3c.dom.Element;

/**
 * <pre>
 * 	<target>
 * 		<path>${X3WEB_TOOLSROOT}/WEBAPPS/WAWEBSERVER/WEB-INF/web.xml</path>
 * 		<originalcopy>
 * 			<dir></dir><!-- the same as the target if none -->
 * 			<name>web.xml.original</name>
 * 		</originalcopy>
 * 	</target>
 * </pre>
 * 
 * @author ogattaz
 * 
 */
public class CXFileTextPatchTarget {

	/**
	 * @param aTargetElmts
	 * @return
	 */
	public static List<CXFileTextPatchTarget> parse(final Element aTargetElmt,
			final Map<String, String> aReplacements) throws Exception {
		List<CXFileTextPatchTarget> wPatchTargets = new ArrayList<CXFileTextPatchTarget>();

		Element wPathElmt = CXDomUtils.getFirstChildElmtByTag(aTargetElmt,
				"path");
		if (wPathElmt == null) {
			throw new Exception(
					"There's no [path] element in the [target] element");
		}
		Element wOriginalCopyhElmt = CXDomUtils.getFirstChildElmtByTag(
				aTargetElmt, "originalcopy");
		if (wOriginalCopyhElmt == null) {
			throw new Exception(
					"There's no [originalcopy] element in the [target] element");
		}
		Element wDirElmt = CXDomUtils.getFirstChildElmtByTag(
				wOriginalCopyhElmt, "dir");
		if (wDirElmt == null) {
			throw new Exception(
					"There's no [dir] element in the [originalcopy] element");
		}
		Element wNameElmt = CXDomUtils.getFirstChildElmtByTag(
				wOriginalCopyhElmt, "name");
		if (wNameElmt == null) {
			throw new Exception(
					"There's no [name] element in the [originalcopy] element");
		}

		// TODO : gestion des caractère jocker dans le path pour retourner n
		// targets

		CXFileTextPatchTarget wTarget = new CXFileTextPatchTarget();

		wTarget.pFilePathToBePatched = CXStringUtils.replaceVariables(wPathElmt
				.getTextContent().trim(), aReplacements);

		wTarget.pFileToBePatched = new CXFile(wTarget.pFilePathToBePatched);

		String wCopyDir = CXStringUtils.replaceVariables(wDirElmt
				.getTextContent().trim(), aReplacements);

		if (wCopyDir == null || wCopyDir.isEmpty()) {
			wCopyDir = new CXFile(wTarget.pFilePathToBePatched).getParent();
		}
		String wCopyName = CXStringUtils.replaceVariables(wNameElmt
				.getTextContent().trim(), aReplacements);

		wTarget.pFileToSaveOriginal = new CXFile(wCopyDir, wCopyName);

		wTarget.pFilePathToSaveOriginal = wTarget.pFileToSaveOriginal
				.getAbsolutePath();

		wPatchTargets.add(wTarget);

		return wPatchTargets;
	}

	/**
	 * @param aPatchElmts
	 * @return
	 */
	public static List<CXFileTextPatchTarget> parse(
			final List<Element> aTargetElmts,
			final Map<String, String> aReplacements) throws Exception {
		List<CXFileTextPatchTarget> wPatchTargets = new ArrayList<CXFileTextPatchTarget>();
		for (Element wTargetElmt : aTargetElmts) {
			wPatchTargets.addAll(parse(wTargetElmt, aReplacements));
		}
		return wPatchTargets;
	}

	private String pFilePathToBePatched = null;
	private String pFilePathToSaveOriginal = null;
	private CXFile pFileToBePatched = null;
	private CXFile pFileToSaveOriginal = null;

	/**
	 * 
	 */
	public CXFileTextPatchTarget() {
		super();
	}

	/**
	 * @return
	 */
	public String getFileExtensionToBePatched() {
		return pFileToBePatched.getExtension();
	}

	/**
	 * @return
	 */
	public String getFileExtensionToSaveOriginal() {
		return pFileToSaveOriginal.getExtension();
	}
	
	
	/**
	 * @return
	 */
	public String getFileNameToBePatched() {
		return pFileToBePatched.getName();
	}

	/**
	 * @return
	 */
	public String getFileNameToSaveOriginal() {
		return pFileToSaveOriginal.getName();
	}

	/**
	 * @return
	 */
	public String getFilePathToBePatched() {
		return pFilePathToBePatched;
	}

	/**
	 * @returnbn
	 */
	public String getFilePathToSaveOriginal() {
		return pFilePathToSaveOriginal;
	}

	/**
	 * @param aEncoding
	 * @return
	 */
	public CXFileText getFileTextToBePatched(final String aEncoding) {
		CXFileText wFileText = new CXFileText(pFileToBePatched);
		wFileText.setDefaultEncoding(aEncoding);
		return wFileText;
	}

	/**
	 * @param aEncoding
	 * @return
	 */
	public CXFileText getFileTextToSaveOriginal(final String aEncoding) {
		CXFileText wFileText = new CXFileText(pFileToSaveOriginal);
		wFileText.setDefaultEncoding(aEncoding);
		return wFileText;
	}

	/**
	 * @return
	 */
	public CXFile getFileToBePatched() {
		return pFileToBePatched;
	}

	/**
	 * @return
	 */
	public CXFile getFileToSaveOriginal() {
		return pFileToSaveOriginal;
	}

	/**
	 * @return true if restored
	 */
	public boolean restore() {

		CXFile wFileToSaveOriginal = new CXFile(getFilePathToSaveOriginal());
		if (wFileToSaveOriginal.exists()) {
			CXFile wFileToBePatched = new CXFile(getFilePathToBePatched());
			if (wFileToBePatched.exists()) {
				wFileToBePatched.delete();
			}
			wFileToSaveOriginal.renameTo(wFileToBePatched);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new CXFile(getFilePathToBePatched()).getName();
	}
}
