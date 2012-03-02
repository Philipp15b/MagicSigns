package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSign;
import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * Sign that allows teleportation.
 * 
 * Line 2 must contain coordinates (comma-separated, e.g. <code>20,20,20</code>)
 * 
 */
@BuildPermission("magicsigns.teleport.create")
@UsePermission("magicsigns.teleport.use")
public class TeleportSign extends MagicSign {

	private final Location destination;

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Teleport]");
	}

	public TeleportSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		if (lines[1].split(",").length == 3) {
			String[] coords = lines[1].split(",");

			destination = new Location(sign.getWorld(), new Integer(coords[0]),
					new Integer(coords[1]), new Integer(coords[2]));
		} else {
			throw new InvalidSignException("Line 2 must contain coordinates!");
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().teleport(destination);
		MSMsg.TELEPORT_SUCCESS.send(event.getPlayer());
	}

}
