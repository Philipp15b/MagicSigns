package de.philworld.bukkit.magicsigns.signs.command;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.config.Configuration;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

/**
 * A sign that executes commands in the server's context. Caution!
 * 
 * The command can be written on the three last lines.
 * 
 * <code>%p</code> will be replaced with the player's name.
 * 
 */
@MagicSignInfo(
		name = "CCommand",
		friendlyName = "Console Command sign",
		description = "A sign that executes commands in the server's context. Caution!",
		buildPerm = "magicsigns.consolecommand.create",
		usePerm = "magicsigns.consolecommand.use")
public class ConsoleCommandSign extends CommandSign {

	public static Configuration getConfig() {
		return null; // we reuse CommandSigns' config
	}

	public ConsoleCommandSign(BlockLocation location, String[] lines)
			throws InvalidSignException {
		super(location, lines);
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		for (String cmd : this.commands) {
			Bukkit.getServer().dispatchCommand(
					Bukkit.getServer().getConsoleSender(),
					formatCommand(cmd, event));
		}
	}

}
