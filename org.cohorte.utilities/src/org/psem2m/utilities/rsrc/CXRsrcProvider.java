package org.psem2m.utilities.rsrc;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.psem2m.utilities.files.CXFileDir;
import org.psem2m.utilities.scripting.CXJsObjectBase;

/**
 * #12 Manage chains of resource providers
 *
 * Class Ressource provider
 *
 * @author ogattaz
 *
 */
public abstract class CXRsrcProvider extends CXJsObjectBase implements Iterator<CXRsrcProvider>, Cloneable {

	private int pCacheExpires = 0;
	private int pConnectTimeoutMs = 0;
	private CXRsrcUriDir pDefaultDirectory = new CXRsrcUriDir("");
	private Charset pDefCharset = null;
	// #12 Manage chains of resource providers
	private CXRsrcProvider pNext;
	private int pReadTimeoutMs = 0;

	public CXRsrcProvider(final Charset aDefCharset) {
		this(0, 0, aDefCharset);
	}

	/**
	 * @param aProv
	 */
	protected CXRsrcProvider(final CXRsrcProvider aProv) {
		super();
		if (aProv != null) {
			pReadTimeoutMs = aProv.pReadTimeoutMs;
			pConnectTimeoutMs = aProv.pConnectTimeoutMs;
			pDefaultDirectory = aProv.pDefaultDirectory.clone();
			pCacheExpires = aProv.pCacheExpires;
			pDefCharset = aProv.pDefCharset;
		}
	}

	/**
	 * @param aReadTimeOutMs
	 * @param aConnectTimeOutMs
	 * @param aDefCharset
	 */
	public CXRsrcProvider(final int aReadTimeOutMs, final int aConnectTimeOutMs, final Charset aDefCharset) {
		this(aReadTimeOutMs, aConnectTimeOutMs, null, aDefCharset);
	}

	/**
	 * @param aReadTimeOutMs
	 * @param aConnectTimeOutMs
	 * @param aDir
	 * @param aDefCharset
	 */
	public CXRsrcProvider(final int aReadTimeOutMs, final int aConnectTimeOutMs, final CXRsrcUriDir aDir,
			final Charset aDefCharset) {
		pDefCharset = aDefCharset == null ? Charset.defaultCharset() : aDefCharset;
		pReadTimeoutMs = aReadTimeOutMs;
		pConnectTimeoutMs = aConnectTimeOutMs;
		setDefaultDirectory(aDir);
	}

	/**
	 * #12 Manage chains of resource providers
	 *
	 * @param aNext
	 */
	public void add(final CXRsrcProvider aNext) {
		if (hasNext()) {
			next().setNext(aNext);
		} else {
			setNext(aNext);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.CXJsObjectBase#addDescriptionInBuffer(
	 * java.lang.Appendable)
	 */
	@Override
	public Appendable addDescriptionInBuffer(Appendable aSB) {
		aSB = aSB == null ? new StringBuilder(1024) : aSB;
		descrAddProp(aSB, "DefaultDirectory", pDefaultDirectory.getPath());
		descrAddProp(aSB, "ReadTimeout", String.valueOf(pReadTimeoutMs));
		descrAddProp(aSB, "ConnectTimeout", String.valueOf(pConnectTimeoutMs));
		descrAddProp(aSB, "CacheExpires", pCacheExpires);
		// #12 Manage chains of resource providers
		descrAddProp(aSB, "hasNext", hasNext());
		if (hasNext()) {
			descrAddText(aSB, "\n");
			pNext.addDescriptionInBuffer(aSB);
		}
		return aSB;
	}

	/**
	 * @return
	 */
	public boolean cacheExpires() {
		return pCacheExpires > 0;
	}

	/**
	 * @return
	 */
	public int cacheGetExpiresSec() {
		return pCacheExpires;
	}

	/**
	 * @return
	 */
	public boolean cacheIsNoCache() {
		return pCacheExpires == 0;
	}

	/**
	 * @return
	 */
	public boolean cacheNeverExpires() {
		return pCacheExpires < 0;
	}

	/**
	 * @param aRsrc
	 * @return
	 * @throws java.io.IOException
	 */
	public boolean checkTimeStamp(final CXRsrc<?> aRsrc) throws java.io.IOException {
		if (aRsrc == null) {
			return true;
		}
		return aRsrc.getTimeStampSyst() == getTimeStamp(aRsrc);
	}

	/**
	 * @param aPath
	 * @param aFulPath
	 * @return
	 * @throws Exception
	 */
	private CXRsrcUriPath checkUriPath(final CXRsrcUriPath aPath, final boolean aFulPath) throws Exception {
		if (aPath == null || !aPath.isValid()) {
			throw new Exception("Unable to check a" + (aPath == null ? "Null" : "empty") + " resource path");
		}
		if (!aPath.hasName()) {
			throw new Exception("Unable to check a resource path having no name [" + aPath.getFullPath() + "]");
		}
		if (!aFulPath && !pDefaultDirectory.isEmpty()) {
			if (aPath.getFullPath().startsWith("/file:/")) {
				return new CXRsrcUriPath(pDefaultDirectory, aPath.getFullPath().substring(7));
			} else {
				return new CXRsrcUriPath(pDefaultDirectory, aPath);
			}

		}
		return aPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract CXRsrcProvider clone();

	/**
	 * @param aCnx
	 * @return
	 */
	protected String connectionToString(final URLConnection aCnx) {
		StringBuilder wBuf = new StringBuilder();
		wBuf.append("URL [").append(aCnx.getURL().toString()).append("\n");
		wBuf.append("ModifiedSince [").append(String.valueOf(aCnx.getIfModifiedSince())).append("]\n");
		wBuf.append("Expiration [").append(String.valueOf(aCnx.getExpiration())).append("]\n");
		wBuf.append("LastModified [").append(String.valueOf(aCnx.getLastModified())).append("]\n");
		wBuf.append("UseCache [").append(String.valueOf(aCnx.getUseCaches())).append("]\n");
		wBuf.append("ReadTimeout [").append(String.valueOf(aCnx.getReadTimeout())).append("]\n");
		wBuf.append("ConnectTimeout [").append(String.valueOf(aCnx.getConnectTimeout())).append("]\n");
		wBuf.append("Encoding [").append(aCnx.getContentEncoding()).append("]\n");
		wBuf.append("ContentLength [").append(String.valueOf(aCnx.getContentLength())).append("]\n");
		wBuf.append("HEADER\n");
		try {
			Iterator<Entry<String, List<String>>> wIt = aCnx.getHeaderFields().entrySet().iterator();
			while (wIt.hasNext()) {
				Entry<String, List<String>> wEntry = wIt.next();
				wBuf.append("  ");
				if (wEntry.getKey() != null) {
					wBuf.append(wEntry.getKey());
				}
				Iterator<String> wIt1 = wEntry.getValue().iterator();
				while (wIt1.hasNext()) {
					wBuf.append("[").append(wIt1.next()).append("]");
				}
				wBuf.append("\n");
			}
		} catch (Exception e) {
			wBuf.append("Error [").append(e.getMessage()).append("]\n");
		}
		wBuf.append("PROPERTIES\n");
		try {
			Iterator<Entry<String, List<String>>> wIt = aCnx.getRequestProperties().entrySet().iterator();
			while (wIt.hasNext()) {
				Entry<String, List<String>> wEntry = wIt.next();
				wBuf.append("  ");
				if (wEntry.getKey() != null) {
					wBuf.append(wEntry.getKey());
				}
				Iterator<String> wIt1 = wEntry.getValue().iterator();
				while (wIt1.hasNext()) {
					wBuf.append("[").append(wIt1.next()).append("]");
				}
				wBuf.append("\n");
			}
		} catch (Exception e) {
			wBuf.append("Error [").append(e.getMessage()).append("]\n");
		}
		return wBuf.toString();
	}

	/**
	 * @param aPath
	 *            Chemin par rapport au repertoire par defaut
	 * @return
	 */
	public boolean existsDef(final String aPath) {
		return existsFulPath(
				pDefaultDirectory.isEmpty() ? new CXRsrcUriPath(aPath) : new CXRsrcUriPath(pDefaultDirectory, aPath));
	}

	/**
	 * @param aPath
	 *            Full path
	 * @return
	 */
	protected abstract boolean existsFulPath(CXRsrcUriPath aPath);

	/**
	 * @return
	 */
	public int getConnectTimeout() {
		return pConnectTimeoutMs;
	}

	/**
	 * @return
	 */
	public Charset getDefCharset() {
		return pDefCharset;
	}

	/**
	 * @return
	 */
	public CXRsrcUriDir getDefDirectory() {
		return pDefaultDirectory;
	}

	protected abstract String getDirAbsPathDirectory(CXRsrcUriPath aPath);

	protected abstract List<String> getListPathDirectory(CXRsrcUriPath aPath, final Pattern aPattern);

	/**
	 * @return
	 */
	public int getReadTimeout() {
		return pReadTimeoutMs;
	}

	/**
	 * Lit le timestamp courant de aRsrc
	 *
	 * @param aRsrc
	 * @return 0 si aRsrc
	 * @throws java.io.IOException
	 */
	public long getTimeStamp(final CXRsrc<?> aRsrc) throws java.io.IOException {
		if (aRsrc == null) {
			return 0;
		}
		URL wUrl = urlNew(aRsrc.getPath());
		URLConnection wCnx = openConnection(wUrl);
		return wCnx.getLastModified();
	}

	/**
	 * @param aPath
	 * @return
	 */
	public String getUrlStrDef(final String aPath) {
		return aPath == null ? null : new CXRsrcUriPath(pDefaultDirectory, aPath).getUrlStr(urlGetAddress());
	}

	/**
	 * #12 Manage chains of resource providers
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return (pNext != null);
	}

	/**
	 * @return True si acces fichier en local du serveur - False si access remote
	 *         (http)
	 */
	public abstract boolean isLocal();

	/**
	 * @return
	 */
	public boolean isValid() {
		return true;
	}

	/**
	 * #12 Manage chains of resource providers
	 *
	 * @see java.util.Iterator#next()
	 */
	@Override
	public CXRsrcProvider next() {
		if (!hasNext()) {
			throw new NoSuchElementException("no next provider available");
		}
		return pNext;
	}

	/**
	 * @param aUrl
	 * @return
	 * @throws java.io.IOException
	 */
	protected URLConnection openConnection(final URL aUrl) throws java.io.IOException {
		URLConnection wCnx = aUrl.openConnection();
		// Indication de lecture seule
		wCnx.setDoInput(true);
		wCnx.setDoOutput(false);
		if (pReadTimeoutMs != 0) {
			wCnx.setReadTimeout(pReadTimeoutMs);
		}
		if (pConnectTimeoutMs != 0) {
			wCnx.setConnectTimeout(pConnectTimeoutMs);
		}

		return wCnx;
	}

	public void purgeCache() {
		// nothn
	}

	protected CXRsrcText readRsrcTextContent(final CXRsrcUriPath aPath, Map<String, String> aFullPath, long aTimeStamp,
			final boolean aForceSecondes) throws Exception {
		URL wUrl = null;
		URLConnection wCnx = null;
		boolean wCheckTimeStamp = aTimeStamp > 0;
		wUrl = urlNew(aPath);
		wCnx = openConnection(wUrl);
		long wCurTimeStamp = wCnx.getLastModified();
		if (aForceSecondes) {
			wCurTimeStamp = (wCurTimeStamp / 1000) * 1000;
			aTimeStamp = (aTimeStamp / 1000) * 1000;
		}
		wCheckTimeStamp = wCheckTimeStamp && wCurTimeStamp > 0;
		if (wCheckTimeStamp && aTimeStamp == wCurTimeStamp) {
			return null;
		} else {
			// X3 n'ecrit pas le BOM -> On precise l'encoding - Toujours
			// Utf8
			CXRsrcTextReadInfo wInfo = CXRsrcTextUnicodeReader.readAll(wCnx, pDefCharset);
			CXRsrcText wRsrc = new CXRsrcText(aPath, wInfo, wCurTimeStamp);
			return wRsrc;
		}
	}

	/*
	 * #12 Manage chains of resource providers
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("the removing of the next resource provider isn't supported");

	}

	/**
	 * Lecture d'une ressource deja lue -> est un fullpath et non pas un path par
	 * rapport au repertoire par defaut
	 *
	 * @param aRsrc
	 * @param aCheckTimeStamp
	 * @return
	 * @throws Exception
	 */
	public CXRsrc<?> rsrcRead(final CXRsrc<?> aRsrc, Map<String, String> aFullPath, final boolean aCheckTimeStamp)
			throws Exception {
		assert aRsrc != null : "Null resource";
		if (aRsrc.isText()) {
			return rsrcReadTxt(aRsrc.getPath(), aFullPath, aCheckTimeStamp ? aRsrc.getTimeStampSyst() : 0, false, true);
		} else {
			return rsrcReadByte(aRsrc.getPath(), aCheckTimeStamp ? aRsrc.getTimeStampSyst() : 0, false, true);
		}
	}

	public CXRsrc<?> rsrcRead(final CXRsrcUriPath aPath, final long aTimeStamp, final boolean aForceSecondes)
			throws Exception {
		return rsrcRead(aPath, null, aTimeStamp, aForceSecondes, false);
	}

	/**
	 * Lecture text ou byte en fonction du mime-type
	 *
	 * @param aRsrcPath
	 * @return
	 * @throws Exception
	 */
	public CXRsrc<?> rsrcRead(final CXRsrcUriPath aRsrcPath, Map<String, String> aFullPath) throws Exception {
		return rsrcRead(aRsrcPath.getFullPath(), aFullPath, 0);
	}

	/**
	 * @param aPath
	 * @param aTimeStamp
	 * @param aForceSecondes
	 * @return
	 * @throws Exception
	 */
	public CXRsrc<?> rsrcRead(final CXRsrcUriPath aPath, Map<String, String> aFullPath, final long aTimeStamp,
			final boolean aForceSecondes) throws Exception {
		return rsrcRead(aPath, aFullPath, aTimeStamp, aForceSecondes, false);
	}

	/**
	 * @param aPath
	 * @param aTimeStamp
	 * @param aForceSecondes
	 * @param aFulPath
	 * @return
	 * @throws Exception
	 */
	private CXRsrc<?> rsrcRead(final CXRsrcUriPath aPath, Map<String, String> aFullPath, final long aTimeStamp,
			final boolean aForceSecondes, final boolean aFulPath) throws Exception {
		if (aPath != null) {
			CXMimeType wMime = aPath.getMimeType();
			if (wMime != null && wMime.isText()) {
				return rsrcReadTxt(aPath, aFullPath, aTimeStamp, aForceSecondes, aFulPath);
			} else {
				return rsrcReadByte(aPath, aTimeStamp, aForceSecondes, aFulPath);
			}
		} else {
			throw new Exception((aPath == null ? "Null" : "empty") + " resource path");
		}
	}

	public CXRsrc<?> rsrcRead(final String aRsrcPath) throws Exception {
		return rsrcRead(aRsrcPath, null, 0);
	}

	public CXRsrc<?> rsrcRead(final String aRsrcPath, final long aTimeStampSyst) throws Exception {
		return rsrcRead(aRsrcPath, null, aTimeStampSyst, false);
	}

	public CXRsrc<?> rsrcRead(final String aRsrcPath, final long aTimeStampSyst, final boolean aForceSecond)
			throws Exception {
		return rsrcRead(new CXRsrcUriPath(aRsrcPath), null, aTimeStampSyst, aForceSecond);
	}

	/**
	 * Lecture text ou byte en fonction du mime-type
	 *
	 * @param aRsrcPath
	 * @return
	 * @throws Exception
	 */
	public CXRsrc<?> rsrcRead(final String aRsrcPath, Map<String, String> aFullPath) throws Exception {
		return rsrcRead(aRsrcPath, aFullPath, 0);
	}

	/**
	 * Lecture text ou byte en fonction du mime-type Path = String et Timestamp (>0
	 * --> Check - <=0 no check)
	 *
	 * @param aRsrcPath
	 * @param aTimeStampSyst
	 * @return
	 * @throws Exception
	 */
	public CXRsrc<?> rsrcRead(final String aRsrcPath, Map<String, String> aFullPath, final long aTimeStampSyst)
			throws Exception {
		return rsrcRead(aRsrcPath, aFullPath, aTimeStampSyst, false);
	}

	/**
	 * @param aRsrcPath
	 * @param aTimeStampSyst
	 * @param aForceSecond
	 * @return
	 * @throws Exception
	 */
	public CXRsrc<?> rsrcRead(final String aRsrcPath, Map<String, String> aFullPath, final long aTimeStampSyst,
			final boolean aForceSecond) throws Exception {
		return rsrcRead(new CXRsrcUriPath(aRsrcPath), aFullPath, aTimeStampSyst, aForceSecond);
	}

	/**
	 * @param aRsrcPath
	 * @return
	 * @throws Exception
	 */
	public CXRsrcByte rsrcReadByte(final CXRsrcUriPath aRsrcPath) throws Exception {
		return rsrcReadByte(aRsrcPath.getFullPath(), 0);
	}

	/**
	 * @param aPath
	 * @param aTimeStamp
	 * @param aForceSecondes
	 * @return
	 * @throws Exception
	 */
	public CXRsrcByte rsrcReadByte(final CXRsrcUriPath aPath, final long aTimeStamp, final boolean aForceSecondes)
			throws Exception {
		return rsrcReadByte(aPath, aTimeStamp, aForceSecondes, false);
	}

	/**
	 *
	 *
	 * @param aPath
	 * @param aTimeStamp
	 *            si >0 --> Check , si <=0 no check)
	 * @param aForceSecondes
	 *            true - Le time stamp lu est converti en secondes (millisecondes
	 *            par defaut)
	 * @param aFulPath
	 * @return
	 * @throws Exception
	 */
	private CXRsrcByte rsrcReadByte(CXRsrcUriPath aPath, long aTimeStamp, final boolean aForceSecondes,
			final boolean aFulPath) throws Exception {
		CXRsrcByte wRsrc = null;
		URL wUrl = null;
		try {
			aPath = checkUriPath(aPath, aFulPath);
			boolean wCheckTimeStamp = aTimeStamp > 0;
			wUrl = urlNew(aPath);
			URLConnection wCnx = openConnection(wUrl);

			long wCurTimeStamp = wCnx.getLastModified();
			if (aForceSecondes) {
				wCurTimeStamp = (wCurTimeStamp / 1000) * 1000;
				aTimeStamp = (aTimeStamp / 1000) * 1000;
			}
			wCheckTimeStamp = wCheckTimeStamp && wCurTimeStamp > 0;
			if (wCheckTimeStamp && aTimeStamp == wCurTimeStamp) {
				return null;
			} else {
				wRsrc = new CXRsrcByte(aPath, CXRsrcByteReader.readAll(wCnx), wCurTimeStamp);
			}
		} catch (Exception e) {
			throwExcepReadByte(aPath == null ? "null" : wUrl == null ? aPath.getFullPath() : wUrl.toString(), e);
		}
		return wRsrc;
	}

	/**
	 * @param aRsrcPath
	 * @return
	 * @throws Exception
	 */
	public CXRsrcByte rsrcReadByte(final String aRsrcPath) throws Exception {
		return rsrcReadByte(aRsrcPath, 0);
	}

	/**
	 * Path = String et Timestamp (>0 --> Check - <=0 no check)
	 *
	 * @param aRsrcPath
	 * @param aTimeStampSyst
	 * @return
	 * @throws Exception
	 */
	public CXRsrcByte rsrcReadByte(final String aRsrcPath, final long aTimeStampSyst) throws Exception {
		return rsrcReadByte(aRsrcPath, aTimeStampSyst, false);
	}

	/**
	 * @param aRsrcPath
	 * @param aTimeStampSyst
	 * @param aForceSecond
	 * @return
	 * @throws Exception
	 */
	public CXRsrcByte rsrcReadByte(final String aRsrcPath, final long aTimeStampSyst, final boolean aForceSecond)
			throws Exception {
		return rsrcReadByte(new CXRsrcUriPath(aRsrcPath), aTimeStampSyst, aForceSecond);
	}

	public CXRsrcText rsrcReadTxt(final CXRsrcUriPath aRsrcPath) throws Exception {
		return rsrcReadTxt(aRsrcPath.getFullPath(), null, 0);
	}

	public CXRsrcText rsrcReadTxt(final CXRsrcUriPath aPath, final long aTimeStamp, final boolean aForceSecondes)
			throws Exception {
		return rsrcReadTxt(aPath, null, aTimeStamp, aForceSecondes, false);
	}

	/**
	 * @param aRsrcPath
	 * @return
	 * @throws Exception
	 */
	public CXRsrcText rsrcReadTxt(final CXRsrcUriPath aRsrcPath, Map<String, String> aFullPath) throws Exception {
		return rsrcReadTxt(aRsrcPath.getFullPath(), aFullPath, 0);
	}

	/**
	 * @param aPath
	 * @param aTimeStamp
	 * @param aForceSecondes
	 * @return
	 * @throws Exception
	 */
	public CXRsrcText rsrcReadTxt(final CXRsrcUriPath aPath, Map<String, String> aFullPath, final long aTimeStamp,
			final boolean aForceSecondes) throws Exception {
		return rsrcReadTxt(aPath, aFullPath, aTimeStamp, aForceSecondes, false);
	}

	/**
	 * @param aPath
	 * @param aTimeStamp
	 * @param aForceSecondes
	 * @param aFulPath
	 * @return
	 * @throws Exception
	 */
	private CXRsrcText rsrcReadTxt(final CXRsrcUriPath aPath, Map<String, String> aFullPath, final long aTimeStamp,
			final boolean aForceSecondes, final boolean aFulPath) throws Exception {
		CXRsrcText wRsrc = null;
		CXRsrcUriPath wPath = null;
		URL wUrl = null;
		try {
			wPath = checkUriPath(aPath, aFulPath);

			wRsrc = readRsrcTextContent(wPath, aFullPath, aTimeStamp, aForceSecondes);

		} catch (Exception e) {
			if (hasNext()) {
				return next().rsrcReadTxt(aPath, aFullPath, aTimeStamp, aForceSecondes, aFulPath);
			}
			throwExcepReadText("Unable to read "
					+ ((wPath == null) ? "null" : (wUrl == null) ? aPath.getFullPath() : wUrl.toString()), e);
		}
		return wRsrc;
	}

	public CXRsrcText rsrcReadTxt(final String aRsrcPath) throws Exception {
		return rsrcReadTxt(aRsrcPath, null, 0);
	}

	public CXRsrcText rsrcReadTxt(final String aRsrcPath, final long aTimeStampSyst) throws Exception {
		return rsrcReadTxt(aRsrcPath, null, aTimeStampSyst, false);
	}

	public CXRsrcText rsrcReadTxt(final String aRsrcPath, final long aTimeStampSyst, final boolean aForceSecond)
			throws Exception {
		return rsrcReadTxt(new CXRsrcUriPath(aRsrcPath), null, aTimeStampSyst, aForceSecond);
	}

	/**
	 * @param aRsrcPath
	 * @return
	 * @throws Exception
	 */
	public CXRsrcText rsrcReadTxt(final String aRsrcPath, Map<String, String> aFullPath) throws Exception {
		return rsrcReadTxt(aRsrcPath, aFullPath, 0);
	}

	/**
	 * Path = String et Timestamp (>0 --> Check - <=0 no check)
	 *
	 * @param aRsrcPath
	 * @param aTimeStampSyst
	 * @return
	 * @throws Exception
	 */
	public CXRsrcText rsrcReadTxt(final String aRsrcPath, Map<String, String> aFullPath, final long aTimeStampSyst)
			throws Exception {
		return rsrcReadTxt(aRsrcPath, aFullPath, aTimeStampSyst, false);
	}

	/**
	 * @param aRsrcPath
	 * @param aTimeStampSyst
	 * @param aForceSecond
	 * @return
	 * @throws Exception
	 */
	public CXRsrcText rsrcReadTxt(final String aRsrcPath, Map<String, String> aFullPath, final long aTimeStampSyst,
			final boolean aForceSecond) throws Exception {
		return rsrcReadTxt(new CXRsrcUriPath(aRsrcPath), aFullPath, aTimeStampSyst, aForceSecond);
	}

	public CXRsrcText rsrcReadTxt(final String aRsrcPath, Map<String, String> aFullPath, String aFullParam)
			throws Exception {
		return rsrcReadTxt(aRsrcPath, aFullPath, 0);
	}

	public CXListRsrcText rsrcReadTxts(final CXRsrcUriPath aPath, Map<String, String> aFullPath, final long aTimeStamp,
			final boolean aForceSecondes) throws Exception {
		return rsrcReadTxts(aPath, aFullPath, aTimeStamp, aForceSecondes, false);
	}

	protected CXListRsrcText rsrcReadTxts(final CXRsrcUriPath aPath, Map<String, String> aFullPath,
			final long aTimeStamp, final boolean aForceSecondes, final boolean aFulPath) throws Exception {
		CXRsrcText wRsrc = null;
		CXRsrcUriPath wPath = null;
		URL wUrl = null;
		CXListRsrcText wListRsrc = new CXListRsrcText();

		try {
			wPath = checkUriPath(aPath, aFulPath);
			if (wPath.getFullPath().contains("*")) {
				String wRegexp = wPath.getName().replaceAll("\\*", "\\.\\*");
				final Pattern wPattern = Pattern.compile(wRegexp);
				// look to list of file in that directory
				CXFileDir wDir = new CXFileDir(wPath.getParent().getPath());
				List<String> wPaths = Arrays.asList(wDir.list(new FilenameFilter() {

					@Override
					public boolean accept(final File dir, final String name) {
						Matcher wMatch = wPattern.matcher(name);
						return wMatch.find();
					}
				}));
				String wParentPath = wDir.getAbsolutePath();
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

	public CXListRsrcText rsrcReadTxts(final String aRsrcPath) throws Exception {
		return rsrcReadTxts(aRsrcPath, null, 0);
	}

	public CXListRsrcText rsrcReadTxts(final String aRsrcPath, final long aTimeStampSyst) throws Exception {
		return rsrcReadTxts(aRsrcPath, null, aTimeStampSyst, false);
	}

	public CXListRsrcText rsrcReadTxts(final String aRsrcPath, final long aTimeStampSyst, final boolean aForceSecond)
			throws Exception {
		return rsrcReadTxts(new CXRsrcUriPath(aRsrcPath), null, aTimeStampSyst, aForceSecond);
	}

	/**
	 * read list of RsrcTexts
	 *
	 * @param aRsrcPath
	 * @return
	 * @throws Exception
	 */
	public CXListRsrcText rsrcReadTxts(final String aRsrcPath, Map<String, String> aFullPath) throws Exception {
		return rsrcReadTxts(aRsrcPath, aFullPath, 0);
	}

	public CXListRsrcText rsrcReadTxts(final String aRsrcPath, Map<String, String> aFullPath, final long aTimeStampSyst)
			throws Exception {
		return rsrcReadTxts(aRsrcPath, aFullPath, aTimeStampSyst, false);
	}

	public CXListRsrcText rsrcReadTxts(final String aRsrcPath, Map<String, String> aFullPath, final long aTimeStampSyst,
			final boolean aForceSecond) throws Exception {
		return rsrcReadTxts(new CXRsrcUriPath(aRsrcPath), aFullPath, aTimeStampSyst, aForceSecond);
	}

	/**
	 * @param aExp
	 */
	public void setCacheExpires(final int aExp) {
		pCacheExpires = aExp;
	}

	/**
	 * @param aConnectTimeOutMs
	 */
	public void setConnectTimeout(final int aConnectTimeOutMs) {
		pConnectTimeoutMs = aConnectTimeOutMs;
	}

	/**
	 * @param aDir
	 */
	public void setDefaultDirectory(final CXRsrcUriDir aDir) {
		pDefaultDirectory = aDir == null ? new CXRsrcUriDir("") : aDir;
	}

	/**
	 * @param aPath
	 */
	public void setDefaultDirectory(final String aPath) {
		setDefaultDirectory(new CXRsrcUriDir(aPath));
	}

	/**
	 * #12 Manage chains of resource providers
	 *
	 * @param aNext
	 */
	public void setNext(final CXRsrcProvider aNext) {
		pNext = aNext;
	}

	/**
	 * @param aReadTimeOutMs
	 */
	public void setReadTimeout(final int aReadTimeOutMs) {
		pReadTimeoutMs = aReadTimeOutMs;
	}

	/**
	 * #10
	 *
	 * @return the size of the chain
	 */
	public int size() {
		int wNb = 0;
		if (hasNext()) {
			wNb += next().size();
		}
		return 1 + wNb;
	}

	/**
	 * @param aUrl
	 * @param e
	 * @throws Exception
	 */
	protected void throwExcepReadByte(final String aUrl, final Exception e) throws Exception {
		throw new Exception("Error reading byte resource[" + aUrl + "]", e);
	}

	/**
	 * @param aUrl
	 * @param e
	 * @throws Exception
	 */
	protected void throwExcepReadText(final String aUrl, final Exception e) throws Exception {
		throw new Exception("Error reading text resource[" + aUrl + "]", e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String wStr = urlGetAddress();
		if (wStr == null) {
			wStr = getDefDirectory().toString();
		}
		if (hasNext()) {
			wStr += ',' + next().toString();
		}
		return wStr;
	}

	/**
	 * Renvoie l'adresse de l'url (http://host:Port...) Renvoie "" ou "/" si file
	 * provider
	 *
	 * @return
	 */
	public abstract String urlGetAddress();

	protected URL urlNew(final CXRsrcUriPath aPath) throws MalformedURLException {
		return new URL(aPath.getUrlStr(urlGetAddress()));
	}
}
