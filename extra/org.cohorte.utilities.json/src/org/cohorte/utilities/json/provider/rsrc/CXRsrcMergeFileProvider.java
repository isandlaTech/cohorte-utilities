package org.cohorte.utilities.json.provider.rsrc;

import java.nio.charset.Charset;
import java.util.Map;

import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.json.JSONObject;
import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXListRsrcText;
import org.psem2m.utilities.rsrc.CXRsrcProviderFile;
import org.psem2m.utilities.rsrc.CXRsrcText;
import org.psem2m.utilities.rsrc.CXRsrcTextReadInfo;
import org.psem2m.utilities.rsrc.CXRsrcUriPath;
import org.psem2m.utilities.rsrc.IRsrcNotifierHandler;

public class CXRsrcMergeFileProvider  extends CXRsrcProviderFile {

	/**
	 * @param aDefaultPath
	 * @param aDefCharset
	 * @throws Exception
	 */
	public CXRsrcMergeFileProvider(final CXFileDir aDefaultPath, final Charset aDefCharset) throws Exception {
		super(aDefaultPath, aDefCharset);
	}

	/**
	 * @param aProv
	 */
	protected CXRsrcMergeFileProvider(final CXRsrcProviderFile aProv) {
		super(aProv);

	}

	/**
	 * @param aDefaultPath
	 * @param aDefCharset
	 * @throws Exception
	 */
	public CXRsrcMergeFileProvider(final String aDefaultPath, final Charset aDefCharset) throws Exception {
		super(aDefaultPath, aDefCharset);
	}

	public CXRsrcMergeFileProvider(final String aDefaultPath, final Charset aDefCharset,
			final IRsrcNotifierHandler aNotifierHandler, final IActivityLogger aLogger) throws Exception {
		super(aDefaultPath,aDefCharset,aNotifierHandler,aLogger);
	}

	public static  JSONObject merge(JSONObject aJson1,JSONObject aJson2) {
		for(final String wKey:aJson2.keySet()) {
			if( !aJson1.keySet().contains(wKey) ) {
				// just add it
				aJson1.put(wKey, aJson2.get(wKey));
			}else {
				// need to merge content
				if( aJson1.optJSONObject(wKey) != null && aJson2.optJSONObject(wKey)!= null ) {
					aJson1.put(wKey, merge( aJson1.optJSONObject(wKey),aJson2.optJSONObject(wKey)));
				}else if( aJson1.optJSONArray(wKey) != null && aJson2.optJSONArray(wKey)!= null ) {
					for(int i=0;i< aJson2.optJSONArray(wKey).length();i++) {
						aJson1.optJSONArray(wKey).put(aJson2.optJSONArray(wKey).get(i));
					}
				}else {
					aJson1.put(wKey,aJson2.opt(wKey));
				}
			}
		}
		return aJson1;
	}

	@Override
	public CXListRsrcText rsrcReadTxts(final String aRsrcPath, Map<String, String> aFullPath) throws Exception {
		final CXListRsrcText wListTextRes = new CXListRsrcText();
		JSONObject wFileJSON = new JSONObject();
		for(final String wFilePath:aRsrcPath.split(";") ) {
			final CXListRsrcText wTexts =  rsrcReadTxts(wFilePath, aFullPath, 0);
			for(final CXRsrcText wText :wTexts) {
				// merge all JSON file to a single json
				final String wStr = wText.getContent();
				final JSONObject wJson = new JSONObject(wStr);
				wFileJSON = merge(wFileJSON,wJson);
			}
		}


		final CXRsrcText wRes =  new CXRsrcText(new CXRsrcUriPath(""),
				CXRsrcTextReadInfo.newInstanceFromString(wFileJSON.toString()));
		wListTextRes.add(wRes);
		return wListTextRes;
	}

}
