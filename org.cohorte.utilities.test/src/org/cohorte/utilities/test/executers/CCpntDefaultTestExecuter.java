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

package org.cohorte.utilities.test.executers;

import java.util.Arrays;
import java.util.Comparator;

import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.cohorte.remote.IRemoteServicesConstants;
import org.cohorte.utilities.test.IBundleTest;
import org.cohorte.utilities.test.ITestExecuter;
import org.psem2m.isolates.base.IIsolateLoggerSvc;

/**
 * Component implementing the default behavior of a Test Executer.
 *
 * It aggregates all Bundle Tests of the local Cohorte Isolate and launches
 * their test cases. Executor aggregates also a set of TestListeners so that the
 * results could be published.
 *
 * @author bdebbabi
 *
 */
@Component(name = "Cohorte-CCpntDefaultTestExecuter-factory")
@Provides(specifications = { ITestExecuter.class })
public class CCpntDefaultTestExecuter implements ITestExecuter {

	@Requires
	private IBundleTest[] pBundleTests;

	/** Cohorte Isolate logger, injected by iPOJO */
	@Requires
	private IIsolateLoggerSvc pLogger;

	/**
	 * The "pelix.remote.export.reject" property limits the remote export of the
	 * service
	 */
	@ServiceProperty(name = IRemoteServicesConstants.PROP_EXPORT_REJECT, immutable = true)
	private final String pRejectExport = ITestExecuter.class.getName();

	/** Test Listeners **/
	@Requires
	private TestListener[] pTestListeners;

	/*
	 * @Bind public void bindTestListener(final TestListener aListener) {
	 * synchronized (pTestListeners) { pTestListeners.add(aListener); } }
	 */

	@Override
	public void run() {
		IBundleTest[] wTests = this.pBundleTests.clone();

		TestResult aResult = new TestResult();
		synchronized (pTestListeners) {
			for (TestListener wListener : pTestListeners) {
				aResult.addListener(wListener);
			}
		}

		Arrays.sort(wTests, new Comparator<IBundleTest>() {
			@Override
			public int compare(final IBundleTest o1, final IBundleTest o2) {
				return o1.getOrder() - o2.getOrder();
			}
		});
		this.pLogger.logInfo(this, "run", "Aggrecation [%d] test(s).",
				wTests.length);
		TestSuite wSuite = new TestSuite("OSGi Bundles Test Aggregation");
		for (IBundleTest wTest : wTests) {
			this.pLogger.logInfo(this, "run", "Adding [%s] to test suite.",
					wTests.toString());
			wSuite.addTest(wTest);
		}
		this.pLogger.logInfo(this, "run", "Running test suite.");
		wSuite.run(aResult);
		this.pLogger.logInfo(this, "run", "Test successful? [%s].",
				aResult.wasSuccessful());
	}
	/*
	 * @Unbind public synchronized void unbindTestListener(final TestListener
	 * aListener) { synchronized (pTestListeners) {
	 * pTestListeners.remove(aListener); } }
	 */
}
