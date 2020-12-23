package org.cohorte.utilities.picosoc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.psem2m.utilities.CXStringUtils;

/**
 * #48 Correction & enhancements
 * 
 * 
 * @author ogattaz
 * 
 * @param <T>
 */
public class CServiceKey<T> implements Comparable<CServiceKey<T>> {

	private static final String NO_PROPS = "{}";
	private static final boolean STRICT_MATCHING = true;
	
	private final Class<? extends T> pSpecification;
	private final int pHash;
	
	// #48 can change if the property is set or remove
	private  Map<String, String> pProperties;

	// #48 can change if the property is set or remove
	private  String pKey;

	/**
	 * @param aSpecification
	 * @param aProperties
	 */
	public CServiceKey(final Class<? extends T> aSpecification,
			final Map<String, String> aProperties) {

		super();
		pSpecification = aSpecification;
		pHash = pSpecification.getName().hashCode();
		pProperties = (aProperties != null) ? aProperties : new CServiceProperties();
		pKey = calcKey();
	}

	/**
	 * @return the calculated key
	 */
	private String calcKey() {
		// #48
		String wSerializedProps = (hasProperty()) ?CXStringUtils.stringMapToString(pProperties):NO_PROPS;
		
		return String.format("/%s/%s/", pSpecification.getName(),wSerializedProps);
	}
	/**
	 * @return
	 */
	public boolean hasProperty() {
		return getNbProperty()>0;
	}
	/**
	 * @return
	 */
	public boolean hasNoProperty() {
		return getNbProperty()==-1;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CServiceKey<T> o) {
		return pKey.compareTo(o.pKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object obj) {

		if (obj instanceof CServiceKey) {
			// #48 Correction !
			return pKey.equals(((CServiceKey<T>) obj).pKey);
		}
		return super.equals(obj);
	}

	/**
	 * @param aKey
	 * @return
	 */
	public String getProperty(final String aKey) {
		return pProperties.get(aKey);
	}
	/**
	 * #48
	 * @return
	 */
	public int getNbProperty() {
		return pProperties.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return pHash;
	}

	/**
	 * #48
	 * 
	 * @param aServiceKey
	 *            a ServiceKey containing a spcification and a properties
	 * @return true if the specifications are the same and if all the given
	 *         properties exist and have the same values in the properties of
	 *         the current key
	 */
	boolean match(final CServiceKey<?> aServiceKey) {
		
		return match(aServiceKey,STRICT_MATCHING);
	}
	
	/**

	 * #48
	 * @param aServiceKey
	 *          a ServiceKey containing a spcification and a properties
	 * @param aStrictMatching
	 * 			if true the number of properties of the given ServiceKey have to be equals to the number of properties of the current key
	 * @return true if the specifications are the same and if all the given
	 *         properties exist and have the same values in the properties of
	 *         the current key
	 */
	boolean match(final CServiceKey<?> aServiceKey,boolean aStrictMatching) {

		if (!pSpecification.equals(aServiceKey.pSpecification)) {
			return false;
		}
		// #48 correction
		if (aServiceKey.hasNoProperty() && hasNoProperty() ) {
			return true;
		}
		// #48 correction
		if (aStrictMatching &&  ( aServiceKey.getNbProperty()!= getNbProperty() )) {
			return false;
		}
				
		// Comparison of the content of the properties : all the 
		if (aServiceKey.hasProperty()) {
			final Map<String, String> wProperties = aServiceKey.pProperties;
			for (Map.Entry<String, String> wEntry : wProperties.entrySet()) {
				if (!wEntry.getValue().equals(getProperty(wEntry.getKey()))) {
					return false;
				}
			}
		}
		// Return true : the properties of the two ServiceKeys contain the same keys and values
		return true;
	}

	/**
	 * @param aKey
	 * @return
	 */
	String removeProperty(final String aKey) {
		int wOldSize = getNbProperty();
		if (wOldSize<0) {
			throw new RuntimeException(String.format("Unable to remove a property [%s], this ServiceKey hasn't property",  aKey));
		}
		String wOldValue = pProperties.remove(aKey);
		// #48 if the key is modified modification
		if (wOldValue!=null) {
			pKey =calcKey();
		}
		return wOldValue;
	}

	/**
	 * @param aKey
	 * @param aValue
	 */
	String setProperty(final String aKey, final String aValue) {
		int wOldSize = getNbProperty();
		if (wOldSize<0) {
			pProperties = new CServiceProperties();
			wOldSize=0;
		}
		String wOldValue = pProperties.put(aKey, aValue);
		// #48 if the key is modified modification
		if (wOldValue!=null) {
			pKey = calcKey();
		}
		return wOldValue;
	}
	
	/**
	 * @param aProperties a map of existing or new properties
	 * @return the number of new properties
	 */
	public int setProperties(final Map<String, String> aProperties) {
		int wOldSize = getNbProperty();
		if (wOldSize<0) {
			pProperties = new CServiceProperties();
			wOldSize=0;
		}
		pProperties.putAll(aProperties);
		int wNewSize = this.getNbProperty();
		pKey = calcKey();
		return (wNewSize-wOldSize);
	}

		
		

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return pKey;
	}
}
