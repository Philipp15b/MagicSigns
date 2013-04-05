package de.philworld.bukkit.magicsigns.signs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * Sign that allows teleportation.
 * 
 * Line 2 must contain coordinates (comma-separated, e.g. <code>20,20,20</code>
 * ), line 3 can contain yaw and pitch (comma-seperated, as above) where pitch
 * is optional.
 * 
 */
@MagicSignInfo(
		friendlyName = "Teleport sign",
		description = "Sign that allows teleportation.",
		buildPerm = "magicsigns.teleport.create",
		usePerm = "magicsigns.teleport.use")
public class TeleportSign extends PurchasableMagicSign {

	@SuppressWarnings("serial") private static final Map<String, Integer> YAW_SHORTHANDS = new HashMap<String, Integer>(
			8) {
		{
			put("N", 180);
			put("NE", 225);
			put("E", 270);
			put("SE", 315);
			put("S", 0);
			put("SW", 45);
			put("W", 90);
			put("NW", 135);
		}
	};

	private final Location destination;

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Teleport]");
	}

	public TeleportSign(Location location, String[] lines)
			throws InvalidSignException {
		super(location, lines);

		String[] coords = lines[1].split(",");
		if (coords.length == 3) {
			try {
				destination = new Location(location.getWorld(), new Integer(
						coords[0]), new Integer(coords[1]), new Integer(
						coords[2]));
			} catch (NumberFormatException e) {
				throw new InvalidSignException(
						"Invalid numbers in coordinates!");
			}
		} else {
			throw new InvalidSignException(
					"Line 2 must contain coordinates in the format '1,2,3'!");
		}

		String[] direction = lines[2].split(",");
		if (direction.length > 2) { // too many arguments
			throw new InvalidSignException(
					"Line 3 must specifiy yaw and pitch in the format 'yaw, pitch'!");
		}
		String yaw = direction[0].trim();
		if (!yaw.isEmpty()) {
			if (YAW_SHORTHANDS.containsKey(yaw)) {
				destination.setYaw(YAW_SHORTHANDS.get(yaw));
			} else {
				try {
					destination.setYaw(new Integer(yaw));
				} catch (NumberFormatException e) {
					throw new InvalidSignException("Yaw is an invalid number!");
				}
			}
		}
		if (direction.length > 1) {
			try {
				destination.setPitch(new Integer(direction[1]));
			} catch (NumberFormatException e) {
				throw new InvalidSignException("Pitch is an invalid number!");
			}
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().teleport(destination);
		MSMsg.TELEPORT_SUCCESS.send(event.getPlayer());
	}

}
