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
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author ogattaz
 */
public class CXFileDirSynchronizer {

	private static final String KEYEND = "zzzzz";
	private static final String KEYSTART = "";

	private CXFile pFileFrom = null;
	private CXFile pFileNextFrom = null;
	private CXFile pFileNextTarget = null;
	private CXFile pFileTarget = null;
	private final CXFileDir pFromDir;
	private boolean pIterEofFrom;
	private boolean pIterEofTarget;
	private Iterator<File> pIterFrom;
	private int pIterIdxFrom = 0;
	private int pIterIdxTarget = 0;
	private Iterator<File> pIterTarget;
	private String pKeyFrom = KEYSTART;
	private String pKeyNextFrom = KEYSTART;
	private String pKeyNextTarget = KEYSTART;
	private String pKeyTarget = KEYSTART;
	private final String pPathFrom;
	private final String pPathTarget;
	private final CXFileDir pTargetDir;

	private final boolean pWithSubDir;

	/**
	 * @param aTargetDir
	 * @param aFromDir
	 * @throws Exception
	 */
	protected CXFileDirSynchronizer(CXFileDir aTargetDir, CXFileDir aFromDir) {
		this(aTargetDir, aFromDir, IXFilesContainer.WITH_SUBDIRS);
	}

	/**
	 * @param aTargetDir
	 * @param aFromDir
	 * @throws Exception
	 */
	protected CXFileDirSynchronizer(CXFileDir aTargetDir, CXFileDir aFromDir, boolean aWithSubDir) {
		super();
		pTargetDir = aTargetDir;
		pPathTarget = getAbsolutePath(pTargetDir);
		pFromDir = aFromDir;
		pPathFrom = getAbsolutePath(pFromDir);
		pWithSubDir = aWithSubDir;
	}

	/**
	 * @param aFileFrom
	 * @param aKey
	 * @param aPathTarget
	 */
	protected void doAddOne(CXFile aFileFrom, String aKey, CXFileDir aTargetDir) throws IOException {
		// to be overwritten ...
	}

	/**
	 * @param aFileTarget
	 */
	protected void doDeleteOne(CXFile aFileTarget) throws IOException {
		// to be overwritten ...
	}

	/**
	 * @param pFileFrom
	 * @param pFileTarget
	 */
	protected void doSyncOne(CXFile aFileFrom, CXFile aFileTarget) throws IOException {
		// to be overwritten ...
	}

	/**
	 * 
	 */
	public void exec() throws IOException {

		initIteratorFrom();
		initIteratorTarget();

		readIterFrom();
		readIterTarget();

		int wCompare;
		boolean wMustReadFrom = true;
		boolean wMustReadTarget = true;

		boolean wMusDoSomething = mustDoSomthing(pKeyFrom, pKeyTarget);

		while (wMusDoSomething) {
			// lecture FROM
			if (wMustReadFrom) {
				setCurrentAndReadIterFrom();
			}

			// lecture TARGET
			if (wMustReadTarget) {
				setCurrentAndReadIterTarget();
			}
			wMusDoSomething = mustDoSomthing(pKeyFrom, pKeyTarget);

			if (wMusDoSomething) {

				wCompare = pKeyFrom.compareTo(pKeyTarget);
				// logDebug("Compare=[%d]", wCompare);

				if (wCompare == 0) {
					doSyncOne(pFileFrom, pFileTarget);
					wMustReadFrom = true;
					wMustReadTarget = true;

				} else if (wCompare < 0) {
					doAddOne(pFileFrom, pKeyFrom, pTargetDir);
					wMustReadFrom = true;
					wMustReadTarget = false;

				} else if (wCompare > 0) {
					doDeleteOne(pFileTarget);
					wMustReadFrom = false;
					wMustReadTarget = true;
				}
			}
		}
	}

	private String getAbsolutePath(CXFileDir aDir) {
		String wPath = aDir.getAbsolutePath();
		if (wPath.charAt(wPath.length() - 1) != File.separatorChar) {
			wPath = wPath + File.separatorChar;
		}
		return wPath;
	}

	/**
	 * @return le filtre a appliquer a la recherche des fichiers et dossiers
	 *         contenus dans "pFromDir"
	 */
	protected FileFilter getFileFiletFrom() {
		// to be overwritten ...
		return null;
	}

	/**
	 * @return le filtre a appliquer a la recherche des fichiers et dossiers
	 *         contenus dans "pTargetDir"
	 */
	protected FileFilter getFileFiletTarget() {
		// to be overwritten ...
		return null;
	}

	protected CXFileDir getFromDir() {
		return pFromDir;
	}

	/**
	 * @return la liste des fichiers et dossiers contenus dans "pFromDir" et ses
	 *         sous dossier si "pWithSubDir"
	 * @throws Exception
	 */
	protected CXSortListFiles getListFrom() throws IOException {
		return pFromDir.scanAll(getFileFiletFrom(), pWithSubDir);
	}

	/**
	 * @return la liste des fichiers et dossiers contenus dans "pTargetDir" et
	 *         ses sous dossier si "pWithSubDir"
	 * @throws Exception
	 */
	protected CXSortListFiles getListTarget() throws IOException {
		return pTargetDir.scanAll(getFileFiletTarget(), pWithSubDir);
	}

	protected CXFileDir getTargetDir() {
		return pTargetDir;
	}

	// 16j_101
	private boolean hasLogger() {
		return false;// pLogger != null;
	}

	/**
	 * @throws Exception
	 */
	protected void initIteratorFrom() throws IOException {
		pIterFrom = getListFrom().iterator();
	}

	/**
	 * @throws Exception
	 */
	protected void initIteratorTarget() throws IOException {
		pIterTarget = getListTarget().iterator();
	}

	// 16j_101
	protected void logWrite(String aFormat, Object... args) {
		// if (hasLogger())
		// pLogger.logWrite(aFormat, args);
	}

	/**
	 * @param aFromKey
	 * @param aTargetKey
	 * @return
	 */
	private boolean mustDoSomthing(String aFromKey, String aTargetKey) {
		// 16j_101
		if (hasLogger()) {
			logWrite("mustDoSomthing  From=[%s],targ=[%s]", aFromKey, aTargetKey);
		}
		return !KEYEND.equals(aFromKey) || !KEYEND.equals(aTargetKey);
	}

	private void readIterFrom() {
		pIterEofFrom = !pIterFrom.hasNext();
		pIterIdxFrom += (!pIterEofFrom) ? 1 : 0;
		// 16j_101
		if (hasLogger()) {
			logWrite("readIterFrom   EOF=[%b],Idx=[%d]", pIterEofFrom, pIterIdxFrom);
		}
		pFileNextFrom = (!pIterEofFrom) ? new CXFile(pIterFrom.next().getAbsolutePath()) : null;
		setKeyNextFrom();
	}

	private void readIterTarget() {
		pIterEofTarget = !pIterTarget.hasNext();
		pIterIdxTarget += (!pIterEofTarget) ? 1 : 0;
		// 16j_101
		if (hasLogger()) {
			logWrite("readIterTarget EOF=[%b],Idx=[%d]", pIterEofTarget, pIterIdxTarget);
		}
		pFileNextTarget = (!pIterEofTarget) ? new CXFile(pIterTarget.next().getAbsolutePath())
				: null;
		setKeyNextTarget();
	}

	private void setCurrentAndReadIterFrom() {
		pFileFrom = pFileNextFrom;
		pKeyFrom = pKeyNextFrom;
		readIterFrom();
	}

	private void setCurrentAndReadIterTarget() {
		pFileTarget = pFileNextTarget;
		pKeyTarget = pKeyNextTarget;
		readIterTarget();
	}

	private void setKeyNextFrom() {
		if (pFileNextFrom == null) {
			pKeyNextFrom = KEYEND;
		} else {
			int wOffset = pPathFrom.length();
			String wName = pFileNextFrom.getAbsolutePath();
			pKeyNextFrom = wName.substring(wOffset);
		}
		// 16j_101
		if (hasLogger()) {
			logWrite("pKeyNextFrom=[%s]", pKeyNextFrom);
		}
	}

	private void setKeyNextTarget() {
		if (pFileNextTarget == null) {
			pKeyNextTarget = KEYEND;
		} else {
			int wOffset = pPathTarget.length();
			String wName = pFileNextTarget.getAbsolutePath();
			pKeyNextTarget = wName.substring(wOffset);
		}
		// 16j_101
		if (hasLogger()) {
			logWrite("pKeyNextTarget=[%s]", pKeyNextTarget);
		}
	}

	// 16j_101
	// protected void setLogger(IXLogBase aLogger) {
	// pLogger = aLogger;
	// }
}
