package org.psem2m.utilities.scripting;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * @author ogattaz
 * 
 */
public class CXJsRunnerMap extends CXJsObjectBase implements Map<String, CXJsRunner> {

	private final Map<String, CXJsRunner> pMap = new Hashtable<>();

	/**
	 * @param aBundleLogger
	 * @param aActivityLogger
	 * @param aConfig
	 * @throws Exception
	 */
	public CXJsRunnerMap() {
		super();
	}

	@Override
	public void clear() {
		pMap.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return pMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return pMap.containsValue(value);
	}

	public void destroy() {
		pMap.clear();
	}

	@Override
	public Set<java.util.Map.Entry<String, CXJsRunner>> entrySet() {
		return pMap.entrySet();
	}

	@Override
	public CXJsRunner get(Object key) {
		return pMap.get(key);
	}

	@Override
	public boolean isEmpty() {
		return pMap.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return pMap.keySet();
	}

	@Override
	public CXJsRunner put(String key, CXJsRunner value) {
		return pMap.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends CXJsRunner> m) {
		pMap.putAll(m);
	}

	@Override
	public CXJsRunner remove(Object key) {
		return pMap.remove(key);
	}

	@Override
	public int size() {
		return pMap.size();
	}

	@Override
	public Collection<CXJsRunner> values() {
		return pMap.values();
	}

}
