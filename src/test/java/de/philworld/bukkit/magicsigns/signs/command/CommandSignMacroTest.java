package de.philworld.bukkit.magicsigns.signs.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.signs.MacroTest;

public class CommandSignMacroTest extends MacroTest {

	public CommandSignMacroTest() {
		super(CommandSign.class, "parseCommands");
	}

	@Test
	public void testCommandParsing() throws Exception {
		test("/command", "command");
		test("/command1 && /command2", "command1", "command2");
		test("/command1 && /command2 && /command3", "command1", "command2", "command3");
	}

	@Test
	public void testInvalidCommand() throws Exception {
		try {
			test("invalid");
			fail("Should have thrown an InvalidSignException!");
		} catch (InvalidSignException e) {
			assertEquals("Expected command (beginning with a '/') or a macro (enclosed within '$')!", e.getMessage());
		}
	}

	@Test
	public void testMacroInsertion() throws Exception {
		addMacro("simple", "/cmd1", "/cmd2");
		test("$simple$", "cmd1", "cmd2");

		addMacro("yo-dawg-i-heard-you-like-macros", "$simple$", "/cmd3");
		test("$yo-dawg-i-heard-you-like-macros$", "cmd1", "cmd2", "cmd3");
	}

	@Test
	public void testMissingMacro() throws Exception {
		try {
			test("$404$");
			fail("Should have thrown an InvalidSignException!");
		} catch (InvalidSignException e) {
			assertEquals("Could not find command macro '404'!", e.getMessage());
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
