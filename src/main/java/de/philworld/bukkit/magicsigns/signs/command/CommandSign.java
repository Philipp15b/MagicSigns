package de.philworld.bukkit.magicsigns.signs.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.config.Configuration;
import de.philworld.bukkit.magicsigns.config.MacroConfiguration;
import de.philworld.bukkit.magicsigns.signs.PurchasableMagicSign;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

/**
 * A sign that executes commands on the sign in the player's context.
 * 
 * <code>%p</code> will be replaced with the player's name.
 * 
 */
@MagicSignInfo(
		name = "Command",
		friendlyName = "Command sign",
		description = "A sign that executes commands on the sign in the player's context.",
		buildPerm = "magicsigns.command.create",
		usePerm = "magicsigns.command.use")
public class CommandSign extends PurchasableMagicSign {

	/**
	 * Configuration that saves all macros.
	 */
	private static class LocalConfiguration extends MacroConfiguration {
		public LocalConfiguration() {
			super("command-macros");
		}
	}

	private static LocalConfiguration config = new LocalConfiguration();

	public static Configuration getConfig() {
		return config;
	}

	protected List<String> commands = new LinkedList<String>();

	public CommandSign(BlockLocation location, String[] lines) throws InvalidSignException {
		super(location, lines);
		commands = parseCommands(lines[1] + lines[2]);
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		for (String cmd : commands) {
			Bukkit.getServer().dispatchCommand(event.getPlayer(), formatCommand(cmd, event));
		}

	}

	protected String formatCommand(String cmd, PlayerInteractEvent event) {
		return cmd.replace("%p", event.getPlayer().getName());
	}

	private static List<String> parseCommands(String input) throws InvalidSignException {
		return parseCommands(Arrays.asList(input.split(" && ")));
	}

	private static List<String> parseCommands(List<String> input) throws InvalidSignException {
		List<String> commands = new ArrayList<String>(2);
		for (String r : input) {
			r = r.trim();
			char first = r.charAt(0);
			if (first == '/') {
				commands.add(r.substring(1));
			} else if (first == '$') {
				int macroEnd = r.lastIndexOf('$');
				if (macroEnd == -1 || macroEnd == 0)
					throw new InvalidSignException("Expected closing '$' after macro beginning with '$'!");
				String macroName = r.substring(1, macroEnd);
				List<String> macroValue = config.getMacros().get(macroName);
				if (macroValue == null)
					throw new InvalidSignException("Could not find command macro '" + macroName + "'!");
				commands.addAll(parseCommands(macroValue));
			} else {
				throw new InvalidSignException(
						"Expected command (beginning with a '/') or a macro (enclosed within '$')!");
			}
		}
		return commands;
	}

}
