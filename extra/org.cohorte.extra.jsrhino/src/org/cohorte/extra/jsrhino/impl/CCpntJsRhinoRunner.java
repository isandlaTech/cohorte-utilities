package org.cohorte.extra.jsrhino.impl;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.ipojo.annotations.Validate;
import org.cohorte.extra.jsrhino.IJsRhinoRunner;
import org.cohorte.remote.IRemoteServicesConstants;
import org.mozilla.javascript.Context;
import org.psem2m.isolates.base.IIsolateLoggerSvc;

import de.christophkraemer.rhino.javascript.RhinoScriptEngineFactory;

@Component(name = "cohorte-isolate-CCpntJsRhinoRunner-Factory")
@Instantiate(name = "cohorte-isolate-CCpntJsRhinoRunner")
@Provides(specifications = { IJsRhinoRunner.class })
public class CCpntJsRhinoRunner implements IJsRhinoRunner {

	/**
	 * The "pelix.remote.export.reject" property limits the remote export of the
	 * service
	 */
	@ServiceProperty(name = IRemoteServicesConstants.PROP_EXPORT_REJECT, immutable = true)
	private final String pBaseXShellCommandsNotRemote = IJsRhinoRunner.class.getName();

	/**
	 * Cohorte Logger.
	 */
	@Requires
	private IIsolateLoggerSvc pLogger;

	/**
	 *
	 */
	public CCpntJsRhinoRunner() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.extra.jsrhino.IJsRhinoRunner#getEngineFactory()
	 */
	private ScriptEngineFactory getEngineFactory() {
		return new RhinoScriptEngineFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cohorte.extra.jsrhino.IJsRhinoRunner#getManager()
	 */
	@Override
	public ScriptEngineManager getManager() {
		ScriptEngineManager wScriptEngineManager = new ScriptEngineManager();
		wScriptEngineManager.registerEngineName(JS_RHINO_NAME, getEngineFactory());
		return wScriptEngineManager;
	}

	/**
	 *
	 */
	@Invalidate
	public void invalidate() {
		pLogger.logInfo(this, "invalidate", "invalidating...");

		// raz

		pLogger.logInfo(this, "invalidate", "invalidated");
	}

	/**
	 *
	 */
	@Validate
	public void validate() {
		pLogger.logInfo(this, "validate", "Validating...");

		try {
			String wImplementationVersion = Context.enter().getImplementationVersion();

			pLogger.logInfo(this, "validate", "org.mozilla.rhino ImplementationVersion=[%s]", wImplementationVersion);

		} catch (Throwable e) {
			pLogger.logInfo(this, "validate", "ERROR %s", e);
		}

		pLogger.logInfo(this, "validate", "Validated. ");
	}
}
