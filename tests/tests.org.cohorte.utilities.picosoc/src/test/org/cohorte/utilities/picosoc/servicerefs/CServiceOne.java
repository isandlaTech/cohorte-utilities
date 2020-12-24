package test.org.cohorte.utilities.picosoc.servicerefs;

import org.psem2m.utilities.CXStringUtils;

/**
 * #48
 * 
 * @author ogattaz
 *
 */
public class CServiceOne implements ISpecificationOne {

	private final String pId;

	/**
	 * 
	 */
	public CServiceOne(final String aId) {
		super();
		pId = aId;
	}

	/**
	 * @return
	 */
	public String getId() {
		return pId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("%s_%s_%s", getClass().getSimpleName(), CXStringUtils
						.strAdjustRight(String.valueOf(hashCode()), 4, ' '),
						getId());
	}
}
