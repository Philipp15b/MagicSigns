package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSign;
import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * A sign that can modify player's velocity.
 * 
 * It accepts also an vector (comma-separated) for velocity on the second line.
 * For example: <code>0,300,0</code> to make a huge jump in the air.
 * 
 */
@BuildPermission("magicsigns.rocket.create")
@UsePermission("magicsings.rocket.use")
public class RocketSign extends MagicSign {

	private Vector velocity = new Vector(0, 100, 0);

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Rocket]");
	}

	public RocketSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		if (!lines[1].isEmpty()) {
			String[] parts = lines[1].split(",");

			velocity = new Vector(new Integer(parts[0]), new Integer(parts[1]),
					new Integer(parts[2]));
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().setVelocity(velocity);
		MSMsg.ROCKETED.send(event.getPlayer());
	}

}
