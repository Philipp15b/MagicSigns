package de.philworld.bukkit.magicsigns.signs.permission;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.MagicSigns;

@MagicSignInfo(
		buildPerm = "magicsigns.lpermission.create",
		description = "A sign that gives a player permissions in the current world.",
		friendlyName = "Local Permission sign",
		usePerm = "magicsigns.lpermission.use")
public class LocalPermissionSign extends PermissionSign {

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[LPermission]")
				&& MagicSigns.getPermission() != null;
	}

	public LocalPermissionSign(Location location, String[] lines)
			throws InvalidSignException {
		super(location, lines);
	}

	@Override
	protected boolean hasPermission(Player p, String perm) {
		return MagicSigns.getPermission().has(p.getWorld(), p.getName(), perm);
	}

	@Override
	protected void addPermission(Player p, String perm) {
		MagicSigns.getPermission().playerAdd(p.getWorld(), p.getName(), perm);
	}

}
