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

package org.cohorte.utilities.test.shell;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.cohorte.remote.IRemoteServicesConstants;
import org.cohorte.utilities.test.IShellCommand;
import org.cohorte.utilities.test.ITestExecuter;
import org.osgi.framework.BundleContext;
import org.psem2m.isolates.base.IIsolateLoggerSvc;

@Component(name = "Cohorte-CCpntTestCommand-factory")
@Provides(specifications = { IShellCommand.class })
public class CCpntTestCommand implements IShellCommand {

	/** OSGi Bundle Context */
	private BundleContext pBundleContext;

	/** The Gogo commands */
	@ServiceProperty(name = "osgi.command.function", value = "{test}")
	private String[] pCommands;

	/** Test Executor **/
	@Requires
	ITestExecuter pExecuter;

	/** Cohorte Logger service */
	@Requires
	private IIsolateLoggerSvc pLogger;

	/**
	 * The "pelix.remote.export.reject" property limits the remote export of the
	 * service
	 */
	@ServiceProperty(name = IRemoteServicesConstants.PROP_EXPORT_REJECT, immutable = true)
	private final String pRejectExport = IShellCommand.class.getName();

	/** The Gogo commands scope */
	@ServiceProperty(name = "osgi.command.scope", value = "cohorte")
	private String pScope;

	/**
	 * Constructor
	 *
	 * @param aBundleContext
	 */
	public CCpntTestCommand(final BundleContext aBundleContext) {

	}

	public synchronized void test() {
		pExecuter.run();
	}

}
