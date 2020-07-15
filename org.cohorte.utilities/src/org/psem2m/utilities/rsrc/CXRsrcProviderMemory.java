package org.psem2m.utilities.rsrc;

import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CXRsrcProviderMemory extends CXRsrcProvider {

	private Map<String, CXRsrcText> pMapMemory;

	public CXRsrcProviderMemory() {
		super(Charset.defaultCharset());
		pMapMemory = new Hashtable<>();
	}

	public void add(final String aKey, final CXRsrcText aContent) {
		pMapMemory.put(aKey, aContent);
	}

	@Override
	public CXRsrcProvider clone() {
		CXRsrcProviderMemory wRsrc = new CXRsrcProviderMemory();
		wRsrc.setMap(pMapMemory);
		return wRsrc;
	}

	@Override
	protected boolean existsFulPath(final CXRsrcUriPath arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getDirAbsPathDirectory(CXRsrcUriPath aPath) {

		return aPath.getFullPath();
	}

	@Override
	protected List<String> getListPathDirectory(CXRsrcUriPath aPath, final Pattern aPattern) {
		return null;
	}

	@Override
	public boolean isLocal() {
		return true;
	}

	@Override
	public CXRsrcText rsrcReadTxt(final String aContentId) throws Exception {
		return rsrcReadTxt(aContentId, null);

	}

	@Override
	public CXRsrcText rsrcReadTxt(final String aContentId, Map<String, String> aQueryParam) throws Exception {
		String wContentId = aContentId;
		if (aContentId.contains("?")) {
			wContentId = aContentId.substring(0, wContentId.indexOf("?"));
		}
		if (pMapMemory.get(wContentId) != null) {
			return pMapMemory.get(wContentId);
		} else {
			throw new Exception(String.format("not exists %s in memory provider", aContentId));
		}
	}

	@Override
	public CXListRsrcText rsrcReadTxts(final String aContentId) throws Exception {
		return rsrcReadTxts(aContentId, null);
	}

	@Override
	public CXListRsrcText rsrcReadTxts(final String aContentId, Map<String, String> aQueryParam) throws Exception {
		CXListRsrcText wList = new CXListRsrcText();
		String wContentId = aContentId;
		if (aContentId.contains("?")) {
			wContentId = aContentId.substring(0, wContentId.indexOf("?"));
		}
		if (pMapMemory.get(wContentId) != null) {
			wList.add(pMapMemory.get(wContentId));
		} else {
			throw new Exception(String.format("not exists %s in memory provider", aContentId));
		}
		return wList;
	}

	protected void setMap(final Map<String, CXRsrcText> aMapMemory) {
		pMapMemory = aMapMemory;
	}

	@Override
	public String urlGetAddress() {
		return null;
	}

}