package org.psem2m.utilities.files;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.psem2m.utilities.CXDomUtils;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLoggerBase;
import org.w3c.dom.Element;

/**
 * <pre>
 * <patches>
 * 	<target>
 * 		<path>${X3WEB_TOOLSROOT}/WEBAPPS/WAWEBSERVER/X3/index.jsp</path>
 * 		<originalcopy>
 * 			<dir></dir>
 * 			<name>index.jsp.original</name>
 * 		</originalcopy>
 * 	</target>
 * 	<patch>
 * 	...
 * 	</patch>	
 * </patches>
 * </pre>
 * 
 * @author ogattaz
 * 
 */
public class CXFileTextPatches {

	private final IActivityLoggerBase pLogger;
	private final List<CXFileTextPatch> pPatches = new ArrayList<CXFileTextPatch>();
	private final Map<String, String> pReplacements;
	private final List<CXFileTextPatchTarget> pTargets = new ArrayList<CXFileTextPatchTarget>();

	/**
	 * @param aReplacementParams
	 *            a Map containing values of the parameters ${paramId} in the
	 *            paths of the targets
	 */
	public CXFileTextPatches(final Map<String, String> aReplacementParams) {
		this(aReplacementParams, null);
	}

	/**
	 * @param aReplacementParams
	 *            a Map containing values of the parameters ${paramId} in the
	 *            paths of the targets
	 * @param aLogger
	 */
	public CXFileTextPatches(final Map<String, String> aReplacementParams,
			final IActivityLoggerBase aLogger) {
		super();
		pReplacements = aReplacementParams;
		pLogger = (aLogger != null) ? aLogger : CActivityLoggerNull
				.getInstance();
		pLogger.logInfo(this, "<init>", "instanciated NbVariables=[%d]",
				(pReplacements != null) ? pReplacements.size() : -1);
	}

	/**
	 * @return the list of the patches defined in the file
	 */
	public List<CXFileTextPatch> getPatches() {
		return pPatches;
	}

	/**
	 * @return the list of the targets defined in the file
	 */
	public List<CXFileTextPatchTarget> getTargets() {
		return pTargets;
	}

	/**
	 * @param aDom
	 *            an XML document instance
	 * @return
	 * @throws Exception
	 */
	private int parse(final CXDomUtils aDom) throws Exception {

		pPatches.clear();
		pTargets.clear();

		if (!"patches".equals(aDom.getRootElmt().getNodeName())) {
			throw new Exception(
					"The root element of the xml document is not [patches]");
		}
		List<Element> wTargetElemts = aDom.getElementsByTagName("target");
		if (wTargetElemts.size() < 1) {
			throw new Exception(
					"There's no [target] element as child of the root element");
		}

		List<Element> wPatcheElemts = aDom.getElementsByTagName("patch");
		if (wPatcheElemts.size() < 1) {
			throw new Exception(
					"There's no [patch] element as child of the root element");
		}

		pTargets.addAll(CXFileTextPatchTarget.parse(wTargetElemts,
				pReplacements));

		pLogger.logInfo(this, "parse", "NbTargets=[%s]", pTargets.size());

		pPatches.addAll(CXFileTextPatch.parse(wPatcheElemts, pReplacements));

		pLogger.logInfo(this, "parse", "NbPatches=[%s]", pPatches.size());

		return pPatches.size();
	}

	/**
	 * @param aPatchFile
	 * @return
	 * @throws Exception
	 */
	public int parse(final CXFileText aPatchFile) throws Exception {
		return parse(new CXDomUtils(aPatchFile));
	}

	/**
	 * @param aPatchXmlSource
	 * @return
	 * @throws Exception
	 */
	public int parse(final String aPatchXmlSource) throws Exception {
		return parse(new CXDomUtils(aPatchXmlSource));
	}
}
