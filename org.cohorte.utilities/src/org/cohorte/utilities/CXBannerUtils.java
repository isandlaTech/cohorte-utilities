package org.cohorte.utilities;

import java.util.ArrayList;

/**
 * MOD_OG_20210823
 * 
 * Building of banners as those générated by the test class "CTestBannerUtils"
 * 
 * <pre>
 * CXArtSetting wSetting = new CXArtSetting(new Font(CXAsciiArt.FONT_MONOSPACED, Font.PLAIN, 20), '.', '@');
 * 
 * String wArtContent = new CXAsciiArt().drawString('sed Ipsum');
 * 
 * int wLenMaxOfLines = CXBannerUtils.lenMaxOfLines(wArtContent);
 * 
 * StringBuilder wText = new StringBuilder(wArtContent);
 * wText.append('\n').append(randomWords(wLenMaxOfLines));
 * wText.append('\n').append(randomWords(wLenMaxOfLines));
 * 
 * String wBanner = CXBannerUtils.build('#', '.', -1, 0, CXBannerUtils.WITH_BLANK_LINES, wText.toString());
 * </pre>
 * 
 * <pre>
 * #########################################################################################################################
 * #.......................................................................................................................#
 * #.....................................@@@..................@@@..........................................................#
 * #.....................................@@@..................@@@..........................................................#
 * #.....................................@@@..................@@@..........................................................#
 * #.....................................@@@..................@@@..........................................................#
 * #.........@@@@@.......@@@@@......@@@@@@@@................@@@@@.....@@@@@@@@.......@@@@@....@@@....@@@.@@@@@@.@@@@.......#
 * #.......@@@@@@@@....@@@@@@@@....@@@@@@@@@...............@@@@@@.....@@@@@@@@@....@@@@@@@@...@@@....@@@.@@@@@@@@@@@.......#
 * #.......@@@@@@@@....@@@@@@@@@..@@@@@@@@@@...............@@@@@@.....@@@@@@@@@@...@@@@@@@@...@@@....@@@.@@@@@@@@@@@@......#
 * #.......@@@....@...@@@@...@@@..@@@@..@@@@..................@@@.....@@@@..@@@@...@@@....@...@@@....@@@.@@@@@@@@.@@@......#
 * #.......@@@........@@@@@@@@@@..@@@...@@@@..................@@@.....@@@@...@@@...@@@........@@@....@@@.@@@..@@@.@@@......#
 * #.......@@@@@@@....@@@@@@@@@@@.@@@....@@@..................@@@.....@@@....@@@...@@@@@@@....@@@....@@@.@@@..@@@.@@@......#
 * #.......@@@@@@@@...@@@@@@@@@@@.@@@....@@@..................@@@.....@@@....@@@...@@@@@@@@...@@@....@@@.@@@..@@..@@@......#
 * #.........@@@@@@...@@@.........@@@....@@@..................@@@.....@@@....@@@.....@@@@@@...@@@....@@@.@@@..@@..@@@......#
 * #............@@@@..@@@.........@@@...@@@@..................@@@.....@@@@...@@@........@@@@..@@@@..@@@@.@@@..@@..@@@......#
 * #......@@@...@@@@..@@@@....@@..@@@@..@@@@..................@@@.....@@@@..@@@@..@@@...@@@@..@@@@..@@@@.@@@..@@..@@@......#
 * #......@@@@@@@@@....@@@@@@@@@..@@@@@@@@@@...............@@@@@@@@@@.@@@@@@@@@@..@@@@@@@@@....@@@@@@@@@.@@@..@@..@@@......#
 * #......@@@@@@@@@....@@@@@@@@@...@@@@@@@@@...............@@@@@@@@@@.@@@@@@@@@...@@@@@@@@@....@@@@@@@@@.@@@..@@..@@@......#
 * #........@@@@@.........@@@@.......@@@..............................@@@.@@@.......@@@@@.......@@@@.......................#
 * #..................................................................@@@..................................................#
 * #..................................................................@@@..................................................#
 * #..................................................................@@@..................................................#
 * #..................................................................@@@..................................................#
 * #.......................................................................................................................#
 * #.Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore ..#
 * #.magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd .....#
 * #.gubergren, no sea takimata sanctus....................................................................................#
 * #.......................................................................................................................#
 * #.Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore ..#
 * #.magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd .....#
 * #.gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing .#
 * #.elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,................................#
 * #.......................................................................................................................#
 * #########################################################################################################################
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

	public static final char CHAR_SPACE = CXTextLineUtils.CHAR_SPACE;

	public static final char CHAR_SPACE_UNBREAKABLE = CXTextLineUtils.CHAR_SPACE_UNBREAKABLE;
	public static final int NO_TEXT_OFFSET = 0;

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
	 * @param aBorderChar
	 * @param aWidth
	 * @param aText
	 * @return
	 */
	public static String build(final char aBorderChar, final int aWidth, final String aText) {

		return build(aBorderChar, CHAR_SPACE_UNBREAKABLE, aWidth, NO_TEXT_OFFSET, WITH_BLANK_LINES, aText);
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
