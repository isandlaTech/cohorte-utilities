package org.psem2m.utilities.logging;

import java.io.IOException;

/**
 * @author ogattaz
 * 
 */
public class CLogToolsException {

	private final static String NO_STACK = "no stack available";

	private static final String PREFIX_CAUSES = "causes";
	private static final String PREFIX_CLASS = "class";
	private static final String PREFIX_MESS = "mess";
	private static final String PREFIX_STACK = "stack";

	private final static String PSEM2M_CLASSES_PREFIX = "org.psem2m.";

	private final static CLogToolsException sCLogToolsException = new CLogToolsException();

	static final char SEPARATOR_COMA = ',';

	static final char SEPARATOR_LINE = '\n';

	/**
	 * @return
	 */
	public static CLogToolsException getInstance() {
		return sCLogToolsException;
	}

	/**
	 * 
	 */
	private CLogToolsException() {
		super();
	}

	/**
	 * @param aSB
	 * @param e
	 * @param aSeparator
	 * @return
	 * @throws Exception
	 */
	private StringBuilder addECausesInBuffer(final StringBuilder aSB,
			final Throwable e, final char aSeparator) {

		aSB.append(PREFIX_CAUSES).append('=').append(aSeparator);
		appendCauses(aSB, null, e, aSeparator);
		return aSB;
	}

	/**
	 * Ajoute la composante (classe) d'une exception dans un StringBuilder
	 * 
	 * @param aSB
	 * @param e
	 * @return
	 */
	private StringBuilder addEClassInBuffer(final StringBuilder aSB,
			final Throwable e) {
		return aSB.append(PREFIX_CLASS).append('=').append('[')
				.append(e.getClass().getName()).append(']');
	}

	/**
	 * @param aSB
	 * @param e
	 * @param aSeparator
	 * @return
	 */
	private StringBuilder addEDescrFullInBuffer(final StringBuilder aSB,
			final Throwable e, final char aSeparator) {
		if (aSeparator == SEPARATOR_LINE) {
			aSB.append(aSeparator);
		}
		addEClassInBuffer(aSB, e).append(SEPARATOR_LINE);

		addEMessInBuffer(aSB, e, aSeparator).append(SEPARATOR_LINE);

		addEStackInBuffer(aSB, e, aSeparator).append(SEPARATOR_LINE);

		if (e.getCause() != null) {
			addECausesInBuffer(aSB, e, aSeparator).append(SEPARATOR_LINE);
		}

		return aSB;
	}

	/**
	 * Ajoute la composante (mess) d'une exception dans un Buffer *
	 * 
	 * @param aSB
	 * @param e
	 * @return
	 */
	private StringBuilder addEMessInBuffer(final StringBuilder aSB,
			final Throwable e, final char aSeparator) {

		return aSB.append(PREFIX_MESS).append('=').append('[')
				.append(eMessage(e)).append(']');
	}

	/**
	 * Ajoute la composante (stack) d'une exception dans un StringBuilder
	 * 
	 * @param aSB
	 * @param e
	 * @return
	 */
	private StringBuilder addEStackInBuffer(final StringBuilder aSB,
			final Throwable e, final char aSeparator) {

		aSB.append(PREFIX_STACK).append('=').append(aSeparator);
		appendStack(aSB, null, e, aSeparator);
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aShift
	 * @param e
	 * @param aSeparator
	 * @return
	 */
	private StringBuilder appendCauses(final StringBuilder aSB,
			final String aShift, final Throwable e, final char aSeparator) {
		Throwable wCause = e.getCause();
		int wI = 0;
		while (wCause != null) {

			if (wI > 0) {
				aSB.append(aSeparator);
			}
			if (aShift != null) {
				aSB.append(aShift);
			}
			aSB.append(String.format("(%2d) %s | %s | %s", wI, wCause
					.getClass().getSimpleName(), wCause.getLocalizedMessage(),
					firstLineOfStackInString(wCause)));
			aSB.append(PREFIX_CAUSES).append('=').append(aSeparator);
			appendStack(aSB,"\t",wCause,aSeparator);

			wCause = wCause.getCause();
			wI++;
		}
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aShift
	 * @param e
	 * @param aSeparator
	 * @return
	 * @throws IOException
	 */
	private StringBuilder appendStack(final StringBuilder aSB,
			final String aShift, final Throwable e, final char aSeparator) {
		StackTraceElement[] wStackElements = e.getStackTrace();
		StackTraceElement wStackElement;
		int wMax = wStackElements.length;
		if (wMax == 0) {
			aSB.append("No stack information found");
		} else {
			int wI = 0;
			while (wI < wMax) {
				wStackElement = wStackElements[wI];
				if (wI > 0) {
					aSB.append(aSeparator);
				}
				if (aShift != null) {
					aSB.append(aShift);
				}
				aSB.append(String.format("(%2d) ", wI));
				appendStackLine(aSB, wStackElement);
				wI++;
			}
		}
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aStackTraceElement
	 * @return
	 * @throws IOException
	 */
	private StringBuilder appendStackLine(final StringBuilder aSB,
			final StackTraceElement aStackTraceElement) {
		aSB.append(aStackTraceElement.getClassName());
		aSB.append('(');
		aSB.append(aStackTraceElement.getMethodName());
		aSB.append(':');
		aSB.append(String.valueOf(aStackTraceElement.getLineNumber()));
		aSB.append(')');
		return aSB;
	}

	// /**
	// * @param e
	// * @param aPackagePrefix
	// * @return
	// */
	// private String buildErrMessCantGetFirstLine(final Throwable e) {
	// return String.format(MESS_CANT_GET_FLOS, e.getMessage(), eStack(e));
	// }
	//
	// /**
	// * @param e
	// * @param aPackagePrefix
	// * @return
	// */
	// private String buildErrMessCantGetFirstPackageLine(final Throwable e,
	// final String aPackagePrefix) {
	// return String.format(MESS_CANT_GET_FPLIS, aPackagePrefix,
	// e.getMessage(), eStack(e));
	// }
	//
	// /**
	// * @param e
	// */
	// private String buildErrMessCantGetStack(final Throwable e) {
	// return String.format(MESS_CANT_GET_STACK, e.getMessage(), eStack(e));
	// }

	// /**
	// * @param e
	// * @param aDumpedThrowable
	// * @return
	// */
	// private String buildExceptionDumpErrorMess(final Exception e,
	// final Throwable aDumpedThrowable) {
	// StringBuilder wSB = new StringBuilder();
	// wSB.append(LIB_THROWABLE_DUMP_ERROR);
	// if (e != null) {
	// wSB.append(String.format(FORMAT_EXCEPTION, e.getClass()
	// .getSimpleName()));
	// wSB.append(String.format(FORMAT_MESAGE, e.getMessage()));
	// wSB.append(CXException.getFirstLineOfStack(e));
	// }
	// if (aDumpedThrowable != null) {
	// wSB.append(String.format(FORMAT_THROWABLE, aDumpedThrowable
	// .getClass().getSimpleName()));
	// wSB.append(String.format(FORMAT_MESAGE,
	// aDumpedThrowable.getMessage()));
	// wSB.append(CXException.getCleanedStackOfThrowable(aDumpedThrowable));
	// }
	// return wSB.toString();
	// }

	/**
	 * @param aStatck
	 * @return
	 */
	private String cutStackAfterOrgPsem2m(String aStatck) {
		int wPos = aStatck.lastIndexOf(PSEM2M_CLASSES_PREFIX);
		if (wPos > -1) {
			wPos = aStatck.indexOf('\n', wPos);
			if (wPos > 0) {
				aStatck = aStatck.substring(0, wPos);
			}
		}
		return aStatck;
	}

	/**
	 * Retourne les composantes (classe,why ,mess,stack) d'une exception dans
	 * une string
	 * 
	 * @param e
	 * @return
	 */
	public String eInString(final Throwable e) {
		return eInString(e, SEPARATOR_LINE);
	}

	/**
	 * @param e
	 * @param aSeparator
	 * @return
	 */
	public String eInString(final Throwable aThrowable, final char aSeparator) {
		return addEDescrFullInBuffer(new StringBuilder(512), aThrowable,
				aSeparator).toString();

	}

	/**
	 * @param e
	 * @return
	 */
	private String eMessage(final Throwable e) {
		String wMess = null;
		if (e == null) {
			wMess = "no message, throwable is null";
		}
		if (wMess == null) {
			wMess = e.getLocalizedMessage();
		}
		if (wMess == null || wMess.isEmpty()) {
			wMess = e.getMessage();
		}
		if (wMess == null || wMess.isEmpty()) {
			wMess = "no message in " + e.getClass().getSimpleName();
		}
		return wMess;
	}

	/**
	 * @param aShift
	 * @param e
	 * @return
	 */
	private String eStack(final String aShift, final Throwable e,
			final char aSeparator) {
		return appendStack(new StringBuilder(), aShift, e, aSeparator)
				.toString();

	}

	/**
	 * @param e
	 * @return
	 * @throws IOException
	 */
	private String eStack(final Throwable e) {
		return eStack(null, e, SEPARATOR_LINE);
	}

	/**
	 * @param e
	 * @return the stack in a string. The stack is limited to the "org.psem2m"
	 * @used by CLogLineBuffer
	 */
	String eStackToString(final Throwable e) {
		String wS = eStack(e);
		if (wS == null) {
			return NO_STACK;
		}

		return cutStackAfterOrgPsem2m(wS);

	}

	/**
	 * @param e
	 * @return the stack in a formated string like :
	 *         "(idx) class | mess | class(method:line)"
	 * @used by CLogLineBuffer
	 */
	String firstLineOfStackInString(final Throwable e) {
		return appendStackLine(new StringBuilder(), e.getStackTrace()[0])
				.toString();

	}

	/**
	 * 
	 * 
	 * @param e
	 * @param aPackagePrefix
	 * @return
	 */
	private String getFirstPackageLineInStack(final Throwable e,
			final String aPackagePrefix) {

		String wS = eStack(e);

		int wPos = (aPackagePrefix != null) ? wS.indexOf(aPackagePrefix) : -1;

		if (wPos > -1) {
			wS = wS.substring(wPos);
			wPos = wS.indexOf('\n');
			if (wPos > 0) {
				wS = wS.substring(0, wPos);
			}

		}
		return wS;

	}

	/**
	 * 
	 * @param e
	 * @return the first line of the stack in a string
	 */
	String getFirstPsem2mLineInStack(final Throwable e) {
		return getFirstPackageLineInStack(e, PSEM2M_CLASSES_PREFIX);
	}
}
