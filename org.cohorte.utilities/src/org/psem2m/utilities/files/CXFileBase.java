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
package org.psem2m.utilities.files;

import java.io.File;

/**
 * Classe de base pour la gestion de fichiers et répertoires
 * 
 * @author Sage - Grenoble
 * 
 */
public class CXFileBase extends File {

	public static final String EOL = "\n";
	public static final char EOLChar = '\n';
	public static final String MANIFEST_MF = "MANIFEST.MF";
	public static final String META_INF = "META-INF";
	private static final long serialVersionUID = 3258131349545432374L;
	public static final String TMP = "tmp";

	/**
	 * @param aPath
	 * @return
	 */
	public static String checkSeparator(String aPath) {
		if (aPath == null) {
			return null;
		} else {
			return aPath.replace(CXFileBase.getBadSeparatorChar(), separatorChar);
		}
	}

	/**
	 * @return
	 */
	public static char getBadSeparatorChar() {
		if (separatorChar == '\\') {
			return '/';
		} else {
			return '\\';
		}
	}

	/**
	 * @return
	 */
	public static String getEOL() {
		return EOL;
	}

	/**
	 * @return
	 */
	public static char getEOLChar() {
		return EOLChar;
	}

	/**
	 * @param aFile
	 * @param aSubPath
	 */
	public CXFileBase(File aFile, String aSubPath) {
		super(aFile,checkSeparator(aSubPath));
	}

	/**
	 * @param aFile
	 */
	public CXFileBase(File aFile) {
		super(aFile.getAbsolutePath());
	}

	/**
	 * @param aPath
	 */
	public CXFileBase(String aPath) {
		super(aPath);
	}

	/**
	 * 
	 * @param aPath
	 * @param aSubPath
	 */
	public CXFileBase(String aPath, String aSubPath) {
		super(aPath, checkSeparator(aSubPath));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.File#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CXFileBase)) {
			return super.equals(obj);
		}

		CXFileBase wFileBase = (CXFileBase) obj;
		if ((this.isFile() && wFileBase.isFile())
				|| (this.isDirectory() && wFileBase.isDirectory())) {
			if (isPathCaseSensitive()) {
				return wFileBase.getAbsolutePath().equals(getAbsolutePath());
			} else {
				return wFileBase.getAbsolutePath().equalsIgnoreCase(getAbsolutePath());
			}
		} else {
			return false;
		}
	}

	/**
	 * Méthode volontairement passée en @Deprecated pour forcer l'utilisation de
	 * getAbsolutePath
	 */
	@Override
	@Deprecated
	public String getPath() {
		return super.getPath();
	}

	/**
	 * Retourne un path relatif
	 * 
	 * @param aRacine
	 * @return
	 * @throws Exception
	 */
	public String getRelativePath(String aRacine) throws Exception {
		String wResult = this.getAbsolutePath();
		String wDirCanonical = aRacine;
		String wCurrentCanonical = this.getAbsolutePath();

		if (wCurrentCanonical.indexOf(wDirCanonical) == 0) {
			wResult = wCurrentCanonical.substring(wDirCanonical.length());
		}
		if (wResult.startsWith(separator)) {
			wResult = wResult.substring(1);
		}
		return wResult;
	}

	/*
	 * règle sonar : Checks that classes that override equals() also override
	 * hashCode().
	 * 
	 * (non-Javadoc)
	 * 
	 * @see java.io.File#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @return true if the separatorchar used by the file system is a slash
	 */
	protected boolean isPathCaseSensitive() {
		return separatorChar == '/';
	}

	/**
	 * @param aMethod
	 * @param aMessage
	 */
	protected void traceError(String aMethod, String aMessage) {
		System.out.print("FILE ERROR - Path='" + getAbsolutePath() + "'\n");
		if (aMessage != null && aMessage != null) {
			System.out.print("--> Method=" + aMethod + "\n");
			System.out.print("--> Message=" + aMessage + "\n");
		}
	}

	/**
	 * @param aMethod
	 * @param a
	 */
	protected void traceError(String aMethod, Throwable a) {
		if (a != null) {
			traceError(aMethod, a.getMessage());
		}
	}
}
