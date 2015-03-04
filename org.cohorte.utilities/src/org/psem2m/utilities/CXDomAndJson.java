package org.psem2m.utilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * Simplified dom converter and dom updater
 * 
 * The method "convertXmlToJson()" converts a subtree from a node to a json
 * object
 * 
 * <pre>
 * <?xml version="1.0" ?>
 * <root>
 *   <Request>
 * 		<DataToSend>
 * 			<source-url>file:///C:/temp/mysource.docx</source-url>
 * 			<target-name>myconvertedfile.pdf</target-name>
 * 			<target-url>file:///C:/mypdfresults</target-url>
 * 		</DataToSend>
 *   </Request>
 *   <Response>
 *     <ReceivedData>
 *   </Response>
 * </root>
 * </pre>
 * 
 * The resulting json object containing the elements of the sub-tree starting at
 * "DataToSend"
 * 
 * <pre>
 * {
 *     "target-url": "file:///C:/mypdfresults",
 *     "target-name": "myconvertedfile.pdf",
 *     "source-url": "file:///C:/temp/mysource.docx"
 * }
 * </pre>
 * 
 * If many sub-element have the same node name; the converter detect an array
 * 
 * <pre>
 * 		<HeadersToSend>
 * 			<header>
 * 				<id>h1</id>
 * 				<value>V1</value>
 * 			</header>
 * 			<header>
 * 				<id>h2</id>
 * 				<value>V2</value>
 * 			</header>
 * 		</HeadersToSend>
 * </pre>
 * 
 * <pre>
 *     "HeadersToSend": [
 *       {
 *         "id": "h1",
 *         "value": "V1"
 *       },
 *       {
 *         "id": "h2",
 *         "value": "V2"
 *       }
 *     ],
 * </pre>
 * 
 * 
 * The method "updateXmlFromJson()" updates the existing elements of a sub-tree
 * of an xml document
 * 
 * Note: this method doesn't create element
 * 
 * <pre>
 * {
 * 	"Job-UUID" : "6eebcee0­1bd9­4185­9714­bec086eecb2f", 
 * 	"Creation-Date" : "2015­02­09 01:00:00", 
 * 	"Job-Progress" : 0,
 * 	"meta" :
 * 		{
 * 			"status": 200,
 * 			"msg": "OK",
 * 			"duration": "5.806",
 * 			"RequestURI": "/converter/docToPdf"
 * 		}
 * }
 * </pre>
 * 
 * The result :
 * 
 * <pre>
 * <?xml version="1.0" encoding="UTF-8"?><root>
 * 	<Request/>
 * 	<Response>
 * 		<ReceivedHeaders/>
 * 		<ReceivedData>
 * 			<Creation-Date>2015­02­09 01:00:00</Creation-Date>
 * 			<Job-UUID>6eebcee0­1bd9­4185­9714­bec086eecb2f</Job-UUID>
 * 			<Job-Progress>0</Job-Progress>
 * 		</ReceivedData>
 * 	</Response>
 * </root>
 * </pre>
 * 
 * 
 * ATTENTION: To manage more complex XML DOMs, use Jettison , a JSON StAX
 * Implementation
 * 
 * @see http://jettison.codehaus.org
 * 
 * 
 * @author ogattaz
 * 
 */
public class CXDomAndJson {

	/**
	 * 
	 */
	public CXDomAndJson() {
		super();
	}

	/**
	 * @param aElement
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> convertElementToMap(final Element aElement)
			throws Exception {

		if (aElement == null) {
			return null;
		}
		Map<String, String> wMap = new LinkedHashMap<String, String>();

		List<Element> wElmts = getChildElements(aElement);
		if (!wElmts.isEmpty()) {

			for (Element wElmt : wElmts) {
				wMap.put(wElmt.getNodeName(), wElmt.getTextContent());
			}
		}
		return wMap;
	}

	/**
	 * @param aJsonObject
	 *            an instance of json object
	 * @return an instance of Document
	 * @throws Exception
	 */
	public Document convertJsonToXml(final JSONObject aJsonObject)
			throws Exception {

		throw new UnsupportedOperationException("Not implemented");
	}

	/**
	 * convert an xml document to a json object instance
	 * 
	 * @param aDocument
	 *            an instance of Document
	 * @return an instance of json object
	 * @throws Exception
	 *             if an array is not well defined. Look at the method
	 *             "retreiveArrayName()"
	 */
	public JSONObject convertXmlToJson(final Document aDocument)
			throws Exception {

		return convertXmlToJson(aDocument.getDocumentElement());
	}

	/**
	 * convert a sub-tree of tan xml document to a json object instance
	 * 
	 * 
	 * @param aElement
	 *            an instance of element
	 * @return an instance of json object
	 * @throws Exception
	 *             if an array is not well defined. Look at the method
	 *             "retreiveArrayName()"
	 */
	public JSONObject convertXmlToJson(final Element aElement) throws Exception {

		if (aElement == null) {
			return null;
		}
		List<Element> wElmts = getChildElements(aElement);
		if (wElmts.isEmpty()) {
			return new JSONObject();
		}
		// if the children are an array : convert to an JsonArray
		String wArrayName = retreiveArrayName(wElmts);
		if (wArrayName != null) {
			JSONObject wJsonObject = new JSONObject();
			wJsonObject.put(aElement.getNodeName(),
					convertXmlToJsonArray(wElmts));
			return wJsonObject;
		} else {
			return convertXmlToJsonObject(wElmts);
		}
	}

	/**
	 * @param aElements
	 * @return
	 * @throws Exception
	 */
	private JSONArray convertXmlToJsonArray(final List<Element> aElements)
			throws Exception {

		JSONArray wJSONArray = new JSONArray();
		if (aElements != null && !aElements.isEmpty()) {

			for (Element wElemt : aElements) {
				wJSONArray.put(convertXmlToJson(wElemt));
			}
		}
		return wJSONArray;
	}

	/**
	 * @param aElements
	 * @return
	 * @throws Exception
	 */
	private JSONObject convertXmlToJsonObject(final List<Element> aElements)
			throws Exception {

		JSONObject wJson = new JSONObject();

		if (aElements != null && !aElements.isEmpty()) {

			for (Element wElmt : aElements) {

				// retreive the children
				List<Element> wElements = getChildElements(wElmt);

				// if there is at least one child element
				if (!wElements.isEmpty()) {
					// if the children are an array : convert to an JsonArray
					String wArrayName = retreiveArrayName(wElements);
					if (wArrayName != null) {
						wJson.put(wElmt.getNodeName(),
								convertXmlToJsonArray(wElements));
					} else
					// convert to an JsonObject
					{
						wJson.put(wElmt.getNodeName(),
								convertXmlToJsonObject(wElements));
					}
				} else
				// there'no sub element
				{
					// if ther's some sub text node
					if (wElmt.hasChildNodes()) {
						wJson.put(wElmt.getNodeName(), wElmt.getTextContent());
					} else {
						wJson.put(wElmt.getNodeName(), new JSONObject());
					}
				}
			}
		}
		return wJson;
	}

	/**
	 * @param aElement
	 * @return
	 */
	private List<Element> getChildElements(Element aElement) {

		if (aElement == null) {
			return null;
		}
		List<Element> wElmts = new ArrayList<Element>();
		NodeList wList = aElement.getChildNodes();
		for (int wI = 0; wI < wList.getLength(); wI++) {
			if (wList.item(wI).getNodeType() == Node.ELEMENT_NODE) {
				wElmts.add((Element) wList.item(wI));
			}
		}
		return wElmts;
	}

	/**
	 * @param aElements
	 * @return
	 * @throws Exception
	 *             if all the sub-elements of an element have not the same node
	 *             name if at least two of them have the same
	 */
	private String retreiveArrayName(final List<Element> aElements)
			throws Exception {
		String wSameName = null;
		int wNbSame = -1;
		for (Element wElmt : aElements) {
			String wName = wElmt.getNodeName();
			if (!wName.equals(wSameName)) {
				if (wNbSame > 1) {
					throw new Exception(
							String.format(
									"Not a array : %d elements have the same name [%s] but an other element has an other name [%s] ",
									wNbSame, wSameName, wName));
				}
				wSameName = wName;
				wNbSame = 1;
			} else {
				wNbSame++;
			}
		}
		return (wNbSame > 1) ? wSameName : null;
	}

	/**
	 * @param aElemt
	 * @param aObj
	 * @return
	 * @throws Exception
	 */
	private boolean updateOneElementWithObject(final Element aElemt,
			final Object aObj) throws Exception {

		boolean wUpdated = false;

		if (aObj instanceof JSONObject) {
			updateXmlFromJsonObject(aElemt, (JSONObject) aObj);
		} else
		//
		if (aObj instanceof JSONArray) {
			updateXmlFromJsonArray(aElemt, (JSONArray) aObj);

		} else
		//
		{
			String wStr = (aObj != null) ? aObj.toString() : "null";

			CXDomUtils.setTextValue(aElemt, wStr);

			wUpdated = true;
		}
		return wUpdated;
	}

	/**
	 * The method "updateXmlFromJson()" updates the existing elements of an xml
	 * document
	 * 
	 * Note: this method doesn't create element
	 * 
	 * @param aDocuement
	 *            an instance of Document
	 * @param aJsonObject
	 *            an instance of json object
	 * @return true if one ore more element is updated
	 * @throws Exception
	 */
	public boolean updateXmlFromJson(final Document aDocuement,
			final JSONObject aJsonObject) throws Exception {

		return updateXmlFromJsonObject(aDocuement.getDocumentElement(),
				aJsonObject);
	}

	/**
	 * The method "updateXmlFromJson()" updates the existing elements of a
	 * sub-tree of an xml document
	 * 
	 * @param aElemt
	 *            en instance of Element
	 * @param aJsonObject
	 *            an instance of json object
	 * @return true if one ore more element is updated
	 * @throws Exception
	 */
	public boolean updateXmlFromJson(final Element aElemt,
			final JSONObject aJsonObject) throws Exception {

		return updateXmlFromJsonObject(aElemt, aJsonObject);
	}

	/**
	 * @param aElemt
	 * @param aJsonArray
	 * @return
	 * @throws Exception
	 */
	private boolean updateXmlFromJsonArray(final Element aElemt,
			final JSONArray aJsonArray) throws Exception {

		boolean wUpdated = false;

		List<Element> wChilds = CXDomUtils.getElements(aElemt);
		// it's really an array : all the childs have the same name
		if (retreiveArrayName(wChilds) != null) {

			int wMax = aJsonArray.length();
			for (int wIdx = 0; wIdx < wMax; wIdx++) {
				if (wIdx >= wChilds.size()) {
					break;
				}
				Object wObj = aJsonArray.get(wIdx);
				Element wElemt = wChilds.get(wIdx);

				updateOneElementWithObject(wElemt, wObj);
			}

		}
		return wUpdated;
	}

	/**
	 * @param aElemt
	 * @param aJsonObject
	 * @return
	 * @throws Exception
	 */
	private boolean updateXmlFromJsonObject(final Element aElemt,
			final JSONObject aJsonObject) throws Exception {

		boolean wUpdated = false;

		for (String aMemberName : JSONObject.getNames(aJsonObject)) {

			Element wMemberElmt = CXDomUtils.getFirstChildElmtByTag(aElemt,
					aMemberName);

			if (wMemberElmt != null) {

				Object wObj = aJsonObject.opt(aMemberName);

				wUpdated |= updateOneElementWithObject(wMemberElmt, wObj);
			}
		}
		return wUpdated;
	}
}
