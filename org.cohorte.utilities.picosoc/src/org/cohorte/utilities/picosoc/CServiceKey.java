package org.cohorte.utilities.picosoc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.psem2m.utilities.CXStringUtils;

/**
 * @author ogattaz
 * 
 * @param <T>
 */
public class CServiceKey<T> implements Comparable<CServiceKey<T>> {

	private static final Map<String, String> EMPTY_PROPS = new LinkedHashMap<String, String>();
	private final Class<? extends T> pSpecification;
	private final int pHash;
	private final Map<String, String> pProperties;
	private final boolean pWithProperty;
	private String pKey = "";

	/**
	 * @param aSpecification
	 * @param aProperties
	 */
	CServiceKey(final Class<? extends T> aSpecification,
			final Map<String, String> aProperties) {

		super();
		pSpecification = aSpecification;
		pHash = pSpecification.getName().hashCode();
		pWithProperty = (aProperties != null);
		pProperties = pWithProperty ? aProperties : EMPTY_PROPS;
		calcKey();
	}

	/**
	 * @return the calculated key
	 */
	String calcKey() {
		pKey = String.format("/%s/%s/", pSpecification.getName(),
				CXStringUtils.stringMapToString(pProperties));
		return pKey;
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

			if (!pWithProperty || !((CServiceKey<T>) obj).pWithProperty) {
				return pSpecification
						.equals(((CServiceKey<T>) obj).pSpecification);
			}
			return pKey.equals(((CServiceKey<T>) obj).pKey);
		}
		return super.equals(obj);
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
	 * @param aServiceKey
	 *            a ServiceKey containing a spcification and a properties
	 * @return true if the specifications are the same and if all the given
	 *         properties exist and have the same values in the properties of
	 *         the current key
	 */
	boolean match(final CServiceKey<?> aServiceKey) {

		if (!pSpecification.equals(aServiceKey.pSpecification)) {
			return false;
		}
		if (pProperties.isEmpty() && aServiceKey.pProperties.isEmpty()) {
			return true;
		}
		if (pProperties.size() < aServiceKey.pProperties.size()) {
			return false;
		}
		// Return true if the properties of this key contains those of the given
		// key
		return matchProperties(aServiceKey.pProperties);
	}

	/**
	 * @param aProperties
	 *            the properties to test
	 * @return true if all the given properties exist and have the same values
	 *         in the properties of the current key
	 */
	private boolean matchProperties(final Map<String, String> aProperties) {

		if (aProperties != null && !aProperties.isEmpty()) {
			for (Map.Entry<String, String> wEntry : aProperties.entrySet()) {
				if (!wEntry.getValue().equals(pProperties.get(wEntry.getKey()))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param aKey
	 * @return
	 */
	String removeProperty(final String aKey) {
		String wOldValue = pProperties.remove(aKey);
		calcKey();
		return wOldValue;
	}

	/**
	 * @param aKey
	 * @param aValue
	 */
	String setProperty(final String aKey, final String aValue) {
		String wOldValue = pProperties.put(aKey, aValue);
		calcKey();
		return wOldValue;
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
