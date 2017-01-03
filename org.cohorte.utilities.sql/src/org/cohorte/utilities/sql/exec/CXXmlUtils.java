package org.cohorte.utilities.sql.exec;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author ogattaz
 *
 */
class CXXmlUtils {

	final static String ESCAPE_CHARS = "<>&\"\'";

	final static List<String> ESCAPE_STRINGS = Collections
			.unmodifiableList(Arrays.asList(new String[] { "&lt;", "&gt;",
					"&amp;", "&quot;", "&apos;" }));

	private static String UNICODE_HIGH = "" + ((char) 0x7f);

	private static String UNICODE_LOW = "" + ((char) 0x20); // space

	/**
	 * should only use for the content of an attribute or tag
	 *
	 * @param content
	 * @return
	 */
	static String escapeXml(String content) {
		String result = content;

		if ((content != null) && (content.length() > 0)) {
			boolean modified = false;
			final StringBuilder stringBuilder = new StringBuilder(
					content.length());
			for (int i = 0, count = content.length(); i < count; ++i) {
				final String character = content.substring(i, i + 1);
				final int pos = ESCAPE_CHARS.indexOf(character);
				if (pos > -1) {
					stringBuilder.append(ESCAPE_STRINGS.get(pos));
					modified = true;
				} else {
					if ((character.compareTo(UNICODE_LOW) > -1)
							&& (character.compareTo(UNICODE_HIGH) < 1)) {
						stringBuilder.append(character);
					} else {
						stringBuilder.append("&#" + ((int) character.charAt(0))
								+ ";");
						modified = true;
					}
				}
			}
			if (modified) {
				result = stringBuilder.toString();
			}
		}

		return result;
	}
}
