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
 * Line 2 must contain coordinates (comma-separated, e.g. <code>20,20,20</code>),
 * line 3 can contain yaw and pitch (comma-seperated, as above) where pitch is optional.
 *
 */
@MagicSignInfo(
		friendlyName = "Teleport sign",
		description = "Sign that allows teleportation.",
		buildPerm = "magicsigns.teleport.create",
		usePerm = "magicsigns.teleport.use")
public class TeleportSign extends PurchasableMagicSign {
	
	@SuppressWarnings("serial")
	private static final Map<String, Integer> YAW_SHORTHANDS = new HashMap<String, Integer>() {
		{
			put("N", 180);
			put("NE", 225);
			put("E", 270);
			put("SE", 315);
			put("S", 0);
			put("SW", 45);
			put("W", 90);
		}
	};

	private final Location destination;

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Teleport]");
	}

	public TeleportSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		try {
			String[] coords = lines[1].split(",");
			if (coords.length == 3) {
				destination = new Location(sign.getWorld(), new Integer(coords[0]),
						new Integer(coords[1]), new Integer(coords[2]));
			} else {
				throw new InvalidSignException("Line 2 must contain coordinates!");
			}
		
			String[] direction = lines[2].split(",");
			if (direction.length > 2) {
				throw new InvalidSignException("Line 3 must specifiy yaw and pitch in the format 'yaw, pitch'!");
			}
			if (direction.length > 0) {
				String yaw = direction[0].trim();
				if (YAW_SHORTHANDS.containsKey(yaw)) {
					destination.setYaw(YAW_SHORTHANDS.get(yaw));
				} else {
					destination.setYaw(new Integer(yaw));
				}
			}
			if (direction.length > 1) {
				destination.setPitch(new Integer(direction[1]));
			}
		} catch (NumberFormatException e) {
			throw new InvalidSignException("Invalid number(s) given!");
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().teleport(destination);
		MSMsg.TELEPORT_SUCCESS.send(event.getPlayer());
	}

}
