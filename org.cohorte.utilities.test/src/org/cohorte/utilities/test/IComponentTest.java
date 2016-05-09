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

import junit.framework.Test;

/**
 * Interface to represent a test case on iPOJO Component.
 *
 * You should extends CComponentTestBase class rather than implements directly
 * this interface. If not, you should call the super constructor with "testAll"
 * string as parameter. The new class should be an iPOJO component providing
 * this IComponentTest interface.
 *
 * @author bdebbabi
 *
 */
public interface IComponentTest extends Test {
	/**
	 * Test all cases of the current component.
	 *
	 * @throws Exception
	 */
	void testAll() throws Exception;
}
