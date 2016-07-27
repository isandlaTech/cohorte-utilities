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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.psem2m.utilities.CXJvmUtils;
import org.psem2m.utilities.CXStringUtils;

/**
 * Classe de gestion de repertoies !! MonoThread
 *
 * @author ogattaz
 *
 */
public class CXFileDir extends CXFileBase implements IXFilesContainer {

	private static final long serialVersionUID = 3257562910606570550L;

	private static final CXFileDir sTempDir = new CXFileDir(
			getTempDirAbsolutePath());

	private static final CXFileDir sUserDir = new CXFileDir(
			getUserDirAbsolutePath());

	public static final boolean WITH_DIR = true;
	public static final boolean WITH_TEXTFILE = true;

	/**
	 * MOD_OG_20150611 CXFileDir constructors accept array of subdirs
	 *
	 * @param aSubDirs
	 * @return
	 */
	private static String buildSubdir(final String... aSubDirs) {

		final StringBuilder wSB = new StringBuilder();

		if (aSubDirs != null && aSubDirs.length > 0) {
			for (String wSubDir : aSubDirs) {
				if (wSubDir != null && !wSubDir.isEmpty()) {

					if (wSB.length() > 0) {
						wSB.append(separatorChar);
					}
					wSB.append(cleanPath(wSubDir));
				}
			}
		}
		return wSB.toString();
	}

	/**
	 * MOD_OG_20150611 CXFileDir constructors accept array of subdirs
	 *
	 * @param aPath
	 * @return
	 */
	public static String checkPath(final String aPath) {

		if (aPath == null || aPath.isEmpty()) {
			return aPath;
		} else {
			return cleanPath(aPath);
		}
	}

	/**
	 * MOD_OG_20150611 CXFileDir constructors accept array of subdirs
	 *
	 * @param aPath
	 * @return
	 */
	private static String cleanPath(final String aPath) {

		String wPath = checkSeparator(aPath);
		final int wIdxLastChar = wPath.length() - 1;

		if (wPath.charAt(wIdxLastChar) == separatorChar) {
			wPath = wPath.substring(0, wIdxLastChar);
		}
		return wPath;
	}

	/**
	 *
	 * @param aListExt
	 * @return
	 */
	public static FileFilter getExcluderExtension(final String aListExt) {

		return getExcluderExtension(aListExt, null);
	}

	/**
	 *
	 * @param aListExt
	 * @param aSubFileFilter
	 * @return
	 */
	public static FileFilter getExcluderExtension(final String aListExt,
			final FileFilter aSubFileFilter) {

		return new CXFileFilterExtension(aListExt, aSubFileFilter,
				!CXFileFilter.INCLUDE);
	}

	/**
	 *
	 * @param aListRegExp
	 * @return
	 */
	public static FileFilter getExcluderName(final String aListRegExp) {

		return getExcluderName(aListRegExp, null);
	}

	/**
	 *
	 * @param aListRegExp
	 * @param aSubFileFilter
	 * @return
	 */
	public static FileFilter getExcluderName(final String aListRegExp,
			final FileFilter aSubFileFilter) {

		return new CXFileFilterName(aListRegExp, aSubFileFilter,
				!CXFileFilter.INCLUDE);
	}

	/**
	 *
	 *
	 * @param aListSubPath
	 * @return
	 */
	public static FileFilter getExcluderSubPath(final String aListSubPath) {

		return getExcluderSubPath(aListSubPath, null);
	}

	/**
	 *
	 *
	 * @param aListSubPath
	 * @param aSubFileFilter
	 * @return
	 */
	public static FileFilter getExcluderSubPath(final String aListSubPath,
			final FileFilter aSubFileFilter) {

		return new CXFileFilterSubPath(aListSubPath, aSubFileFilter,
				!CXFileFilter.INCLUDE);
	}

	/**
	 *
	 * @return
	 */
	public static FileFilter getExcluderSvn() {

		return getExcluderSvn(null);
	}

	/**
	 * @param aSubFileFilter
	 * @return
	 */
	public static FileFilter getExcluderSvn(final FileFilter aSubFileFilter) {

		return new CXFileFilterSvn(aSubFileFilter, !CXFileFilter.INCLUDE);
	}

	/**
	 * @param aListExt
	 * @return
	 */
	public static FileFilter getFilterExtension(final String aListExt) {

		return getFilterExtension(aListExt, null);
	}

	/**
	 *
	 * @param aListExt
	 * @param aSubFileFilter
	 * @return
	 */
	public static FileFilter getFilterExtension(final String aListExt,
			final FileFilter aSubFileFilter) {

		return new CXFileFilterExtension(aListExt, aSubFileFilter,
				CXFileFilter.INCLUDE);
	}

	/**
	 *
	 * @param aListRegExp
	 * @return
	 */
	public static FileFilter getFilterName(final String aListRegExp) {

		return getFilterName(aListRegExp, null);
	}

	/**
	 *
	 * @param aListRegExp
	 * @param aSubFileFilter
	 * @return
	 */
	public static FileFilter getFilterName(final String aListRegExp,
			final FileFilter aSubFileFilter) {

		return new CXFileFilterName(aListRegExp, aSubFileFilter,
				CXFileFilter.INCLUDE);
	}

	/**
	 *
	 *
	 * @param aListSubPath
	 * @return
	 */
	public static FileFilter getFilterSubPath(final String aListSubPath) {

		return getFilterSubPath(aListSubPath, null);
	}

	/**
	 *
	 * @param aListSubPath
	 * @param aSubFileFilter
	 * @return
	 */
	public static FileFilter getFilterSubPath(final String aListSubPath,
			final FileFilter aSubFileFilter) {

		return new CXFileFilterSubPath(aListSubPath, aSubFileFilter,
				CXFileFilter.INCLUDE);
	}

	/**
	 * @return
	 */
	public static FileFilter getFilterSvn() {

		return getFilterSvn(null);
	}

	/**
	 * @param aSubFileFilter
	 * @return
	 */
	public static FileFilter getFilterSvn(final FileFilter aSubFileFilter) {

		return new CXFileFilterSvn(aSubFileFilter, CXFileFilter.INCLUDE);
	}

	/**
	 * @return
	 */
	public static CXFileDir getTempDir() {

		return sTempDir;
	}

	/**
	 * @return
	 */
	private static String getTempDirAbsolutePath() {

		String wTempPath = System.getProperty("java.io.tmpdir");

		// Windows 2008 - java.io.tmpdir could be empty
		if (wTempPath == null || wTempPath.isEmpty()) {
			wTempPath = System.getProperty("user.dir");
			System.out
					.println(String
							.format("WARNING: The temporary folder \"java.io.tmpdir\" is not set. Uses \"user.dir\" as temp dir [%s]",
									wTempPath));
			System.setProperty("java.io.tmpdir", wTempPath);
		}

		try {
			CXFileDir wTempDir = new CXFileDir(wTempPath);
			if (!wTempDir.exists()) {
				if (wTempDir.createHierarchy()) {
					System.out.println(String
							.format("WARNING: Creates temporary folder [%s]",
									wTempPath));
				}
			}

		} catch (Exception e1) {
			Exception wEx = new Exception(String.format(
					"Unable to create the temporary folder [%s]", wTempPath));
			wEx.printStackTrace();
			// default
			return System.getProperty("user.dir");
		}

		try {
			File wTempFile = File.createTempFile("tmp", "txt");
			String wPath = wTempFile.getParent();
			wTempFile.delete();
			return wPath;
		} catch (Exception e) {
			Exception wEx = new Exception(
					String.format(
							"Unable to create a temporary file in the folder java.io.tmpdir=[%s] using the method File:createTempFile(...) ",
							System.getProperty("java.io.tmpdir")), e);
			wEx.printStackTrace();
			// default
			return System.getProperty("user.dir");
		}
	}

	/**
	 *
	 * @return
	 */
	public static CXFileDir getUserDir() {

		return sUserDir;
	}

	/**
	 *
	 * @return
	 */
	private static String getUserDirAbsolutePath() {
		return System.getProperty(CXJvmUtils.SYSPROP_USER_DIR);
	}

	/**
	 * @param aRoot
	 * @param aArgs
	 * @return
	 */
	public static CXFileDir newInstance(final CXFileDir aRoot, Object... aArgs) {

		aArgs = aArgs == null || aArgs.length == 0 ? null : aArgs;
		final StringBuilder wSB = new StringBuilder();
		if (aArgs != null && aArgs.length != 0) {
			for (final Object xObj : aArgs) {
				final String wPath = CXStringUtils
						.strFullTrim(xObj != null ? xObj.toString() : null);
				if (wPath != null && !wPath.isEmpty()) {
					final char wFirst = wPath.charAt(0);
					if (wFirst != '/' && wFirst != '\\') {
						wSB.append(separatorChar);
					}
					wSB.append(wPath);
				}
			}
		}
		return aRoot == null ? new CXFileDir(wSB.toString()) : new CXFileDir(
				aRoot, wSB.toString());
	}

	/**
	 * @param aArgs
	 * @return
	 */
	public static CXFileDir newInstance(final Object... aArgs) {

		return newInstance(null, aArgs);
	}

	/**
	 * JAVA EXCEPTION : Impossible de creer le repertoire
	 *
	 * Waits for NFS to propagate a file creation, imposing a timeout.
	 *
	 * @param file
	 *            The file
	 * @param seconds
	 *            The maximum time in seconds to wait.
	 * @return True if file exists.
	 *
	 *         Needs a clearer javadoc to see its real purpose for someone
	 *         without NFS-knowledge.
	 */
	private static boolean waitFor(final File file, final int seconds) {

		int timeout = 0;
		int tick = 0;
		while (!file.exists()) {
			if (tick++ >= 10) {
				tick = 0;
				if (timeout++ > seconds) {
					return false;
				}
			}
			try {
				Thread.sleep(100);
			} catch (final InterruptedException ignore) {
			} catch (final Exception ex) {
				break;
			}
		}
		return true;
	}

	private IXDirScanListener pDirScanListener = null;

	private int pScanLevel = 0;

	/**
	 * @param aDir
	 */
	public CXFileDir(final File aDir) {
		super(aDir);
	}

	/**
	 * @param aParentDir
	 * @param aSubDir
	 */
	public CXFileDir(final File aDir, final String aSubDir) {
		super(aDir, checkPath(aSubDir));
	}

	/**
	 * MOD_OG_20150611 CXFileDir constructors accept array of subdirs
	 *
	 * @param aDir
	 * @param aSubDirs
	 */
	public CXFileDir(final File aDir, final String... aSubDirs) {
		super(aDir, buildSubdir(aSubDirs));
	}

	/**
	 * @param aFullPath
	 */
	public CXFileDir(final String aFullPath) {

		super(checkPath(aFullPath));
	}

	/**
	 * @param aParentDir
	 * @param aSubDir
	 */
	public CXFileDir(final String aParentDir, final String aSubDir) {

		super(checkPath(aParentDir), checkPath(aSubDir));
	}

	/**
	 * MOD_OG_20150611 CXFileDir constructors accept array of subdirs
	 *
	 * @param aParentDir
	 * @param aSubDirs
	 */
	public CXFileDir(final String aParentDir, final String... aSubDirs) {

		super(checkPath(aParentDir), buildSubdir(aSubDirs));
	}

	/**
	 *
	 * Supprime seulement le contenu du repertoire - Recursif
	 *
	 * @return
	 * @throws IOException
	 */
	public int clean() throws IOException {

		return clean(null);
	}

	/**
	 *
	 *
	 * @param aFileFilter
	 * @return
	 * @throws IOException
	 */
	public int clean(final FileFilter aFileFilter) throws IOException {

		return remove(false, aFileFilter);
	}

	/**
	 * Copie tous le contenu du repertoire courant --> aDstDir - Recursif
	 *
	 * @param aDstDir
	 *            le dossier de destination : le cree s'il n'existe pas, le
	 *            supprime s'il existe
	 * @return
	 * @throws IOException
	 */
	public int copyTo(final CXFileDir aDstDir) throws IOException {

		aDstDir.remove();
		return copyTo(aDstDir, null, true, true);
	}

	/**
	 *
	 * Copie le contenu du repertoire courant --> aDstDir - Recursif
	 *
	 * @param aDstDir
	 *            le dossier de destination : le cree s'il n'existe pas,
	 * @param aDeleteExinstingContent
	 *            si vrai, supprime le dossier de destination s'il existe
	 * @return
	 * @throws IOException
	 */
	public int copyTo(final CXFileDir aDstDir,
			final boolean aDeleteExinstingContent) throws IOException {

		if (aDeleteExinstingContent) {
			aDstDir.remove();
		}
		return copyTo(aDstDir, null, true, true);
	}

	/**
	 * Copie le contenu du repertoire courant --> aDstDir - Recursif
	 *
	 * @param aDstDir
	 *            le dossier de destination : le cree s'il n'existe pas, le
	 *            supprime s'il existe
	 *
	 * @param aFilter
	 *            --> Filtre sur les fichiers a copier - "ext,ext,..."
	 * @return
	 * @throws IOException
	 */
	public int copyTo(final CXFileDir aDstDir, final FileFilter aFilter)
			throws IOException {

		aDstDir.remove();
		return copyTo(aDstDir, aFilter, true, true);
	}

	/**
	 * Copie du contenu du repertoire courant --> aDstDir - Recursif Ne cree pas
	 * les repertoires vides
	 *
	 * @param aFilter
	 *            --> Filtre sur les fichiers a copier - "ext,ext,..."
	 * @param aFilter
	 *            =null --> Aucun filtre sur les fichiers a copier
	 * @param aDeleteExinstingFiles
	 *            =true --> Ecrase les fichier existants ou pas
	 * @param aCreateEmptyDir
	 *            =true --> Cree les repertoires qui ne contiennent aucun
	 *            fichier
	 * @param aCreateEmptyDir
	 *            =false --> Ne cree que les repertoires qui contienet des
	 *            fichiers
	 * @return le nombre de fichiers copies et repertoires crees
	 * @throws IOException
	 */
	public int copyTo(final CXFileDir aDstDir, final FileFilter aFilter,
			final boolean aDeleteExinstingFiles, final boolean aCreateEmptyDir)
			throws IOException {

		if (!exists()) {
			return 0;
		}

		if (!aDstDir.exists()) {
			aDstDir.mkdirs();
		}

		int wRes = 0;
		final Iterator<File> wIt = getMyFiles(aFilter, true).iterator();
		while (wIt.hasNext()) {
			final File wFile = wIt.next();
			if (wFile.isFile()) {
				((CXFile) wFile).copyTo(aDstDir, wFile.getName(),
						aDeleteExinstingFiles);
				wRes++;
			} else if (wFile.isDirectory()) {
				wRes += ((CXFileDir) wFile).copyTo(
						new CXFileDir(aDstDir.getAbsolutePath(), wFile
								.getName()), aFilter, aDeleteExinstingFiles,
						aCreateEmptyDir);
			}
		}
		if (wRes == 0 && !aCreateEmptyDir) {
			aDstDir.delete();
		}

		return wRes;
	}

	/**
	 * Cree le repertoire et son arborescence
	 *
	 * @return true si un repertoire a ete cree false si il existait deja ou non
	 *         cree
	 * @throws IOException
	 */
	public boolean createHierarchy() throws IOException {

		if (exists()) {
			return false;
		}
		boolean wCreated = this.mkdirs();
		// comme NTFS met un peu de temps pour propager les maj dees table
		// d'allocation...
		if (!wCreated) {
			wCreated = waitFor(this, 1);
		}
		return wCreated;
	}

	/**
	 * @throws IOException
	 */
	public void failIfNotExist() throws IOException {

		if (!exists()) {
			throw new IOException("Directory not found [" + getAbsolutePath()
					+ "]");
		}
	}

	/**
	 * utilisation de la constante WITH_TEXTFILE
	 *
	 * @param aFilter
	 * @param aWithDirs
	 * @return
	 * @throws IOException
	 */
	public ArrayList<File> getMyFiles(final FileFilter aFilter,
			final boolean aWithDirs) throws IOException {

		return getMyFiles(aFilter, aWithDirs, !WITH_TEXTFILE);
	}

	/*
	 * Renvoie la liste des Fichier et dossiers - Non recursif
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.files.IXFilesContainer#getMyFiles(java.io.FileFilter
	 * , boolean, boolean)
	 */
	@Override
	public ArrayList<File> getMyFiles(final FileFilter aFilter,
			final boolean aWithDirs, final boolean aInstanciateTxtFiles)
			throws IOException {

		failIfNotExist();

		File[] wList;
		if (aFilter == null) {
			wList = this.listFiles();
		} else {
			wList = this.listFiles(aFilter);
		}
		final ArrayList<File> wResult = new ArrayList<File>(wList.length);

		for (int wI = 0; wI < wList.length; wI++) {
			final File wFile = wList[wI];

			if (wFile.isFile()) {
				final CXFileBase wNewFile = newFile(wFile.getAbsolutePath(),
						aInstanciateTxtFiles);
				if (hasScanListener()) {
					getScanListener().listenOneFile(getScanLevel(),
							wResult.size(), wNewFile);
				}
				wResult.add(wNewFile);
			} else if (aWithDirs && wFile.isDirectory()) {
				final CXFileBase wNewFileDir = newFileDir(wFile
						.getAbsolutePath());
				if (hasScanListener()) {
					getScanListener().listenOneFile(getScanLevel(),
							wResult.size(), wNewFileDir);
				}
				wResult.add(wNewFileDir);
			}

		}

		return wResult;
	}

	/**
	 * renvoi la liste des fichiers dans l'orde aphabetique
	 *
	 * @param aFilter
	 * @param aWithDirs
	 * @return
	 * @throws IOException
	 */
	public ArrayList<File> getMySortedFiles(final FileFilter aFilter,
			final boolean aWithDirs) throws IOException {

		final ArrayList<File> wList = getMySortedFiles(aFilter, aWithDirs,
				!WITH_TEXTFILE);

		return wList;
	}

	/**
	 *
	 * Renvoie la liste des Fichier et dossiers dans l'ordre alphabetique- Non
	 * recursif
	 *
	 *
	 * @param aFilter
	 *            Filtre sur les fichiers
	 * @param aWithDirs
	 *            False --> ignore pas les sous-repertoires
	 * @param aInstanciateTxtFiles
	 * @return
	 * @throws IOException
	 */
	public ArrayList<File> getMySortedFiles(final FileFilter aFilter,
			final boolean aWithDirs, final boolean aInstanciateTxtFiles)
			throws IOException {

		failIfNotExist();

		File[] wListSorted;

		if (aFilter == null) {
			wListSorted = this.listFiles();
		} else {
			wListSorted = this.listFiles(aFilter);
		}

		Arrays.sort(wListSorted);

		final ArrayList<File> wResult = new ArrayList<File>(wListSorted.length);

		for (int wI = 0; wI < wListSorted.length; wI++) {
			final File wFile = wListSorted[wI];
			if (wFile.isFile()) {
				final CXFileBase wNewFile = newFile(wFile.getAbsolutePath(),
						aInstanciateTxtFiles);
				if (hasScanListener()) {
					getScanListener().listenOneFile(getScanLevel(),
							wResult.size(), wNewFile);
				}
				wResult.add(wNewFile);
			} else if (aWithDirs && wFile.isDirectory()) {
				final CXFileBase wNewFileDir = newFileDir(wFile
						.getAbsolutePath());
				if (hasScanListener()) {
					getScanListener().listenOneFile(getScanLevel(),
							wResult.size(), wNewFileDir);
				}
				wResult.add(wNewFileDir);
			}

		}
		// System.out.print("getMyFiles : " + this.getAbsolutePath() +
		// " - Count=" + wResult.size()+ "\n");
		return wResult;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public CXFileDir getParentDirectory() {

		return new CXFileDir(getParent());
	}

	/**
	 * @param aEndWithSep
	 *            Ajout ou non d'un separateur en fin du getPath()
	 * @return
	 */
	public String getPath(final boolean aEndWithSep) {

		final String wTemp = super.getAbsolutePath();
		if (wTemp.endsWith(separator)) {
			if (aEndWithSep) {
				return wTemp;
			} else {
				return wTemp.substring(0, wTemp.length() - 1);
			}
		} else {
			if (aEndWithSep) {
				return wTemp + separatorChar;
			} else {
				return wTemp;
			}
		}
	}

	/**
	 * @return
	 */
	private int getScanLevel() {

		return pScanLevel;
	}

	/**
	 * @return
	 */
	private IXDirScanListener getScanListener() {

		return pDirScanListener;
	}

	/**
	 * @return
	 */
	private boolean hasScanListener() {

		return pDirScanListener != null;
	}

	/**
	 * True si le repertoie est vide
	 */
	public boolean isEmpty() {

		return this.listFiles().length == 0;
	}

	/**
	 * @return
	 */
	public boolean isMetaInf() {

		return this.getName().equals(META_INF);
	}

	/**
	 * @return
	 */
	private boolean isScanLevelZero() {

		return getScanLevel() == 0;
	}

	/**
	 * Supprime aDstDir Deplace le repertoire courant --> aDstDir - Recursif
	 *
	 * @param aDstDir
	 * @return
	 * @throws IOException
	 */
	public int moveTo(final CXFileDir aDstDir) throws IOException {

		aDstDir.remove();
		return moveTo(aDstDir, null, true, true);
	}

	/**
	 * Supprime aDstDir Deplace le repertoire courant --> aDstDir - Recursif
	 *
	 * @param aDstDir
	 * @param aFilter
	 *            Filtre sur les fichiers a copier - "ext,ext,..."
	 * @return
	 * @throws IOException
	 */
	/**
	 * @param aDstDir
	 * @param aFilter
	 * @return
	 * @throws IOException
	 */
	public int moveTo(final CXFileDir aDstDir, final FileFilter aFilter)
			throws IOException {

		aDstDir.remove();
		return moveTo(aDstDir, aFilter, true, true);
	}

	/**
	 * Deplacement du repertoire courant --> aDstDir - Recursif Ne cree pas les
	 * repertoires vides
	 *
	 * @param aFilter
	 *            --> Filtre sur les fichiers a copier - "ext,ext,..."
	 * @param aFilter
	 *            =null --> Aucun filtre sur les fichiers a copier
	 * @param aDeleteExinstingFiles
	 *            =true --> Ecrase les fichier existants ou pas
	 * @param aCreateEmptyDir
	 *            =true --> Cree les repertoires qui ne contiennent aucun
	 *            fichier
	 * @param aCreateEmptyDir
	 *            =false --> Ne cree que les repertoires qui contienet des
	 *            fichiers
	 * @return le nombre de fichiers copies et repertoires crees
	 * @throws IOException
	 */
	public int moveTo(final CXFileDir aDstDir, final FileFilter aFilter,
			final boolean aDeleteExinstingFiles, final boolean aCreateEmptyDir)
			throws IOException {

		if (!exists()) {
			return 0;
		}

		if (!aDstDir.exists()) {
			aDstDir.mkdirs();
		}

		int wRes = 0;
		final ArrayList<File> wMyFiles = getMyFiles(aFilter, true);
		final Iterator<File> wIt = wMyFiles.iterator();
		while (wIt.hasNext()) {
			final File wFile = wIt.next();
			if (wFile.isFile()) {
				((CXFile) wFile).moveTo(aDstDir, wFile.getName(),
						aDeleteExinstingFiles);
				wRes++;
			} else if (wFile.isDirectory()) {
				wRes += ((CXFileDir) wFile).moveTo(
						new CXFileDir(aDstDir.getAbsolutePath(), wFile
								.getName()), aFilter, aDeleteExinstingFiles,
						aCreateEmptyDir);
			}
		}
		// Suppression si dossier source si vide
		if (wRes == 0 && !aCreateEmptyDir) {
			aDstDir.delete();
		}
		// Suppression si aucun fichier et sous-dossier - Sinon on conserve le
		// dossier -> Cas d'un filtre no null
		if (this.isEmpty()) {
			this.delete();
		}

		return wRes;
	}

	/**
	 * Instancie un objet fichier - CAdminFile et classes derivees
	 */
	protected CXFile newFile(final String aPath,
			final boolean aInstanciateTxtFiles) {

		if (aInstanciateTxtFiles) {
			return new CXFileText(aPath);
		} else {
			return new CXFile(aPath);
		}
	}

	/**
	 * Instancie un objet repertoire - CAdminFileDir et classes derivees
	 *
	 * @param aPath
	 * @return
	 */
	protected CXFileDir newFileDir(final String aPath) {

		return new CXFileDir(aPath);
	}

	/**
	 *
	 * @param aXFileProcessor
	 * @return
	 * @throws IOException
	 */
	public boolean processAllFiles(final IXFileProcessor aXFileProcessor)
			throws IOException {

		return processAllFiles(aXFileProcessor, WITH_DIRS, NO_FILTER);
	}

	/**
	 *
	 * @param aList
	 * @param aFilter
	 * @param aWithSubDirs
	 * @param aXFileProcessor
	 * @return
	 * @throws IOException
	 */
	protected boolean processAllFiles(final IXFileProcessor aXFileProcessor,
			final boolean aWithSubDirs, final FileFilter aFilter)
			throws IOException {

		boolean wContinue = true;

		final Iterator<File> wIt = getMyFiles(aFilter, WITH_DIRS,
				!WITH_TEXTFILE).iterator();
		while (wContinue && wIt.hasNext()) {
			final File wFile = wIt.next();

			wContinue = aXFileProcessor.processFile(wFile);
			if (wFile.isDirectory() && aWithSubDirs) {
				((CXFileDir) wFile).processAllFiles(aXFileProcessor,
						aWithSubDirs, aFilter);
			}
		}
		return wContinue;
	}

	/**
	 *
	 * Supprime le repertoire (et son contenu) - Recursif
	 *
	 * @return
	 * @throws IOException
	 */
	public int remove() throws IOException {

		return remove(REMOVE_ME, NO_FILTER);
	}

	/**
	 *
	 * @param aRemoveMe
	 *            remove the directory if true
	 * @return the number of removed file
	 * @throws IOException
	 */
	private int remove(final boolean aRemoveMe, final FileFilter aFileFilter)
			throws IOException {

		int wNbFile = 0;
		if (exists()) {
			final Iterator<File> wIt = getMyFiles(aFileFilter, WITH_DIRS)
					.iterator();
			while (wIt.hasNext()) {
				final File wFile = wIt.next();
				if (wFile.isFile()) {
					if (aFileFilter == null || aFileFilter.accept(wFile)) {
						try {
							// MOD_OG_20160719
							// Use Files.delete() rather than File.delete() to
							// get an explicit exception to diagnose easily the
							// troubles
							Files.delete(wFile.toPath());
						} catch (Exception e) {
							throw new IOException(
									String.format(
											"Unable to delete file [%s]. canWrite=[%b]. isFileEmpty=[%b]",
											getAbsolutePath(), canWrite(),
											((CXFile) wFile).isEmpty()), e);
						}
						wNbFile++;
					}
				} else if (wFile.isDirectory()) {
					wNbFile += ((CXFileDir) wFile).remove(true, aFileFilter);
				}
			}
			if (aRemoveMe) {
				try {
					// MOD_OG_20160719
					// Use Files.delete() rather than File.delete() to get an
					// explicit exception to diagnose easily the troubles
					Files.delete(this.toPath());
				} catch (Exception e) {
					throw new IOException(
							String.format(
									"Unable to delete dir [%s]. canWrite=[%b]. isDirEmpty=[%b]",
									getAbsolutePath(), canWrite(), isEmpty()),
									e);
				}
				wNbFile++;
			}
		}
		return wNbFile;
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAll() throws Exception {

		return scanAll(null, null, true, false);
	}

	/**
	 *
	 * @param aList
	 * @param aFilter
	 * @param aSubDirs
	 * @param aInstanciateTxtFiles
	 * @return
	 * @throws IOException
	 */
	@Override
	public CXSortListFiles scanAll(CXSortListFiles aList,
			final FileFilter aFilter, final boolean aSubDirs,
			final boolean aInstanciateTxtFiles) throws IOException {

		if (aList == null) {
			aList = new CXSortListFiles();
		}
		final Iterator<File> wIt = getMyFiles(aFilter, true,
				aInstanciateTxtFiles).iterator();
		while (wIt.hasNext()) {
			final File wFile = wIt.next();

			aList.add(wFile);
			if (wFile.isDirectory() && aSubDirs) {
				((CXFileDir) wFile).scanAll(aList, aFilter, aSubDirs,
						aInstanciateTxtFiles);
			}

		}
		return aList;
	}

	/**
	 *
	 * @param aFilter
	 * @param aSubDirs
	 * @return
	 * @throws IOException
	 */
	public CXSortListFiles scanAll(final FileFilter aFilter,
			final boolean aSubDirs) throws IOException {

		return scanAll(null, aFilter, aSubDirs, false);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public CXSortListFiles scanAllDirs() throws IOException {

		return scanAllDirs(null, null, true, true);
	}

	/**
	 *
	 * @param aSubDirs
	 * @return
	 * @throws IOException
	 */
	public CXSortListFiles scanAllDirs(final boolean aSubDirs)
			throws IOException {

		return scanAllDirs(null, null, aSubDirs, true);
	}

	/**
	 *
	 * @param aList
	 * @param aSubDirs
	 * @return
	 * @throws IOException
	 */
	public CXSortListFiles scanAllDirs(final CXSortListFiles aList,
			final boolean aSubDirs) throws IOException {

		return scanAllDirs(aList, null, aSubDirs, true);
	}

	/**
	 *
	 * @param aList
	 * @param aFilter
	 * @param aSubDirs
	 * @param aInstanciateTxtFiles
	 * @return
	 * @throws IOException
	 */
	@Override
	public CXSortListFiles scanAllDirs(CXSortListFiles aList,
			final FileFilter aFilter, final boolean aSubDirs,
			final boolean aInstanciateTxtFiles) throws IOException {

		if (aList == null) {
			aList = new CXSortListFiles();
		}
		final Iterator<File> wIt = getMyFiles(aFilter, true,
				aInstanciateTxtFiles).iterator();
		while (wIt.hasNext()) {
			final File wFile = wIt.next();
			if (wFile.isDirectory()) {
				aList.add(wFile);
				if (aSubDirs) {
					((CXFileDir) wFile).scanAllDirs(aList, aFilter, aSubDirs,
							aInstanciateTxtFiles);
				}
			}
		}
		return aList;
	}

	/**
	 *
	 * @param aFilter
	 * @param aSubDirs
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAllDirs(final FileFilter aFilter,
			final boolean aSubDirs) throws Exception {

		return scanAllDirs(null, aFilter, aSubDirs, true);
	}

	/**
	 * Renvoie la liste de tous les Fichiers (CAdminFile) et dossiers - Recursif
	 * Utilise si pas de lecture/ecriture sur les fichiers
	 *
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAllFiles() throws Exception {

		return scanAllFiles(null, null, true, false);
	}

	/**
	 *
	 * @param aWithSubDirs
	 *            si False --> Ne prend pas en compte les sous-repertoires
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAllFiles(final boolean aWithSubDirs)
			throws Exception {

		return scanAllFiles(null, null, aWithSubDirs, false);
	}

	/**
	 * Renvoie la liste des Fichier et dossiers - Recursif aFilter --> Filtre
	 * sur les fichiers - null authorise aSubDirs --> False --> Ne prend pas en
	 * compte les sous-repertoires aInstanciateTxtFiles --> True instancie un
	 * CAdminTextFile (avec methode de lecture/ecriture)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.psem2m.utilities.files.IXFilesContainer#scanAllFiles(org.psem2m.utilities
	 * .files.CXSortListFiles, java.io.FileFilter, boolean, boolean)
	 */
	@Override
	public CXSortListFiles scanAllFiles(CXSortListFiles aList,
			final FileFilter aFilter, final boolean aSubDirs,
			final boolean aInstanciateTxtFiles) throws Exception {

		if (aList == null) {
			aList = new CXSortListFiles();
		}

		final Iterator<File> wIt = getMyFiles(aFilter, true,
				aInstanciateTxtFiles).iterator();
		while (wIt.hasNext()) {
			final File wFile = wIt.next();
			if (wFile.isFile()) {
				aList.add(wFile);
			} else if (wFile.isDirectory() && aSubDirs) {
				if (hasScanListener()) {
					((CXFileDir) wFile).setScanListner(getScanListener());
				}
				((CXFileDir) wFile).setScanLevel(getScanLevel() + 1);
				((CXFileDir) wFile).scanAllFiles(aList, aFilter, aSubDirs,
						aInstanciateTxtFiles);
			}
		}
		if (hasScanListener() && isScanLevelZero()) {
			getScanListener().listenEndScan();
		}
		return aList;
	}

	/**
	 * Renvoie la liste des Fichier (CAdminFile) et dossiers -
	 *
	 * Recursif Utilise si pas de lecture/ecriture sur les fichiers aFilter -->
	 * Filtre sur les fichiers - null authorise aSubDirs --> False --> Ne prend
	 * pas en compte les sous-repertoires
	 *
	 * @param aFilter
	 * @param aSubDirs
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAllFiles(final FileFilter aFilter,
			final boolean aSubDirs) throws Exception {

		return scanAllFiles(null, aFilter, aSubDirs, false);
	}

	/**
	 * Renvoie la liste des Fichier (CAdminFile) et dossiers -
	 *
	 * Recursif Utilise si pas de lecture/ecriture sur les fichiers
	 * aListeExtensions --> Liste des extension separees par ';' - null
	 * authorise aSubDirs --> False --> Ne prend pas en compte les
	 * sous-repertoires
	 *
	 * @param aListeExtensions
	 * @param aSubDirs
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAllFiles(final String aListeExtensions,
			final boolean aSubDirs) throws Exception {

		if (aListeExtensions != null) {
			return scanAllFiles(getFilterExtension(aListeExtensions), aSubDirs);
		} else {
			return scanAllFiles((FileFilter) null, aSubDirs);
		}
	}

	/**
	 * Renvoie la liste de tous les Fichiers (CAdminFileText) et dossiers -
	 * Recursif Utilise si lecture/ecriture sur les fichiers
	 *
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAllTextFiles() throws Exception {

		return scanAllFiles(null, null, true, true);
	}

	/**
	 * Renvoie la liste des Fichier (CAdminFileText) et dossiers - Recursif
	 * Utilise si lecture/ecriture sur les fichiers aFilter --> Filtre sur les
	 * fichiers - null authorise aSubDirs --> False --> Ne prend pas en compte
	 * les sous-repertoires
	 *
	 * @param aFilter
	 * @param aSubDirs
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAllTextFiles(final FileFilter aFilter,
			final boolean aSubDirs) throws Exception {

		return scanAllFiles((CXSortListFiles) null, aFilter, aSubDirs, true);
	}

	/**
	 * Renvoie la liste des Fichier (CAdminFileText) et dossiers - Recursif
	 * Utilise si pas de lecture/ecriture sur les fichiers aListeExtensions -->
	 * Liste des extension separees par ';' - null authorise aSubDirs --> False
	 * --> Ne prend pas en compte les sous-repertoires
	 *
	 * @param aListeExtensions
	 * @param aSubDirs
	 * @return
	 * @throws Exception
	 */
	public CXSortListFiles scanAllTextFiles(final String aListeExtensions,
			final boolean aSubDirs) throws Exception {

		if (aListeExtensions != null) {
			return scanAllTextFiles(getFilterExtension(aListeExtensions),
					aSubDirs);
		} else {
			return scanAllTextFiles((FileFilter) null, aSubDirs);
		}
	}

	/**
	 * @param aScanLevel
	 */
	private void setScanLevel(final int aScanLevel) {

		pScanLevel = aScanLevel;
	}

	/**
	 * @param aDirScanListener
	 */
	public void setScanListner(final IXDirScanListener aDirScanListener) {

		pDirScanListener = aDirScanListener;
	}
}
