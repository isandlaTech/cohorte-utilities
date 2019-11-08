package org.cohorte.utilities.json.provider.rsrc;

import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcProviderHttp;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;

/**
 * provider that match with $template tag. this provide transforme the content
 * of the tag $template: {....} to a new content without reading anything. it's
 * not a include file/memory or http provide.
 *
 * @author apisu
 *
 */
public class CXRsrcGitLabProvider extends CXRsrcProviderHttp {

	private final IActivityLogger pActivityLogger;

	public CXRsrcGitLabProvider(final String aHostName, final int aPort, final IActivityLogger aLogger) {
		super(aHostName, aPort, Charset.defaultCharset());
		pActivityLogger = aLogger;
	}

	@Override
	public CXRsrcProviderHttp clone() {
		CXRsrcGitLabProvider wRsrc = new CXRsrcGitLabProvider(getAddress().getAddress(), getAddress().getTcpPort(),
				pActivityLogger);
		return wRsrc;
	}

	@Override
	protected boolean existsFulPath(final CXRsrcUriPath aPath) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getDirAbsPathDirectory(final CXRsrcUriPath aPath) {

		return aPath.getFullPath();
	}

	@Override
	protected List<String> getListPathDirectory(final CXRsrcUriPath aPath, final Pattern aPattern) {
		return null;
	}

	@Override
	public boolean isLocal() {
		return true;
	}

}
