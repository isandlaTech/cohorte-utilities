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
 * 			<dir></dir><!-- if none : the same as the target -->
 * 			<name>web.xml.original</name><!-- if none : the same as the target + .original -->
 * 		</originalcopy>
 * 		<options>
 *      	<variables replace=yes" delimiter="$$" />
 *      </options>
 * 	</target>
 * </pre>
 * 
 * @author ogattaz
 * 
 */
public class CXFileTextPatchTarget {

	/**
	 * @param aTargetElmt
	 * @param aReplacementParams
	 *            a map of values to replace the ${paramId} in the paths of the
	 *            targets definitions
	 * @return
	 * @throws Exception
	 */
	public static List<CXFileTextPatchTarget> parse(final Element aTargetElmt,
			final Map<String, String> aReplacementParams) throws Exception {

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

		// never null for an element node
		String wFilePathToBePatched = wPathElmt
				.getTextContent().trim();
		// replace the params ${paramId}
		if (!wFilePathToBePatched.isEmpty()){
			wFilePathToBePatched = CXStringUtils.replaceVariables(wFilePathToBePatched, aReplacementParams);
		}
		// if it is empty => error
		else{
			throw new Exception(
					"The [path] element in the [target] element is empty");	
		}
		wTarget.pFilePathToBePatched  = wFilePathToBePatched;
		wTarget.pFileToBePatched = new CXFile(wFilePathToBePatched);


		// never null for an element node
		String wCopyDir = wDirElmt.getTextContent().trim();
		// replace the params ${paramId}
		if (!wCopyDir.isEmpty()){
			wCopyDir= CXStringUtils.replaceVariables(wCopyDir, aReplacementParams);
		}
		// use the same dir as the file to be patched
		else{
			wCopyDir = wTarget.pFileToBePatched.getParent();
		}
		
		// never null for an element node
		String wCopyName = wNameElmt.getTextContent().trim();
		// replace the params ${paramId}
		if (!wCopyName.isEmpty()){
			wCopyName= CXStringUtils.replaceVariables(wCopyName, aReplacementParams);
		}
		// else, use the name of the file to be patched and add the suffix".orginal"
		else{
			wCopyName = wTarget.pFileToBePatched.getName()+".original";
		}
		
		wTarget.pFileToSaveOriginal = new CXFile(wCopyDir, wCopyName);

		wTarget.pFilePathToSaveOriginal = wTarget.pFileToSaveOriginal
				.getAbsolutePath();

		wTarget.pFileToSavePatchLog = new CXFile(wCopyDir,
				wTarget.pFileToBePatched.getName() + ".patching.log");

		wTarget.pOptions = new CXFileTextPatchTargetOptions(aTargetElmt);

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
	private CXFile pFileToSavePatchLog = null;
	private CXFileTextPatchTargetOptions pOptions = null;

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
	public String getFileExtensionToSavePatchLog() {
		return pFileToSavePatchLog.getExtension();
	}

	/**
	 * @return
	 */
	public String getFileNameToBePatched() {
		return pFileToBePatched.getName();
	}

	/**
	 * @return the file name of the copy of the original file
	 */
	public String getFileNameToSaveOriginal() {
		return pFileToSaveOriginal.getName();
	}

	/**
	 * @return the file name suffixed by ".patching.log"
	 */
	public String getFileNameToSavePachLog() {
		return pFileToSavePatchLog.getName();
	}

	/**
	 * @return
	 */
	public String getFilePathToBePatched() {
		return pFilePathToBePatched;
	}

	/**
	 * @return
	 */
	public String getFilePathToSaveOriginal() {
		return pFilePathToSaveOriginal;
	}

	/**
	 * @param aFile
	 * @param aEncoding
	 * @return
	 */
	private CXFileText getFileText(final CXFile aFile, final String aEncoding) {
		CXFileText wFileText = new CXFileText(aFile);
		wFileText.setDefaultEncoding(aEncoding);
		return wFileText;
	}

	/**
	 * @param aEncoding
	 * @return
	 */
	public CXFileText getFileTextToBePatched(final String aEncoding) {
		return getFileText(pFileToBePatched, aEncoding);
	}

	/**
	 * @param aEncoding
	 * @return
	 */
	public CXFileText getFileTextToSaveOriginal(final String aEncoding) {
		return getFileText(pFileToSaveOriginal, aEncoding);
	}

	/**
	 * @param aEncoding
	 * @return
	 */
	public CXFileText getFileTextToSavePatchLog(final String aEncoding) {
		return getFileText(pFileToSavePatchLog, aEncoding);
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
	 * @return
	 */
	public CXFile getFileToSavePachLog() {
		return pFileToSavePatchLog;
	}

	/**
	 * @return
	 */
	public CXFileTextPatchTargetOptions getOptions() {
		return pOptions;
	}

	/**
	 * @return true if restored
	 */
	public boolean restore() {

		// if ".patchin.log" file exists => remove it
		if (pFileToSavePatchLog != null && pFileToSavePatchLog.exists()) {
			pFileToSavePatchLog.delete();
		}

		// if ".original" file exists => remove it
		if (pFileToSaveOriginal != null && pFileToSaveOriginal.exists()) {
			if (pFileToBePatched.exists()) {
				pFileToBePatched.delete();
			}
			pFileToSaveOriginal.renameTo(pFileToBePatched);
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

/**
 * <pre>
 * 	<options>
 * 		<variables replace="true" delimiter="$$" />
 * 	</options>
 * </pre>
 * 
 * @author ogattaz
 * 
 */
class CXFileTextPatchTargetOptions {

	private boolean pHasOptions = false;
	private boolean pReplacpVariables = false;
	private String pVariablesdDelimiter = CXStringUtils.EMPTY;

	/**
	 * @param aOptionsElmt
	 */
	CXFileTextPatchTargetOptions(final Element aTargetElmt) {
		super();
		init(aTargetElmt);
	}

	/**
	 * @return
	 */
	public String getVariablesdDelimiter() {
		return pVariablesdDelimiter;
	}

	/**
	 * @return
	 */
	public boolean hasOptions() {
		return pHasOptions;
	}

	/**
	 * @param aOptionsElmt
	 */
	private void init(final Element aTargetElmt) {

		Element wOptionsElmt = CXDomUtils.getFirstChildElmtByTag(aTargetElmt,
				"options");

		if (wOptionsElmt != null) {

			Element wVariablesElmt = CXDomUtils.getFirstChildElmtByTag(
					wOptionsElmt, "variables");

			if (wVariablesElmt != null) {
				pReplacpVariables = "true".equalsIgnoreCase(wVariablesElmt
						.getAttribute("replace"));
				pVariablesdDelimiter = wVariablesElmt.getAttribute("delimiter");
				pHasOptions = true;
			}
		}
	}

	/**
	 * @return
	 */
	public boolean mustReplaceVariables() {
		return pReplacpVariables;
	}
}
