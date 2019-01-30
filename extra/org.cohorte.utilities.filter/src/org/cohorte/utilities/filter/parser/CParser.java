package org.cohorte.utilities.filter.parser;

import java.util.ArrayList;
import java.util.List;

import org.cohorte.utilities.filter.expression.CExpression;
import org.cohorte.utilities.filter.expression.CExpressionArray;
import org.cohorte.utilities.filter.expression.CExpressionValue;
import org.cohorte.utilities.filter.expression.CExpressionWithFieldArray;
import org.cohorte.utilities.filter.expression.CExpressionWithFieldObject;
import org.cohorte.utilities.filter.expression.EOperator;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONObject;

/**
 * parse the Json that correspond to filters and create CFilters object that
 * represent it
 * 
 * @author apisu
 *
 */
public class CParser {
	public static List<Object> parse(JSONArray aArray) throws CParseException {
		List<Object> wValues = new ArrayList<>();
		for (int i = 0; i < aArray.length(); i++) {
			Object wItem = aArray.opt(i);
			if (wItem instanceof JSONObject) {
				wValues.add(parse((JSONObject) wItem));
			} else if (wItem instanceof JSONArray) {
				throw new CParseException("");
			} else {
				wValues.add(wItem);
			}
		}
		return wValues;
	}

	public static CExpression parse(JSONObject aObject) throws CParseException {
		return parse(aObject, null);
	}

	public static CExpression parse(JSONObject aObject, String aField) throws CParseException {
		EOperator wOperator = null;
		String wField = aField;
		CExpression wExp = null;
		for (String aKey : aObject.keySet()) {

			wOperator = EOperator.getEnum(aKey);
			if (wOperator == null && wField == null) {
				// its a field
				wField = aKey;
				if (aObject.optJSONObject(wField) != null) {
					return parse(aObject.optJSONObject(wField), wField);
				} else {
					return new CExpressionValue(wField, EOperator.EQ, aObject.opt(wField));
				}
			} else {

				if (wOperator.hasField()) {
					if (wField == null) {
						throw new CParseException("");
					} else {
						if (wOperator.isOperandArray()) {
							wExp = new CExpressionWithFieldArray(wField, wOperator);
							JSONArray wArr = aObject.optJSONArray(aKey);
							((CExpressionWithFieldArray) wExp).setValues(parse(wArr));
						} else {
							JSONObject wObj = aObject.optJSONObject(aKey);
							if (wObj != null) {
								wExp = new CExpressionWithFieldObject(wField, wOperator);
								((CExpressionWithFieldObject) wExp).setValue(parse(wObj));

							} else {
								Object wVal = aObject.opt(aKey);
								wExp = new CExpressionValue(wField, wOperator, wVal);
							}
						}
					}
				} else if (wField != null) {
					throw new CParseException("");

				} else {
					if (wOperator.isOperandArray()) {
						wExp = new CExpressionArray(wOperator);

						JSONArray wArr = aObject.optJSONArray(aKey);
						((CExpressionArray) wExp).setValues(parse(wArr));
					} else {
						throw new CParseException("");

					}
				}

			}
		}

		return wExp;
	}
}
