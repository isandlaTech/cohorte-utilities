package org.cohorte.utilities.tests;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ogattaz
 * 
 */
public class CAppOptionsBase extends CAppCommandLineParser implements
		IAppOptions {

	public final static String OPT_DEBUG = "debug";
	public final static String OPT_USAGE = "usage";
	public final static String OPT_VERBOSE = "verbose";
	public final static String OPT_KIND = "kind";

	List<String> pOoptionHelpStrings = new ArrayList<String>();

	private Boolean pIsDebugOn = null;

	private Boolean pIsVerboseOn = null;

	/**
	 * @param aApplicationName
	 * @throws Exception
	 */
	public CAppOptionsBase(String aApplicationName) throws Exception {
		super();
		defineAppName(aApplicationName);

		defineOptionHelp(addStringOption('k', OPT_KIND),
				"A kind of info for exemple (a generic option)");

		defineOptionHelp(addBooleanOption('d', OPT_DEBUG),
				"Print debug information");

		defineOptionHelp(addBooleanOption('v', OPT_VERBOSE),
				"Print extra information");

		defineOptionHelp(addBooleanOption('?', OPT_USAGE), "usage");
	}

	/**
	 * @param aSB
	 * @return
	 */
	protected StringBuilder addUsageInSB(StringBuilder aSB, String aSeparator) {

		for (String wHelp : pOoptionHelpStrings) {
			if (aSB.length() > 0)
				aSB.append(aSeparator);
			aSB.append(wHelp);
		}
		return aSB;
	}

	/**
	 * @param aAppName
	 */
	protected void defineAppName(String aAppName) {
		pOoptionHelpStrings.add(aAppName);
	}

	/**
	 * @param aOption
	 * @param helpString
	 * @return
	 */
	protected void defineOptionHelp(final Option<?> aOption, String helpString) {
		pOoptionHelpStrings.add(String.format(" -%s/--%s [%s] : %s",
				aOption.shortForm(2), aOption.longForm(10),
				aOption.getMandatoryLib(9), helpString));
	}

	/**
	 * @return
	 * @throws OptionException
	 */
	public String getKindValue() throws OptionException {
		return getStringOptionValue(OPT_KIND);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.m1i.tool.commons.IAppOptions#getUsage()
	 */
	@Override
	public String getUsage() {
		return addUsageInSB(new StringBuilder(), "\n").toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.m1i.tool.commons.IAppOptions#isDebugOn()
	 */
	@Override
	public boolean isDebugOn() throws Exception {
		if (pIsDebugOn == null)
			pIsDebugOn = getBooleanOptionValue(CAppOptionsBase.OPT_DEBUG, false);
		return pIsDebugOn.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.m1i.tool.commons.IAppOptions#isVerboseOn()
	 */
	@Override
	public boolean isVerboseOn() throws Exception {
		if (pIsVerboseOn == null)
			pIsVerboseOn = getBooleanOptionValue(CAppOptionsBase.OPT_VERBOSE,
					false);
		return pIsVerboseOn.booleanValue();
	}

}
