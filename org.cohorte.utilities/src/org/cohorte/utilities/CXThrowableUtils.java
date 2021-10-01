package org.cohorte.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * MOD_OG_20210823
 * 
 * @author ogattaz
 *
 */
public class CXThrowableUtils {

	/**
	 * @param e
	 * @return the result of "e.printStackTrace()" in a String
	 */
	public static String dumpStackTrace(Throwable e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
