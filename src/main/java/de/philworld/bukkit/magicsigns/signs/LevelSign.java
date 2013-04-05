package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * Adds a specific amount of levels to a player.
 * 
 */
@MagicSignInfo(
		name = "Level",
		friendlyName = "Level sign",
		description = "Adds a specific amount of levels to a player.",
		buildPerm = "magicsigns.level.create",
		usePerm = "magicsigns.level.use")
public class LevelSign extends PurchasableMagicSign {

	private int additionalLevels = 1;

	public LevelSign(Location location, String[] lines)
			throws InvalidSignException {
		super(location, lines);

		if (!lines[1].isEmpty()) {
			try {
				additionalLevels = new Integer(lines[1]);
			} catch (NumberFormatException e) {
				throw new InvalidSignException(
						"The level on line 2 must be a number or empty!");
			}
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		p.setLevel(p.getLevel() + additionalLevels);
		MSMsg.LEVEL_ADDED.send(p, "" + additionalLevels);
	}

}
