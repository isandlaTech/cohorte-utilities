package org.cohorte.iot.json.validator.api;

public class CJsonGeneratorFactory {
	private static Object lock = new Object();
	private static CJsonGeneratorDefault sGenerator;

	/**
	 * get singleton generator with custom logger. if the singleton exists it
	 * switch the logger
	 *
	 * @return
	 */
	public static CJsonGeneratorDefault getSingleton() {
		if (sGenerator == null) {
			synchronized (lock) {
				if (sGenerator == null) {
					sGenerator = new CJsonGeneratorDefault();
				}
			}
		}
		return sGenerator;
	}

	/**
	 * get new generator with custom logger. if the singleton exists it switch
	 * the logger
	 *
	 * @return
	 */
	public static CJsonGeneratorDefault newInstance() {
		return new CJsonGeneratorDefault();
	}
}
