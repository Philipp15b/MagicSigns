package de.philworld.bukkit.magicsigns.signs.command;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;

/**
 * A sign that executes commands in the server's context. Caution!
 * 
 * The command can be written on the three last lines.
 * 
 * <code>%p</code> will be replaced with the player's name.
 * 
 */
@MagicSignInfo(
		friendlyName = "Console Command sign",
		description = "A sign that executes commands in the server's context. Caution!",
		buildPerm = "magicsigns.consolecommand.create",
		usePerm = "magicsigns.consolecommand.use")
public class ConsoleCommandSign extends CommandSign {

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[CCommand]");
	}

	public ConsoleCommandSign(Block sign, String[] lines)
			throws InvalidSignException {
		super(sign, lines);
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
