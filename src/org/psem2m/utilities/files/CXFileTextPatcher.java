package org.psem2m.utilities.files;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLoggerBase;

/**
 * @author ogattaz
 * 
 */
public class CXFileTextPatcher {

	private final CXFileText pFileToBePatched;
	private final CXFileText pFileToSaveOriginal;
	private List<CXFileTextPatch> pListOfPaches = null;
	private final IActivityLoggerBase pLogger;
	private List<String> pPatchedLines = null;
	private boolean pWithPrefix = false;

	/**
	 * @param aFileText
	 */
	public CXFileTextPatcher(final CXFileText aFileToBePatched,
			final CXFileText aFileToSaveOriginal)
			throws IllegalArgumentException {
		this(aFileToBePatched, aFileToSaveOriginal, null);
	}

	/**
	 * @param aFileText
	 * @param aLogger
	 */
	public CXFileTextPatcher(final CXFileText aFileToBePatched,
			final CXFileText aFileToSaveOriginal,
			final IActivityLoggerBase aLogger) throws IllegalArgumentException {
		super();

		pFileToBePatched = validFileToBePatched(aFileToBePatched);
		pFileToSaveOriginal = validSavedFileText(aFileToSaveOriginal);

		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
		pLogger.logInfo(this, "<init>",
				"instanciated FileToBePatched=[%s] FileToSaveOriginal=[%s]",
				pFileToBePatched.getName(), pFileToSaveOriginal.getName());
	}

	/**
	 * @param aTarget
	 * @throws IllegalArgumentException
	 */
	public CXFileTextPatcher(final CXFileTextPatchTarget aTarget)
			throws IllegalArgumentException {
		this(aTarget, Charset.defaultCharset().toString(), null);
	}

	/**
	 * @param aTarget
	 * @param aLogger
	 * @throws IllegalArgumentException
	 */
	public CXFileTextPatcher(final CXFileTextPatchTarget aTarget,
			final IActivityLoggerBase aLogger) throws IllegalArgumentException {
		this(aTarget, Charset.defaultCharset().toString(), aLogger);
	}

	/**
	 * @param aTarget
	 * @param aEncoding
	 * @param aLogger
	 * @throws IllegalArgumentException
	 */
	public CXFileTextPatcher(final CXFileTextPatchTarget aTarget,
			final String aEncoding, final IActivityLoggerBase aLogger)
			throws IllegalArgumentException {
		this(aTarget.getFileTextToBePatched(aEncoding), aTarget
				.getFileTextToSaveOriginal(aEncoding), aLogger);
	}

	/**
	 * @param aListLines
	 * @param aLine
	 * @param aPrefix
	 */
	private void addLine(final List<String> aListLines, final String aLine,
			final String aPrefix) {
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
	private List<String> applyOnePatch(final CXFileTextPatch aPatch,
			final List<String> aLinesToBePatched) throws Exception {

		pLogger.logInfo(this, "applyOnePatch",
				"LinesToBePatched size=[%d] Patch:%s",
				(aLinesToBePatched != null) ? aLinesToBePatched.size() : -1,
				aPatch);

		// test if the patch is not already in place
		testIfPatchable(aLinesToBePatched, aPatch);

		List<String> wLinesPatched = new ArrayList<String>();

		// find the location
		int wPatchStartLineIdx = findLocation(aLinesToBePatched, aPatch);

		if (wPatchStartLineIdx < 0) {
			throw new Exception(
					String.format(
							"Unable to find the location lines of the patch [%s]",
							CXStringUtils.stringListToString(aPatch
									.getLocationLines())));
		}

		pLogger.logInfo(this, "applyOnePatch", "PatchStartLineIdx=[%d]",
				wPatchStartLineIdx);

		if (aPatch.isAfter()) {
			wPatchStartLineIdx += aPatch.getNbLocationLines();
		}

		// copies lines before
		for (int wIdx = 0; wIdx < wPatchStartLineIdx; wIdx++) {
			addLine(wLinesPatched, aLinesToBePatched.get(wIdx),
					calcPrefix(wLinesPatched, "before"));
		}

		// copies the lines of the patch
		for (String wLine : aPatch.getTextLines()) {
			addLine(wLinesPatched, wLine, calcPrefix(wLinesPatched, "patch"));
		}

		// copies lines after
		for (int wIdx = wPatchStartLineIdx; wIdx < aLinesToBePatched.size(); wIdx++) {
			addLine(wLinesPatched, aLinesToBePatched.get(wIdx),
					calcPrefix(wLinesPatched, "after"));
		}

		return wLinesPatched;
	}

	/**
	 * @param aPatchDom
	 * @param aSavedFileText
	 * @throws Exception
	 */
	public List<String> applyPatches(final List<CXFileTextPatch> aListOfPaches)
			throws Exception {
		pLogger.logInfo(this, "applyPatches", "ListOfPaches size=[%d]",
				(aListOfPaches != null) ? aListOfPaches.size() : -1);

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
	public List<String> applyPatchesAndSave(
			final List<CXFileTextPatch> aListOfPaches) throws Exception {

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
	private String calcPrefix(final List<String> aListLines,
			final String aPrefix) {
		return (pWithPrefix) ? String.format("%4d %6s >", aListLines.size(),
				aPrefix) : null;
	}

	/**
	 * @param aLinesToBePatched
	 * @param aLocationLines
	 * @return
	 */
	private int findLocation(final List<String> aLinesToBePatched,
			final CXFileTextPatch aPatch) {

		pLogger.logInfo(this, "findLocation", "Nb Lines to find=[%d]", aPatch
				.getLocationLines().size());

		int wFoundStartIdx = -1;
		int wNbFoundLines = 0;
		int wLineToBePatchedIdx = 0;

		String wLocationLine = null;

		for (String wLineToBePatched : aLinesToBePatched) {

			if (wLocationLine == null) {
				wLocationLine = aPatch.getLoweredTrimedLocationLine(wNbFoundLines);
			}

			if (wLineToBePatched != null
					&& wLineToBePatched.toLowerCase().contains(wLocationLine)) {
				wNbFoundLines++;
				pLogger.logInfo(this, "findLocation", "NbFoundLines=[%d][%s]",
						wNbFoundLines, wLocationLine);
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
	public boolean isPatchesApplied() {
		return hasListOfPatches() && hasPatchedLines();
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

		pFileToBePatched.copyTo(pFileToSaveOriginal, true);
		pLogger.logInfo(this, "saveResult", "SavedOriginal=[%s]",
				pFileToSaveOriginal.getAbsolutePath());

		pFileToBePatched.writeAll(aPatchedLines);
		pLogger.logInfo(this, "saveResult", "  PatchedFile=[%s]",
				pFileToBePatched.getAbsolutePath());
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
	 * @param aWithPrefix
	 */
	public void setWithPrefix(final boolean aWithPrefix) {
		pWithPrefix = aWithPrefix;
	}

	/**
	 * 
	 */
	public void setWithPrefixOff() {
		setWithPrefix(false);
	}

	/**
	 * 
	 */
	public void setWithPrefixOn() {
		setWithPrefix(true);
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
	private boolean testIfPatchable(final List<String> aLinesToBePatched,
			final CXFileTextPatch aPatch) throws Exception {

		try {
			int wIdxPatchTextLine = 0;
			for (String wPatchTextLine : aPatch.getTextLines()) {
				int wIdxLineToBePatched = 0;
				for (String wLineToBePatched : aLinesToBePatched) {

					if (wLineToBePatched != null
							&& wLineToBePatched.contains(wPatchTextLine.trim())) {
						throw new Exception(
								String.format(
										"the line (%d)[%s] of the patch is already present in the line of the target (%d)[%s]",
										wIdxPatchTextLine,
										wPatchTextLine.trim(),
										wIdxLineToBePatched, wLineToBePatched));
					}
					wIdxLineToBePatched++;
				}
				wIdxPatchTextLine++;
			}
			return true;
		} catch (Exception e) {
			throw new Exception(
					"One line of the patch is already present in the file to be patched",
					e);
		}
	}

	/**
	 * @param aFileToBePatched
	 * @throws IllegalArgumentException
	 *             if the passed File is not an existing file
	 */
	private CXFileText validFileToBePatched(final CXFileText aFileToBePatched)
			throws IllegalArgumentException {

		if (aFileToBePatched == null) {
			throw new IllegalArgumentException(
					"Unable to instanciate a CXFilePatcher using a null CXFileText");
		}
		if (!aFileToBePatched.exists()) {
			throw new IllegalArgumentException(
					String.format(
							"Unable to instanciate a CXFilePatcher using a unexisting CXFileText [%s]",
							aFileToBePatched.getAbsolutePath()));
		}
		if (!aFileToBePatched.isFile()) {
			throw new IllegalArgumentException(
					String.format(
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
	private CXFileText validSavedFileText(final CXFileText aSavedFileText)
			throws IllegalArgumentException {
		if (aSavedFileText == null) {
			throw new IllegalArgumentException(
					"Unable to save original file, the passed CXFileText is null ");
		}
		if (aSavedFileText.exists()) {
			if (!aSavedFileText.isFile()) {
				throw new IllegalArgumentException(
						String.format(
								"Unable to save original file, the passed CXFileText [%s] is'nt a file",
								aSavedFileText.getAbsolutePath()));
			}
		}
		return aSavedFileText;
	}

}
