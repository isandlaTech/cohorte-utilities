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
import junit.runner.BaseTestRunner;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.psem2m.isolates.base.IIsolateLoggerSvc;

/**
 * Logger Test listener.
 *
 * Test Results are writen to Cohorte Isolate logger.
 *
 * @author bdebbabi
 *
 */
@Component(name = "Cohorte-CCpntLoggerTestListener-Factory")
@Provides(specifications = { TestListener.class })
public class CCpntLoggerTestListener implements TestListener {

	private static final String PROP_EXPORT_REJECT = "pelix.remote.export.reject";

	/** Cohorte Isolate logger, injected by iPOJO */
	@Requires
	private IIsolateLoggerSvc pLogger;

	/**
	 * The "pelix.remote.export.reject" property limits the remote export of the
	 * service
	 */
	@ServiceProperty(name = PROP_EXPORT_REJECT, immutable = true)
	private final String pRejectExport = TestListener.class.getName();

	@Override
	public void addError(final Test test, final Throwable e) {
		pLogger.logDebug(this, "addError", "Error in test [%s]!\n%s", test
				.getClass().getName(), BaseTestRunner.getFilteredTrace(e));
	}

	@Override
	public void addFailure(final Test test, final AssertionFailedError e) {
		pLogger.logDebug(this, "addFailure", "Test [%s] failed!\n%s", test
				.getClass().getName(), BaseTestRunner.getFilteredTrace(e));
	}

	@Override
	public void endTest(final Test test) {
		pLogger.logDebug(this, "endTest", "Test [%s] succeeded.",
				test.toString());
	}

	@Override
	public void startTest(final Test test) {
		pLogger.logDebug(this, "startTest", "Test [%s] started.",
				test.toString());
	}

}
