package de.philworld.bukkit.magicsigns.signs.permission;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.config.Configuration;

@MagicSignInfo(
		name = "WPermission",
		buildPerm = "magicsigns.wpermission.create",
		description = "A sign that gives a player permissions in a certain world.",
		friendlyName = "World Permission sign",
		usePerm = "magicsigns.wpermission.use")
public class WorldPermissionSign extends PermissionSign {

	public static Configuration getConfig() {
		return null; // we reuse PermissionSign's config
	}

	private final World world;

	public WorldPermissionSign(Location location, String[] lines)
			throws InvalidSignException {
		super(location, lines);
		String worldName = lines[2].trim();
		if (!worldName.isEmpty()) {
			world = Bukkit.getServer().getWorld(worldName);
			if (world == null)
				throw new InvalidSignException("Could not find world '"
						+ worldName + "'!");
		} else {
			throw new InvalidSignException(
					"The third line must contain a world!");
		}
	}

	@Override
	protected String getPermissions(String[] lines) {
		return lines[1];
	}

	@Override
	protected boolean hasPermission(Player p, String perm) {
		return MagicSigns.getPermission().has(world, p.getName(), perm);
	}

	@Override
	protected void addPermission(Player p, String perm) {
		MagicSigns.getPermission().playerAdd(world, p.getName(), perm);
	}

}
