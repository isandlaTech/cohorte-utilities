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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;

/**
 * Load resource from xml properties available in the classpath
 *
 *
 * @author isandlaTech - ogattaz
 *
 */
public class CXResourcesXml extends CXResources {

	private static final String XML = "xml";

	/**
	 * @param aId
	 * @param aLocale
	 */
	public CXResourcesXml(final String aId, final Locale aLocale) {
		super();
		pId = aId;
		setAskedLocale(aLocale);
		loadResourceXml(aId, aLocale);
	}

	/**
	 * @param aId
	 * @param aLocale
	 * @return
	 */
	private void loadResourceXml(final String aId, final Locale aLocale) {
		try {
			InputStream wResourceStream = findResourceStream(aId, aLocale, XML);

			// size of the streamed properties => 2 times the size of the xml
			// stream
			int wSize = wResourceStream.available() * 2;

			Properties wProperties = new Properties();
			wProperties.loadFromXML(wResourceStream);
			ByteArrayOutputStream wOuputStream = new ByteArrayOutputStream(
					wSize);
			wProperties.store(wOuputStream, IConstants.EMPTY);
			ByteArrayInputStream wBAIS = new ByteArrayInputStream(
					wOuputStream.toByteArray());
			pResourceBundle = new PropertyResourceBundle(wBAIS);

		} catch (Exception e) {
			pWhy = e.getMessage();
			pResourceBundle = null;
		}
	}
}
