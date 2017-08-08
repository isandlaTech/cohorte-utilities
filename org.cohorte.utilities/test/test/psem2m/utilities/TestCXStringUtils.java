package test.psem2m.utilities;

import junit.framework.TestCase;

import org.psem2m.utilities.CXStringUtils;

public class TestCXStringUtils extends TestCase {

	public void testIs() {
		assertTrue(CXStringUtils.isFloat("1.2"));
		assertTrue(CXStringUtils.isFloat("1,2", ','));
		assertFalse(CXStringUtils.isFloat("1,2", '.'));
		assertFalse(CXStringUtils.isFloat("1,2"));
		assertFalse(CXStringUtils.isFloat("a,b"));
		assertFalse(CXStringUtils.isFloat("a.b"));

	}
}
