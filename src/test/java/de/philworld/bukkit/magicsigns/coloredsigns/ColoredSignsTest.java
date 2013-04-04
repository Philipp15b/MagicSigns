package de.philworld.bukkit.magicsigns.coloredsigns;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.Test;

public class ColoredSignsTest {

	private final ColoredSignsListener listener = new ColoredSignsListener();

	private void assertLine(int index, String in, String expected) {
		String[] lines = { "", "", "", "" };
		lines[index] = in;

		Player mockPlayer = mock(Player.class);
		when(mockPlayer.hasPermission(ColoredSigns.COLOR_SIGNS_PERMISSION))
				.thenReturn(true);

		SignChangeEvent event = new SignChangeEvent(null, mockPlayer, lines);
		listener.onSignChange(event);
		assertEquals(expected, event.getLine(index));
	}

	@Test
	public void test() {
		assertLine(1, "&r", "§r");
		assertLine(2, "&&r", "&§r");
		assertLine(0, "&&&r", "&&§r");
		assertLine(3, "&a", "§a");
		assertLine(1, "&&a", "&§a");
		assertLine(2, "&f", "§f");
		assertLine(3, "&k", "§k");
		assertLine(3, "& a", "& a");
		assertLine(1, "&& a", "&& a");
		assertLine(2, "&af", "§af");
		assertLine(0, "hello && world", "hello && world");
	}

}
