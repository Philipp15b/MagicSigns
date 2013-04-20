package de.philworld.bukkit.magicsigns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.locks.MagicSignsLockCommandExecutor;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.util.BlockLocation;
import de.philworld.bukkit.magicsigns.util.MSMsg;

public class MagicSignsCommandExecutor implements CommandExecutor {

	private final MagicSigns plugin;
	private final MagicSignsLockCommandExecutor magicSignsLockCommandExecutor = new MagicSignsLockCommandExecutor();

	public MagicSignsCommandExecutor(MagicSigns plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		String base = args.length > 0 ? args[0].toLowerCase() : "";

		try {
			if (base.equals("reload")) {
				reload(sender, label);
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage("Please only use in game!");
				return true;
			}
			Player p = (Player) sender;

			if (base.isEmpty() || base.equals("info")) {
				return info(p, label);
			}

			else if (base.equals("edit")) {
				return plugin.getSignEdit().getCmdExecutor()
						.edit(p, label, args);
			}

			else if (base.equals("unmask")) {
				return plugin.getSignEdit().getCmdExecutor().unmask(p, label);
			}

			else if (base.equals("lock")) {
				return magicSignsLockCommandExecutor.lock(p, label, args);
			}

			else if (base.equals("unlock")) {
				return magicSignsLockCommandExecutor.unlock(p, label);
			}

			return false;

		} catch (PermissionException e) {
			MSMsg.NO_PERMISSION.send(sender);
			return true;
		}
	}

	public boolean info(Player p, String label) {
		if (!p.hasPermission("magicsigns.command-info")) {
			MSMsg.NO_PERMISSION.send(p);
			return true;
		}

		Block target = p.getTargetBlock(null, 100);
		if (target == null) {
			MSMsg.POINT_AT_SIGN.send(p);
			return true;
		}

		if (!(target.getState() instanceof Sign)) {
			MSMsg.POINT_AT_SIGN.send(p);
			return true;
		}

		p.sendMessage(ChatColor.GOLD + "--------------------");
		p.sendMessage(ChatColor.GOLD + "MagicSigns Sign Info");
		p.sendMessage(ChatColor.GOLD + "--------------------");

		MagicSign sign = plugin.getSignManager().getSign(
				new BlockLocation(target.getLocation()));
		if (sign == null) {
			MSMsg.NOT_MAGIC_SIGN.send(p);
			return true;
		}

		p.sendMessage(ChatColor.AQUA + "Type of this Magic Sign: "
				+ ChatColor.BLUE + sign.getFriendlyName());

		p.sendMessage(ChatColor.BLUE + " - " + sign.getDescription());

		if (sign.isMasked()) {
			p.sendMessage(ChatColor.LIGHT_PURPLE
					+ "This sign is masked (shows a different text than the MagicSign uses for its work)! See the original text with "
					+ ChatColor.GRAY + "/" + label + " unmask");
		}

		p.sendMessage("");

		plugin.getSignEdit().getCmdExecutor().sendEditNote(p, label);

		return true;
	}

	public boolean reload(CommandSender p, String label)
			throws PermissionException {
		if (!p.hasPermission("magicsigns.reload"))
			throw new PermissionException();
		try {
			plugin.getConfig().load(
					new File(plugin.getDataFolder(), "config.yml"));
			plugin.getSignManager().reloadConfig(plugin.getConfig());
			p.sendMessage(ChatColor.GREEN + "Reloaded the config!");
			return true;
		} catch (FileNotFoundException e) {
			plugin.getLogger().log(Level.SEVERE,
					"Error reloading the configuration!", e);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE,
					"Error reloading the configuration!", e);
		} catch (InvalidConfigurationException e) {
			plugin.getLogger().log(Level.SEVERE,
					"Error reloading the configuration!", e);
		}
		p.sendMessage(ChatColor.RED
				+ "Failed to load the config, see the server log!");
		return true;
	}

}
