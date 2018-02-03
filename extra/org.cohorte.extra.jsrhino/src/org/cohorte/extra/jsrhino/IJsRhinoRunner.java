package org.cohorte.extra.jsrhino;

import javax.script.ScriptEngineManager;

/**
 * @author ogattaz
 *
 */
public interface IJsRhinoRunner {

	String JS_RHINO_NAME = "rhino";

	ScriptEngineManager getManager();

}
