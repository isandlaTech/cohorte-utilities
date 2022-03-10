package test.org.cohorte.utilities.picosoc.servicerefs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.Map;

import org.cohorte.utilities.CXMethodUtils;
import org.cohorte.utilities.junit.CAbstractJunitTest;
import org.cohorte.utilities.picosoc.CServicReference;
import org.cohorte.utilities.picosoc.CServiceKey;
import org.cohorte.utilities.picosoc.CServiceProperties;
import org.cohorte.utilities.picosoc.CServicesRegistry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * #48
 * 
 * @author ogattaz
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CTestServiceReferences extends CAbstractJunitTest {

	static final String LINE_SPACE = "                                                                                                            ";
	static final String LINE_TIRET = "----------------------------------------------------------------------";
	static final String LINES = LINE_TIRET + '\n' + LINE_SPACE;

	static final String PROP1 = "P1";

	static final String PROP2 = "P2";

	static final String PROP3 = "P3";

	static final String VAL1 = "V1";

	static final String VAL2 = "V2";

	static final String VAL3 = "V3";

	/**
 *
 */
	@AfterClass
	public static void destroy() {

		// log the destroy banner containing the report
		logBannerDestroy();
	}

	/**
 *
 */
	@BeforeClass
	public static void initialize() throws Exception {

		// initialise the map of the test method of the current junit test class

		initializeTestsRegistry();

		// log the initialization banner
		logBannerInitialization();
	}

	/**
	 * 
	 */
	public CTestServiceReferences() {
		super();
		getLogger().logInfo(this, "<init>", "instaciated");
	}

	/**
	 * @param aValue
	 */
	private void assertOK(final Object aValue) {
		String wMethodName = CXMethodUtils.getMethodName(0);
		getLogger().logInfo(this, wMethodName, "ASSERT EQUALS [%s] OK", aValue);
	}

	/**
	 * 
	 */
	private void doDumpRegistry() {
		String wDump = CServicesRegistry.getRegistry().dump();

		getLogger().logInfo(this, "doDumpRegistry", "%sDUMP REGISTRY\n%s",
				LINES, wDump);

	}

	/**
	 * @param aId
	 * @param aSpecification
	 * @param aProperties
	 * @throws Exception
	 */
	private <T> void doGetService(final String aId,
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties) throws Exception {

		getLogger().logInfo(this, "doGetService",
				"                    Id=[%s]", aId);

		CServiceKey<T> wSearchedKey = new CServiceKey<>(aSpecification,
				aProperties);
		getLogger().logInfo(this, "doGetService",
				"           SearchedKey=[%s]", wSearchedKey);

		CServicReference<T> wCServicReference = CServicesRegistry.getRegistry()
				.getServiceRef(aSpecification, aProperties);

		getLogger().logInfo(this, "doGetService",
				"ServicRef in registry =[%s]", wCServicReference);

		@SuppressWarnings("unchecked")
		CServicReference<T> wKnownServicReference = (CServicReference<T>) getTestsContext()
				.get(aId);

		assertEquals(wKnownServicReference, wCServicReference);

		assertOK(wCServicReference);

		T wService = CServicesRegistry.getRegistry().getService(aSpecification,
				aProperties);
		getLogger().logInfo(this, "doGetService",
				"               Service=[%s]", wService);

	}

	/**
	 * @param aStrictMode
	 * @param aInfos
	 * @param aSpecification
	 * @param aProperties
	 * @throws Exception
	 */
	private <T> int doGetServiceReferences(final boolean aStrictMode,
			final String aInfos, Class<? extends T> aSpecification,
			final Map<String, String> aProperties) throws Exception {

		String wLabelStrict = (aStrictMode) ? "SEARCH_MODE_STRICT"
				: "SEARCH_MODE_NOT_STRICT";

		this.

		getLogger().logInfo(this, "doGetServiceReferences", "%s%s %s", LINES,
				wLabelStrict, aInfos);

		List<CServicReference<T>> wServicReferences = CServicesRegistry
				.getRegistry().getServiceRefs(aSpecification, aProperties,
						aStrictMode);

		for (CServicReference<T> wServicReference : wServicReferences) {
			getLogger().logInfo(this, "doGetServiceReferences",
					"ServicRef in registry=[%s]", wServicReference);
		}

		return wServicReferences.size();
	}

	/**
	 * @param aId
	 * @param aProperties
	 * @throws Exception
	 */
	private <T> void doModifyPropsOfServiceRef(final String aId,
			final Map<String, String> aProperties) throws Exception {

		getLogger().logInfo(this, "doModifyPropsOfServiceRef",
				"                    Id=[%s]", aId);

		// retreive the CServicReference in the tests context
		@SuppressWarnings("unchecked")
		CServicReference<T> wKnownServicReference = (CServicReference<T>) getTestsContext()
				.get(aId);
		getLogger().logInfo(this, "doModifyPropsOfServiceRef",
				"ServicRef in context  =[%s]", wKnownServicReference);

		// retreive the Service in the CServicReference
		Object wService = wKnownServicReference.getService();

		getLogger().logInfo(this, "doModifyPropsOfServiceRef",
				"               Service=[%s]", wService);

		// retreive the CServicReference in the registry using the servive
		CServicReference<?> wCServicReference = CServicesRegistry.getRegistry()
				.findServiceRef(wService);

		getLogger().logInfo(this, "doModifyPropsOfServiceRef",
				"ServicRefe in registry=[%s]", wCServicReference);

		assertEquals(wKnownServicReference, wCServicReference);

		wCServicReference.setProperties(aProperties);

		getLogger().logInfo(this, "doModifyPropsOfServiceRef",
				"   ServicRef  modified=[%s]", wCServicReference);

	}

	/**
	 * @param aId
	 * @param aSpecification
	 * @param aProperties
	 * @param aService
	 * @return
	 * @throws Exception
	 */
	private <T> CServicReference<T> doRegister(final String aId,
			Class<? extends T> aSpecification,
			final Map<String, String> aProperties, final T aService)
			throws Exception {

		getLogger().logInfo(this, "doRegister", "        Service=[%s]",
				aService);

		CServicReference<T> wCServicReference = CServicesRegistry.getRegistry()
				.registerService(aSpecification, aProperties, aService);

		getTestsContext().put(aId, wCServicReference);

		getLogger().logInfo(this, "doRegister", "ServicReference=[%s]",
				wCServicReference);

		return wCServicReference;
	}

	/**
	 * @param aId
	 * @param aProperties
	 * @throws Exception
	 */
	private <T> void doRemovePropsOfServiceRef(final String aId,
			final String... aPropertyNames) throws Exception {

		getLogger().logInfo(this, "doRemovePropsOfServiceRef",
				"                   Id=[%s]", aId);

		// retreive the CServicReference in the tests context
		@SuppressWarnings("unchecked")
		CServicReference<T> wKnownServicReference = (CServicReference<T>) getTestsContext()
				.get(aId);
		getLogger().logInfo(this, "doRemovePropsOfServiceRef",
				"ServicRef in context =[%s]", wKnownServicReference);

		// retreive the Service in the CServicReference
		Object wService = wKnownServicReference.getService();

		getLogger().logInfo(this, "doRemovePropsOfServiceRef",
				"              Service=[%s]", wService);

		// retreive the CServicReference in the registry using the servive
		CServicReference<?> wCServicReference = CServicesRegistry.getRegistry()
				.findServiceRef(wService);

		getLogger().logInfo(this, "doRemovePropsOfServiceRef",
				"ServicRef in registry=[%s]", wCServicReference);

		assertEquals(wKnownServicReference, wCServicReference);

		for (String wPropertyName : aPropertyNames) {
			wCServicReference.removeProperty(wPropertyName);
		}

		getLogger().logInfo(this, "doModifyPropsOfServiceRef",
				"  ServicRef  modified=[%s]", wCServicReference);

	}
	/**
	 * @throws Exception
	 */
	@Test
	public void test05NewRefistry() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "New Registries";
		try {
			logBegin(this, wMethodName, "%s Begin... ", wAction);

			CServicesRegistry wReg1 = CServicesRegistry.getRegistry();
			
			getLogger().logInfo(this, wMethodName, "Registry 1: %s",wReg1);

			doDumpRegistry();

			CServicesRegistry wReg2 = CServicesRegistry.getRegistry();
			
			getLogger().logInfo(this, wMethodName, "Registry 2: %s",wReg2);
			
			assertEquals(wReg2,wReg1);
			
			doDumpRegistry();
			
			try {
				CServicesRegistry.newRegistry();
				
				// not OK if here !
				assertFalse(true);
				
			} catch (Exception e) {
				getLogger().logSevere(this, wMethodName, "Registry 3: %s",e.getMessage());
				assertTrue(e.getMessage().contains("already exists"));
			}



			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}	
		
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void test10RegisterService() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "New services";
		try {
			logBegin(this, wMethodName, "%s Begin... ", wAction);

			doRegister("A1", ISpecificationOne.class, null, new CServiceOne(
					"A1"));

			CServiceProperties wPropsB1 = CServiceProperties.newProps(PROP1,
					VAL1);
			doRegister("B1", ISpecificationOne.class, wPropsB1,
					new CServiceOne("B1"));

			CServiceProperties wPropsC1 = CServiceProperties.newProps(PROP1,
					VAL1).addPair(PROP2, VAL2);
			doRegister("C1", ISpecificationOne.class, wPropsC1,
					new CServiceOne("C1"));

			doDumpRegistry();

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test20RetreiveService() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Get services";
		try {
			logBegin(this, wMethodName, "%s Begin... ", wAction);

			/*
			 * A1
			 */
			doGetService("A1", ISpecificationOne.class, null);
			/*
			 * B1
			 */
			CServiceProperties wPropsB1 = CServiceProperties.newProps(PROP1,
					VAL1);
			doGetService("B1", ISpecificationOne.class, wPropsB1);
			/*
			 * C1
			 */
			CServiceProperties wPropsC1 = CServiceProperties.newProps(PROP1,
					VAL1).addPair(PROP2, VAL2);
			doGetService("C1", ISpecificationOne.class, wPropsC1);

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test30ModifySServiceRef() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Modify ServiceRef";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);
			/*
			 * A1
			 */
			CServiceProperties wProps1 = CServiceProperties.newProps(PROP3,
					VAL3);
			doModifyPropsOfServiceRef("A1", wProps1);
			doGetService("A1", ISpecificationOne.class, wProps1);

			// test the old key doent match
			try {
				doGetService("A1", ISpecificationOne.class, null);
				fail();
			} catch (Exception e) {
				assertTrue(
						"The service is not retreived withou properties (cf. old key)",
						e.getMessage().contains("Unable to get the service"));
				assertOK(true);
			}
			/*
			 * B1
			 */
			CServiceProperties wPropsB1 = CServiceProperties.newProps(PROP1,
					VAL1 + "modified").addPair(PROP3, VAL3);
			doModifyPropsOfServiceRef("B1", wPropsB1);
			doGetService("B1", ISpecificationOne.class, wPropsB1);
			/*
			 * C1
			 */
			CServiceProperties wPropsC1 = CServiceProperties.newProps()
					.addPair(PROP2, VAL2 + "modified").addPair(PROP3, VAL3);
			doModifyPropsOfServiceRef("C1", wPropsC1);

			// test remove
			doRemovePropsOfServiceRef("C1", PROP1);

			// test retreive without PROP1
			doGetService("C1", ISpecificationOne.class, wPropsC1);

			doDumpRegistry();

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test40GetServiceReferencess() throws Exception {
		String wMethodName = CXMethodUtils.getMethodName(0);
		String wAction = "Get service references";
		try {

			logBegin(this, wMethodName, "%s Begin...", wAction);
			/*
			 * 
			 */
			int wNb = doGetServiceReferences(
					CServicesRegistry.SEARCH_MODE_NOT_STRICT,
					"no props => assert 3 services", ISpecificationOne.class,
					null);
			assertEquals(3, wNb);
			assertOK(3);
			/*
			 * 
			 */
			CServiceProperties wPropsSearchA = CServiceProperties.newProps(
					PROP1, VAL1 + "modified");
			wNb = doGetServiceReferences(
					CServicesRegistry.SEARCH_MODE_NOT_STRICT,
					"One prop P1 => assert 1 service", ISpecificationOne.class,
					wPropsSearchA);
			assertEquals(1, wNb);
			assertOK(1);
			/*
			 * 
			 */
			CServiceProperties wPropsSearchB = CServiceProperties.newProps(
					PROP3, VAL3);
			wNb = doGetServiceReferences(
					CServicesRegistry.SEARCH_MODE_NOT_STRICT,
					"One prop P3 => assert 3 services",
					ISpecificationOne.class, wPropsSearchB);
			assertEquals(3, wNb);
			assertOK(3);
			/*
			 * 
			 */
			wNb = doGetServiceReferences(CServicesRegistry.SEARCH_MODE_STRICT,
					"no props => assert 0 service", ISpecificationOne.class,
					null);

			assertEquals(0, wNb);
			assertOK(0);

			doDumpRegistry();

			logEndOK(this, wMethodName, "%s End OK.", wAction);
		} catch (Throwable e) {
			logEndKO(this, wMethodName, e);
			throw e;
		}
	}
}
