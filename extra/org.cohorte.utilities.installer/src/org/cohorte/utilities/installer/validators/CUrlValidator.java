package org.cohorte.utilities.installer.validators;

import static org.cohorte.utilities.installer.CInstallerTools.getServiceLogger;

import java.net.URL;
import java.util.regex.Pattern;

import org.psem2m.utilities.logging.IActivityLogger;

import com.izforge.izpack.panels.userinput.processorclient.ProcessingClient;
import com.izforge.izpack.panels.userinput.validator.Validator;

/**
 * MOD_OG_20160729
 *
 * @author ogattaz
 *
 *         TODO: use Apache commons UrlValidator class
 *         http://commons.apache.org/
 *         proper/commons-validator/apidocs/org/apache/
 *         commons/validator/routines/UrlValidator.html
 *
 */
public class CUrlValidator implements Validator {

	/**
	 * @see http
	 *      ://www.mkyong.com/regular-expressions/domain-name-regular-expression
	 *      -example/
	 *
	 *      <pre>
	 * ^			#Start of the line
	 *  (			    #Start of group #1
	 * 	  (?! -)		    	#Can't start with a hyphen
	 * 	  [A-Za-z0-9-]{1,63}	#Domain name is [A-Za-z0-9-], between 1 and 63 long
	 * 	  (?<!-)				#Can't end with hyphen
	 * 	  \\.		   			#Follow by a dot "."
	 *  )+			    #End of group #1, this group must appear at least 1 time, but allowed multiple times for subdomain
	 *  [A-Za-z]{2,6}	#TLD is [A-Za-z], between 2 and 6 long
	 * $			#end of the line
	 * </pre>
	 */
	private static final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";

	private static Pattern pDomainNameOnly;

	private final IActivityLogger pLogger;

	/**
	 *
	 */
	public CUrlValidator() {
		pLogger = getServiceLogger();

		pDomainNameOnly = Pattern.compile(DOMAIN_NAME_PATTERN);

		pLogger.logInfo(this, "<init>", "DomainNameOnly Regex [%s]",
				pDomainNameOnly);
	}

	/**
	 * @return
	 */
	private boolean checkUrl(final String aUri) {

		pLogger.logInfo(this, "checkUrl", "Checking URL [%s] format", aUri);

		try {
			URL wURL = new URL(aUri);

			// Gets the protocol name of this URL.
			String wProtocol = wURL.getProtocol();

			// Gets the host name of this URL, if applicable. The format of the
			// host conforms to RFC 2732, i.e. for a literal IPv6 address, this
			// method will return the IPv6 address enclosed in square brackets
			// ('[' and ']').
			String wHostName = wURL.getHost();

			// Gets the port number of this URL.
			int wPort = wURL.getPort();

			pLogger.logInfo(this, "checkUrl",
					"URL Host=[%s] Port=[%s] Protocol=[%s]", wHostName, wPort,
					wProtocol);

			if (!"http".equals(wProtocol) && !"https".equals(wProtocol)) {
				pLogger.logWarn(
						this,
						"checkUrl",
						"The provided URL [%s] isn't valid! the protocol [%s] isn't 'http' or 'https'. Validation returns false",
						aUri, wProtocol);
				return false;
			}

			if (!isValidDomainName(wHostName)) {
				pLogger.logWarn(
						this,
						"checkUrl",
						"The provided URL [%s] isn't valid! the hostname [%s] isn't valid. Validation returns false",
						aUri, wHostName);
				return false;
			}
			/**
			 * http://www.iana.org/assignments/service-names-port-numbers/
			 * service-names-port-numbers.xhtml
			 *
			 * [RFC6335]
			 *
			 * Ports (0-1023), User Ports (1024-49151), and the Dynamic and/or
			 * Private Ports (49152-65535); the difference uses of these ranges
			 * is described in
			 */
			if (wPort < 1 || wPort > 49151) {
				pLogger.logWarn(
						this,
						"checkUrl",
						"The provided URL [%s] isn't valid! the port [%s] isn't in the ranges (0-1023) and (1024-49151). Validation returns false",
						aUri, wPort);
				return false;
			}

			return true;
		} catch (Exception e) {
			pLogger.logWarn(
					this,
					"checkUri",
					"The provided Uri [%s] isn't valid! Validation returns false",
					aUri);
			return false;
		}

	}

	/**
	 * @see http
	 *      ://www.mkyong.com/regular-expressions/domain-name-regular-expression
	 *      -example/
	 *
	 * @param domainName
	 * @return
	 */
	public boolean isValidDomainName(final String domainName) {
		return pDomainNameOnly.matcher(domainName).find();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.izforge.izpack.panels.userinput.validator.Validator#validate(com.
	 * izforge.izpack.panels.userinput.processorclient.ProcessingClient)
	 */
	public boolean validate(final ProcessingClient client) {

		String value1 = client.getFieldContents(0);

		if ((value1 == null) || (value1.length() == 0)) {
			pLogger.logWarn(this, "validate",
					"No provided URL ! Validation returns false");
			return false;
		} else {
			pLogger.logInfo(this, "validate", "Provided URL : %s.", value1);
			return checkUrl(value1);
		}
	}
}
