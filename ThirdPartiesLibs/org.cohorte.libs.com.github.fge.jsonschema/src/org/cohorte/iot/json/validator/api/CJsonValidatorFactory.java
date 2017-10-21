package org.cohorte.iot.json.validator.api;

public class CJsonValidatorFactory {
	private static Object lock = new Object();

	private static IValidator pSingleton;

	public static IValidator getSingleton() {
		synchronized (lock) {
			if (pSingleton == null) {
				pSingleton = new CJsonValidatorDefault();
			}
		}
		return pSingleton;
	}

	public static IValidator newInstance() {
		return new CJsonValidatorDefault();

	}
}
