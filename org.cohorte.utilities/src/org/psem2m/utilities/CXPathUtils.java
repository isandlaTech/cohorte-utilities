package org.psem2m.utilities;

import java.io.IOException;

import org.psem2m.utilities.files.CXFileUtf8;
import org.w3c.dom.Node;

/**
 * class that allow to read data from a xml identified by an xpath and write in
 * a specific xml data identified by an xpath
 * 
 * @author apisu
 *
 */
public class CXPathUtils {
	/**
	 * add in the xml store in path aXmlFilePath the value in the node
	 * identified by the XPath
	 * 
	 * @param aXmlFilePath
	 * @param aXPath
	 * @param aValue
	 * @throws Exception
	 */
	public static void appendTextInNode(CXFileUtf8 aXmlFile, String aXPath, String aValue, String aSeparator)
			throws Exception {
		CXDomUtils wDom = new CXDomUtils(aXmlFile);
		Node wNode = wDom.getNodeByXPath(aXPath);
		if (wNode != null) {
			String wCurrentContent = wNode.getTextContent();
			if (wCurrentContent != null && !wCurrentContent.isEmpty()) {
				// append with a separator
				wNode.setTextContent(wCurrentContent = wCurrentContent + aSeparator + aValue);
			}
		}
		// write the file in
		aXmlFile.writeAll(wDom.toXml());
	}

	/**
	 * add in the xml store in path aXmlFilePath the value in the node
	 * identified by the XPath
	 * 
	 * @param aXmlFilePath
	 *            : path of the xml file to modify
	 * @param aXPath
	 *            : xpath of the node element to modify
	 * @param aValue
	 *            : value to add in the node
	 * @param aSeparator
	 *            : separator to add if we already have data in the node
	 * @throws Exception
	 */
	public static void appendTextInNode(String aXmlFilePath, String aXPath, String aValue, String aSeparator)
			throws Exception {
		CXFileUtf8 wFile = new CXFileUtf8(aXmlFilePath);
		if (!wFile.exists()) {
			throw new IOException(String.format("file %s not found ", aXmlFilePath));
		}
		appendTextInNode(wFile, aXPath, aValue, aSeparator);
	}

	public static Node readNodeFromXPath(CXFileUtf8 aXmlFilePath, String aXPath) throws Exception {
		CXDomUtils wDom = new CXDomUtils(aXmlFilePath);
		return wDom.getNodeByXPath(aXPath);
	}

	/**
	 * return the node that is identified by xpath in the xmlFile identified by
	 * the filePath
	 * 
	 * @param aXmlFilePath
	 * @param aXPath
	 * @return
	 */
	public static Node readNodeFromXPath(String aXmlFilePath, String aXPath) throws Exception {
		CXFileUtf8 wFile = new CXFileUtf8(aXmlFilePath);
		if (!wFile.exists()) {
			throw new IOException(String.format("file %s not found ", aXmlFilePath));
		}
		return readNodeFromXPath(wFile, aXPath);
	}

	public static String readTextFromXPath(CXFileUtf8 aXmlFile, String aXPath) throws Exception {
		Node wNode = readNodeFromXPath(aXmlFile, aXPath);
		if (wNode != null && wNode.getTextContent() != null) {
			return wNode.getTextContent();
		}
		return null;
	}

	public static String readTextFromXPath(String aXmlFilePath, String aXPath) throws Exception {
		Node wNode = readNodeFromXPath(aXmlFilePath, aXPath);
		if (wNode != null && wNode.getTextContent() != null) {
			return wNode.getTextContent();
		}
		return null;
	}

	/**
	 * add in the xml store in path aXmlFilePath the value in the node
	 * identified by the XPath
	 * 
	 * @param aXmlFilePath
	 * @param aXPath
	 * @param aValue
	 * @throws Exception
	 */
	public static void replaceTextInNode(CXFileUtf8 aXmlFile, String aXPath, String aValue) throws Exception {
		CXDomUtils wDom = new CXDomUtils(aXmlFile);
		Node wNode = wDom.getNodeByXPath(aXPath);
		if (wNode != null) {
			String wCurrentContent = wNode.getTextContent();
			if (wCurrentContent != null && !wCurrentContent.isEmpty()) {
				// append with a separator
				wNode.setTextContent(aValue);
			}
		}
		// write the file in
		aXmlFile.writeAll(wDom.toXml());
	}

	/**
	 * add in the xml store in path aXmlFilePath the value in the node
	 * identified by the XPath
	 * 
	 * @param aXmlFilePath
	 *            : path of the xml file to modify
	 * @param aXPath
	 *            : xpath of the node element to modify
	 * @param aValue
	 *            : value to add in the node
	 * @param aSeparator
	 *            : separator to add if we already have data in the node
	 * @throws Exception
	 */
	public static void replaceTextInNode(String aXmlFilePath, String aXPath, String aValue) throws Exception {
		CXFileUtf8 wFile = new CXFileUtf8(aXmlFilePath);
		if (!wFile.exists()) {
			throw new IOException(String.format("file %s not found ", aXmlFilePath));
		}
		replaceTextInNode(wFile, aXPath, aValue);
	}

}
