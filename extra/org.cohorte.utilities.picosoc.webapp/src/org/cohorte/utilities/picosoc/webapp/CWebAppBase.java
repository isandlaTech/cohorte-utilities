package org.cohorte.utilities.picosoc.webapp;

import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletContext;

import org.apache.catalina.loader.WebappClassLoader;
import org.cohorte.utilities.picosoc.CAbstractComponentBase;
import org.cohorte.utilities.picosoc.CComponentLoggerFile;
import org.cohorte.utilities.picosoc.config.ISvcWebAppProperties;
import org.psem2m.utilities.CXDomUtils;
import org.psem2m.utilities.CXException;
import org.psem2m.utilities.files.CXFileDir;
import org.w3c.dom.Element;

/**
 * @author ogattaz
 *
 */
public abstract class CWebAppBase extends CAbstractComponentBase implements ISvcWebApp {

	/**
	 *
	 * @return the ContextPath
	 */
	private static String retreiveWebAppName(final String aName) {
		String wWebAppNane = aName;

		ClassLoader wCurrentClassLoader = CWebAppBase.class.getClassLoader();

		boolean wIsWebappClassLoader = (wCurrentClassLoader instanceof WebappClassLoader);
		if (wIsWebappClassLoader) {
			wWebAppNane = ((WebappClassLoader) wCurrentClassLoader).getContextName();
		}
		// keep only alphanumeric chararcters and underscore '_'
		// @see
		// http://stackoverflow.com/questions/1805518/replacing-all-non-alphanumeric-characters-with-empty-strings
		wWebAppNane = wWebAppNane.replaceAll("[^A-Za-z0-9_]", "");

		CComponentLoggerFile.logInMain(Level.INFO, CWebAppBase.class, "retreiveWebAppName",
				"IsWebappClassLoader=[%s] WebAppNane=[%s]", wIsWebappClassLoader, wWebAppNane);

		return wWebAppNane;
	}

	private String pContextPath = null;

	private final String pWebAppName;

	private String pWebAppFilePath = null;

	/**
	 * @param aDefaultName
	 */
	protected CWebAppBase(String aDefaultName) {
		super();
		pWebAppName = retreiveWebAppName(aDefaultName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcWebApp#getContextPath()
	 */
	@Override
	public String getContextPath() {
		return pContextPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.webapp.utilities.ISvcWebApp#getNbWebAppProperties()
	 */
	@Override
	public int getNbWebAppProperties() {
		return (hasWebAppProperties()) ? getWebAppProperties().size() : -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebappDir()
	 */
	@Override
	public CXFileDir getWebAppDir() {
		return new CXFileDir(getWebAppFilePath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebappDirName()
	 */
	@Override
	public String getWebAppDirName() {

		return (hasWebAppFilePath()) ? getWebAppDir().getName() : pWebAppName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.x3.loadbalancer.ISvcWebAppListener#getWebappPath()
	 */
	@Override
	public String getWebAppFilePath() {
		return pWebAppFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isandlatech.x3.loadbalancer.ISvcWebApp#getWebAppName()
	 */
	@Override
	public String getWebAppName() {
		return  pWebAppName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cohorte.utilities.picosoc.webapp.ISvcWebApp#getWebAppProperties()
	 */
	@Override
	public abstract ISvcWebAppProperties getWebAppProperties();

	/**
	 * @return
	 */
	@Override
	public boolean hasWebAppFilePath() {
		return getWebAppFilePath() != null;
	}

	/**
	 * @return true if the WebApp properties is loaded
	 */
	@Override
	public boolean hasWebAppProperties() {
		return getWebAppProperties() != null;
	}

	/**
	 * @param aContextPath
	 */
	protected void setContextPath(String aContextPath) {
		pContextPath = aContextPath;
	}

	/**
	 * @param aWebAppFilePath
	 */
	protected void setWebAppFilePath(String aWebAppFilePath) {
		pWebAppFilePath = aWebAppFilePath;
	}
	
	/**
	 * MOD_BD_20180220 adds dumpServletContext utility method to CWebAppBase
	 * @param aServletContext
	 * @return
	 */
	protected String dumpServletContext(ServletContext aServletContext) {
		StringBuilder wSB = new StringBuilder();
		wSB.append("SERVLET CONTEXT:");
		wSB.append(String.format("\n\tServerInfo=[%s]", aServletContext.getServerInfo()));
		wSB.append(String.format("\n\tContextPath=[%s]", aServletContext.getContextPath()));
		wSB.append(String.format("\n\tRootRealPath=[%s]", aServletContext.getRealPath("/")));
		wSB.append(String.format("\n\tMajorVersion=[%s] MinorVersion=[%s]", aServletContext.getMajorVersion(),
				aServletContext.getMinorVersion()));
		wSB.append(String.format("\n\tAttributes:{%s}", dumpServletContextAttributes(aServletContext)));
		wSB.append(String.format("\n\tInitParameters:{%s}", dumpServletContextInitParams(aServletContext)));

		return wSB.toString();

	}

	/**
	 * MOD_BD_20180220 adds dumpServletContext utility method to CWebAppBase
	 * @param aSession
	 * @return
	 */
	protected String dumpServletContextAttributes(ServletContext aServletContex) {
		Enumeration<String> wAttributNames = aServletContex.getAttributeNames();
		StringBuilder wSB = new StringBuilder();
		while (wAttributNames.hasMoreElements()) {
			String wName = wAttributNames.nextElement();
			Object wValue = aServletContex.getAttribute(wName);

			// org.apache.tomcat.util.scan.MergedWebXm
			if (wName.endsWith("MergedWebXml") && wValue != null)
				wValue = extractWebXmlInfos(wValue.toString());

			wSB.append(String.format("\n\t\t%s=[%s],", wName, wValue));
		}
		return wSB.toString();
	}

	/**
	 * MOD_BD_20180220 adds dumpServletContext utility method to CWebAppBase
	 * @param aSession
	 * @return
	 */
	protected String dumpServletContextInitParams(ServletContext aServletContex) {
		Enumeration<String> wNames = aServletContex.getInitParameterNames();
		StringBuilder wSB = new StringBuilder();
		while (wNames.hasMoreElements()) {
			String wName = wNames.nextElement();
			wSB.append(String.format("\n\t\t%s=[%s],", wName, aServletContex.getInitParameter(wName)));
		}
		return wSB.toString();
	}

	/**
	 * MOD_BD_20180220 adds dumpServletContext utility method to CWebAppBase
	 * <pre>
	 *     <display-name>AgiliumWeb</display-name>
	 *     <listener-class>com.m1i.agilium.web.beans.WebAppListener</listener-class>
	 *     <servlet-name>jsp</servlet-name>
	 *     <servlet-class>org.apache.jasper.servlet.JspServlet</servlet-class>
	 *     <session-timeout>60</session-timeout>
	 *     <cookie-config>
	 * </pre>
	 *
	 * @return
	 */
	private String extractWebXmlInfos(String aXml) {

		StringBuilder wSB = new StringBuilder();

		try {
			CXDomUtils wDom = new CXDomUtils(aXml);
			addElemtsTextInSb(wSB, wDom.getElementsByTagName("display-name"));
			addElemtsTextInSb(wSB, wDom.getElementsByTagName("listener-class"));
			addElemtsTextInSb(wSB, wDom.getElementsByTagName("servlet-name"));
			addElemtsTextInSb(wSB, wDom.getElementsByTagName("servlet-class"));
			addElemtsTextInSb(wSB, wDom.getElementsByTagName("session-timeout"));
			addElemtsTextInSb(wSB, wDom.getElementsByTagName("cookie-config"));
		} catch (Exception e) {
			wSB.append(CXException.eInString(e));
		}
		return wSB.toString();
	}
	
	/**
	 *
	 * @param aSB
	 * @param aElemts
	 */
	private void addElemtsTextInSb(StringBuilder aSB, List<Element> aElemts) {

		for (Element wElmt : aElemts) {

			aSB.append(String.format("\n\t\t\t%s=[%s],", wElmt.getNodeName(), wElmt.getTextContent()));
		}
	}
}
