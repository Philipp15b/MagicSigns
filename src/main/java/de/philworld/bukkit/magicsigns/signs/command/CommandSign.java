package de.philworld.bukkit.magicsigns.signs.command;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.config.ConfigurationBase;
import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;
import de.philworld.bukkit.magicsigns.signs.MagicSign;

/**
 * A sign that executes commands on the sign in the player's context.
 *
 * <code>%p</code> will be replaced with the player's name.
 *
 */
@BuildPermission("magicsigns.command.create")
@UsePermission("magicsigns.command.use")
public class CommandSign extends MagicSign {

	public static final String COMMAND_DELIMITER = " && ";
	public static final String MACRO_START = "$";
	public static final String MACRO_END = "$";

	/**
	 * Configuration that saves all macros in a Map.
	 */
	public static class LocalConfiguration extends ConfigurationBase {

		public Map<String, List<String>> macros = new HashMap<String, List<String>>();

		@Override
		public void load(ConfigurationSection section) {
			section = section.getConfigurationSection("macros");
			if (section != null) {
				Map<String, Object> values = section.getValues(false);

				// iterate over all macros
				for (Map.Entry<String, Object> entry : values.entrySet()) {

					String key = entry.getKey();
					try {

						// save values as lists in the macros map
						@SuppressWarnings("unchecked")
						List<String> commands = (List<String>) entry.getValue();
						macros.put(key, commands);

					} catch (ClassCastException e) {
						MagicSigns
								.inst()
								.getLogger()
								.log(Level.WARNING,
										"Config value of 'macros."
												+ key
												+ "' must by of type 'List<String>', found instead '"
												+ entry.getValue().getClass()
														.getName() + "'!");
					}
				}
			}

		}

		@Override
		public ConfigurationSection save(ConfigurationSection section) {
			return section; // nothing to save, just read
		}
	}

	public static LocalConfiguration config = new LocalConfiguration();

	public static void loadConfig(ConfigurationSection section) {
		config.load(section);
		if (config.macros != null)
			System.out.println(config.macros.toString());
	}

	/**
	 * Splits the text by the backslash and replaces macros. Returns a list
	 * representing all the commands.
	 *
	 * <p>TODO Catch endless loops of self-recalling macros.
	 *
	 * @param text
	 *            - The text to format
	 * @return List of the commands
	 */
	public static LinkedList<String> format(String text) {

		String[] commands = text.split(COMMAND_DELIMITER);

		LinkedList<String> cmdList = new LinkedList<String>();

		for (String command : commands) {

			// iterate over all available macros and check if present
			boolean macroFound = false;
			for (Map.Entry<String, List<String>> entry : config.macros
					.entrySet()) {
				String key = entry.getKey();
				List<String> macroCmds = entry.getValue();

				// if the macro was found insert the commands of this macro
				if (command.contains(MACRO_START + key + MACRO_END)) {

					// format all macros
					List<String> formattedMacros = new LinkedList<String>();

					for (String macroCmd : macroCmds) {
						formattedMacros.addAll(format(macroCmd));
					}

					cmdList.addAll(formattedMacros);
					macroFound = true;
					break;
				}
			}

			// if no macro was found, just insert this command into the list
			if (!macroFound) {
				cmdList.add(command);
			}

		}

		return cmdList;
	}

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Command]");
	}

	protected LinkedList<String> commands = new LinkedList<String>();

	public CommandSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);
		commands.addAll(format(lines[1] + lines[2] + lines[3]));
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		for (String cmd : this.commands) {
			Bukkit.getServer().dispatchCommand(event.getPlayer(),
					cmd.replace("%p", event.getPlayer().getName()));
		}

	}

}
