package org.psem2m.utilities.rsrc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * Class fournisseur de ressource fichier
 *
 * @author ogattaz
 *
 */
public class CXRsrcProviderFile extends CXRsrcProvider {

	public static final String NETWORK_DRIVE = "//";
	public static final String URL_FILE = "file";

	// Utiliser pour les newtwork drives (\\fdalbo\temp)
	private String pAddress = null;

	private final AtomicBoolean pContinueWatching = new AtomicBoolean(true);

	CXFileDir pDefaultFileDir = null;

	private ExecutorService pExecutorService;

	IActivityLogger pLogger;

	private IRsrcNotifierHandler pNotifierHandler;

	WatchService pWatchService;

	/**
	 * @param aDefaultPath
	 * @param aDefCharset
	 * @throws Exception
	 */
	public CXRsrcProviderFile(final CXFileDir aDefaultPath, final Charset aDefCharset) throws Exception {
		this(aDefaultPath == null ? null : aDefaultPath.getAbsolutePath(), aDefCharset);
	}

	/**
	 * @param aProv
	 */
	protected CXRsrcProviderFile(final CXRsrcProviderFile aProv) {
		super(aProv);
		if (aProv != null) {
			pAddress = aProv.pAddress;
		}
	}

	/**
	 * @param aDefaultPath
	 * @param aDefCharset
	 * @throws Exception
	 */
	public CXRsrcProviderFile(final String aDefaultPath, final Charset aDefCharset) throws Exception {
		this(aDefaultPath, aDefCharset, null, CActivityLoggerNull.getInstance());
	}

	public CXRsrcProviderFile(final String aDefaultPath, final Charset aDefCharset,
			final IRsrcNotifierHandler aNotifierHandler, final IActivityLogger aLogger) throws Exception {
		super(aDefCharset);
		setDefaultDirectoryCheck(aDefaultPath);
		pLogger = aLogger;
		if (aNotifierHandler != null) {
			pLogger.logInfo(this, "CXRsrcProviderFile<construct>", "create executorService for watching directory");
			pExecutorService = Executors.newFixedThreadPool(1, new ThreadFactory() {

				@Override
				public Thread newThread(final Runnable aRunnable) {
					// TODO Auto-generated method stub
					return new Thread(aRunnable, String.format("WatcherService-CRsrcProviderFile-%d", hashCode()));
				}
			});
			// create a watcherService
			pNotifierHandler = aNotifierHandler;
			pWatchService = FileSystems.getDefault().newWatchService();
			Path pPath = Paths.get(getDefDirectory().getPath());
			pPath.register(pWatchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_DELETE);

			pExecutorService.submit(new Runnable() {

				@Override
				public void run() {
					// check with a interval the modification on file in
					// directory using a watcher
					pLogger.logInfo(this, "run", "watch directory %s", getDefDirectory().getPath());
					while (pContinueWatching.get()) {
						try {

							WatchKey wKey;
							try {
								wKey = pWatchService.take();
							} catch (InterruptedException wE) {
								return;
							}
							if (wKey != null) {
								for (WatchEvent<?> wEvent : wKey.pollEvents()) {

									pNotifierHandler.handle(wEvent.kind(), getDefDirectory().getPath()
											+ File.separatorChar + wEvent.context().toString());
								}
							}
							boolean valid = wKey.reset();
							if (valid) {
								break;
							}
						} catch (Exception e) {
							pLogger.logSevere(this, "run", "Error watcher thread %s", e);
							pContinueWatching.set(false);
						}
					}
				}
			});
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.rsrc.CXRsrcProvider#addDescriptionInBuffer(java.
	 * lang.Appendable)
	 */
	@Override
	public Appendable addDescriptionInBuffer(final Appendable aSB) {
		return super.addDescriptionInBuffer(aSB);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.rsrc.CXRsrcProvider#clone()
	 */
	@Override
	public CXRsrcProviderFile clone() {
		return new CXRsrcProviderFile(this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.psem2m.utilities.rsrc.CXRsrcProvider#existsFulPath(org.psem2m.utilities
	 * .rsrc.CXRsrcUriPath)
	 */
	@Override
	protected boolean existsFulPath(final CXRsrcUriPath aPath) {
		try {
			URLConnection wCnx = openConnection(urlNew(aPath));
			wCnx.connect();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean getContinue() {
		return pContinueWatching.get();
	}

	public CXFileDir getDefaultDirectory() {
		return pDefaultFileDir;
	}

	@Override
	protected String getDirAbsPathDirectory(CXRsrcUriPath aPath) {
		CXFileDir wDir = new CXFileDir(aPath.getParent().getPath());

		return wDir.getAbsolutePath();
	}

	@Override
	protected List<String> getListPathDirectory(CXRsrcUriPath aPath, final Pattern aPattern) {
		CXFileDir wDir = new CXFileDir(aPath.getParent().getPath());
		List<String> wPaths = Arrays.asList(wDir.list(new FilenameFilter() {

			@Override
			public boolean accept(final File dir, final String name) {
				Matcher wMatch = aPattern.matcher(name);
				return wMatch.find();
			}
		}));
		return wPaths;
	}

	/*
	 * True si acces fichier en local du serveur - False si access remote (http)
	 *
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.rsrc.CXRsrcProvider#isLocal()
	 */
	@Override
	public boolean isLocal() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.rsrc.CXRsrcProvider#openConnection(java.net.URL)
	 */
	@Override
	protected URLConnection openConnection(final URL aUrl) throws IOException {
		return super.openConnection(aUrl);
	}

	@Override
	protected CXListRsrcText rsrcReadTxts(final CXRsrcUriPath aPath, Map<String, String> aFullPath,
			final long aTimeStamp, final boolean aForceSecondes, final boolean aFulPath) throws Exception {
		CXRsrcText wRsrc = null;
		CXRsrcUriPath wPath = null;
		URL wUrl = null;
		CXListRsrcText wListRsrc = new CXListRsrcText();

		try {
			wPath = checkUriPath(aPath, aFulPath);
			if (wPath.getFullPath().contains("*")) {

				// look to list of file in that directory
				List<String> wPaths = getDefaultDirectory().scanAllAsListString(aPath.getUriPath());

				String wParentPath = getDefaultDirectory().getAbsolutePath();
				if (wPaths != null) {
					for (String aSubFilePath : wPaths) {
						wListRsrc.add(
								readRsrcTextContent(new CXRsrcUriPath(wParentPath + File.separatorChar + aSubFilePath),
										aFullPath, aTimeStamp, aForceSecondes));

					}
				}
			} else {
				wRsrc = readRsrcTextContent(wPath, aFullPath, aTimeStamp, aForceSecondes);
				wListRsrc.add(wRsrc);
			}
		} catch (Exception e) {
			if (hasNext()) {
				wListRsrc.add(next().rsrcReadTxt(aPath, aFullPath, aTimeStamp, aForceSecondes, aFulPath));
				return wListRsrc;
			}
			throwExcepReadText("Unable to read "
					+ ((wPath == null) ? "null" : (wUrl == null) ? aPath.getFullPath() : wUrl.toString()), e);
		}
		return wListRsrc;
	}

	public void setContinue(final boolean aContinue) {
		pContinueWatching.set(aContinue);
	}

	/**
	 * Surchargee pour check
	 *
	 * @param aDefaultPath
	 * @throws Exception
	 */
	public void setDefaultDirectoryCheck(final String aDefaultPath) throws Exception {
		String wDefDir = null;
		String wInput = aDefaultPath == null ? null : aDefaultPath.trim();
		if (wInput != null && wInput.length() != 0) {
			File wFile = new File(wInput);
			if (!wFile.isDirectory()) {
				throwExcepBadDir(aDefaultPath);
			}
			if (!wFile.exists()) {
				throwExcepDirNotFound(aDefaultPath);
			}
			// System.out.println(wFile.toURI().toURL().toString());
			wDefDir = wFile.toURI().toURL().getPath();
		} else {
			throwExcepDirEmpty();
		}
		// Dans le cas d'un network drive l'url doit etre file://fdalbo/d$/temp
		// -> La classe CXtdRsrcUrlDir qui represente un path vers un repertoire
		// supprime le double / (/fdalbo/d$/temp)
		// -> On met a jour pAddress avec '/' pour le rajouter lorsqu'on
		// construit le path complet de l'url avec la method abstraite
		// urlGetAddress
		if (wDefDir.startsWith(NETWORK_DRIVE)) {
			pAddress = CXRsrcUriDir.SEPARATOR_STR;
		}
		super.setDefaultDirectory(wDefDir);
		pDefaultFileDir = new CXFileDir(aDefaultPath);
	}

	/**
	 * @param aPath
	 * @throws Exception
	 */
	protected void throwExcepBadDir(final String aPath) throws Exception {
		throw new Exception("Bad directory path [" + aPath + "]");
	}

	/**
	 * @throws Exception
	 */
	protected void throwExcepDirEmpty() throws Exception {
		throw new Exception("Empty directory path");
	}

	/**
	 * Pur surcharge des exceptions
	 *
	 * @param aPath
	 * @throws Exception
	 */
	protected void throwExcepDirNotFound(final String aPath) throws Exception {
		throw new Exception("Directory not found [" + aPath + "]");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.rsrc.CXRsrcProvider#urlGetAddress()
	 */
	@Override
	public String urlGetAddress() {
		return pAddress;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.psem2m.utilities.rsrc.CXRsrcProvider#urlNew(org.psem2m.utilities.
	 * rsrc.CXRsrcUriPath)
	 */
	@Override
	protected URL urlNew(final CXRsrcUriPath aPath) throws MalformedURLException {
		return new URL(URL_FILE, "", aPath.getUrlStr(urlGetAddress()));
	}
}
