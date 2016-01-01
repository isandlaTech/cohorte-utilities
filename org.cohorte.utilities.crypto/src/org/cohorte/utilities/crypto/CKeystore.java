package org.cohorte.utilities.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.psem2m.utilities.logging.CActivityLoggerNull;
import org.psem2m.utilities.logging.IActivityLogger;

/**
 * @author ogattaz
 *
 */
public class CKeystore {

	private boolean pDirty = false;
	private File pFile = null;
	private final KeyStore pKS;
	private boolean pLoaded = false;
	private String wPassPhrase = null;
	

	/**
	 * @param aPassPhrase
	 * @throws KeyStoreException
	 * @deprecated
	 */
	public CKeystore(final String aPassPhrase) throws KeyStoreException {
		this(EKeystoreType.JKS,aPassPhrase);
	}
	
	/**
	 * @param aKeystoreType
	 * @param aPassPhrase
	 * @throws KeyStoreException
	 */
	public CKeystore(final EKeystoreType aKeystoreType ,final String aPassPhrase) throws KeyStoreException {
		super();
		wPassPhrase = aPassPhrase;
		pKS = KeyStore.getInstance(aKeystoreType.name());
	}

	/**
	 * @param aFile
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws CertificateException
	 * @throws NoSuchAlgorithmException
	 */
	public CKeystore(final String aPassPhrase, final File aFile)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, FileNotFoundException, IOException {

		this(EKeystoreType.fromFileExtension(aFile), aPassPhrase);
		setFile(aFile);
	}

	/**
	 * @param aPassPhrase
	 * @param aFilePath
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public CKeystore(final String aPassPhrase, final String aFilePath)
			throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, FileNotFoundException, IOException {

		this(aPassPhrase,
				(aFilePath != null && !aFilePath.isEmpty()) ? new File(
						aFilePath) : null);
	}

	/**
	 * @param aSB
	 * @return
	 */
	public StringBuilder addDescriptionInSB(final StringBuilder aSB) {

		aSB.append(String
				.format("FileName=[%s] FileExists=[%s] isLoaded=[%s] isEmpty=[%s] NbEntries=[%s] isDirty=[%s]",
						getFileName(), isFileReadable(), isLoaded(), isEmpty(),
						getNbEntries(), isDirty()));
		return aSB;
	}

	/**
	 * @return
	 * @throws KeyStoreException
	 */
	public List<String> getEntries() {

		try {
			if (isLoaded()) {
				return Collections.list(pKS.aliases());
			}
		} catch (KeyStoreException e) {
			getLogger().logSevere(this, "getEntries", "ERROR %s", e);
		}
		return new ArrayList<String>();
	}
	
	/**
	 * @param aAlias
	 * @return
	 * @throws KeyStoreException
	 */
	public boolean isCertificateEntry(final String aAlias) throws KeyStoreException{
		
		return pKS.isCertificateEntry(aAlias);
	}
	
	/**
	 * @param aAlias
	 * @return
	 * @throws KeyStoreException
	 */
	public boolean contains(final String aAlias) throws KeyStoreException{
		return isCertificateEntry(aAlias) || isKeyEntry(aAlias);
	}
	/**
	 * @param aAlias
	 * @return
	 * @throws KeyStoreException
	 */
	public boolean isKeyEntry(final String aAlias) throws KeyStoreException{
		
		return pKS.isKeyEntry(aAlias);
	}
	/**
	 * @param aAlias
	 * @return
	 * @throws KeyStoreException
	 */
	public Certificate getCerificate(final String aAlias) throws KeyStoreException{
		
		return pKS.getCertificate(aAlias);
	}
	
	/**
	 * @param aAlias
	 * @param aPassword
	 * @return
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 */
	public Key getKey(final String aAlias,final String aPassword) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException{
		
		return pKS.getKey(aAlias, aPassword.toCharArray());
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return (pFile != null) ? pFile.getName() : null;
	}

	/**
	 * @return
	 */
	public KeyStore getKeyStore(){
		return pKS;
	}
	/**
	 * @return
	 */
	protected IActivityLogger getLogger() {
		return CActivityLoggerNull.getInstance();
	}

	/**
	 * @return
	 */
	public int getNbEntries() {
		return getEntries().size();
	}

	/**
	 * @return
	 */
	private char[] getPassPhraseAsChars() {
		return (wPassPhrase != null) ? wPassPhrase.toCharArray() : new char[0];
	}

	/**
	 * @return
	 */
	public boolean isDirty() {
		return pDirty;
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return getNbEntries() < 1;
	}

	/**
	 * @param aFile
	 * @return
	 */
	private boolean isFileReadable() {
		return (pFile != null && pFile.exists() && pFile.isFile());
	}

	/**
	 * @param aFile
	 * @return
	 */
	private boolean isFileWritable() {
		return (pFile != null && !pFile.exists());
	}

	/**
	 *
	 */
	public boolean isLoaded() {
		return pLoaded;
	}

	/**
	 * @return true if loaded from the file
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public boolean load() throws NoSuchAlgorithmException,
			CertificateException, FileNotFoundException, IOException {

		if (isFileReadable()) {
			pKS.load(new FileInputStream(pFile), getPassPhraseAsChars());
			pLoaded = true;
		}
		// if the file doesn't exist => init with a null streams
		else {
			pKS.load(null, null);
			getLogger().logWarn(this, "load", "KeyStore not loaded. File is not readable");
		}

		return pLoaded;
	}

	/**
	 * @param aAloas
	 * @param aCert
	 * @throws KeyStoreException
	 */
	public void setCerificate(final String aAloas, final Certificate aCert)
			throws KeyStoreException {

		pKS.setCertificateEntry(aAloas, aCert);
	}
	
	/**
	 * @param aAlias
	 * @param aKey
	 * @param aPassword
	 * @param aChain
	 * @throws KeyStoreException
	 */
	public void setKeyEntry(final String aAlias, final PrivateKey aKey,final String aPassword,final Certificate[] aChain)
			throws KeyStoreException {

		pKS.setKeyEntry(aAlias, aKey, aPassword.toCharArray(), aChain);;
	}
	
	/**
	 * @param aAlias
	 * @param aKey
	 * @param aChain
	 * @throws KeyStoreException
	 */
	public void setKeyEntry(final String aAlias, final byte[] aKey,final Certificate[] aChain)
			throws KeyStoreException {

		pKS.setKeyEntry(aAlias, aKey, aChain);;
	}

	/**
	 * @param aFile
	 * @return
	 */
	public void setFile(final File aFile) {

		pFile = aFile;
	}

	/**
	 * @return true if stored in the file
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public boolean store() throws KeyStoreException, NoSuchAlgorithmException,
			CertificateException, FileNotFoundException, IOException {

		File wTempFile = null;
		boolean wStored = false;
		try {
			// if the file already exist => move file to tempfile
			if (isFileReadable()) {
				wTempFile = new File(pFile.getAbsolutePath() + ".temp");
				if (wTempFile.exists()) {
					wTempFile.delete();
				}
				pFile.renameTo(wTempFile);
			}
			if (isFileWritable()) {
				pKS.store(new FileOutputStream(pFile), getPassPhraseAsChars());
				wStored = true;
				pDirty = false;
			}
			getLogger().logWarn(this, "store", "FileName=[%s] Stored=[%s]",
					getFileName(), wStored);
			return wStored;

		} finally {
			if (wTempFile != null) {
				// if stored => delete tempfile
				if (wStored) {
					wTempFile.delete();
				}
				// if not stored => rename tempfile to file
				else {
					pFile.delete();
					wTempFile.renameTo(pFile);
					getLogger().logWarn(this, "store",
							"KeyStore not stored. File restored");
				}

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return addDescriptionInSB(new StringBuilder()).toString();
	}

}
