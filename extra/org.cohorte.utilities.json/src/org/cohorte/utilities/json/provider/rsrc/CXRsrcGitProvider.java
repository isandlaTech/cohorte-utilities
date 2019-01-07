package org.cohorte.utilities.json.provider.rsrc;

import java.nio.charset.Charset;

import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.rsrc.CXHttpAuthentication;
import org.psem2m.utilities.rsrc.CXHttpProxy;
import org.psem2m.utilities.rsrc.CXRsrcProviderHttp;
import org.psem2m.utilities.rsrc.CXRsrcText;
import org.psem2m.utilities.rsrc.CXRsrcTextReadInfo;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;
import org.psem2m.utilities.rsrc.CXRsrcUrlAddress;

/**
 * provider with cache for file and git credential. it retrieve throw http the
 * file that are store in git
 *
 * @author apisu
 *
 */
public class CXRsrcGitProvider extends CXRsrcProviderHttp {

	/**
	 * @param aAddress
	 * @param aProxy
	 * @param aAuthentication
	 * @param aDefCharset
	 */
	public CXRsrcGitProvider(final CXRsrcUrlAddress aAddress,
			final CXHttpProxy aProxy,
			final CXHttpAuthentication aAuthentication,
			final Charset aDefCharset) {
		super(aAddress, aProxy, aAuthentication, aDefCharset);

	}

	/**
	 * @param aHostName
	 * @param aSecured
	 * @param aDefCharset
	 */
	public CXRsrcGitProvider(final String aHostName, final boolean aSecured,
			final Charset aDefCharset) {
		this(aHostName, 0, aSecured, aDefCharset);
	}

	/**
	 * @param aHostName
	 * @param aDefCharset
	 */
	public CXRsrcGitProvider(final String aHostName, final Charset aDefCharset) {
		super(aHostName, aDefCharset);
	}

	/**
	 * @param aHostName
	 * @param aPort
	 * @param aSecured
	 * @param aDefCharset
	 */
	public CXRsrcGitProvider(final String aHostName, final int aPort,
			final boolean aSecured, final Charset aDefCharset) {
		super(aHostName, aPort, aSecured, aDefCharset);

	}

	/**
	 * @param aHostName
	 * @param aPort
	 * @param aDefCharset
	 */
	public CXRsrcGitProvider(final String aHostName, final int aPort,
			final Charset aDefCharset) {
		super(aHostName, aPort, aDefCharset);
	}

	@Override
	protected boolean existsFulPath(final CXRsrcUriPath aPath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	/**
	 * we receive the generator object and need to generate an new object
	 * variable resolution
	 */
	@Override
	public CXRsrcText rsrcReadTxt(final String aPath) throws Exception {
		CXRsrcText wText = super.rsrcReadTxt(aPath);
		JSONArray wResult = new JSONArray();

		if (wText != null && wText.getContent() != null) {
			String[] wLines = wText.getContent().split("\n");
			for (String aLine : wLines) {
				wResult.put(aLine);
			}

		}
		return new CXRsrcText(new CXRsrcUriPath(""),
				CXRsrcTextReadInfo.newInstanceFromString(wResult.toString()));
	}

	@Override
	public String urlGetAddress() {
		// TODO Auto-generated method stub
		return null;
	}

}
