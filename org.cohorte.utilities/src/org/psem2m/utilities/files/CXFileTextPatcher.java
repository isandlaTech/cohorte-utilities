package org.psem2m.utilities.files;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLoggerBase;

/**
 * @author ogattaz
 * 
 */
public class CXFileTextPatcher {

	private boolean pApplyWithPrefix = false;
	private final CXFileText pFileToBePatched;
	private final CXFileText pFileToSaveOriginal;
	private List<CXFileTextPatch> pListOfPaches = null;
	private final IActivityLoggerBase pLogger;
	private List<String> pPatchedLines = null;
	private final Properties pReplacementVariables = new Properties();
	private CXFileTextPatchTargetOptions pTargetOptions = null;

	/**
	 * @param aFileText
	 */
	public CXFileTextPatcher(final CXFileText aFileToBePatched, final CXFileText aFileToSaveOriginal)
			throws IllegalArgumentException {
		this(aFileToBePatched, aFileToSaveOriginal, null);
	}

	/**
	 * @param aFileText
	 * @param aLogger
	 */
	public CXFileTextPatcher(final CXFileText aFileToBePatched, final CXFileText aFileToSaveOriginal,
			final IActivityLoggerBase aLogger) throws IllegalArgumentException {
		super();

		pFileToBePatched = validFileToBePatched(aFileToBePatched);
		pFileToSaveOriginal = validSavedFileText(aFileToSaveOriginal);

		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull.getInstance();
		pLogger.logInfo(this, "<init>", "instanciated FileToBePatched=[%s] FileToSaveOriginal=[%s]",
				pFileToBePatched.getName(), pFileToSaveOriginal.getName());
	}

	/**
	 * @param aTarget
	 * @throws IllegalArgumentException
	 */
	public CXFileTextPatcher(final CXFileTextPatchTarget aTarget) throws IllegalArgumentException {
		this(aTarget, Charset.defaultCharset().toString(), null);
	}

	/**
	 * @param aTarget
	 * @param aLogger
	 * @throws IllegalArgumentException
	 */
	public CXFileTextPatcher(final CXFileTextPatchTarget aTarget, final IActivityLoggerBase aLogger)
			throws IllegalArgumentException {
		this(aTarget, Charset.defaultCharset().toString(), aLogger);
	}

	/**
	 * @param aTarget
	 * @param aEncoding
	 * @param aLogger
	 * @throws IllegalArgumentException
	 */
	public CXFileTextPatcher(final CXFileTextPatchTarget aTarget, final String aEncoding,
			final IActivityLoggerBase aLogger) throws IllegalArgumentException {
		this(aTarget.getFileTextToBePatched(aEncoding), aTarget.getFileTextToSaveOriginal(aEncoding), aLogger);

		pTargetOptions = aTarget.getOptions();
	}

	/**
	 * @param aListLines
	 * @param aLine
	 * @param aPrefix
	 */
	private void addLine(final List<String> aListLines, final String aLine, final String aPrefix) {
		if (aPrefix != null) {
			aListLines.add(String.format("%s%s", aPrefix, aLine));
		} else {
			aListLines.add(aLine);
		}
	}

	/**
	 * @param aPatch
	 * @param aLinesToBePatched
	 * @return
	 * @throws Exception
	 */
	private List<String> applyOnePatch(final CXFileTextPatch aPatch, final List<String> aLinesToBePatched)
			throws Exception {

		pLogger.logInfo(this, "applyOnePatch", "LinesToBePatched size=[%d] Patch:%s",
				(aLinesToBePatched != null) ? aLinesToBePatched.size() : -1, aPatch);

		// test if the patch is not already in place
		testIfPatchable(aLinesToBePatched, aPatch);

		List<String> wLinesPatched = new ArrayList<>();

		// find the location
		int wPatchStartLineIdx = findLocation(aLinesToBePatched, aPatch);

		if (wPatchStartLineIdx < 0) {
			throw new Exception(String.format("Unable to find the location lines of the patch [%s]",
					CXStringUtils.stringListToString(aPatch.getLocationLines())));
		}

		pLogger.logInfo(this, "applyOnePatch", "PatchStartLineIdx=[%d]", wPatchStartLineIdx);

		if (aPatch.isAlterLineAfter()) {
			// copies lines before
			for (int wIdx = 0; wIdx < wPatchStartLineIdx; wIdx++) {
				addLine(wLinesPatched, aLinesToBePatched.get(wIdx), calcPrefix(wLinesPatched, "before"));
			}
			// alter current patch line to add element in it
			// add in the current line all string to be added if it doesn't
			// exists
			String wLineToPach = aLinesToBePatched.get(wPatchStartLineIdx);

			int wStartAlter = wLineToPach.indexOf(aPatch.getAlterLinStart()) + aPatch.getAlterLinStart().length();
			String wLineToPachNew = "";
			for (String wStringToReplace : aPatch.getTextLines()) {
				if (!wLinesPatched.contains(wStringToReplace)) {
					wLineToPachNew = wLineToPach.substring(0, wStartAlter) + wStringToReplace;
					if (!wLineToPach.substring(wStartAlter).startsWith(aPatch.getAlterLineEnd())) {
						wLineToPachNew += aPatch.getAlterLineSeparator();
					}
					wLineToPachNew += wLineToPach.substring(wStartAlter);

				}
				addLine(wLinesPatched, wLineToPachNew, calcPrefix(wLinesPatched, "alter"));

			}

			// copies lines after
			for (int wIdx = wPatchStartLineIdx + 1; wIdx < aLinesToBePatched.size(); wIdx++) {
				addLine(wLinesPatched, aLinesToBePatched.get(wIdx), calcPrefix(wLinesPatched, "after"));
			}

		} else {
			if (aPatch.isAfter()) {
				wPatchStartLineIdx += aPatch.getNbLocationLines();
			}
			// copies lines before
			for (int wIdx = 0; wIdx < wPatchStartLineIdx; wIdx++) {
				addLine(wLinesPatched, aLinesToBePatched.get(wIdx), calcPrefix(wLinesPatched, "before"));
			}
			// copies the lines of the patch
			for (String wLine : aPatch.getTextLines()) {

				if (pTargetOptions != null && pTargetOptions.mustReplaceVariables() && hasReplacementVariables()) {
					wLine = replaceVariablesInLine(wLine, pTargetOptions.getVariablesdDelimiter());
				}
				addLine(wLinesPatched, wLine, calcPrefix(wLinesPatched, "patch"));
			}
			// copies lines after
			for (int wIdx = wPatchStartLineIdx; wIdx < aLinesToBePatched.size(); wIdx++) {
				addLine(wLinesPatched, aLinesToBePatched.get(wIdx), calcPrefix(wLinesPatched, "after"));
			}
		}

		return wLinesPatched;
	}

	/**
	 * @param aPatchDom
	 * @param aSavedFileText
	 * @throws Exception
	 */
	public List<String> applyPatches(final List<CXFileTextPatch> aListOfPaches) throws Exception {
		pLogger.logInfo(this, "applyPatches", "ListOfPaches size=[%d]", (aListOfPaches != null) ? aListOfPaches.size()
				: -1);

		// remove old result and the old patches
		resetPatchedLines();
		resetListOfPaches();

		List<String> wLines = pFileToBePatched.readLines();

		for (CXFileTextPatch wPatch : aListOfPaches) {
			wLines = applyOnePatch(wPatch, wLines);
		}

		// stores the applied patches
		setListOfPaches(aListOfPaches);
		// stores the result
		setPatchedLines(wLines);

		return wLines;
	}

	/**
	 * @param aListOfPaches
	 * @return
	 * @throws Exception
	 */
	public List<String> applyPatchesAndSave(final List<CXFileTextPatch> aListOfPaches) throws Exception {

		pLogger.logInfo(this, "applyAndSavePatches", "ListOfPaches size=[%d]",
				(aListOfPaches != null) ? aListOfPaches.size() : -1);
		List<String> wPatchedLines = applyPatches(aListOfPaches);
		// save the files (original and copy) if all patches applied
		saveResult(wPatchedLines);
		return wPatchedLines;
	}

	/**
	 * @param aPrefix
	 * @return
	 */
	private String calcPrefix(final List<String> aListLines, final String aPrefix) {
		return (pApplyWithPrefix) ? String.format("%4d %6s >", aListLines.size(), aPrefix) : null;
	}

	/**
	 * @param aLinesToBePatched
	 * @param aLocationLines
	 * @return
	 */
	private int findLocation(final List<String> aLinesToBePatched, final CXFileTextPatch aPatch) {

		pLogger.logInfo(this, "findLocation", "Nb Lines to find=[%d]", aPatch.getLocationLines().size());

		int wFoundStartIdx = -1;
		int wNbFoundLines = 0;
		int wLineToBePatchedIdx = 0;

		String wLocationLine = null;

		for (String wLineToBePatched : aLinesToBePatched) {

			if (wLocationLine == null) {
				wLocationLine = aPatch.getLoweredTrimedLocationLine(wNbFoundLines);
			}

			if (wLineToBePatched != null && wLineToBePatched.toLowerCase().contains(wLocationLine)) {
				wNbFoundLines++;
				pLogger.logInfo(this, "findLocation", "NbFoundLines=[%d][%s]", wNbFoundLines, wLocationLine);
				wLocationLine = null;
				if (wFoundStartIdx == -1) {
					wFoundStartIdx = wLineToBePatchedIdx;
				}

			} else {
				wNbFoundLines = 0;
				wLocationLine = null;
				wFoundStartIdx = -1;
			}

			if (wNbFoundLines > 0) {
				if (wNbFoundLines == aPatch.getLocationLines().size()) {
					return wFoundStartIdx;
				}
			}
			wLineToBePatchedIdx++;

		}
		return -1;
	}

	/**
	 * @return
	 */
	public List<String> getPatchedLines() {
		return pPatchedLines;
	}

	/**
	 * @return
	 */
	public String getPatchedText() {
		List<String> wPatchedLines = getPatchedLines();
		if (wPatchedLines == null) {
			return null;
		}
		StringBuilder wSB = new StringBuilder();

		for (String wLine : wPatchedLines) {
			wSB.append(wLine).append('\n');
		}
		wSB.deleteCharAt(wSB.length() - 1);
		return wSB.toString();
	}

	/**
	 * @return
	 */
	public boolean hasListOfPatches() {
		return pListOfPaches != null;
	}

	/**
	 * @return
	 */
	public boolean hasPatchedLines() {
		return getPatchedLines() != null;
	}

	/**
	 * @return
	 */
	public boolean hasReplacementVariables() {
		return pReplacementVariables != null && pReplacementVariables.size() > 0;
	}

	/**
	 * @return
	 */
	public boolean isPatchesApplied() {
		return hasListOfPatches() && hasPatchedLines();
	}

	/**
	 * @param aLine
	 * @param aVariableDelimiter
	 * @return
	 */
	private String replaceVariablesInLine(final String aLine, final String aVariableDelimiter) {

		if (aLine == null || aLine.isEmpty()) {
			return aLine;
		}
		int wPosDelimStart = aLine.indexOf(aVariableDelimiter);
		if (wPosDelimStart == -1) {
			return aLine;
		}
		int wPosDelimEnd = aLine.indexOf(aVariableDelimiter, wPosDelimStart + aVariableDelimiter.length());
		if (wPosDelimEnd == -1) {
			return aLine;
		}
		String wVariableName = aLine.substring(wPosDelimStart + aVariableDelimiter.length(), wPosDelimEnd);

		String wValue = pReplacementVariables.getProperty(wVariableName);
		if (wValue == null) {
			return aLine;
		}
		if (pTargetOptions != null && pTargetOptions.isRuleForceSlashOn()) {
			// replacing all occurrences of oldChars
			wValue = wValue.replace('\\', '/');
		}
		String wNewLine = aLine.replace(aLine.substring(wPosDelimStart, wPosDelimEnd + aVariableDelimiter.length()),
				wValue);

		if (!wNewLine.contains(aVariableDelimiter)) {
			return wNewLine;
		}
		return replaceVariablesInLine(wNewLine, aVariableDelimiter);
	}

	/**
	 * 
	 */
	private void resetListOfPaches() {
		setListOfPaches(null);
	}

	/**
	 * 
	 */
	private void resetPatchedLines() {
		setPatchedLines(null);
	}

	/**
	 * @throws Exception
	 */
	public void saveResult() throws Exception {

		if (!isPatchesApplied()) {
			throw new Exception("no applied patches to save");
		}
		saveResult(getPatchedLines());
	}

	/**
	 * @param aPatchedLines
	 * @throws Exception
	 */
	private void saveResult(final List<String> aPatchedLines) throws Exception {

		if (pFileToSaveOriginal.exists()) {
			boolean wDeleted = pFileToSaveOriginal.delete();
			pLogger.logInfo(this, "saveResult", "DeletePreviousOriginal=[%s]", wDeleted);
		}
		pFileToBePatched.renameTo(pFileToSaveOriginal);
		pLogger.logInfo(this, "saveResult", "SavedOriginal=[%s]", pFileToSaveOriginal.getAbsolutePath());

		pFileToBePatched.writeAll(aPatchedLines);
		pLogger.logInfo(this, "saveResult", "  PatchedFile=[%s]", pFileToBePatched.getAbsolutePath());
	}

	/**
	 * Patch with prefix example:
	 * 
	 * <pre>
	 *    2 before >  <display-name>Interactive Web Server</display-name>
	 *    3  patch ><!-- X3 crypted exchange : patch start : add listeners -->
	 *    4  patch ><listener>
	 *    5  patch ><listener-class>com.isandlatech.x3.cryptedexchange.CWebAppListener</listener-class>
	 *    6  patch ></listener>
	 *    7  patch ><listener>
	 *    8  patch ><listener-class>com.isandlatech.x3.cryptedexchange.CSessionListener</listener-class>
	 *    9  patch ></listener>
	 *   10  patch ><!-- X3 crypted exchange : patch end -->
	 *   11  after >  <security-constraint>
	 *   12  after >          <web-resource-collection>
	 * </pre>
	 * 
	 * @param aWithPrefix
	 *            the flag to activate or not the prefixing of the patched lines
	 *            with "before","patch" or "after".
	 */
	public void setApplyWithPrefix(final boolean aWithPrefix) {
		pApplyWithPrefix = aWithPrefix;
	}

	/**
	 * 
	 */
	public void setApplyWithPrefixOff() {
		setApplyWithPrefix(false);
	}

	/**
	 * 
	 */
	public void setApplyWithPrefixOn() {
		setApplyWithPrefix(true);
	}

	/**
	 * @param aListOfPaches
	 */
	private void setListOfPaches(final List<CXFileTextPatch> aListOfPaches) {
		pListOfPaches = aListOfPaches;
	}

	/**
	 * @param aPatchedLines
	 */
	private void setPatchedLines(final List<String> aPatchedLines) {
		pPatchedLines = aPatchedLines;
	}

	/**
	 * @param aReplacementVariables
	 *            a properties instance containing pairs "Id+Value". These
	 *            values are used to replace the variables present in the lines
	 *            of the patches
	 */
	public void setReplacementVariables(final Properties aReplacementVariables) {

		if (aReplacementVariables != null && aReplacementVariables.size() > 0) {
			pReplacementVariables.clear();
			pReplacementVariables.putAll(aReplacementVariables);
		}
	}

	/**
	 * @param aLinesToBePatched
	 *            a set of line to be patched
	 * @param aPatch
	 *            a instance of patch
	 * @return true of no line of the text of the patch is found in the lines to
	 *         be patched
	 * @throws Exception
	 */
	private boolean testIfPatchable(final List<String> aLinesToBePatched, final CXFileTextPatch aPatch)
			throws Exception {

		try {
			int wIdxPatchTextLine = 0;
			for (String wPatchTextLine : aPatch.getTextLines()) {
				int wIdxLineToBePatched = 0;
				for (String wLineToBePatched : aLinesToBePatched) {

					if (wLineToBePatched != null && wLineToBePatched.contains(wPatchTextLine.trim())) {
						throw new Exception(String.format(
								"the line (%d)[%s] of the patch is already present in the line of the target (%d)[%s]",
								wIdxPatchTextLine, wPatchTextLine.trim(), wIdxLineToBePatched, wLineToBePatched));
					}
					wIdxLineToBePatched++;
				}
				wIdxPatchTextLine++;
			}
			return true;
		} catch (Exception e) {
			throw new Exception("One line of the patch is already present in the file to be patched", e);
		}
	}

	/**
	 * @param aFileToBePatched
	 * @throws IllegalArgumentException
	 *             if the passed File is not an existing file
	 */
	private CXFileText validFileToBePatched(final CXFileText aFileToBePatched) throws IllegalArgumentException {

		if (aFileToBePatched == null) {
			throw new IllegalArgumentException("Unable to instanciate a CXFilePatcher using a null CXFileText");
		}
		if (!aFileToBePatched.exists()) {
			throw new IllegalArgumentException(String.format(
					"Unable to instanciate a CXFilePatcher using a unexisting CXFileText [%s]",
					aFileToBePatched.getAbsolutePath()));
		}
		if (!aFileToBePatched.isFile()) {
			throw new IllegalArgumentException(String.format(
					"Unable to instanciate a CXFilePatcher using a non file CXFileText [%s]",
					aFileToBePatched.getAbsolutePath()));
		}
		return aFileToBePatched;
	}

	/**
	 * @param aSavedFileText
	 * @return
	 * @throws IllegalArgumentException
	 */
	private CXFileText validSavedFileText(final CXFileText aSavedFileText) throws IllegalArgumentException {
		if (aSavedFileText == null) {
			throw new IllegalArgumentException("Unable to save original file, the passed CXFileText is null ");
		}
		if (aSavedFileText.exists()) {
			if (!aSavedFileText.isFile()) {
				throw new IllegalArgumentException(String.format(
						"Unable to save original file, the passed CXFileText [%s] is'nt a file",
						aSavedFileText.getAbsolutePath()));
			}
		}
		return aSavedFileText;
	}

}
