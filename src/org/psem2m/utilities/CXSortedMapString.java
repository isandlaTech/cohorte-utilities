/*******************************************************************************
 * Copyright (c) 2011 www.isandlatech.com (www.isandlatech.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ogattaz (isandlaTech) - initial API and implementation
 *******************************************************************************/
package org.psem2m.utilities;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Renvoie un comparateur pour les properties
 */
class CMapStringComparator<E> extends CXAbstractListComparator<E> {

	private boolean pSortByKey = true;

	/**
	 * @param aSortAsc
	 */
	public CMapStringComparator(final boolean aSortAsc) {
		super(aSortAsc);
	}

	/**
	 * @param aSortAsc
	 * @param aSortByKey
	 */
	public CMapStringComparator(final boolean aSortAsc, final boolean aSortByKey) {
		super(aSortAsc);
		pSortByKey = aSortByKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.CXAbstractListComparator#compareObjects(java.lang
	 * .Object, java.lang.Object)
	 */
	@Override
	protected int compareObjects(final Object a, final Object b) {
		return getData(a).compareTo(getData(b));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.adonix.adminsrv.utils.CXAbstractListComparator#equalsObjects(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	protected boolean equalsObjects(final Object a, final Object b) {
		String wKeyA = (String) ((Entry<?, ?>) a).getKey();
		String wKeyB = (String) ((Entry<?, ?>) b).getKey();
		return wKeyA.equals(wKeyB);
	}

	/**
	 * @param a
	 * @return
	 */
	protected String getData(final Object a) {
		if (pSortByKey) {
			return (String) ((Entry<?, ?>) a).getKey();
		} else {
			return (String) ((Entry<?, ?>) a).getValue();
		}
	}
}

/**
 * java.lang.Comparable
 * 
 * @author ogattaz
 * 
 */
class CStringEntry implements Map.Entry<String, String>,
		Comparable<Map.Entry<String, String>> {

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String toString(final Object aValue) {

		if (aValue instanceof String[]) {
			return CXStringUtils.stringTableToString((String[]) aValue);
		}
		if (aValue instanceof List<?>) {
			return CXStringUtils.stringListToString((List<String>) aValue);
		}
		return String.valueOf(aValue);
	}

	private final Object pKey;

	private Object pValue;

	/**
	 * @param aEntry
	 */
	CStringEntry(final Entry<?, ?> aEntry) {
		this(aEntry.getKey(), aEntry.getValue());
	}

	/**
	 * @param aKey
	 * @param aValue
	 */
	/**
	 * @param aKey
	 * @param aValue
	 */
	CStringEntry(final Object aKey, final Object aValue) {
		super();
		pKey = aKey;
		pValue = aValue;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Entry<String, String> aEntry) {

		return getKey().compareTo(aEntry.getKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map.Entry#getKey()
	 */
	@Override
	public String getKey() {
		return toString(pKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map.Entry#getValue()
	 */
	@Override
	public String getValue() {
		return toString(pValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map.Entry#setValue(java.lang.Object)
	 */
	@Override
	public String setValue(final String aValue) {
		Object wOldValue = pValue;
		pValue = aValue;
		return toString(wOldValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s=[%s]", getKey(), getValue());
	}
}

/**
 * @author ogattaz
 * 
 */
public class CXSortedMapString extends CXSortList<Entry<String, String>> {

	private static final long serialVersionUID = 9156273794753617100L;

	/**
	 * @param aDictionary
	 * @return
	 */
	public static CXSortedMapString convert(final Dictionary<?, ?> aDictionary) {
		return convert(aDictionary, ASCENDING, SORTBYKEY);
	}

	/**
	 * @param aDictionary
	 * @param aSortAsc
	 * @param aSortByKey
	 * @return
	 */
	public static CXSortedMapString convert(final Dictionary<?, ?> aDictionary,
			final boolean aSortAsc, final boolean aSortByKey) {
		CXSortedMapString wMS = new CXSortedMapString(aSortAsc);
		wMS.setComparator(new CMapStringComparator<Entry<String, String>>(
				aSortAsc, aSortByKey));

		Enumeration<?> wKeysEnum = aDictionary.keys();
		while (wKeysEnum.hasMoreElements()) {
			Object wKey = wKeysEnum.nextElement();
			wMS.add(new CStringEntry(wKey, aDictionary.get(wKey)));
		}
		return wMS;
	}

	/**
	 * @param aDictionary
	 * @return
	 */
	public static CXSortedMapString convert(final Map<?, ?> aMap) {
		return convert(aMap, ASCENDING, SORTBYKEY);
	}

	/**
	 * @param aDictionary
	 * @param aSortAsc
	 * @param aSortByKey
	 * @return
	 */
	public static CXSortedMapString convert(final Map<?, ?> aMap,
			final boolean aSortAsc, final boolean aSortByKey) {

		CXSortedMapString wMS = new CXSortedMapString(aSortAsc);

		wMS.setComparator(new CMapStringComparator<Entry<String, String>>(
				aSortAsc, aSortByKey));

		Set<?> wKeys = aMap.keySet();

		for (Object wKey : wKeys) {
			wMS.add(new CStringEntry(wKey, aMap.get(wKey)));
		}
		return wMS;
	}

	/**
   * 
   */
	public CXSortedMapString() {
		this(CXSortList.ASCENDING);
	}

	/**
	 * @param aSortAsc
	 */
	public CXSortedMapString(final boolean aSortAsc) {
		super(aSortAsc);
	}

	/**
	 * @param aComp
	 */
	public CXSortedMapString(
			final CMapStringComparator<Entry<String, String>> aComp) {
		super(aComp);
	}

	/**
	 * 
	 * @param aData
	 * @param aSortAsc
	 *            Tri sur les cles
	 */
	public CXSortedMapString(final Map<String, String> aData,
			final boolean aSortAsc) {
		this(aData, aSortAsc, SORTBYKEY);
	}

	/**
	 * 
	 * @param aData
	 * @param aSortAsc
	 *            -Tri
	 * @param aSortByKey
	 *            TrueType de tri (True --> Tri par Key - false --> tri par
	 *            valeurs)
	 */
	public CXSortedMapString(final Map<String, String> aData,
			final boolean aSortAsc, final boolean aSortByKey) {
		super();
		init(aData.entrySet().iterator(),
				new CMapStringComparator<Entry<String, String>>(aSortAsc,
						aSortByKey));
	}

	/**
	 * 
	 */
	public void add(final Object aKey, final Object aValue) {
		add(new CStringEntry(aKey, aValue));
	}

	/**
	 * @param aIterator
	 * @param aComp
	 */
	private void init(final Iterator<Entry<String, String>> aIterator,
			final CMapStringComparator<Entry<String, String>> aComp) {
		setComparator(aComp);
		if (aIterator != null) {
			while (aIterator.hasNext()) {
				add(aIterator.next());
			}
		}
	}

}