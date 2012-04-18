package de.philworld.bukkit.magicsigns;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.util.MSMsg;

public class MagicSignsCommandExecutor implements CommandExecutor {

	private MagicSigns plugin;

	public MagicSignsCommandExecutor(MagicSigns plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unused")
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

		// Grab the command base and any arguments.
		String base = (args.length > 0) ? args[0].toLowerCase() : "";
		String arg1 = (args.length > 1) ? args[1].toLowerCase() : "";
		String arg2 = (args.length > 2) ? args[2].toLowerCase() : "";
		String arg3 = (args.length > 3) ? args[3].toLowerCase() : "";

		try {

			// INFO COMMAND
			if (base == "" || base.equalsIgnoreCase("info")) {

				if (!p.hasPermission("magicsigns.command-info")) {
					MSMsg.NO_PERMISSION.send(p);
					return true;
				}

				Block target = p.getTargetBlock(null, 100);
				if (target == null) {
					p.sendMessage(ChatColor.RED + "Please point at a sign!");
					return true;
				}

				if (!(target.getState() instanceof Sign)) {
					p.sendMessage(ChatColor.RED + "Please point at a sign!");
					return true;
				}

				p.sendMessage(ChatColor.GOLD + "--------------------");
				p.sendMessage(ChatColor.GOLD + "MagicSigns Sign Info");
				p.sendMessage(ChatColor.GOLD + "--------------------");

				MagicSign sign = plugin.getSignManager().getSign(target
						.getLocation());
				if (sign == null) {
					p.sendMessage(ChatColor.GREEN
							+ "This is just a normal sign.");
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
			}

			else if (base.equalsIgnoreCase("edit")) {
				return plugin.getSignEdit().getCmdExecutor().edit(p, label, args);
			}

			else if (base.equalsIgnoreCase("unmask")) {
				if (!p.hasPermission("magicsigns.edit.unmask"))
					throw new PermissionException();

				Block target = p.getTargetBlock(null, 100);
				if (target == null) {
					p.sendMessage(ChatColor.RED + "Please point at a sign!");
					return true;
				}

				if (!(target.getState() instanceof Sign)) {
					p.sendMessage(ChatColor.RED + "Please point at a sign!");
					return true;
				}

				MagicSign magicSign = plugin.getSignManager().getSign(target.getLocation());
				if(magicSign == null) {
					p.sendMessage(ChatColor.RED + "This is not a MagicSign hence it can not be unmasked!");
					return true;
				}

				Sign targetSign = (Sign) target.getState();

				for(int i = 0; i < magicSign.lines.length; i++) {
					targetSign.setLine(i, magicSign.lines[i]);
				}
				targetSign.update();

				p.sendMessage("The sign has been unmasked!");

				return true;
			}

		} catch (PermissionException e) {
			MSMsg.NO_PERMISSION.send(p);
		}

		return false;
	}
}
