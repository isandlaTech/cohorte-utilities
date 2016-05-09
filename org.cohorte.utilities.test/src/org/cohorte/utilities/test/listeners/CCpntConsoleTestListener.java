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

package org.cohorte.utilities.test.listeners;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.cohorte.remote.IRemoteServicesConstants;

/**
 * Console Test listener.
 *
 * Test Results are writen to default system out and err.
 *
 * @author ashahwan & bdebbabi
 *
 */
@Component(name = "Cohorte-CCpntConsoleTestListener-factory")
@Provides(specifications = { TestListener.class })
public class CCpntConsoleTestListener implements TestListener {

	/**
	 * The "pelix.remote.export.reject" property limits the remote export of the
	 * service
	 */
	@ServiceProperty(name = IRemoteServicesConstants.PROP_EXPORT_REJECT, immutable = true)
	private final String pRejectExport = TestListener.class.getName();

	@Override
	public void addError(final Test test, final Throwable e) {
		System.err.println(String.format("Error in test [%s]!\n%s", test
				.getClass().getName(), e.getMessage()));
	}

	@Override
	public void addFailure(final Test test, final AssertionFailedError e) {
		System.err.println(String.format("Test [%s] failed!\n%s", test
				.getClass().getName(), e.getMessage()));
	}

	@Override
	public void endTest(final Test test) {
		System.out
				.println(String.format("Test [%s] finished.", test.toString()));
	}

	@Override
	public void startTest(final Test test) {
		System.out
				.println(String.format("Test [%s] started.", test.toString()));
	}

}
