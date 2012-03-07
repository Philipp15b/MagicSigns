package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSign;
import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;

/**
 * A sign that executes commands on the sign in the player's context.
 *
 * <code>%p</code> will be replaced with the player's name.
 *
 */
@BuildPermission("magicsigns.command.create")
@UsePermission("magicsigns.command.use")
public class CommandSign extends MagicSign {

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Command]");
	}

	private String command;

	public CommandSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);
		this.command = lines[1] + lines[2] + lines[3];
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		Bukkit.getServer().dispatchCommand(event.getPlayer(),
				this.command.replace("%p", event.getPlayer().getName()));
	}

}
