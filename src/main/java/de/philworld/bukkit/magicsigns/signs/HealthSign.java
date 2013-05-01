package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.BlockLocation;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * A sign that sets the health of a player to a specific value.
 * 
 * @see {@link HealSign} - Increases the player's health.
 * 
 */
@MagicSignInfo(
		name = "Health",
		friendlyName = "Health sign",
		description = "A sign that sets the health of a player to a specific value.",
		buildPerm = "magicsigns.health.create",
		usePerm = "magicsigns.health.use")
public class HealthSign extends PurchasableMagicSign {

	private int healthNumber = 20;

	public HealthSign(BlockLocation location, String[] lines) throws InvalidSignException {
		super(location, lines);

		if (!lines[1].isEmpty()) {
			try {
				healthNumber = new Integer(lines[1]);
			} catch (NumberFormatException e) {
				throw new InvalidSignException("The health on line 2 must be a number or empty!");
			}
		}

	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().setHealth(healthNumber);
		MSMsg.HEAL_SUCCESS.send(event.getPlayer());
	}

}
