package org.cohorte.utilities.filter.parser;

import java.util.ArrayList;
import java.util.List;

import org.cohorte.utilities.filter.expression.CExpression;
import org.cohorte.utilities.filter.expression.CExpressionArray;
import org.cohorte.utilities.filter.expression.CExpressionValue;
import org.cohorte.utilities.filter.expression.CExpressionWithFieldArray;
import org.cohorte.utilities.filter.expression.CExpressionWithFieldObject;
import org.cohorte.utilities.filter.expression.ExpressionOperator;
import org.cohorte.utilities.filter.expression.IExpression;
import org.psem2m.utilities.json.JSONArray;
import org.psem2m.utilities.json.JSONException;
import org.psem2m.utilities.json.JSONObject;

/**
 * parse the Json that correspond to filters and create CFilters object that
 * represent it
 *
 * @author apisu
 *
 */
public class CParser {

	public static CExpression parse(final JSONObject aObject) throws CParseException {
		return parse(aObject, null);
	}

	public static CExpression parse(final JSONObject aObject, final String aField) throws CParseException {
		ExpressionOperator wOperator = null;
		String wField = aField;
		CExpression wExp = null;
		for (String aKey : aObject.keySet()) {

			wOperator = ExpressionOperator.getEnum(aKey);
			if (wOperator == null && wField == null) {
				// its a field
				wField = aKey;
				if (aObject.optJSONObject(wField) != null) {
					return parse(aObject.optJSONObject(wField), wField);
				} else {
					return new CExpressionValue(wField, ExpressionOperator.EQ, aObject.opt(wField));
				}
			} else {
				if (wOperator == null) {
					throw new CParseException(String.format("Operator not found expected=[%s], found=[%s]",
							ExpressionOperator.listAll(), aKey));

				} else {

					if (wOperator.hasField()) {
						if (wField == null) {
							throw new CParseException("");
						} else {
							if (wOperator.isOperandArray()) {
								wExp = new CExpressionWithFieldArray(wField, wOperator);
								JSONArray wArr = aObject.optJSONArray(aKey);
								((CExpressionWithFieldArray) wExp).setValues(parseValue(wArr));
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
						throw new CParseException(String.format("Operator %s must not be use with a field %s",
								wOperator.toString(), wField));

					} else {
						if (wOperator.isOperandArray()) {
							wExp = new CExpressionArray(wOperator);

							JSONArray wArr = aObject.optJSONArray(aKey);
							((CExpressionArray) wExp).setValues(parseExpressions(wArr));
						} else {
							throw new CParseException(
									String.format("Operator %s must be an array opeator ", wOperator.toString()));

						}
					}
				}
			}
		}

		return wExp;
	}

	public static CExpression parse(final String aFilter) throws CParseException {
		JSONObject wObject;
		try {
			wObject = new JSONObject(aFilter);
			return parse(wObject, null);

		} catch (JSONException e) {
			throw new CParseException(e, "can't parse Filter");
		}
	}

	private static List<IExpression> parseExpressions(final JSONArray aArray) throws CParseException {
		List<IExpression> wValues = new ArrayList<>();
		for (int i = 0; i < aArray.length(); i++) {
			Object wItem = aArray.opt(i);
			if (wItem instanceof JSONObject) {
				wValues.add(parse((JSONObject) wItem));
			} else {
				throw new CParseException("");
			}
		}
		return wValues;
	}

	private static List<Object> parseValue(final JSONArray aArray) throws CParseException {
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
}
