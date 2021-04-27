package org.cohorte.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * MOD_OG_20210427 Sharing utilities developped in Dimensions core
 * 
 * @author ogattaz
 *
 */
public class CXClassFilter implements IXClassFilter {

	private static final String MESS_ON_ADD = "Unable to add a package in the CClassFilter, the given package name is %s";

	private static final String MESS_ON_INSTANCIATE = "Unable to instanciate a CClassFilter, the given package name is %s";

	private List<String> pClassNameRegex = null;

	private Class<?> pExtendedClass = null;

	private List<Class<?>> pImplementedInterfaces = null;

	private List<String> pPackageNames = new ArrayList<>();

	private boolean pWithSubPackages = true;

	/**
	 * @param aPackageName
	 */
	public CXClassFilter(final String aPackageName) {
		super();

		pPackageNames.add(validatePackageName(MESS_ON_INSTANCIATE, aPackageName));
	}

	/**
	 * @param aClassNameRegex
	 * @return
	 */
	@Override
	public IXClassFilter addClassNameRegex(final String... aClassNameRegexArray) {
		if (pClassNameRegex == null) {
			pClassNameRegex = new ArrayList<>();
		}
		for (String wClassNameRegex : aClassNameRegexArray) {
			pClassNameRegex.add(wClassNameRegex);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.agilium.dimensions.core.utils.IClassesFiller#addImplementedInterface(java.
	 * lang.Class)
	 */
	@Override
	public IXClassFilter addImplementedInterface(final Class<?>... aInterfaceArray) {
		if (pImplementedInterfaces == null) {
			pImplementedInterfaces = new ArrayList<>();
		}
		for (Class<?> wInterface : aInterfaceArray) {
			pImplementedInterfaces.add(wInterface);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.agilium.dimensions.core.utils.IClassesFiller#addPackageName(java.lang.
	 * String)
	 */
	@Override
	public IXClassFilter addPackageName(final String... aPackageNameArray) {

		for (String wPackageName : aPackageNameArray) {

			pPackageNames.add(validatePackageName(MESS_ON_ADD, wPackageName));
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassFilter#getClassNameRegex()
	 */
	@Override
	public List<String> getClassNameRegex() {
		return pClassNameRegex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassesFiller#getExtendedClass()
	 */
	@Override
	public Class<?> getExtendedClass() {
		return pExtendedClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.agilium.dimensions.core.utils.IClassesFiller#getImplementedInterfaces()
	 */
	@Override
	public List<Class<?>> getImplementedInterfaces() {
		return pImplementedInterfaces;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassFilter#getNbClassNameRegex()
	 */
	@Override
	public int getNbClassNameRegex() {
		return (hasClassNameRegex()) ? pClassNameRegex.size() : -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.agilium.dimensions.core.utils.IClassesFiller#getNbImplementedInterfaces()
	 */
	@Override
	public int getNbImplementedInterfaces() {
		return (hasImplementedInterface()) ? pImplementedInterfaces.size() : -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassesFiller#getNbPackageNames()
	 */
	@Override
	public int getNbPackageNames() {
		return pPackageNames.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassesFiller#getPackagePaths()
	 */
	@Override
	public List<String> getPackageNames() {
		return pPackageNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassFilter#hasClassNameRegex()
	 */
	@Override
	public boolean hasClassNameRegex() {
		return pClassNameRegex != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassesFiller#hasExtendedClass()
	 */
	@Override
	public boolean hasExtendedClass() {
		return pExtendedClass != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.agilium.dimensions.core.utils.IClassesFiller#hasImplementedInterface()
	 */
	@Override
	public boolean hasImplementedInterface() {
		return getNbImplementedInterfaces() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.agilium.dimensions.core.utils.IClassFilter#isSimpleClassNameMatches(java.
	 * lang.String)
	 */
	@Override
	public boolean isSimpleClassNameMatches(String aClassName) {
		if (!hasClassNameRegex()) {
			return true;
		}
		if (aClassName == null || aClassName.isEmpty()) {
			return false;
		}

		for (String wClassNameRegex : pClassNameRegex) {

			if (aClassName.matches(wClassNameRegex)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassesFiller#withSubPackages()
	 */
	@Override
	public boolean isWithSubPackages() {
		return pWithSubPackages;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.agilium.dimensions.core.utils.IClassesFiller#setExtendedClass(java.lang.
	 * Class)
	 */
	@Override
	public IXClassFilter setExtendedClass(Class<?> aExtendedClass) {
		pExtendedClass = aExtendedClass;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.agilium.dimensions.core.utils.IClassFilter#setWithoutSubPackage()
	 */
	@Override
	public IXClassFilter setWithoutSubPackage() {
		pWithSubPackages = false;
		return this;
	}

	/**
	 * @param aMessageFormat
	 * @param aPackageName
	 * @return
	 */
	private String validatePackageName(final String aMessageFormat, String aPackageName) {

		if (aPackageName == null) {
			throw new RuntimeException(String.format(aMessageFormat, "null"));
		}
		if (aPackageName.isEmpty()) {
			throw new RuntimeException(String.format(aMessageFormat, "empty"));
		}
		return aPackageName;
	}

}
