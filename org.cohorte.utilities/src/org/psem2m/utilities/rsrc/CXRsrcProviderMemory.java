package org.psem2m.utilities.rsrc;

import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Map;

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
	public boolean isLocal() {
		return true;
	}

	@Override
	public CXRsrcText rsrcReadTxt(final String aContentId) throws Exception {
		String wContentId = aContentId;
		if (aContentId.contains("?")) {
			wContentId = aContentId.substring(0, wContentId.indexOf("?"));
		}
		if (pMapMemory.get(wContentId) != null) {
			return pMapMemory.get(wContentId);
		} else {
			throw new Exception(String.format(
					"not exists %s in memory provider", aContentId));
		}
	}

	protected void setMap(final Map<String, CXRsrcText> aMapMemory) {
		pMapMemory = aMapMemory;
	}

	@Override
	public String urlGetAddress() {
		return null;
	}

}