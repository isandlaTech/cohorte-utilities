package org.cohorte.utilities.sql.exec;

import java.util.Map;
import java.util.regex.Pattern;

import org.psem2m.utilities.CXStringUtils;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * Build a SQL Request from a model
 *
 * Each line of the model is a part.
 *
 * A part is optional if it contains one or more variables (eg. ${id} )
 *
 * The builder tries to replace the variables with a replacement map. If at the
 * end of the replacement treatment some variable stay in place, the part is
 * invalid and it isn't add in the request
 *
 * eg: a Model with n parts in n lines
 *
 * <pre>
 * --
 * -- base "word" table "city"
 * --
 * SELECT `ID`,`Name`,`CountryCode` ,`District`,`Population` FROM world.city
 * WHERE `Name` LIKE '${like}'
 * ORDER BY `${order}`
 * ${descending}
 * LIMIT ${limit}
 * ;
 * </pre>
 *
 * Using the replacement {"order=CountryCode", "descending=DESC", "limit=50"},
 * we got :
 *
 * <pre>
 * SELECT `ID`,`Name`,`CountryCode` ,`District`,`Population` FROM world.city
 * ORDER BY `CountryCode`
 * DESC
 * LIMIT 50
 * ;
 * </pre>
 *
 *
 * @author ogattaz
 *
 */
public class CDBRequestBuilder {

	public static final int ALL_MANDATORY = 1;
	public static final int ALL_OPTIONAL = 0;

	private final IActivityLogger pLogger;

	private final String pRequestModel;

	// detect variable using the format : "${xxx}"
	private final Pattern sPatternHasVarible = Pattern
			.compile("\\$\\{(.+?)\\}");

	/**
	 * @param aRequestModel
	 */
	public CDBRequestBuilder(final IActivityLogger aLogger,
			final String aRequestModel) {

		super();
		pLogger = aLogger;
		pRequestModel = aRequestModel;
	}

	/**
	 * @return the request
	 * @throws Exception
	 */
	public String getRequest() {
		// returns the model ! (no replacements given )
		return pRequestModel;
	}

	/**
	 *
	 * @param aReplacements
	 * @return
	 * @throws Exception
	 */
	public String getRequest(final Map<String, String> aReplacements)
			throws Exception {

		return getRequest(aReplacements, ALL_OPTIONAL);
	}

	/**
	 * @param aReplacements
	 * @param aReplacementOptions
	 * @return
	 * @throws Exception
	 */
	public String getRequest(final Map<String, String> aReplacements,
			final int aReplacementOptions) throws Exception {
		final StringBuilder wSB = new StringBuilder();

		final String[] wParts = pRequestModel.split("\\n");

		for (String wPart : wParts) {

			if (wPart != null && !wPart.isEmpty()
					&& !wPart.trim().startsWith("--")) {

				// the part is valid if it doesn't contain a variable
				boolean wPartValid = !sPatternHasVarible.matcher(wPart).find();
				if (!wPartValid) {
					wPart = CXStringUtils
							.replaceVariables(wPart, aReplacements);
					// recalculate the validity of the part
					wPartValid = !sPatternHasVarible.matcher(wPart).find();

					if (!wPartValid && pLogger != null) {
						pLogger.logDebug(this, "getRequest",
								"part not valid : [%s]", wPart);
					}
				}
				// if the part is valid !
				if (wPartValid) {
					wSB.append('\n').append(wPart);
				}
				// else if the part is not valid
				else if ((aReplacementOptions & ALL_MANDATORY) > 0) {
					throw new Exception(String.format(
							"Unable to replace variable(s) in the part [%s] ",
							wPart));
				}
			}
		}
		return wSB.toString();
	}
}
