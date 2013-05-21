package de.philworld.bukkit.magicsigns.signs.permission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.signs.MacroTest;

public class PermissionSignMacroTest extends MacroTest {

	public PermissionSignMacroTest() {
		super(PermissionSign.class, "parsePermissions");
	}

	@Test
	public void testPermissionParsing() throws Exception {
		test("p.e.r.m.i.s.s.i.o.n", "p.e.r.m.i.s.s.i.o.n");
		test("perm.1 && perm.2", "perm.1", "perm.2");
		test("perm.1 && perm.2 && perm.3", "perm.1", "perm.2", "perm.3");
	}

	@Test
	public void testMacroInsertion() throws Exception {
		addMacro("simple", "some.permission", "some.other");
		test("$simple$", "some.permission", "some.other");

		addMacro("yo-dawg-i-heard-you-like-macros", "$simple$", "another");
		test("$yo-dawg-i-heard-you-like-macros$", "some.permission", "some.other", "another");
	}

	@Test
	public void testMissingMacro() throws Exception {
		try {
			test("$404$");
			fail("Should have thrown an InvalidSignException!");
		} catch (InvalidSignException e) {
			assertEquals("Could not find permission macro '404'!", e.getMessage());
		}
	}

	@Test
	public void testInvalidMacro() throws Exception {
		try {
			test("$invalid");
			fail("Should have thrown an InvalidSignException!");
		} catch (InvalidSignException e) {
			assertEquals("Expected closing '$' after macro beginning with '$'!", e.getMessage());
		}
	}

}
