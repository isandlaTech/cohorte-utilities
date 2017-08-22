package org.psem2m.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class CXQueryString {

	/**
	 * @param aQueryString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, List<String>> splitQuery(final String aQueryString)
			throws UnsupportedEncodingException {
		return splitQuery(aQueryString, "&", "=");

	}

	/**
	 *
	 * @param aQueryString
	 * @param sepKey
	 *            : string that seperate the key value couple e.g & for a
	 *            queryString
	 * @param sepVal
	 *            : String that seperate the key and the value e.g = for a query
	 *            stirng
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, List<String>> splitQuery(
			final String aQueryString, final String aSepKey,
			final String aSepVal) throws UnsupportedEncodingException {
		String wSepKey = aSepKey != null ? aSepKey : "&";
		String wSepVal = aSepVal != null ? aSepVal : "=";

		final Map<String, List<String>> wQueryPairs = new LinkedHashMap<String, List<String>>();

		final String[] pairs = aQueryString.split(wSepKey);

		for (final String pair : pairs) {
			final int idx = pair.indexOf(wSepVal);
			final String key = idx > 0 ? URLDecoder.decode(
					pair.substring(0, idx), "UTF-8") : pair;
			if (!wQueryPairs.containsKey(key)) {
				wQueryPairs.put(key, new LinkedList<String>());
			}
			final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder
					.decode(pair.substring(idx + 1), "UTF-8") : null;
			wQueryPairs.get(key).add(value);
		}
		return wQueryPairs;
	}

	/**
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 *
	 * @see https
	 *      ://stackoverflow.com/questions/13592236/parse-a-uri-string-into-
	 *      name-value-collection
	 */
	public static Map<String, List<String>> splitQuery(final URL aUrl)
			throws UnsupportedEncodingException {

		return splitQuery(aUrl.getQuery());
	}

	public static Map<String, String> splitQueryFirst(final String aQueryString)
			throws UnsupportedEncodingException {
		return splitQueryFirst(aQueryString, null, null);
	}

	/**
	 * return only the first value
	 *
	 * @param aQueryString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Map<String, String> splitQueryFirst(
			final String aQueryString, final String aSepKey,
			final String aSepVal) throws UnsupportedEncodingException {

		Map<String, List<String>> wSplit = splitQuery(aQueryString, aSepKey,
				aSepVal);
		Map<String, String> wResult = new HashMap<String, String>();
		for (String wKey : wSplit.keySet()) {
			List<String> wVals = wSplit.get(wKey);
			wResult.put(wKey, CXStringUtils.stringListToString(wVals, "|"));
		}

		return wResult;

	}

	/**
	 * @param aMap
	 *            a instance of "List<?,?>" or "List<?,List<?>>"
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String urlEncodeUTF8(final Map<?, ?> aMap)
			throws UnsupportedEncodingException {

		final StringBuilder wSB = new StringBuilder();

		for (final Map.Entry<?, ?> entry : aMap.entrySet()) {
			if (wSB.length() > 0) {
				wSB.append("&");
			}
			final String wKey = urlEncodeUTF8(entry.getKey().toString());
			Object wValue = entry.getValue();

			if (wValue instanceof List) {
				for (Object wSubValue : ((List<?>) wValue)) {
					if (wSubValue != null) {
						wSubValue = "null";
					}
					wSB.append(String.format("%s=%s", wKey,
							urlEncodeUTF8(wSubValue.toString())));
				}
			}
			//
			else {
				if (wValue != null) {
					wValue = "null";
				}
				wSB.append(String.format("%s=%s", wKey,
						urlEncodeUTF8(wValue.toString())));
			}
		}
		return wSB.toString();
	}

	/**
	 * @param s
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String urlEncodeUTF8(final String s)
			throws UnsupportedEncodingException {
		return URLEncoder.encode(s, "UTF-8");

	}

	private final Map<String, List<String>> pQueryPairs = new HashMap<String, List<String>>();

	/**
	 *
	 */
	public CXQueryString() {
		super();
	}

	/**
	 * @param aQueryString
	 * @throws UnsupportedEncodingException
	 */
	public CXQueryString(final String aQueryString)
			throws UnsupportedEncodingException {
		this();
		pQueryPairs.putAll(splitQuery(aQueryString));
	}

	/**
	 * @param aUrl
	 * @throws UnsupportedEncodingException
	 */
	public CXQueryString(final URL aUrl) throws UnsupportedEncodingException {
		this();
		pQueryPairs.putAll(splitQuery(aUrl.getQuery()));
	}

	/**
	 * @return
	 */
	public Map<String, List<String>> getInternalMap() {
		return pQueryPairs;
	}

	/**
	 * @param aKey
	 * @return
	 */
	public String getValue(final String aKey) {
		final List<String> wValues = pQueryPairs.get(aKey);
		if (aKey == null) {
			return null;
		}
		if (wValues.size() == 1) {
			return wValues.get(0);
		} else {
			return CXStringUtils.stringListToString(wValues);
		}
	}

	/**
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	public String getValue(final String aKey, final String aDefault) {
		final String wValue = getValue(aKey);
		return wValue == null ? aDefault : wValue;
	}

	/**
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	public boolean getValueBool(final String aKey, final boolean aDefault) {
		final String wValue = getValue(aKey);
		return wValue == null ? aDefault : "true".equalsIgnoreCase(wValue)
				|| "on".equalsIgnoreCase(wValue);
	}

	/**
	 * @param aKey
	 * @param aDefault
	 * @return
	 */
	public int getValueInt(final String aKey, final int aDefault) {
		return CXStringUtils.strToInt(getValue(aKey), aDefault);
	}

	/**
	 * @param aKey
	 * @return
	 */
	public List<String> getValues(final String aKey) {
		return pQueryPairs.get(aKey);
	}

	/**
	 * @param aKey
	 * @param aValues
	 */
	public void put(final String aKey, final List<String> aValues) {

		final List<String> wValues = pQueryPairs.get(aKey);
		if (wValues == null) {
			pQueryPairs.put(aKey, aValues);
		}
		//
		else {
			wValues.addAll(aValues);
		}
	}

	/**
	 * @param aKey
	 * @param aValue
	 */
	public void put(final String aKey, final String aValue) {

		List<String> wValues = pQueryPairs.get(aKey);
		if (wValues == null) {
			wValues = new ArrayList<String>();
			wValues.add(aValue);
			pQueryPairs.put(aKey, wValues);
		}
		//
		else {
			wValues.add(aValue);
		}
	}

	/**
	 * @return an instance of properties containing only the first value of each
	 *         QueryPair
	 */
	public Map<String, String> toMapOfString() {

		final Map<String, String> wMapOfString = new HashMap<String, String>();

		for (final Entry<String, List<String>> wEntry : getInternalMap()
				.entrySet()) {

			final List<String> wValues = wEntry.getValue();
			final String wValue = (wValues.size() > 0) ? wValues.get(0) : null;

			wMapOfString.put(wEntry.getKey(), wValue);
		}

		return wMapOfString;
	}

	/**
	 * @return an instance of properties containing only the first value of each
	 *         QueryPair
	 */
	public Properties toProperties() {

		final Properties wProperties = new Properties();

		for (final Entry<String, List<String>> wEntry : getInternalMap()
				.entrySet()) {

			final List<String> wValues = wEntry.getValue();
			final String wValue = (wValues.size() > 0) ? wValues.get(0) : null;

			wProperties.put(wEntry.getKey(), wValue);
		}

		return wProperties;
	}

	/**
	 * @return the convertion of the internal QueryPairs map to an url query
	 *         string
	 *
	 * @throws UnsupportedEncodingException
	 */
	public String toQueryString() throws UnsupportedEncodingException {
		return urlEncodeUTF8(pQueryPairs);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return CXStringUtils.stringMapToString(toMapOfString());
	}

}
