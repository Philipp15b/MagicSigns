package de.philworld.bukkit.magicsigns.signs.permission;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.config.Configuration;
import de.philworld.bukkit.magicsigns.config.MacroConfiguration;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.PurchasableMagicSign;
import de.philworld.bukkit.magicsigns.util.MacroUtil;

@MagicSignInfo(
		name = "Permission",
		buildPerm = "magicsigns.permission.create",
		description = "A sign that gives a player permissions.",
		friendlyName = "Permission sign",
		usePerm = "magicsigns.permission.use")
public class PermissionSign extends PurchasableMagicSign {

	private static class LocalConfiguration extends MacroConfiguration {
		public LocalConfiguration() {
			super("permission-macros");
		}
	}

	protected static LocalConfiguration config = new LocalConfiguration();

	public static Configuration getConfig() {
		return config;
	}

	private final List<String> permissions;

	public PermissionSign(Location location, String[] lines)
			throws InvalidSignException {
		super(location, lines);
		permissions = MacroUtil.format(lines[1] + lines[2], config.getMacros());
		if (permissions.size() == 0)
			throw new InvalidSignException("No permissions found!");
	}

	@Override
	public boolean withdrawPlayer(Player p) throws PermissionException {
		if (MagicSigns.getPermission() == null)
			throw new PermissionException("Permission support is disabled!");
		boolean hasAll = true;
		for (String perm : permissions) {
			if (!MagicSigns.getPermission().has(p, perm)) {
				hasAll = false;
				break;
			}
		}
		if (hasAll)
			throw new PermissionException("You already have these permissions!");
		return super.withdrawPlayer(p);
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		// Permission support is already checked in withdrawPlayer() which is
		// always called before this.
		for (String perm : permissions) {
			MagicSigns.getPermission().playerAdd(event.getPlayer(), perm);
		}
		event.getPlayer().sendMessage(
				ChatColor.GREEN + "You got some permissions!");
	}

	protected String getPermissions(String[] lines) {
		return lines[1] + lines[2];
	}

	protected boolean hasPermission(Player p, String perm) {
		return MagicSigns.getPermission().has((World) null, p.getName(), perm);
	}

	protected void addPermission(Player p, String perm) {
		MagicSigns.getPermission().playerAdd((World) null, p.getName(), perm);
	}
}
