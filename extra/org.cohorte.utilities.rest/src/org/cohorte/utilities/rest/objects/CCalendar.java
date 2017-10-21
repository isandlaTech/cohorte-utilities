package org.cohorte.utilities.rest.objects;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Serializable calendar object.
 * 
 * @author Ahmad Shahwan
 *
 */
public class CCalendar extends GregorianCalendar implements Serializable {
	
	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = -7248457859796240479L;
	
	/**
	 * Expected date format.
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
	
	/**
	 * Fall-back date format.
	 */
	private static final String DATE_FORMAT_SIMPLE = "yyyy-MM-dd";
	
	private static DateFormat sIsoDateFormate =
			new SimpleDateFormat(DATE_FORMAT);; 
	
	private static DateFormat sFallbackDateFormate =
			new SimpleDateFormat(DATE_FORMAT_SIMPLE);
	
	/**
	 * Constructor.
	 * 
	 * @param wValue
	 * @throws ParseException
	 */
	public CCalendar(String wValue) throws ParseException {
		super();
		try {
			this.setTime(sIsoDateFormate.parse(wValue));
		} catch (ParseException e) {
			this.setTime(sFallbackDateFormate.parse(wValue));
		}
	}
}
