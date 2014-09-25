package org.psem2m.utilities.files;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.psem2m.utilities.CXDomUtils;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLoggerBase;
import org.w3c.dom.Element;

/**
 * target-app and versions are optionnal
 * 
 * <pre>
 * <patches>
 * 	<options>
 * 		<target-app version.mini="131" version.maxi="" /> 
 * 	</options>
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

	public static final String ATTR_VERS_MAXI = "version.maxi";
	public static final String ATTR_VERS_MINI = "version.mini";
	public static final String NODE_OPTIONS = "options";
	public static final String NODE_PATCH = "patch";
	public static final String NODE_PATCHES = "patches";
	public static final String NODE_TARGET = "target";
	public static final String NODE_TARGET_APP = "target-app";
	
	private static final Float VERSION_MAXI = (float) 999.9;
	private static final Float VERSION_MINI = (float) 0.0;

	private final IActivityLoggerBase pLogger;
	private final List<CXFileTextPatch> pPatches = new ArrayList<CXFileTextPatch>();
	private final Map<String, String> pReplacements;
	private Float pTargetAppVersionMaxi = VERSION_MAXI;
	private Float pTargetAppVersionMini =  VERSION_MINI;
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
	 * @param aCuurentVersion
	 * @return
	 */
	public boolean isVersionApplicable(final Float aCurentVersion) {
		
		boolean wVersionMiniLessOrEqualThanCurrent = pTargetAppVersionMini.compareTo(aCurentVersion) <= 0;

		boolean wVersionMaxiEqualOrGreaterThanCurrent = pTargetAppVersionMaxi.compareTo(aCurentVersion) >= 0;
		
		pLogger.logInfo(this, "isVersionApplicable", "versionMiniLessOrEqualThanCurrent   =[%b] [%3.1f]<=[%3.1f]",wVersionMiniLessOrEqualThanCurrent,pTargetAppVersionMini,aCurentVersion);
		pLogger.logInfo(this, "isVersionApplicable", "versionMaxiEqualOrGreaterThanCurrent=[%b] [%3.1f]>=[%3.1f]",wVersionMaxiEqualOrGreaterThanCurrent,pTargetAppVersionMaxi,aCurentVersion);

		boolean wIsApplicable = wVersionMiniLessOrEqualThanCurrent && wVersionMaxiEqualOrGreaterThanCurrent;
		pLogger.logInfo(this, "isVersionApplicable", "isApplicable=[%b] [mini %3.1f]<=[%3.1f]<=[%3.1f maxi]",wIsApplicable,pTargetAppVersionMini,aCurentVersion,pTargetAppVersionMaxi);

		return wIsApplicable;
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

		if (!NODE_PATCHES.equals(aDom.getRootElmt().getNodeName())) {
			throw new Exception(
					"The root element of the xml document is not [patches]");
		}
		List<Element> wTargetElemts = aDom.getElementsByTagName(NODE_TARGET);
		if (wTargetElemts.size() < 1) {
			throw new Exception(
					"There's no [target] element as child of the root element");
		}

		List<Element> wPatcheElemts = aDom.getElementsByTagName(NODE_PATCH);
		if (wPatcheElemts.size() < 1) {
			throw new Exception(
					"There's no [patch] element as child of the root element");
		}

		pTargets.addAll(CXFileTextPatchTarget.parse(wTargetElemts,
				pReplacements));

		pLogger.logInfo(this, "parse", "NbTargets=[%s]", pTargets.size());

		pPatches.addAll(CXFileTextPatch.parse(wPatcheElemts, pReplacements));

		pLogger.logInfo(this, "parse", "NbPatches=[%s]", pPatches.size());

		Element wOptionsElmt = CXDomUtils.getFirstChildElmtByTag(
				aDom.getRootElmt(), NODE_OPTIONS);
		if (wOptionsElmt != null) {
			List<Element> wOptionsList = CXDomUtils.getElements(wOptionsElmt);
			for (Element wOptionElmt : wOptionsList) {
				
				pLogger.logInfo(this, "parse", "option=[%s]", wOptionElmt.getNodeName(),CXDomUtils.dumpAttributes(wOptionElmt));

				if (NODE_TARGET_APP
						.equalsIgnoreCase(wOptionElmt.getNodeName())) {


					pTargetAppVersionMini = parseFloat(
							wOptionElmt.getAttribute(ATTR_VERS_MINI),
							VERSION_MINI);
					pTargetAppVersionMaxi = parseFloat(
							wOptionElmt.getAttribute(ATTR_VERS_MAXI),
							VERSION_MAXI);

					pLogger.logInfo(this, "parse", "TargetApp VersionMini=[%3.1f] VersionMaxi=[%3.1f]]",pTargetAppVersionMini,pTargetAppVersionMaxi);
				}
			}
		}
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

	/**
	 * @param aFloatValue
	 * @param aDefault
	 * @return
	 */
	private Float parseFloat(final String aFloatValue, final Float aDefault) {
		if (aFloatValue == null || aFloatValue.isEmpty()) {
			return aDefault;
		}
		try {
			return Float.parseFloat(aFloatValue);
		} catch (Exception e) {
			return aDefault;
		}

	}
}
