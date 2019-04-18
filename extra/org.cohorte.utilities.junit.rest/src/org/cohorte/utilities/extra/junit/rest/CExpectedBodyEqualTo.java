package org.cohorte.utilities.extra.junit.rest;

import static org.hamcrest.Matchers.equalTo;

import io.restassured.specification.ResponseSpecification;

/**
 * @author ogattaz
 *
 */
public class CExpectedBodyEqualTo {

	private final Boolean pValueBoolean;
	private final Integer pValueInt;
	private final String pValueStr;
	private final String pWhat;

	/**
	 * @param aWhat
	 * @param aValue
	 */
	public CExpectedBodyEqualTo(final String aWhat, final boolean aValue) {
		super();
		pWhat = aWhat;
		pValueBoolean = new Boolean(aValue);
		;
		pValueStr = null;
		pValueInt = null;
	}

	/**
	 * @param aWhat
	 * @param aValue
	 */
	public CExpectedBodyEqualTo(final String aWhat, final int aValue) {
		super();
		pWhat = aWhat;
		pValueBoolean = null;
		pValueStr = null;
		pValueInt = new Integer(aValue);
	}

	/**
	 * @param aWhat
	 * @param aValue
	 */
	public CExpectedBodyEqualTo(final String aWhat, final String aValue) {
		super();
		pWhat = aWhat;
		pValueBoolean = null;
		pValueStr = aValue;
		pValueInt = null;
	}

	/**
	 * @param aResponseSpecification
	 * @return
	 */
	public ResponseSpecification appendTo(
			final ResponseSpecification aResponseSpecification) {
		if (pValueStr != null) {
			return aResponseSpecification.body(pWhat, equalTo(pValueStr));
		} else if (pValueInt != null) {
			return aResponseSpecification.body(pWhat, equalTo(pValueInt));
		} else if (pValueBoolean != null) {
			return aResponseSpecification.body(pWhat, equalTo(pValueBoolean));
		} else {
			throw new RuntimeException(
					"Unable to instanciate a ResponseSpecification: no value set");
		}
	}

	private Object getValue() {
		return (pValueStr != null) ? pValueStr
				: (pValueInt != null) ? pValueInt : pValueBoolean;
	}

	/**
	 * @return
	 */
	private Class<?> getValueClass() {
		if (getValue() != null) {
			return getValue().getClass();
		} else {
			throw new RuntimeException(
					"Unable to get class of value: no value set");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Expected path=[%s] equalTo[%s](%s)", pWhat,
				getValue(), getValueClass().getSimpleName());
	}

}
