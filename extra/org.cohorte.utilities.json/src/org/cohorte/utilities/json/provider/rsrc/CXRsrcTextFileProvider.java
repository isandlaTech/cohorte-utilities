package org.cohorte.utilities.json.provider.rsrc;

import java.nio.charset.Charset;

import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcProviderFile;
import org.psem2m.utilities.rsrc.CXRsrcText;
import org.psem2m.utilities.rsrc.CXRsrcTextReadInfo;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;

/**
 * provide that allow to include a standard file as a JSON array while each line
 * of the file is a element in the array
 *
 * @author apisu
 *
 */
public class CXRsrcTextFileProvider extends CXRsrcProviderFile {

	private final IActivityLogger pActivityLogger;

	public CXRsrcTextFileProvider(final CXFileDir aDefaultPath,
			final IActivityLogger aLogger) throws Exception {
		super(aDefaultPath, Charset.defaultCharset());
		pActivityLogger = aLogger;
	}

	public CXRsrcTextFileProvider(final String aDefaultPath,
			final IActivityLogger aLogger) throws Exception {
		super(aDefaultPath, Charset.defaultCharset());
		pActivityLogger = aLogger;
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
	public CXRsrcText rsrcReadTxt(final String aPath, final String aQueryPath) throws Exception {
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
