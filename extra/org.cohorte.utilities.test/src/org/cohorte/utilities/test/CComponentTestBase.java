/**
 * Copyright 2016 isandlaTech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cohorte.utilities.test;

import java.io.InputStream;
import java.util.Locale;

import junit.framework.TestCase;

import org.psem2m.utilities.CXBytesUtils;
import org.psem2m.utilities.CXResources;
import org.psem2m.utilities.CXStringUtils;

/**
 * Abstract class used by components providing IComponentTest service.
 *
 * It fixes the name of the method to be called for each included test case.
 *
 * @author Ahmad Shahwan
 *
 */
public abstract class CComponentTestBase extends TestCase implements
		IComponentTest {

	protected class CTestInputs extends CXResources {

		public String getTestInput(final String aResourceFullname) {
			try {
				// TODO set local to null (see issue #4)
				InputStream wIS = findResourceStream(aResourceFullname,
						Locale.ENGLISH, null);
				return CXStringUtils.strFromInputStream(wIS,
						CXBytesUtils.ENCODING_UTF_8);
			} catch (Exception e) {
				return null;
			}
		}
	}

	protected CTestInputs pTestInputs = new CTestInputs();

	public CComponentTestBase() {
		/**
		 * Add name of the method to be run. This is necessary when adding test
		 * case as an object (not a class) to a test suite.
		 */
		super("testAll");
	}

	/**
	 *
	 * @param aResourceId
	 *            Full file name (package name + file name + extension). E.g.:
	 *
	 *            <pre>
	 * fr.agilium.ng.server.xmldb.test.components.inputs.testSinglePutGet_1.txt
	 * </pre>
	 * @param aFromLine
	 * @param aToLine
	 * @return
	 */
	protected String getTestInput(final String aResourceId,
			final String aFromLine, final String aToLine) throws Exception {
		String wContent = pTestInputs.getTestInput(aResourceId);
		if (wContent != null) {
			String[] wContentLines = wContent.split("\n");
			int wContentLinesTotal = wContentLines.length;

			int wFrom = 1;
			if (aFromLine != null) {
				wFrom = new Integer(aFromLine).intValue();
				if (wFrom < 1) {
					wFrom = 1;
				}
			}
			int wTo = wContentLinesTotal;
			if (aToLine != null) {
				if (aToLine.equalsIgnoreCase("*")) {
					wTo = wContentLinesTotal;
				} else {
					wTo = new Integer(aToLine).intValue();
					if (wTo < 1) {
						wTo = 1;
					} else if (wTo > wContentLinesTotal) {
						wTo = wContentLinesTotal;
					}
				}
			}
			if (wTo < wFrom) {
				throw new Exception(String.format(
						"To line [%d] is small than From line [%d]!", wTo,
						wFrom));
			}
			if (wContentLinesTotal > 0) {
				StringBuilder wResult = new StringBuilder(
						wContentLines[wFrom - 1]);
				for (int i = wFrom; i < wTo; i++) {
					wResult.append("\n").append(wContentLines[i]);
				}
				return wResult.toString();
			}
			return wContent;
		} else {
			throw new Exception(String.format("RessourceId [%s] not found!",
					aResourceId));
		}
	}
}