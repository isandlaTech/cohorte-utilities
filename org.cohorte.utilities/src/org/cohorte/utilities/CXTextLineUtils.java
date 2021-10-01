package org.cohorte.utilities;

/**
 * MOD_OG_20210823
 * 
 * Building of lines as those générated by the test class "CTestTextLineUtils"
 * 
 * <pre>
 * 		 A  ####################################################################################################
 * 		 B  #### Lorem ipsum dolor sit amet, consectetur adipiscing elit #######################################
 * 		 C1 #                                                                                                  #
 * 		 C2 #..................................................................................................#
 * 		 D1 # Lorem ipsum dolor sit amet, consectetur adipiscing elit                                          #
 * 		 D2 #.Lorem ipsum dolor sit amet, consectetur adipiscing elit..........................................#
 * 		 E1 #         Lorem ipsum dolor sit amet, consectetur adipiscing elit                                  #
 * 		 E2 #.........Lorem ipsum dolor sit amet, consectetur adipiscing elit..................................#
 * 		 F1 #         Lorem ipsum dolor sit amet, consectetur adipiscing elit. Lorem ipsum dolor sit amet, con #
 * 		 F2 #---------Lorem ipsum dolor sit amet, consectetur adipiscing elit. Lorem ipsum dolor sit amet, con-#
 * </pre>
 * 
 * @author ogattaz
 *
 */
public class CXTextLineUtils {

	public static final char CHAR_SPACE = ' ';
	public static final char CHAR_SPACE_UNBREAKABLE = '\u00A0';

	/**
	 * Sample
	 * 
	 * <pre>
	 * 		++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * </pre>
	 * 
	 * @param aChar
	 * @param aLen
	 * @return
	 */
	public static String generateLine(final char aChar, final int aLen) {

		return String.valueOf(new char[aLen]).replace((char) 0x00, aChar);
	}

	/**
	 * Sample (char = '+' & whitechar='.' )
	 * 
	 * <pre>
	 * 		+..................................................................................................................................................+
	 * </pre>
	 * 
	 * @param aChar
	 * @param aWhiteChar
	 * @param aLen
	 * @return
	 */
	public static String generateLineBeginEnd(final char aChar, final char aWhiteChar, final int aLen) {

		return aChar + String.valueOf(new char[aLen - 2]).replace((char) 0x00, aWhiteChar) + aChar;
	}

	/**
	 * @param aChar
	 * @param aWhiteChar
	 * @param aLen
	 * @param aTextOffset
	 * @param aOneLineText
	 * @return
	 */
	public static String generateLineBeginEnd(final char aChar, final char aWhiteChar, final int aLen,
			final int aTextOffset, final String aOneLineText) {

		String wLine = generateLineBeginEnd(aChar, aWhiteChar, aLen);

		if (aOneLineText != null && !aOneLineText.isEmpty()) {

			String wText = truncate(aOneLineText, aLen - (aTextOffset + 2 + 2));

			int wLen = aLen - (aLen - (2 + aTextOffset + wText.length()));

			wLine = wLine.substring(0, 2 + aTextOffset) + wText + wLine.substring(wLen);
		}
		return toUnbreakable(wLine);
	}

	/**
	 * Sample (char = '+' & whitechar='.' )
	 * 
	 * <pre>
	 * 		+.................Step=[ 1] : iaculis tempus habitasse deserunt mollit vestibulum ultricies gravida..................................................+
	 * 		+.Step=[ 1] : iaculis tempus habitasse deserunt mollit vestibulum ultricies gravida..................................................+
	 * </pre>
	 * 
	 * @param aChar
	 * @param aWhiteChar
	 * @param aLen
	 * @param aOneLineText
	 * @return
	 */
	public static String generateLineBeginEnd(final char aChar, final char aWhiteChar, final int aLen,
			final String aOneLineText) {

		return generateLineBeginEnd(aChar, aWhiteChar, aLen, 0, aOneLineText);
	}

	/**
	 * Sample (char = '+' & whitechar=CHAR_SPACE_UNBREAKABLE )
	 * 
	 * <pre>
	 * 		+                                                                                                                                                  +
	 * </pre>
	 * 
	 * @param aChar
	 * @param aLen
	 * @return
	 */
	public static String generateLineBeginEnd(final char aChar, final int aLen) {
		return generateLineBeginEnd(aChar, CHAR_SPACE_UNBREAKABLE, aLen);
	}

	/**
	 * @param aChar
	 * @param aLen
	 * @param aTextOffset
	 * @param aOneLineText
	 * @return
	 */
	public static String generateLineBeginEnd(final char aChar, final int aLen, final int aTextOffset,
			final String aOneLineText) {
		return generateLineBeginEnd(aChar, CHAR_SPACE_UNBREAKABLE, aLen, aTextOffset, aOneLineText);

	}

	/**
	 * Sample (char = '+' & whitechar=CHAR_SPACE_UNBREAKABLE )
	 * 
	 * <pre>
	 * 		+                 Step=[ 1] : iaculis tempus habitasse deserunt mollit vestibulum ultricies gravida                                                +
	 * </pre>
	 * 
	 * @param aChar
	 * @param aLen
	 * @param aOneLineText
	 * @return
	 */
	public static String generateLineBeginEnd(final char aChar, final int aLen, final String aOneLineText) {
		return generateLineBeginEnd(aChar, CHAR_SPACE_UNBREAKABLE, aLen, aOneLineText);
	}

	/**
	 * Sample
	 * 
	 * <pre>
	 * 		---- Output begin -----------------------------------------------------------------------------------------------------------------------------------
	 * </pre>
	 * 
	 * @param aOneLineText
	 * @param aChar
	 * @param aLen
	 * @return
	 */
	public static String generateLineLabel(final char aChar, final int aLen, final String aOneLineText) {

		String wLine = generateLine(aChar, aLen);

		if (aOneLineText != null && !aOneLineText.isEmpty()) {

			String wLabel = CHAR_SPACE_UNBREAKABLE + truncate(toUnbreakable(aOneLineText), aLen - 10)
					+ CHAR_SPACE_UNBREAKABLE;

			int wLen = aLen - (aLen - (4 + wLabel.length()));

			wLine = wLine.substring(0, 4) + wLabel + wLine.substring(wLen);
		}

		return wLine;
	}

	/**
	 * @param aLine
	 * @return
	 */
	public static String toBreakable(final String aLine) {
		return aLine.replace(CHAR_SPACE_UNBREAKABLE, CHAR_SPACE);
	}

	/**
	 * @param aLine
	 * @return
	 */
	public static String toUnbreakable(final String aLine) {
		return aLine.replace(CHAR_SPACE, CHAR_SPACE_UNBREAKABLE);
	}

	/**
	 * @param aText
	 * @param aLen
	 * @return
	 */
	public static String truncate(final String aText, final int aLen) {
		return (aText != null && aText.length() > aLen) ? aText.substring(0, aLen) : aText;
	}

	/**
	 * never instanciate a Helper
	 */
	private CXTextLineUtils() {
		super();
	}

}
