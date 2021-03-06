package org.psem2m.utilities.scripting;

import java.util.Map;

import org.psem2m.utilities.logging.IActivityLogger;
import org.psem2m.utilities.rsrc.CXRsrcProvider;

/**
 * #12 Manage chains of resource providers
 *
 * @author IsandlaTech - ogattaz
 *
 */
public interface IXJsManager {

	public final static String ATTR_EVAL_DURATION = "EvalDuration";
	public final static String ATTR_HANDLER_DURATION = "HandlerDuration";

	/**
	 * @param aProviderId
	 * @param aProvider
	 */
	public void addProvider(final String aProviderId, final CXRsrcProvider aProvider);

	/**
	 * @return
	 */
	public int clearCache();

	/**
	 * get compiled script froma sourceMain
	 * 
	 * @param aSourceMainS
	 * @return
	 * @throws CXJsException
	 */
	public CXJsCompiledScript getCompiledScript(CXJsSourceMain aSourceMainS) throws CXJsException;

	/**
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningContext newRuningContext(int aBufferSize) throws Exception;

	/**
	 * @param aActivityLogger
	 * @param aMain
	 * @param aEngine
	 * @param aScriptUri
	 * @return
	 * @throws Exception
	 */
	public CXJsRunner newRunner(final IActivityLogger aActivityLogger, final CXJsSourceMain aMain,
			final CXJsEngine aEngine, final String aScriptUri) throws Exception;

	/**
	 * @param aProviderId
	 */
	public void removeProvider(final String aProviderId);

	/**
	 * run a compiled script
	 * 
	 * @param aActivityLogger
	 * @param aProviderId
	 * @param aCompiledScript
	 * @param aVariablesMap
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final CXJsCompiledScript aCompiledScript,
			final Map<String, Object> aVariablesMap) throws Exception;

	/**
	 *
	 * @param aActivityLogger
	 * @param aSourceMain
	 * @param aVariablesMap
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final CXJsSourceMain aSourceMain,
			final Map<String, Object> aVariablesMap) throws Exception;

	/**
	 * @param aActivityLogger
	 *            an explict logger to be used rather than that associated to the
	 *            manager
	 * @param aProviderId
	 *            an explict id to search the script uri in only one provider
	 * @param aSourceMain
	 *            a object that represent the main source
	 * @param aVariablesMap
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final String aProviderId,
			final CXJsSourceMain aSourceMain, final Map<String, Object> aVariablesMap) throws Exception;

	/**
	 * @param aActivityLogger
	 *            an explict logger to be used rather than that associated to the
	 *            manager
	 * @param aScriptUri
	 * @param aVariablesMap
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final String aScriptUri,
			final Map<String, Object> aVariablesMap) throws Exception;

	/**
	 * @param aActivityLogger
	 *            an explict logger to be used rather than that associated to the
	 *            manager
	 * @param aProviderId
	 *            an explict id to search the script uri in only one provider
	 * @param aScriptUri
	 * @param aVariablesMap
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningReply runScript(final IActivityLogger aActivityLogger, final String aProviderId,
			final String aScriptUri, final Map<String, Object> aVariablesMap) throws Exception;

	/**
	 * @param aScriptUri
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningReply runScript(final String aScriptUri) throws Exception;

	/**
	 * @param aScriptUri
	 * @param aVariablesMap
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningReply runScript(final String aScriptUri, final Map<String, Object> aVariablesMap) throws Exception;

	/**
	 * @param aProviderId
	 *            an explict id to search the script uri in only one provider
	 * @param aScriptUri
	 * @param aVariablesMap
	 * @return
	 * @throws Exception
	 */
	public IXJsRuningReply runScript(final String aProviderId, final String aScriptUri,
			final Map<String, Object> aVariablesMap) throws Exception;
}
