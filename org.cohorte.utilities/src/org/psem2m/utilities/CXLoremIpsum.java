package org.psem2m.utilities;

/* Copyright (c) 2008 Sven Jacobs

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

/**
 * Simple lorem ipsum text generator.
 *
 * <p>
 * Suitable for creating sample data for test cases and performance tests.
 * </p>
 *
 * @author Sven Jacobs, ogattaz
 * @version 1.0
 */
public class CXLoremIpsum {

	public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";

	public static final String[] LOREM_IPSUM_WORDS = LOREM_IPSUM.split("\\s");

	public final static char NO_SEPARATOR = (char) 0;

	/**
	 * @return
	 */
	public static int getNbWordInLoremIpsum() {
		return LOREM_IPSUM_WORDS.length;
	}

	/**
	 *
	 */
	public CXLoremIpsum() {
		super();
	}

	/**
	 * return a string containing the amount charaters build by additioning the
	 * words of Lorem Ipsum using a space character as separator.
	 *
	 * @param amount
	 *            Amount of characters.
	 * @return a string containing the amount charaters
	 */
	public String getCharacters(int amount) {

		return getCharacters(amount, ' ');
	}

	/**
	 * return a string containing the amount charaters build by additioning the
	 * words of Lorem Ipsum
	 *
	 * @param amount
	 *            Amount of characters.
	 * @param aSeparator
	 *            Give NO_SEPARATOR to have no separator between the words
	 * @return a string containing the amount charaters
	 */
	public String getCharacters(int amount, char aSeparator) {

		final StringBuilder lorem = new StringBuilder();

		// if less than 0 !
		if (amount < 0) {
			amount = -1 * amount;
		}

		int wNbChars = 0;
		while (wNbChars < amount) {

			for (String wWord : LOREM_IPSUM_WORDS) {

				wWord = CXStringUtils.removeChars(",.", wWord);
				lorem.append(wWord);
				wNbChars += wWord.length();

				if (aSeparator != NO_SEPARATOR) {
					lorem.append(aSeparator);
					wNbChars += 1;
				}

				if (wNbChars >= amount) {
					break;
				}
			}
		}
		return lorem.substring(0, amount);
	}

	/**
	 * Returns two paragraphs of lorem ipsum.
	 *
	 * @return Lorem ipsum paragraphs
	 */
	public String getParagraphs() {
		return getParagraphs(2);
	}

	/**
	 * Returns paragraphs of lorem ipsum.
	 *
	 * @param amount
	 *            Amount of paragraphs
	 * @return Lorem ipsum paragraphs
	 */
	public String getParagraphs(int amount) {
		final StringBuilder lorem = new StringBuilder();

		for (int i = 0; i < amount; i++) {
			lorem.append(LOREM_IPSUM);

			if (i < amount - 1) {
				lorem.append('\n');
			}
		}
		return lorem.toString();
	}

	/**
	 * Returns one sentence (50 words) of the lorem ipsum text.
	 *
	 * @return 50 words of lorem ipsum text
	 */
	public String getWords() {
		return getWords(50);
	}

	/**
	 * Returns words from the lorem ipsum text.
	 *
	 * @param amount
	 *            Amount of words
	 * @return Lorem ipsum text
	 */
	public String getWords(int amount) {
		return getWords(amount, 0);
	}

	/**
	 * Returns words from the lorem ipsum text.
	 *
	 * @param amount
	 *            Amount of words
	 * @param startIndex
	 *            Start index of word to begin with (must be >= 0 and <
	 *            NbWordInLoremIpsum)
	 * @return Lorem ipsum text
	 * @throws IndexOutOfBoundsException
	 *             If startIndex is < 0 or > (NbWordInLoremIpsum-1)
	 */
	public String getWords(int amount, int startIndex) {

		final int wMax = getNbWordInLoremIpsum();

		if (startIndex < 0 || startIndex > wMax - 1) {
			throw new IndexOutOfBoundsException(String.format("startIndex must be >= 0 and < %d", wMax));
		}

		int wWordIdx = startIndex;
		final StringBuilder lorem = new StringBuilder();

		for (int i = 0; i < amount; i++) {
			if (wWordIdx == wMax) {
				wWordIdx = 0;
			}

			lorem.append(LOREM_IPSUM_WORDS[wWordIdx]);

			if (i < amount - 1) {
				lorem.append(' ');
			}

			wWordIdx++;
		}

		return lorem.toString();
	}
}
