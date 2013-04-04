package de.philworld.bukkit.magicsigns.coloredsigns;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColoredSignsListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onSignChange(SignChangeEvent event) {
		if (event.getPlayer()
				.hasPermission(ColoredSigns.COLOR_SIGNS_PERMISSION)) {
			for (int line = 0; line <= 3; line++) {
				boolean seenAnd = false;
				for (int ic = 0; ic < event.getLine(line).length(); ic++) {
					char ch = event.getLine(line).charAt(ic);
					if (seenAnd && "0123456789abcdefklmnor".indexOf(ch) != -1) {
						StringBuilder sb = new StringBuilder(
								event.getLine(line));
						sb.setCharAt(ic - 1, '§');
						event.setLine(line, sb.toString());
						seenAnd = false;
					} else if (ch == '&') {
						seenAnd = true;
					} else {
						seenAnd = false;
					}
				}
			}
		}
	}
}
