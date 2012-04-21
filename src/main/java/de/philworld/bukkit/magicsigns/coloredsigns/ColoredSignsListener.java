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
			for (int i = 0; i <= 3; i++) {
				String line = event.getLine(i);
				line = line.replaceAll("&(?<!&&)(?=[0-9a-fA-F])", "\u00A7")
						.replace("&&", "&");
				event.setLine(i, line);
			}
		}
	}

}
