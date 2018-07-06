/*******************************************************************************
 * Copyright (c) 2011 www.isandlatech.com (www.isandlatech.com)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    ogattaz (isandlaTech) - initial API and implementation
 *******************************************************************************/
package org.psem2m.utilities;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author isandlatech (www.isandlatech.com) - ogattaz
 * 
 */
public final class CXJvmUtils {

	private static final int ID_WITDH = 40;

	public static Double JAVA_VERSION_6 = new Double(1.6);
	public static Double JAVA_VERSION_7 = new Double(1.7);
	public static Double JAVA_VERSION_8 = new Double(1.8);

	private static final int LINE_WITDH = 140;
	public static final int MASK_INFOS_JAVA = 1;
	public static final int MASK_INFOS_OS = 2;
	public static final int MASK_INFOS_PATHS = 8;

	public static final int MASK_INFOS_USER = 4;
	public static final int MASK_OTHER_PROPS = 16;
	public static final String MEMORY_KIND_ALLOCATED = "allocated";

	public static final String MEMORY_KIND_FREE = "free";

	public static final String MEMORY_KIND_FREE_TOTAL = "total free";
	public static final String MEMORY_KIND_MAX = "max";
	public static final String[] MEMORY_KINDS = { MEMORY_KIND_FREE, MEMORY_KIND_ALLOCATED, MEMORY_KIND_MAX,
			MEMORY_KIND_FREE_TOTAL };
	public static final char SEP_NUL = (char) 255;
	private static Double sJavaVersion = retreiveVersion();
	public static final String SYSPROP_DEFAULT_CHARSET = "defaultCharset";
	public static final String SYSPROP_JAVA_CLASS_PATH = "java.class.path";
	public static final String SYSPROP_JAVA_CLASS_VERS = "java.class.version";
	public static final String SYSPROP_JAVA_ENDORSED_DIR = "java.endorsed.dirs";
	public static final String SYSPROP_JAVA_EXT_DIR = "java.ext.dirs";
	public static final String SYSPROP_JAVA_HOME = "java.home";
	public static final String SYSPROP_JAVA_IO_TMPDIR = "java.io.tmpdir";
	public static final String SYSPROP_JAVA_RUN_NAME = "java.runtime.name";
	public static final String SYSPROP_JAVA_RUN_VERS = "java.runtime.version";
	public static final String SYSPROP_JAVA_SPEC_VERS = "java.specification.version";
	public static final String SYSPROP_JAVA_VENDOR = "java.vendor";
	public static final String SYSPROP_JAVA_VENDOR_URL = "java.vendor.url";
	public static final String SYSPROP_JAVA_VERS = "java.version";
	public static final String SYSPROP_JAVA_VM_INFO = "java.vm.info";
	public static final String SYSPROP_JAVA_VM_NAME = "java.vm.name";
	public static final String SYSPROP_JAVA_VM_VENDOR = "java.vm.vendor";
	public static final String SYSPROP_JAVA_VM_VERSION = "java.vm.version";
	public static final String SYSPROP_LIB_PATH = "java.library.path";
	public static final String SYSPROP_OS_ARCH = "os.arch";
	public static final String SYSPROP_OS_NAME = "os.name";

	public static final String SYSPROP_OS_VERS = "os.version";

	public static final String SYSPROP_SUPPORTED_ENCODING = "supported.encodings";
	public static final String SYSPROP_USER_COUNTRY = "user.country";

	public static final String SYSPROP_USER_DIR = "user.dir";

	public static final String SYSPROP_USER_HOME = "user.home";

	public static final String SYSPROP_USER_LANG = "user.language";
	public static final String SYSPROP_USER_NAME = "user.name";
	public static final String SYSPROP_USER_REGION = "user.region";

	public static final String SYSPROP_USER_TIMEZONE = "user.timezone";

	public static final String[] SYSPROPS = { SYSPROP_DEFAULT_CHARSET, SYSPROP_JAVA_CLASS_PATH,
			SYSPROP_JAVA_CLASS_VERS, SYSPROP_JAVA_ENDORSED_DIR, SYSPROP_JAVA_EXT_DIR, SYSPROP_JAVA_HOME,
			SYSPROP_JAVA_IO_TMPDIR, SYSPROP_JAVA_RUN_NAME, SYSPROP_JAVA_RUN_VERS, SYSPROP_JAVA_VENDOR,
			SYSPROP_JAVA_VERS, SYSPROP_JAVA_VM_INFO, SYSPROP_JAVA_VM_NAME, SYSPROP_JAVA_VM_VENDOR,
			SYSPROP_JAVA_VM_VERSION, SYSPROP_LIB_PATH, SYSPROP_OS_ARCH, SYSPROP_OS_NAME, SYSPROP_OS_VERS,
			SYSPROP_JAVA_SPEC_VERS, SYSPROP_SUPPORTED_ENCODING, SYSPROP_USER_DIR, SYSPROP_USER_HOME, SYSPROP_USER_LANG,
			SYSPROP_USER_NAME, SYSPROP_USER_COUNTRY, SYSPROP_USER_REGION, SYSPROP_USER_TIMEZONE,
			SYSPROP_JAVA_VENDOR_URL };

	public static final boolean VALUE_MULTI_LINE = true;

	public static final boolean VALUE_ONE_LINE = false;

	public static final int VECTOR_FULL_INFOS = MASK_INFOS_JAVA + MASK_INFOS_OS + MASK_INFOS_USER + MASK_INFOS_PATHS
			+ MASK_OTHER_PROPS;

	public static final int VECTOR_INFOS_LESS_PATHS = MASK_INFOS_JAVA + MASK_INFOS_OS + MASK_INFOS_USER
			+ MASK_OTHER_PROPS;

	private static final String[] XARGS_WITH_VALUE = { "Xmx", "Xms", "Xss", "Xmn", "Xbootclasspath" };

	/**
	 * @param aSB
	 * @param aId
	 * @param aIdSize
	 * @param aValue
	 * @param aValueSize
	 * @param aEndLine
	 *            a endline character (eg ':' in a list of paths )
	 * @return
	 */
	public static StringBuilder addDescrAlignInSB(final StringBuilder aSB, final String aId, final int aIdSize,
			final String aValue, final int aValueSize, final char aEndLine) {

		aSB.append(CXStringUtils.strAdjustRight(aId, aIdSize, ' '));
		aSB.append('=');
		aSB.append('[');

		if (aValue == null || aValue.length() <= aValueSize) {
			aSB.append(aValue);
		} else {
			int wMax = aValue.length();
			int wPos = 0;
			int wValueSize;
			while (wPos < wMax) {
				if (wPos > 0) {
					aSB.append('\n').append(CXStringUtils.strFromChar(' ', aIdSize + 2));
				}
				wValueSize = calcValueSize(aValue, wPos, aEndLine, aValueSize);
				if (wPos + wValueSize < wMax) {
					aSB.append(aValue.substring(wPos, wPos + wValueSize));
				} else {
					aSB.append(aValue.substring(wPos));
				}
				wPos += wValueSize;
			}
		}
		aSB.append(']');
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aId
	 * @return
	 */
	private static StringBuilder addJavaInfoDescrInSB(final StringBuilder aSB, final String aId) {

		return addJavaInfoDescrInSB(aSB, aId, System.getProperty(aId), SEP_NUL);
	}

	/**
	 * @param aSB
	 * @param aId
	 * @param aEndLine
	 * @return
	 */
	private static StringBuilder addJavaInfoDescrInSB(final StringBuilder aSB, final String aId, final char aEndLine) {

		return addJavaInfoDescrInSB(aSB, aId, System.getProperty(aId), aEndLine);
	}

	/**
	 * @param aSB
	 * @param aId
	 * @param aValue
	 * @return
	 */
	private static StringBuilder addJavaInfoDescrInSB(final StringBuilder aSB, final String aId, final String aValue,
			final char aEndLine) {

		return addDescrAlignInSB(aSB, aId, ID_WITDH, aValue, LINE_WITDH - ID_WITDH, aEndLine);
	}

	/**
	 * requete de test du service d'administration
	 * 
	 * @param aSB
	 * @param aSep
	 * @return
	 */
	static StringBuilder addSeparatorInSB(final StringBuilder aSB, final char aSep) {

		if (aSep != SEP_NUL) {
			aSB.append(aSep);
		}
		return aSB;
	}

	/**
	 * @param aRepport
	 * @param aClass
	 * @param aTargetCastClass
	 */
	public static StringBuilder appendClassInfosInSB(final StringBuilder aRepport, final Class<?> aClass) {
		Class<?> wClass = aClass;
		int wLevel = 0;
		while (wClass != null) {
			// MOD_OG_20150409 use getClassLoaderInfos
			aRepport.append(String.format("\nClass(%2d)=[%75s from %s]", wLevel, wClass.getName(),
					getClassLoaderInfos(wClass)));

			appendInterfaceInfosInSB(aRepport, wClass, 1);

			wLevel++;
			wClass = wClass.getSuperclass();
		}
		return aRepport;
	}

	/**
	 * @param aRepport
	 * @param aClass
	 * @param aInterfaceLevel
	 */
	private static StringBuilder appendInterfaceInfosInSB(final StringBuilder aRepport, final Class<?> aClass,
			final int aInterfaceLevel) {

		Class<?>[] wInterfaces = aClass.getInterfaces();
		if (wInterfaces != null && wInterfaces.length > 0) {
			int wNbInterfaces = wInterfaces.length;
			Class<?> wInterface;
			for (int wInterfaceIdx = 0; wInterfaceIdx < wNbInterfaces; wInterfaceIdx++) {
				wInterface = wInterfaces[wInterfaceIdx];

				// MOD_OG_20150409 use getClassLoaderInfos
				aRepport.append(String.format("\n  Interface(%d.%d)=[%68s from %s]", aInterfaceLevel, wInterfaceIdx,
						wInterface.getName(), getClassLoaderInfos(wInterface)));

				appendInterfaceInfosInSB(aRepport, wInterface, (aInterfaceLevel + 1));
			}

		}
		return aRepport;
	}

	/**
	 * 14w_009 - Intégration WebServices
	 * 
	 * @param wSB
	 * @return
	 */
	public static StringBuilder appendJavaContextInSB(final StringBuilder aSB) {

		return appendJavaContextInSB(aSB, '\n', VECTOR_FULL_INFOS, VALUE_MULTI_LINE);
	}

	/**
	 * Append the dump the context of the JVM in a char buffer
	 * 
	 * @param aSB
	 *            the char buffer
	 * @param aSeparator
	 *            the separator included between each information
	 * @param aInformationMask
	 *            the mask to select information
	 * @param aValueMultiLineLine
	 *            accepts format multiline information if true
	 * @return the description of the context of the JVM as a name-value pairs
	 *         list separated by the separator
	 */
	private static StringBuilder appendJavaContextInSB(final StringBuilder aSB, final char aSeparator,
			final int aInformationMask, final boolean aValueMultiLine) {

		if (aValueMultiLine) {
			addSeparatorInSB(aSB, aSeparator);
		}

		if ((aInformationMask & MASK_INFOS_JAVA) > 0) {
			appendSepLineInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_CLASS_VERS);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_ENDORSED_DIR);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_EXT_DIR);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_HOME);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_IO_TMPDIR);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_RUN_NAME);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_RUN_VERS);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_SPEC_VERS);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_VENDOR);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_VENDOR_URL);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_VERS);
			addSeparatorInSB(aSB, aSeparator);
			String wVersInfos = String.format(" JavaVersion=[%s] isJava7=[%s] isJava8=[%s] isLessThanJava8=[%s]",
					getJavaVersion(), isJava7(), isJava8(), isLessThanJava8());
			addDescrAlignInSB(aSB, "version infos", ID_WITDH, wVersInfos, LINE_WITDH - ID_WITDH, SEP_NUL);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_VM_INFO);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_VM_NAME);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_VM_VENDOR);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_VM_VERSION);

		}
		if ((aInformationMask & MASK_INFOS_OS) > 0) {
			if (aSB.length() > 0) {
				addSeparatorInSB(aSB, aSeparator);
			}
			appendSepLineInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_OS_ARCH);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_OS_NAME);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_OS_VERS);
		}
		if ((aInformationMask & MASK_INFOS_USER) > 0) {
			if (aSB.length() > 0) {
				addSeparatorInSB(aSB, aSeparator);
			}
			appendSepLineInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_USER_DIR);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_USER_HOME);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_USER_LANG);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_USER_NAME);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_USER_REGION);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_USER_COUNTRY);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_USER_TIMEZONE);
		}
		if ((aInformationMask & MASK_INFOS_PATHS) > 0) {
			if (aSB.length() > 0) {
				addSeparatorInSB(aSB, aSeparator);
			}
			appendSepLineInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_JAVA_CLASS_PATH, aValueMultiLine ? File.pathSeparatorChar : SEP_NUL);
			addSeparatorInSB(aSB, aSeparator);
			addJavaInfoDescrInSB(aSB, SYSPROP_LIB_PATH, aValueMultiLine ? File.pathSeparatorChar : SEP_NUL);
			addSeparatorInSB(aSB, aSeparator);

			addJavaInfoDescrInSB(aSB, SYSPROP_DEFAULT_CHARSET, Charset.defaultCharset().displayName(), SEP_NUL);
			addSeparatorInSB(aSB, aSeparator);

			addJavaInfoDescrInSB(aSB, SYSPROP_SUPPORTED_ENCODING,
					formatEncodings(aValueMultiLine, CXOSUtils.dumpSupportedEncodings()), aValueMultiLine ? ';'
							: SEP_NUL);
		}

		if ((aInformationMask & MASK_OTHER_PROPS) > 0) {
			if (aSB.length() > 0) {
				addSeparatorInSB(aSB, aSeparator);
			}
			appendSepLineInSB(aSB, aSeparator);
			appendOtherPropsInSB(aSB, aSeparator);
		}
		return aSB;
	}

	/**
	 * Adds all the other properties
	 * 
	 * @param aSB
	 * @return
	 */
	private static StringBuilder appendOtherPropsInSB(final StringBuilder aSB, final char aSeparator) {

		CXSortListProperties wProps = new CXSortListProperties(System.getProperties(), CXSortList.ASCENDING);
		TreeSet<Entry<Object, Object>> wEntries = wProps.getTreeSet();
		String wPropId;
		int wI = 0;
		for (Entry<Object, Object> wEntry : wEntries) {
			wPropId = wEntry.getKey().toString();
			if (!isStandardSysProp(wPropId)) {
				if (wI > 0) {
					addSeparatorInSB(aSB, aSeparator);
				}
				addJavaInfoDescrInSB(aSB, wPropId);
				wI++;
			}
		}
		return aSB;
	}

	/**
	 * @param aSB
	 * @param aSeparator
	 * @return
	 */
	static StringBuilder appendSepLineInSB(final StringBuilder aSB, final char aSeparator) {

		aSB.append(CXStringUtils.strFromChar('-', LINE_WITDH));
		addSeparatorInSB(aSB, aSeparator);
		return aSB;
	}

	/**
	 * @param aValue
	 * @param aPos
	 * @param aEndLine
	 * @param aDefaultSize
	 * @return
	 */
	private static int calcValueSize(final String aValue, final int aPos, final char aEndLine, final int aDefaultSize) {

		if (aEndLine == SEP_NUL) {
			return aDefaultSize;
		}

		int wValueSize = aDefaultSize;
		if (aPos + 1 < aValue.length()) {
			wValueSize = aValue.indexOf(aEndLine, aPos + 1) - aPos;
		}

		if (wValueSize < 0) {
			return aDefaultSize;
		}

		return Math.min(wValueSize + 1, aDefaultSize);
	}

	/**
	 * 
	 * @param aKey
	 *            eg. "-Xmx1024M" or
	 *            "-Xbootclasspath/p:/opt/agilium/v3/Agilium/lib/OB.jar" or
	 *            "-DThreadObserver.on=on"
	 * @return
	 */
	private static String cleanArgKey(String aKey) {
		if (aKey == null || aKey.isEmpty()) {
			return aKey;
		}
		return aKey.substring(1);
	}

	/**
	 * @param aClass
	 * @return
	 */
	public static String dumpClassInfos(final Class<?> aClass) {
		return dumpClassInfos(new StringBuilder(), aClass).toString();
	}

	/**
	 * @param aRepport
	 * @param aClass
	 * @return
	 */
	public static StringBuilder dumpClassInfos(final StringBuilder aRepport, final Class<?> aClass) {

		Class<?> wClass = aClass;
		int wLevel = 0;
		while (wClass != null) {
			aRepport.append(String.format("\nClass(%2d)=[%70s from %s]", wLevel, wClass.getName(),
					wClass.getClassLoader()));

			Class<?>[] wInterfaces = wClass.getInterfaces();
			if (wInterfaces != null && wInterfaces.length > 0) {
				int wNbInterfaces = wInterfaces.length;
				Class<?> wInterface;
				for (int wInterfaceIdx = 0; wInterfaceIdx < wNbInterfaces; wInterfaceIdx++) {
					wInterface = wInterfaces[wInterfaceIdx];
					int wInterfaceLevel = 0;
					while (wInterface != null) {
						aRepport.append(String.format("\n     Interface(%d.%d)=[%70s from %s]", wInterfaceIdx,
								wInterfaceLevel, wInterface.getName(), wInterface.getClassLoader()));
						wInterfaceLevel++;
						wInterface = wInterface.getSuperclass();
					}
				}
			}
			wLevel++;
			wClass = wClass.getSuperclass();
		}
		return aRepport;
	}

	/**
	 * @param aRepport
	 * @param aClass
	 * @param aTargetCastClass
	 * @deprecated
	 */
	@Deprecated
	public static void dumpClassInfosInSB(final StringBuilder aRepport, final Class<?> aClass) {
		dumpClassInfos(new StringBuilder(), aClass).toString();
	}

	/**
	 * Dump the context of the JVM
	 * 
	 * @return the description of the context of the JVM as a name-value pairs
	 *         table
	 */
	public static String dumpJavaContext() {
		return dumpJavaContext('\n', VECTOR_FULL_INFOS, VALUE_MULTI_LINE);

	}

	/**
	 * Dump the context of the JVM
	 * 
	 * 
	 * @param aSeparator
	 *            the separator included between each information
	 * @param aInformationMask
	 *            the mask to select information
	 * @param aValueMultiLineLine
	 *            accepts format multiline information if true
	 * @return the description of the context of the JVM as a name-value pairs
	 *         list separated by the separator
	 * 
	 * @see getJavaContext()
	 */
	public static String dumpJavaContext(final char aSeparator, final int aInformationMask,
			final boolean aValueMultiLineLine) {

		return appendJavaContextInSB(new StringBuilder(512), aSeparator, aInformationMask, aValueMultiLineLine)
				.toString();
	}

	/**
	 * <pre>
	 * -                                 DThreadObserver.on =[          on]
	 * -                      Dcom.sun.management.jmxremote =[            ]
	 * -         Dcom.sun.management.jmxremote.authenticate =[       false]
	 * -                 Dcom.sun.management.jmxremote.port =[        5556]
	 * -                  Dcom.sun.management.jmxremote.ssl =[       false]
	 * -                                     Dfile.encoding =[       UTF-8]
	 * -                                    Djava.io.tmpdir =[/opt/agilium/v3/Agilium/tmp]
	 * -                                          Duser.dir =[/opt/agilium/v3/Agilium]
	 * -                                     Dworkspace.loc =[/Users/ogattaz/workspaces/agilium-v3-forge]
	 * -                                     XX:MaxPermSize =[        256M]
	 * -                                     Xbootclasspath =[/p:/opt/agilium/v3/Agilium/lib/OB.jar]
	 * -                                                Xms =[        512M]
	 * -                                                Xmx =[       1024M]
	 * </pre>
	 * 
	 * @return
	 */
	public static String dumpJvmArgs() {
		StringBuilder wSB = new StringBuilder();
		for (Entry<String, String> wEntry : getJvmArgsMap().entrySet()) {
			wSB.append(String.format("\n- %50s =[%12s]", wEntry.getKey(), wEntry.getValue()));
		}
		return wSB.toString();
	}

	/**
	 * <pre>
	 * -                                        free memory =[     479,454]
	 * -                                   allocated memory =[     503,296]
	 * -                                         max memory =[     932,352]
	 * -                                  total free memory =[     908,510]
	 * </pre>
	 * 
	 * @return
	 */
	public static String dumpMemoryInfos() {

		NumberFormat format = NumberFormat.getInstance();
		StringBuilder wSB = new StringBuilder();
		for (Entry<String, Long> wEntry : getMemoryInfosMap().entrySet()) {
			String wTitle = wEntry.getKey().concat(" memory");
			String wValue = format.format(wEntry.getValue() / 1024);
			wSB.append(String.format("\n- %50s =[%12s]", wTitle, wValue));
		}
		return wSB.toString();
	}

	/**
	 * @param aValueMultiLine
	 * @param adumpSupportedEncodings
	 * @return
	 */
	private static String formatEncodings(final boolean aValueMultiLine, final String aDumpSupportedEncodings) {

		if (!aValueMultiLine) {
			return aDumpSupportedEncodings;
		}

		StringTokenizer wST = new StringTokenizer(aDumpSupportedEncodings, ",");

		StringBuilder wSB = new StringBuilder(wST.countTokens() * 20);
		int wI = 0;
		while (wST.hasMoreTokens()) {
			wSB.append(CXStringUtils.strAdjustLeft(wST.nextToken(), 19, ' '));
			wI++;
			if (wI == 5) {
				wSB.append(';');
				wI = 0;
			}
		}
		return wSB.toString();
	}

	/**
	 * <pre>
	 * Class( 0)=[                                         com.ibm.mq.jms.MQConnectionFactory from o.e.o.i.b.DefaultClassLoader@2cd84149[com.ibm.msg.client.osgi.wmq:8.0.0.0(id=47)]]
	 * Class( 1)=[                  com.ibm.msg.client.jms.admin.JmsJndiConnectionFactoryImpl from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(1.0)=[                                          javax.naming.Referenceable from bootstrap class loader]
	 *   Interface(1.1)=[                                                java.io.Serializable from bootstrap class loader]
	 * Class( 2)=[                      com.ibm.msg.client.jms.admin.JmsConnectionFactoryImpl from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(1.0)=[                         com.ibm.msg.client.jms.JmsConnectionFactory from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(2.0)=[                           com.ibm.msg.client.jms.JmsPropertyContext from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(3.0)=[                   com.ibm.msg.client.jms.JmsReadablePropertyContext from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(4.0)=[                                                java.io.Serializable from bootstrap class loader]
	 *   Interface(3.1)=[                                                       java.util.Map from bootstrap class loader]
	 *   Interface(2.1)=[                                         javax.jms.ConnectionFactory from o.e.o.i.b.DefaultClassLoader@1d2ac818[com.ibm.msg.client.osgi.jms.prereq:8.0.0.0(id=6)]]
	 * Class( 3)=[                     com.ibm.msg.client.jms.internal.JmsPropertyContextImpl from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(1.0)=[                           com.ibm.msg.client.jms.JmsPropertyContext from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(2.0)=[                   com.ibm.msg.client.jms.JmsReadablePropertyContext from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(3.0)=[                                                java.io.Serializable from bootstrap class loader]
	 *   Interface(2.1)=[                                                       java.util.Map from bootstrap class loader]
	 *   Interface(1.1)=[         com.ibm.msg.client.provider.ProviderPropertyContextCallback from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 * Class( 4)=[             com.ibm.msg.client.jms.internal.JmsReadablePropertyContextImpl from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(1.0)=[                   com.ibm.msg.client.jms.JmsReadablePropertyContext from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(2.0)=[                                                java.io.Serializable from bootstrap class loader]
	 * Class( 5)=[                                                           java.lang.Object from bootstrap class loader]
	 * </pre>
	 * 
	 * @param aClass
	 * @return
	 */
	public static String getClassInfos(final Class<?> aClass) {

		return appendClassInfosInSB(new StringBuilder(256), aClass).toString();
	}

	/**
	 * MOD_OG_20150409 create method
	 * 
	 * @return
	 */
	public static String getClassLoaderInfos(final Class<?> aClass) {

		if (aClass == null) {
			return "class null";
		}

		// Some implementations may use null to represent the bootstrap class
		// loader
		ClassLoader wClassLoader = aClass.getClassLoader();

		return (wClassLoader != null) ? wClassLoader.toString() : "bootstrap class loader";
	}

	/**
	 * @return
	 */
	public static String getCurrentThreadInfos() {

		Thread wCurrentThread = Thread.currentThread();
		return String.format("Thread=[%s] isDaemon=[%s] ThreadGroup=[%s] ContextClassLoader=[%s]",
				wCurrentThread.getName(), wCurrentThread.isDaemon(), wCurrentThread.getThreadGroup(),
				wCurrentThread.getContextClassLoader());
	}

	/**
	 * <pre>
	 *   Interface(1.0)=[                         com.ibm.msg.client.jms.JmsConnectionFactory from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(2.0)=[                           com.ibm.msg.client.jms.JmsPropertyContext from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(3.0)=[                   com.ibm.msg.client.jms.JmsReadablePropertyContext from o.e.o.i.b.DefaultClassLoader@77084cb5[com.ibm.msg.client.osgi.jms:8.0.0.0(id=18)]]
	 *   Interface(4.0)=[                                                java.io.Serializable from bootstrap class loader]
	 *   Interface(3.1)=[                                                       java.util.Map from bootstrap class loader]
	 *   Interface(2.1)=[                                         javax.jms.ConnectionFactory from o.e.o.i.b.DefaultClassLoader@1d2ac818[com.ibm.msg.client.osgi.jms.prereq:8.0.0.0(id=6)]]
	 * </pre>
	 * 
	 * @param aInterface
	 * @return
	 */
	public static String getInterfaceInfos(final Class<?> aInterface) {

		return appendInterfaceInfosInSB(new StringBuilder(256), aInterface, 1).toString();
	}

	/**
	 * Dump the context of the JVM
	 * 
	 * @return the description of the context of the JVM as a name-value pairs
	 *         table
	 * @deprecated
	 * @see dumpJavaContext()
	 */
	@Deprecated
	public static String getJavaContext() {

		return dumpJavaContext('\n', VECTOR_FULL_INFOS, VALUE_MULTI_LINE);
	}

	/**
	 * @param aSeparator
	 * @param aInformationMask
	 * @param aValueMultiLineLine
	 * @return
	 * @deprecated
	 * @see dumpJavaContext()
	 */
	@Deprecated
	public static String getJavaContext(final char aSeparator, final int aInformationMask,
			final boolean aValueMultiLineLine) {
		return dumpJavaContext(aSeparator, aInformationMask, aValueMultiLineLine);

	}

	/**
	 * Dump the context of the JVM
	 * 
	 * @param aInformationMask
	 *            the mask to select information
	 * @param aValueMultiLineLine
	 *            accepts format multiline information if true
	 * @return the description of the context of the JVM as a name-value pairs
	 *         table. Each pair in a separate line.
	 * 
	 * @see getJavaContext()
	 */
	public static String getJavaContext(final int aInformationMask, final boolean aValueMultiLineLine) {

		return getJavaContext('\n', aInformationMask, aValueMultiLineLine);
	}

	/**
	 * @return the version of the current Jvm
	 */
	public static Double getJavaVersion() {
		return sJavaVersion;
	}

	/**
	 * 
	 * @return the list of the given argument
	 */
	public static List<String> getJvmArgsList() {
		return ManagementFactory.getRuntimeMXBean().getInputArguments();
	}

	/**
	 * 
	 * @return the map of the given argument with their values
	 */
	public static Map<String, String> getJvmArgsMap() {

		Map<String, String> wMap = new TreeMap<>();

		for (String wArg : getJvmArgsList()) {
			String[] wParts = wArg.split("=");
			String wKey = cleanArgKey(wParts[0]);
			String wValue = "";
			if (wParts.length == 1) {

				for (String wXArg : XARGS_WITH_VALUE) {
					if (wKey.startsWith(wXArg)) {
						int wPos = wXArg.length();
						wValue = wKey.substring(wPos);
						wKey = wKey.substring(0, wPos);
						break;
					}
				}
			} else {
				wValue = wParts[1];
			}
			wMap.put(wKey, wValue);
		}
		return wMap;
	}

	/**
	 *
	 * @return
	 */
	public static Map<String, Long> getMemoryInfosMap() {

		Runtime runtime = Runtime.getRuntime();
		long wMaxMemory = runtime.maxMemory();
		long wAllocatedMemory = runtime.totalMemory();
		long wFreeMemory = runtime.freeMemory();

		Map<String, Long> wMap = new LinkedHashMap<>();
		wMap.put(MEMORY_KIND_FREE, wFreeMemory);
		wMap.put(MEMORY_KIND_ALLOCATED, wAllocatedMemory);
		wMap.put(MEMORY_KIND_MAX, wMaxMemory);
		wMap.put(MEMORY_KIND_FREE_TOTAL, (wFreeMemory + (wMaxMemory - wAllocatedMemory)));
		return wMap;
	}

	/**
	 * @return true if the version of the current Jvm is Java 7
	 */
	public static boolean isJava7() {
		return JAVA_VERSION_7.equals(getJavaVersion());
	}

	/**
	 * @return true if the version of the current Jvm is Java 8
	 */
	public static boolean isJava8() {
		return JAVA_VERSION_8.equals(getJavaVersion());
	}

	/**
	 * @return true if the version of the current Jvm is less than Java 8
	 */
	public static boolean isLessThanJava8() {
		return JAVA_VERSION_8 > getJavaVersion();
	}

	/**
	 * @param aId
	 * @return
	 */
	public static boolean isStandardSysProp(final String aId) {

		if (aId == null || aId.isEmpty()) {
			return false;
		}
		for (String wId : SYSPROPS) {
			if (wId.equals(aId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param aArgs
	 */
	public static void main(String[] aArgs) {
		try {
			System.out.println(dumpMemoryInfos());
			System.out.println(dumpJvmArgs());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * java.version=[1.7.0_45]
	 * 
	 * @return
	 */
	private static Double retreiveVersion() {
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos + 1);
		return Double.parseDouble(version.substring(0, pos));
	}

	/**
	 * 
	 */
	private CXJvmUtils() {
		super();
	}
}
