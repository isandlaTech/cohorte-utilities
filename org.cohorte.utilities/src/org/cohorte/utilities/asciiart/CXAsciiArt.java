package org.cohorte.utilities.asciiart;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author ogattaz
 * 
 * @see https://www.baeldung.com/ascii-art-in-java
 *
 */
public class CXAsciiArt {

	/**
	 * @author ogattaz
	 *
	 */
	class CAsciiArtLine {

		final int pLastBlackPointIdx;
		final StringBuilder pLine;

		/**
		 * @param aLine
		 * @param aLastBlackPointIdx
		 */
		CAsciiArtLine(final StringBuilder aLine, final int aLastBlackPointIdx) {
			super();
			pLine = aLine;
			pLastBlackPointIdx = aLastBlackPointIdx;
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
	class CAsciiArtMatrix extends ArrayList<CAsciiArtLine> {

		private static final long serialVersionUID = 332556891958313861L;

		private int pLenMax = 0;

		/**
		 * 
		 */
		CAsciiArtMatrix() {
			super();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.ArrayList#add(java.lang.Object)
		 */
		@Override
		public boolean add(final CAsciiArtLine aLine) {

			pLenMax = Math.max(pLenMax, aLine.pLastBlackPointIdx);
			return super.add(aLine);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.AbstractCollection#toString()
		 */
		@Override
		public String toString() {

			StringBuilder wSB = new StringBuilder();

			for (CAsciiArtLine wAsciiArtLine : this) {
				if (wSB.length() > 0) {
					wSB.append('\n');
				}
				wSB.append(wAsciiArtLine.toString(pLenMax + 1));
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

	public static final String[] FONTS = new String[] {
			//
			FONT_COURIER,
			//
			FONT_DIALOG,
			//
			FONT_HELVETICA,
			//
			FONT_MONOSPACED,
			//
			FONT_SANSERIF,
			//
			FONT_TIMES,
			//
			FONT_WINDINGS };

	private final int OFFSET_X_BEGIN = 4;

	private final int OFFSET_X_END = OFFSET_X_BEGIN;

	private final char pBlackChar;

	private final int pMaxY;

	private final CXArtSetting pSettings;

	private final char pWhiteChar;

	/**
	 * 
	 */
	public CXAsciiArt() {
		this(new CXArtSetting());
	}

	/**
	 * @param aSettings
	 */
	public CXAsciiArt(final CXArtSetting aSettings) {
		super();

		pSettings = aSettings;

		pWhiteChar = pSettings.getWhiteChar();

		pBlackChar = pSettings.getBlackChar();

		pMaxY = new Double(pSettings.getFont().getSize() * 1.5).intValue();

	}

	/**
	 * @param aText
	 * @param aArtChar
	 * @param aSettings
	 */
	public String drawString(String aText) {

		final int wMaxX = pMaxY * aText.length();

		final BufferedImage wImage = getImageIntegerMode(wMaxX, pMaxY);

		final Graphics2D wGraphics2D = newGraphics2D(wImage);

		wGraphics2D.drawString(aText, OFFSET_X_BEGIN, ((int) (pMaxY * 0.67)));

		final CAsciiArtMatrix wAsciiArtMatrix = new CAsciiArtMatrix();

		StringBuilder wLine;

		for (int y = 0; y < pMaxY; y++) {

			wLine = new StringBuilder();

			boolean wLineHasBlackPoint = false;
			int wLastBlackPointIdx = 0;

			for (int x = 0; x < wMaxX; x++) {

				boolean wIsWhitePoint = wImage.getRGB(x, y) == -16777216;

				wLine.append(wIsWhitePoint ? pWhiteChar : pBlackChar);

				wLineHasBlackPoint = wLineHasBlackPoint || !wIsWhitePoint;

				if (!wIsWhitePoint) {
					wLastBlackPointIdx = Math.max(wLastBlackPointIdx, x);
				}
			}

			// System.out.println(wLine);

			// if not an empty line
			if (wLineHasBlackPoint) {

				wAsciiArtMatrix.add(new CAsciiArtLine(wLine, wLastBlackPointIdx + OFFSET_X_END));
			}

		}

		return wAsciiArtMatrix.toString();
	}

	/**
	 * @param width
	 * @param height
	 * @return
	 */
	private BufferedImage getImageIntegerMode(int width, int height) {

		return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * @param aGraphics
	 * @param aSettings
	 * @return
	 */
	private Graphics2D newGraphics2D(BufferedImage aImage) {

		Graphics wGraphics = aImage.getGraphics();

		wGraphics.setFont(pSettings.getFont());

		Graphics2D wGraphics2D = (Graphics2D) wGraphics;

		wGraphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		return wGraphics2D;
	}

}
