package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * A sign that sets the health of a player to a specific value.
 *
 * @see {@link HealSign} - Increases the player's health.
 *
 */
@BuildPermission("magicsigns.health.create")
@UsePermission("magicsigns.health.use")
public class HealthSign extends PurchasableMagicSign {

	private int healthNumber = 20;

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Health]");
	}

	public HealthSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		if (!lines[1].isEmpty()) {
			healthNumber = new Integer(lines[1]);
		}

	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().setHealth(healthNumber);
		MSMsg.HEAL_SUCCESS.send(event.getPlayer());
	}


}
