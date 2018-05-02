package org.psem2m.utilities.scripting;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;

import org.psem2m.utilities.CXTimer;

/**
 * @author ogattaz
 *
 */
public class CXJsRuningContext extends CXJsObjectBase implements ScriptContext,
		IXJsRuningContext {

	public final static String ACT_EVAL = "eval";
	public final static String ACT_EVAL_COMPILED = "evalCompiled";
	public final static String ACT_INVOKE_FUNC = "invokeFunc";
	public final static String ACT_INVOKE_METH = "invokeMeth";

	private static List<Integer> scopes;

	static {
		scopes = new ArrayList<>(2);
		scopes.add(new Integer(ENGINE_SCOPE));
		scopes.add(new Integer(GLOBAL_SCOPE));
		scopes = Collections.unmodifiableList(scopes);
	}

	protected Bindings engineScope;
	protected Writer errorWriter;
	protected Bindings globalScope;
	private String pAction;
	private StringWriter pBuffer;
	private boolean pEnd = false;
	private StringWriter pErrBuffer;
	private final int pInitSize;
	Object pScriptResult;
	private final CXTimer pTimer = new CXTimer();

	protected Reader reader;

	protected Writer writer;

	/**
	 *
	 * @param aInitSize
	 */
	public CXJsRuningContext() {
		this(8192, null);
	}

	/**
	 * @param aEngineBindings
	 */
	public CXJsRuningContext(final Bindings aEngineBindings) {
		this(8192, aEngineBindings);

	}

	/**
	 * @param aInitSize
	 */
	public CXJsRuningContext(final int aInitSize) {
		this(aInitSize, null);
	}

	/**
	 *
	 * @param aInitSize
	 * @param aEngineBindings
	 */
	public CXJsRuningContext(final int aInitSize, final Bindings aEngineBindings) {
		globalScope = null;
		pInitSize = aInitSize > 0 ? aInitSize : 8192;
		engineScope = (aEngineBindings != null) ? aEngineBindings
				: new SimpleBindings();
		resetWriter();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.psem2m.utilities.scripting.CXJsObjectBase#addDescriptionInBuffer(
	 * java. lang.Appendable)
	 */
	@Override
	public Appendable addDescriptionInBuffer(final Appendable aBuffer) {

		CXJsObjectBase.descrAddLine(aBuffer, "Output buffer - Size", pBuffer
				.getBuffer().length());
		CXJsObjectBase.descrAddIndent(aBuffer, pBuffer.getBuffer().toString());
		if (pErrBuffer != null) {
			CXJsObjectBase.descrAddLine(aBuffer, "Error buffer - Size",
					pErrBuffer.getBuffer().length());
			CXJsObjectBase.descrAddIndent(aBuffer, pErrBuffer.getBuffer()
					.toString());
		}
		return aBuffer;
	}

	/**
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXJsRuningContext#descrToString()
	 */
	@Override
	public String descrToString() {
		return toDescription();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(final String name) {
		if (engineScope.containsKey(name)) {
			return getAttribute(name, ENGINE_SCOPE);
		} else if (globalScope != null && globalScope.containsKey(name)) {
			return getAttribute(name, GLOBAL_SCOPE);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#getAttribute(java.lang.String, int)
	 */
	@Override
	public Object getAttribute(final String name, final int scope) {
		// System.out.println(String.format("getAttribute scope[%d] [%s]",scope,name));
		switch (scope) {
		case ENGINE_SCOPE:
			return engineScope.get(name);
		case GLOBAL_SCOPE:
			if (globalScope != null) {
				return globalScope.get(name);
			}
			return null;
		default:
			throw new IllegalArgumentException("Illegal scope value.");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#getAttributesScope(java.lang.String)
	 */
	@Override
	public int getAttributesScope(final String name) {
		if (engineScope.containsKey(name)) {
			return ENGINE_SCOPE;
		} else if (globalScope != null && globalScope.containsKey(name)) {
			return GLOBAL_SCOPE;
		} else {
			return -1;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#getBindings(int)
	 */
	@Override
	public Bindings getBindings(final int scope) {
		// System.out.println(String.format("getBindings scope[%d] ",scope));

		if (scope == ENGINE_SCOPE) {
			return engineScope;
		} else if (scope == GLOBAL_SCOPE) {
			return globalScope;
		} else {
			throw new IllegalArgumentException("Illegal scope value.");
		}
	}

	/**
	 * @return
	 */
	@Override
	public StringWriter getBuffer() {
		return pBuffer;
	}

	/**
	 * @return
	 */
	@Override
	public long getDurationNs() {
		return pTimer.getDurationNs();
	}

	/**
	 * @return
	 */
	@Override
	public String getDurationStrMs() {
		return new StringBuilder(pAction).append(" - ")
				.append(pTimer.getDurationStrMilliSec()).toString();
	}

	@Override
	public Bindings getEngineBindings() {
		return getBindings(ENGINE_SCOPE);
	}

	/**
	 * @return
	 */
	@Override
	public StringWriter getErrBuffer() {
		if (pErrBuffer == null) {
			pErrBuffer = new StringWriter();
		}
		return pErrBuffer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#getErrorWriter()
	 */
	@Override
	public Writer getErrorWriter() {
		// Uniquement si besoin
		if (errorWriter == null) {
			errorWriter = new PrintWriter(getErrBuffer());
		}
		return errorWriter;
	}

	/*
	 * return handle duration and eval duration
	 * 
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.IXJsRuningReply#getEvalDuration()
	 */
	@Override
	public String getEvalDuration() {

		return (String) getAttribute(IXJsManager.ATTR_EVAL_DURATION);
	}

	@Override
	public Bindings getGlobalBindings() {
		return getBindings(GLOBAL_SCOPE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#getReader()
	 */
	@Override
	public Reader getReader() {
		// Uniquement si besoin
		if (reader == null) {
			reader = new InputStreamReader(System.in);
		}
		return reader;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#getScopes()
	 */
	@Override
	public List<Integer> getScopes() {
		return scopes;
	}

	@Override
	public Object getScriptResult() {
		return pScriptResult;
	}

	/**
	 * @return
	 */
	@Override
	public CXTimer getTimer() {
		return pTimer;
	}

	/**
	 * @return
	 */
	@Override
	public String getTimerInfo() {
		return new StringBuilder().append(pAction).append(" - StartAt[")
				.append(pTimer.getStartAtSecStr()).append("] - StopAt[")
				.append(pTimer.getStopAtSecStr()).append("] - Duration[")
				.append(pTimer.getDurationStrMilliSec()).append("]").toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#getWriter()
	 */
	@Override
	public Writer getWriter() {
		return writer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sage.x3.bridge.bundle.extsps.restserver.script.IJsReply#isEndOK()
	 */
	@Override
	public boolean isEndOK() {

		return pEnd;
	}

	/**
	 * @return
	 */
	@Override
	public boolean isRunning() {
		return pTimer.isCounting();
	}

	/**
	 * @param name
	 */
	@Override
	public void removeAttrEngine(final String name) {
		removeAttribute(name, ENGINE_SCOPE);
	}

	/**
	 * @param name
	 */
	@Override
	public void removeAttrGlobal(final String name) {
		removeAttribute(name, GLOBAL_SCOPE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#removeAttribute(java.lang.String, int)
	 */
	@Override
	public Object removeAttribute(final String name, final int scope) {
		switch (scope) {
		case ENGINE_SCOPE:
			if (getBindings(ENGINE_SCOPE) != null) {
				return getBindings(ENGINE_SCOPE).remove(name);
			}
			return null;
		case GLOBAL_SCOPE:
			if (getBindings(GLOBAL_SCOPE) != null) {
				return getBindings(GLOBAL_SCOPE).remove(name);
			}
			return null;
		default:
			throw new IllegalArgumentException("Illegal scope value.");
		}
	}

	/**
	 *
	 */
	public void resetWriter() {
		pBuffer = new StringWriter(pInitSize);
		writer = new PrintWriter(pBuffer);
		reader = null;
		errorWriter = null;
		pErrBuffer = null;
	}

	/**
	 * @param name
	 * @param value
	 */
	@Override
	public void setAttrEngine(final String name, final Object value) {
		setAttribute(name, value, ENGINE_SCOPE);
	}

	/**
	 * @param name
	 * @param value
	 */
	@Override
	public void setAttrGlobal(final String name, final Object value) {
		setAttribute(name, value, GLOBAL_SCOPE);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#setAttribute(java.lang.String,
	 * java.lang.Object, int)
	 */
	@Override
	public void setAttribute(final String name, final Object value,
			final int scope) {
		// System.out.println(String.format("setAttribute scope[%d] [%s|%s]
		// ]",scope,name,value.toString()));

		switch (scope) {
		case ENGINE_SCOPE:
			engineScope.put(name, value);
			return;
		case GLOBAL_SCOPE:
			if (globalScope != null) {
				globalScope.put(name, value);
			}
			return;
		default:
			throw new IllegalArgumentException("Illegal scope value.");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#setBindings(javax.script.Bindings, int)
	 */
	@Override
	public void setBindings(final Bindings bindings, final int scope) {
		switch (scope) {
		case ENGINE_SCOPE:
			if (bindings == null) {
				throw new NullPointerException("Engine scope cannot be null.");
			}
			engineScope = bindings;
			break;
		case GLOBAL_SCOPE:
			globalScope = bindings;
			break;
		default:
			throw new IllegalArgumentException("Invalid scope value.");
		}
	}

	/**
	 * @param aEnd
	 */
	public void setEndOk(final Object aEnd) {

		this.pEnd = (aEnd != null && aEnd instanceof String && ((String) aEnd)
				.equalsIgnoreCase("true"));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#setErrorWriter(java.io.Writer)
	 */
	@Override
	public void setErrorWriter(final Writer writer) {
		this.errorWriter = writer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#setReader(java.io.Reader)
	 */
	@Override
	public void setReader(final Reader reader) {
		this.reader = reader;
	}

	@Override
	public void setScriptResult(final Object aObject) {
		pScriptResult = aObject;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.script.ScriptContext#setWriter(java.io.Writer)
	 */
	@Override
	public void setWriter(final Writer writer) {
		this.writer = writer;
	}

	/**
	 * @param aAction
	 * @return
	 */
	@Override
	public CXJsRuningContext start(final String aAction) {
		return start(aAction, 0);
	}

	/**
	 * @param aAction
	 * @param aTimeRef
	 * @return
	 */
	@Override
	public CXJsRuningContext start(final String aAction, final long aTimeRef) {
		pAction = aAction;
		pTimer.start(aTimeRef);
		return this;
	}

	/**
	 * @return
	 */
	@Override
	public CXJsRuningContext stop() {
		pTimer.stop();
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.psem2m.utilities.scripting.CXJsObjectBase#toDescription()
	 */
	@Override
	public String toDescription() {
		return addDescriptionInBuffer(new StringBuilder()).toString();
	}
}
