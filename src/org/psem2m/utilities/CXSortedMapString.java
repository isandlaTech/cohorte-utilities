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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
 * @author ogattaz
 * 
 */
public class CXSortedMapString extends CXSortList<Entry<String, String>> {

	private static final long serialVersionUID = 9156273794753617100L;

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
			final CPropertiesComparator<Entry<String, String>> aComp) {
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
				new CPropertiesComparator<Entry<String, String>>(aSortAsc,
						aSortByKey));
	}

	/**
	 * @param aIt
	 * @param aComp
	 */
	private void init(final Iterator<Entry<String, String>> aIt,
			final CPropertiesComparator<Entry<String, String>> aComp) {
		setComparator(aComp);
		if (aIt != null) {
			while (aIt.hasNext()) {
				add(aIt.next());
			}
		}
	}

}