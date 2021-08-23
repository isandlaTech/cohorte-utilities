package org.cohorte.utilities;

import java.util.ArrayList;

/**
 * MOD_OG_20210823
 * 
 * Building of banners as those générated by the test class "CTestBannerUtils"
 * 
 * <pre>
 * 		################################################################################################################################################################
 * 		#                                                                                                                                                              #
 * 		#                                                                                                  ****                                                        #
 * 		#                       *******             ********              *************                    ****                                                        #
 * 		#                     ***********         ***********             **************                   ****                                                        #
 * 		#                   ***************      ************             **************                   ****                                                        #
 * 		#                  *****************    *************             **************                   ****                                                        #
 * 		#                  ******     ******    *****      **             *****                            ****                                                        #
 * 		#                 ******        *****   ****                      *****                            ****                                                        #
 * 		#                 *****         ******  *****                     *****             ********       ****  ******                                                #
 * 		#                *****           *****  *****                     *****           ***********      **** *********                                              #
 * 		#                *****           *****  *******                   *****           ************     **************                                              #
 * 		#                *****            ****   *******                  *****           ************     ***************                                             #
 * 		#                *****            ****   *********                ************    **     *****     *******   *****                                             #
 * 		#                *****            ****    *********               ************            ****     *****      *****                                            #
 * 		#                *****            ****      *********             ************            ****     ****       *****                                            #
 * 		#                *****            ****       ********             ************       *********     ****       *****                                            #
 * 		#                *****            ****         *******            *****            ***********     ****       *****                                            #
 * 		#                *****           *****           *****            *****           ************     ****       *****                                            #
 * 		#                ******          *****           *****            *****          ******   ****     ****       *****                                            #
 * 		#                 *****         ******            ****            *****          *****    ****     ****       *****                                            #
 * 		#                 ******        *****             ****            *****          ****     ****     *****      ****                                             #
 * 		#                  ******     *******   ***      *****            *****          *****   *****     ******    *****                                             #
 * 		#                  *****************    **************            *****          ***************   **************                                              #
 * 		#                    **************     *************             *****          ***************   **************                                              #
 * 		#                     *************     ************              *****           ******** *****   *************                                               #
 * 		#                       **************    ********                                  ****    ****   ****  *****                                                 #
 * 		#                             ***********                                                                                                                      #
 * 		#                               *********                                                                                                                      #
 * 		#                                 ******                                                                                                                       #
 * 		#                                    **                                                                                                                        #
 * 		#                                                                                                                                                              #
 * 		################################################################################################################################################################
 * </pre>
 * 
 * 
 * @author ogattaz
 *
 */
public class CXBannerUtils {

	/**
	 * @author ogattaz
	 *
	 */
	public class CXBannerLine {

		final int pLastNoWhiteCharIdx;
		final String pLine;

		/**
		 * @param aLine
		 * @param aLastBlackPointIdx
		 */
		CXBannerLine(final String aLine) {
			super();
			pLine = aLine;
			pLastNoWhiteCharIdx = aLine.trim().length();
		}

		/**
		 * @return
		 */
		int getLastNoWhiteCharIdx() {
			return pLastNoWhiteCharIdx;
		}

		/**
		 * @return
		 */
		int length() {
			return pLine.length();
		}

		/**
		 * @param aLen
		 * @return
		 */
		String toString(int aLen) {
			return pLine.substring(0, aLen);
		}
	}

	/**
	 * @author ogattaz
	 *
	 */
	public class CXBannertMatrix extends ArrayList<CXBannerLine> {

		private static final long serialVersionUID = 332556891958313861L;

		private int pLenMax = 0;

		/**
		 * 
		 */
		CXBannertMatrix() {
			super();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ArrayList#add(java.lang.Object)
		 */
		@Override
		public boolean add(final CXBannerLine aLine) {

			pLenMax = Math.max(pLenMax, aLine.length());
			return super.add(aLine);
		}

		/**
		 * @param aLine
		 * @return
		 */
		public boolean add(final String aLine) {

			return this.add(sXBannerUtils.new CXBannerLine(aLine));
		}

		/**
		 * @return
		 */
		int getLenMax() {
			return pLenMax;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.AbstractCollection#toString()
		 */
		@Override
		public String toString() {

			StringBuilder wSB = new StringBuilder();

			for (CXBannerLine wBannerLine : this) {
				if (wSB.length() > 0) {
					wSB.append('\n');
				}
				wSB.append(wBannerLine.toString(pLenMax));
			}
			return wSB.toString();
		}
	}

	public static final String FONT_COURIER = "Courier";
	public static final String FONT_DIALOG = "Dialog";
	public static final String FONT_HELVETICA = "Helvetica";
	public static final String FONT_MONOSPACED = "Monospaced";
	public static final String FONT_SANSERIF = "SansSerif";
	public static final String FONT_TIMES = "TimesRoman";
	public static final String FONT_WINDINGS = "Windings";

	public static final String[] FONTS = new String[] { FONT_COURIER, FONT_DIALOG, FONT_HELVETICA, FONT_MONOSPACED,
			FONT_SANSERIF, FONT_TIMES, FONT_WINDINGS };

	private static CXBannerUtils sXBannerUtils = new CXBannerUtils();

	public static final int WITH_BLANK_LINE_FISRT = 2;
	public static final int WITH_BLANK_LINE_LAST = 4;
	public static final int WITH_BLANK_LINES = WITH_BLANK_LINE_FISRT + WITH_BLANK_LINE_LAST;

	public static final int WITH_INTERLIGNE = 1;

	/**
	 * @param aBorderChar
	 * @param aWhiteChar
	 * @param aWidth
	 * @param aTextOffset
	 * @param aModifiers
	 * @param aText
	 * @return
	 */
	public static String build(final char aBorderChar, final char aWhiteChar, final int aWidth, final int aTextOffset,
			final int aModifiers, final String aText) {

		CXBannertMatrix wMatrix = sXBannerUtils.new CXBannertMatrix();

		int wTextOffset = (aTextOffset > 0) ? aTextOffset : 0;

		// if the width of the banner is defined by its the content
		int wWidth = (aWidth < 0) ? lenMaxOfLines(aText) + wTextOffset + 4 : aWidth;

		String wLineFull = CXTextLineUtils.generateLine(aBorderChar, wWidth);
		String wLineBeginEnd = CXTextLineUtils.generateLineBeginEnd(aBorderChar, aWhiteChar, wWidth);

		// adds first lines
		wMatrix.add(wLineFull);

		if (isTrue(WITH_BLANK_LINE_FISRT, aModifiers)) {
			wMatrix.add(wLineBeginEnd);
		}

		boolean wWithInterligne = isTrue(WITH_INTERLIGNE, aModifiers);

		// adds the lines of aText
		int wIdx = 0;
		for (String wOneLine : aText.split("\n")) {

			if (wWithInterligne && wIdx > 0) {
				wMatrix.add(CXTextLineUtils.generateLineBeginEnd(aBorderChar, aWhiteChar, aWidth));
			}
			wMatrix.add(CXTextLineUtils.generateLineBeginEnd(aBorderChar, aWhiteChar, wWidth, wTextOffset, wOneLine));
			wIdx++;
		}

		// adds the last lines
		if (isTrue(WITH_BLANK_LINE_LAST, aModifiers)) {
			wMatrix.add(wLineBeginEnd);
		}

		wMatrix.add(wLineFull);

		return wMatrix.toString();
	}

	/**
	 * @param aBorderChar
	 * @param aWhiteChar
	 * @param aWidth
	 * @param aTextOffset
	 * @param aText
	 * @return
	 */
	public static String build(final char aBorderChar, final char aWhiteChar, final int aWidth, final int aTextOffset,
			final String aText) {

		return build(aBorderChar, aWhiteChar, aWidth, aTextOffset, WITH_BLANK_LINES, aText);
	}

	/**
	 * @param aMask
	 * @param aValue
	 * @return
	 */
	private static boolean isTrue(final int aMask, final int aValue) {

		return (aMask & aValue) > 0;
	}

	/**
	 * @param aText
	 * @return
	 */
	public static int lenMaxOfLines(final String aText) {

		CXBannertMatrix wMatrix = sXBannerUtils.new CXBannertMatrix();
		for (String wOneLine : aText.split("\n")) {
			wMatrix.add(wOneLine);
		}
		return wMatrix.getLenMax();
	}

	/**
	 * 
	 */
	private CXBannerUtils() {
		super();
	}
}
