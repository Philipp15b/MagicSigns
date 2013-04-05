package de.philworld.bukkit.magicsigns;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SignTypeParserTest {

	private static void assertParse(String input, String expected) {
		assertEquals(expected, MagicSignsListener.getSignTypeFor(input));
	}

	@Test
	public void test() {
		assertParse("[Test]", "Test");
		assertParse("[Test", null);
		assertParse("Test]", null);
		assertParse("   [Test]  ", "Test");
		assertParse("\t  [Test]\t ", "Test");
		assertParse("  [  Test ] ", null);
		assertParse("&f[Test]", null);
		assertParse("abc[Test]def", null);
	}

}
