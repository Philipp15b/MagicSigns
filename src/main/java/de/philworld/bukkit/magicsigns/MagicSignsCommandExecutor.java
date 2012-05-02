package de.philworld.bukkit.magicsigns;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.locks.MagicSignsLockCommandExecutor;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.util.MSMsg;

public class MagicSignsCommandExecutor implements CommandExecutor {

	private MagicSigns plugin;
	private MagicSignsLockCommandExecutor magicSignsLockCommandExecutor = new MagicSignsLockCommandExecutor();

	public MagicSignsCommandExecutor(MagicSigns plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {

		// Determine if the sender is a player (and an op), or the console.
		boolean isPlayer = (sender instanceof Player);

		// Cast the sender to Player if possible.
		Player p = (isPlayer) ? (Player) sender : null;

		// no usage from the console cuz we use the player all the time.
		if (!isPlayer) {
			sender.sendMessage("Please only use in game!");
			return true;
		}

		String base = (args.length > 0) ? args[0].toLowerCase() : "";

		try {

			// INFO COMMAND
			if (base == "" || base.equalsIgnoreCase("info")) {
				return info(p, label);
			}

			else if (base.equalsIgnoreCase("edit")) {
				return plugin.getSignEdit().getCmdExecutor()
						.edit(p, label, args);
			}

			else if (base.equalsIgnoreCase("unmask")) {
				return plugin.getSignEdit().getCmdExecutor().unmask(p, label);
			}

			else if (base.equalsIgnoreCase("lock")) {
				return magicSignsLockCommandExecutor.lock(p, label, args);
			}

			else if (base.equalsIgnoreCase("unlock")) {
				return magicSignsLockCommandExecutor.unlock(p, label);
			}

		} catch (PermissionException e) {
			MSMsg.NO_PERMISSION.send(p);
		}

		return false;
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
				target.getLocation());
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


}
