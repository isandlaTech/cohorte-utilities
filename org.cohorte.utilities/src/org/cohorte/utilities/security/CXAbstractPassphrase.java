package org.cohorte.utilities.security;

import org.psem2m.utilities.CXException;

/**
 * MOD_355
 * 
 * @author ogattaz
 * 
 */
public abstract class CXAbstractPassphrase implements IXPassphrase {

	private final boolean pInitialyEncoded;

	private IXPassphrase pNested = null;

	private String pValue;

	private final CXPassphraseType pType;

	/**
	 * @param aType
	 * @param aNested
	 * @throws CXPassphraseSchemeException
	 */
	public CXAbstractPassphrase(final CXPassphraseType aType, final IXPassphrase aNested)
			throws CXPassphraseSchemeException {
		super();

		if (aNested == null) {
			throw new IllegalArgumentException("Can't instanciate a passphrase using null nested Passphrase ");
		}
		pType = aType;
		pNested = aNested;
		pInitialyEncoded = false;
		pValue = null;
	}

	/**
	 * @param aValue
	 *            a encoded or not passphrase
	 * @throws CXPassphraseSchemeException
	 */
	public CXAbstractPassphrase(final CXPassphraseType aType, final String aValue) throws CXPassphraseSchemeException,InstantiationException {
		super();

		if (aValue == null) {
			throw new IllegalArgumentException("Can't instanciate a passphrase using null string");
		}
		pType = aType;
		pNested = null;

		String wEncodedValue = extractEncodedData(aValue);

		try {
			// if the given value is prefixed
			pInitialyEncoded = (wEncodedValue != null);

			// decode the EncodedValue or use the avlue as is
			pValue = (pInitialyEncoded) ? decode(wEncodedValue) : aValue;

		} catch (Exception e) {
			throw new InstantiationException(String.format("Can't instanciate a passphrase %s",CXException.eMiniInString(e)));
		}
	}

	/**
	 * 
	 * @param aValue
	 * @return
	 */
	protected abstract String decode(final String aValue);

	/**
	 * @param aValue
	 * @return
	 */
	protected abstract String encode(final String aValue);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object aObj) {
		if (this == aObj)
			return true;

		if (null == aObj)
			return false;

		if (aObj instanceof CXAbstractPassphrase) {
			CXAbstractPassphrase wPP = (CXAbstractPassphrase) aObj;
			// noinspection StringEquality
			return wPP.pValue == pValue || (null != pValue && pValue.equals(wPP.pValue));
		}

		if (aObj instanceof String)
			return aObj.equals(pValue);

		return false;
	}

	/**
	 * <pre>
	 * b64:bW90ZGVwYXNzZQ0K
	 * basic:bW90ZGVwYXNzZQ0K
	 * base64:bW90ZGVwYXNzZQ0K
	 * OBF:1ri71v1r1v2n1ri71shq1ri71shs1ri71v1r1v2n1ri7
	 * notencoded
	 * </pre>
	 * 
	 * @param aValue
	 *            the value prefixed or not by "b64:" or "base64:" or "basic:"
	 *            or "OBF:" or ...
	 * @return the encoded value if there
	 */
	protected abstract String extractEncodedData(final String aValue) throws CXPassphraseSchemeException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.c2kernel.login.IXPassphrase#getDecoded()
	 */
	public String getDecoded() {

		return (hasNested()) ? getNested().getDecoded() : pValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.c2kernel.login.IXPassphrase#getEncoded()
	 */
	/**
	 * @return
	 */
	public String getEncoded() {

		String wEncodedData = encode( (hasNested()) ? getNested().getEncoded() : getDecoded() );
		return pType.getScheme() + wEncodedData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.security.IXPassphrase#getNested()
	 */
	public IXPassphrase getNested() {
		return pNested;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.utilities.security.IXPassphrase#hasNested()
	 */
	public boolean hasNested() {
		return pNested != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.c2kernel.login.IXPassphrase#isInitialyEncoded()
	 */
	public boolean isInitialyEncoded() {
		return pInitialyEncoded;
	}

	/**
	 * @param aNested
	 */
	void setNested(final IXPassphrase aNested) {
		pNested = aNested;
		pValue = null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return getEncoded();
	}
}
