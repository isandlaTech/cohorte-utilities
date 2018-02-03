package org.psem2m.utilities.scripting;

import java.io.StringWriter;
import java.util.List;

import javax.script.Bindings;

import org.psem2m.utilities.CXTimer;

/**
 * @author IsandlaTech - ogattaz
 * 
 */

public interface IXJsRuningContext extends IXJsRuningReply {

	/**
	 * @return
	 */
	@Deprecated
	public String descrToString();

	/**
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name);

	/**
	 * @param name
	 * @param scope
	 * @return
	 */
	public Object getAttribute(String name, int scope);

	/**
	 * @param name
	 * @return
	 */
	public int getAttributesScope(String name);

	/**
	 * @param scope
	 * @return
	 */
	public Bindings getBindings(int scope);

	/**
	 * @return
	 */
	public StringWriter getBuffer();

	/**
	 * @return
	 */
	public long getDurationNs();

	/**
	 * @return
	 */
	public String getDurationStrMs();

	/**
	 * @return
	 */
	Bindings getEngineBindings();

	/**
	 * @return
	 */
	public StringWriter getErrBuffer();

	/**
	 * @return
	 */
	Bindings getGlobalBindings();

	/**
	 * @return
	 */
	public List<Integer> getScopes();

	/**
	 * @return
	 */
	public CXTimer getTimer();

	/**
	 * @return
	 */
	public boolean isRunning();

	/**
	 * @param name
	 */
	public void removeAttrEngine(String name);

	/**
	 * @param name
	 */
	public void removeAttrGlobal(String name);

	/**
	 * @param name
	 * @param scope
	 * @return
	 */
	public Object removeAttribute(String name, int scope);

	/**
	 * @param name
	 * @param value
	 */
	public void setAttrEngine(String name, Object value);

	/**
	 * @param name
	 * @param value
	 */
	public void setAttrGlobal(String name, Object value);

	/**
	 * @param name
	 * @param value
	 * @param scope
	 */
	public void setAttribute(String name, Object value, int scope);

	/**
	 * @param aAction
	 * @return
	 */
	public CXJsRuningContext start(String aAction);

	/**
	 * @param aAction
	 * @param aTimeRef
	 * @return
	 */
	public CXJsRuningContext start(String aAction, long aTimeRef);

	public CXJsRuningContext stop();

}
