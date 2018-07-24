package test.psem2m.utilities;

import org.psem2m.utilities.CXJvmUtils;

import junit.framework.TestCase;

/**
 * @author ogattaz
 *
 */
public class TestCXJvmUtils extends TestCase {

	public void testIs() {
		assertNotNull(CXJvmUtils.dumpMemoryInfos());

		assertNotNull(CXJvmUtils.dumpJvmArgs());

		assertNotNull(CXJvmUtils.dumpClassInfos(CXJvmUtils.class));

		assertNotNull(CXJvmUtils.dumpJavaContext());
	}
}
