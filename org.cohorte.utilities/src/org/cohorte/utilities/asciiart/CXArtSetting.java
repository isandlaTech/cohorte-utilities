package org.cohorte.utilities.asciiart;

import java.awt.Font;

/**
 * @author ogattaz
 *
 */
public class CXArtSetting {

	private final char pBlackChar;

	private final Font pFont;

	private final char pWhiteChar;

	/**
	 * 
	 */
	public CXArtSetting() {

		this(new Font(CXAsciiArt.FONT_SANSERIF, Font.BOLD, 24), '-', '@');
	}

	/**
	 * @param afont
	 * @param width
	 * @param height
	 */
	public CXArtSetting(Font afont, final char aWhiteChar, final char aBlackChar) {

		super();

		pFont = afont;

		pWhiteChar = aWhiteChar;

		pBlackChar = aBlackChar;
	}

	/**
	 * @param aFontFamily
	 * @param wFontStyle
	 * @param wFontSize
	 * @param aWhiteChar
	 * @param aBlackChar
	 */
	public CXArtSetting(final String aFontFamily, final int wFontStyle, final int wFontSize, final char aWhiteChar,
			final char aBlackChar) {

		this(new Font(aFontFamily, wFontStyle, wFontSize), aWhiteChar, aBlackChar);
	}

	/**
	 * @return
	 */
	public char getBlackChar() {
		return pBlackChar;
	}

	/**
	 * @return
	 */
	public Font getFont() {
		return pFont;
	}

	/**
	 * @return
	 */
	public char getWhiteChar() {
		return pWhiteChar;
	};

}
