package de.philworld.bukkit.magicsigns.signedit;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.util.BlockLocation;
import de.philworld.bukkit.magicsigns.util.MSMsg;
import de.philworld.bukkit.magicsigns.util.MaterialUtil;

public class SignEditCommandExecutor {

	private final SignEdit signEdit;

	public SignEditCommandExecutor(SignEdit signEdit) {
		this.signEdit = signEdit;
	}

	public void sendEditNote(Player p, String label) {
		EditMode mode = signEdit.getEditMode(p);
		if (mode == EditMode.AUTO || mode == EditMode.MASK_MAGIC_SIGNS) {
			p.sendMessage(ChatColor.GREEN + "Mask this sign by clicking with another sign on this.");
		}

		else if (mode == EditMode.MODIFY) {
			p.sendMessage(ChatColor.GREEN + "Edit this sign by clicking with another sign on this.");
		}

		// more info
		if (EditMode.AUTO.hasPermission(p)
				|| (EditMode.MASK_MAGIC_SIGNS.hasPermission(p) && EditMode.MODIFY.hasPermission(p))) {
			p.sendMessage(ChatColor.GRAY + "For more info about editing and masking, type /" + label + " edit");
		}

		else if (EditMode.MASK_MAGIC_SIGNS.hasPermission(p)) {
			p.sendMessage(ChatColor.GRAY + "For more info about masking, type /" + label + " edit");
		}

		else if (EditMode.MODIFY.hasPermission(p)) {
			p.sendMessage(ChatColor.GRAY + "For more info about modifying, type /" + label + " edit");
		}
	}

	public boolean edit(Player p, String label, String[] args) throws PermissionException {

		String arg1 = (args.length > 1) ? args[1].toLowerCase() : "";

		if (arg1.equals("")) {

			p.sendMessage(ChatColor.YELLOW + "It's possible to edit signs by clicking them with other signs.");
			p.sendMessage(ChatColor.YELLOW + "This is possible in different modes.");

			EditMode mode = signEdit.getEditMode(p);
			p.sendMessage(ChatColor.AQUA + "Current edit mode: " + ChatColor.BLUE + mode.toString()); // TODO

			// List of available modes for this player BEGIN
			p.sendMessage("Available modes:");
			if (EditMode.AUTO.hasPermission(p)) {
				p.sendMessage(ChatColor.AQUA + " - " + ChatColor.GRAY + "auto" + ChatColor.AQUA
						+ " Modify normal signs, mask MagicSigns");
			}

			if (EditMode.MASK_MAGIC_SIGNS.hasPermission(p)) {
				p.sendMessage(ChatColor.AQUA + " - " + ChatColor.GRAY + "mask" + ChatColor.AQUA
						+ " Mask MagicSigns (Users will see the mask, the sign will work as before)");
			}

			if (EditMode.MODIFY.hasPermission(p)) {
				p.sendMessage(ChatColor.AQUA + " - " + ChatColor.GRAY + "modify" + ChatColor.AQUA
						+ " Modify/edit all signs.");
			}

			if (EditMode.NONE.hasPermission(p)) {
				p.sendMessage(ChatColor.AQUA + " - " + ChatColor.GRAY + "none" + ChatColor.AQUA + " Don't edit at all.");
			}
			// List END

			p.sendMessage("");

			p.sendMessage(ChatColor.DARK_GREEN + "Change your edit mode with " + ChatColor.GRAY + "/" + label
					+ " edit [MODE]");
		}

		else if (arg1.equalsIgnoreCase("auto")) {
			signEdit.setEditMode(p, EditMode.AUTO);
			p.sendMessage(ChatColor.GREEN
					+ "Your edit mode has been set to auto (MagicSigns wil be masked, others will be modifed).");
		}

		else if (arg1.equalsIgnoreCase("modify")) {
			signEdit.setEditMode(p, EditMode.MODIFY);
			p.sendMessage(ChatColor.GREEN + "Your edit mode has been set to modify (all signs will be changed).");
		}

		else if (arg1.equalsIgnoreCase("mask")) {
			signEdit.setEditMode(p, EditMode.MASK_MAGIC_SIGNS);
			p.sendMessage(ChatColor.GREEN + "Your edit mode has been set to mask (MagicSigns only).");
		}

		else if (arg1.equalsIgnoreCase("none") || arg1.equalsIgnoreCase("off")) {
			signEdit.setEditMode(p, EditMode.NONE);
			p.sendMessage(ChatColor.GREEN + "You have disabled editing.");
		}

		return false;
	}

	public boolean unmask(Player p, String label) throws PermissionException {
		if (!p.hasPermission(SignEdit.UNMASK_PERMISSION))
			throw new PermissionException();

		Block target = p.getTargetBlock(null, 100);
		if (target == null || !MaterialUtil.isSign(target.getType())) {
			MSMsg.POINT_AT_SIGN.send(p);
			return true;
		}

		MagicSign magicSign = signEdit.plugin.getSignManager().getSign(new BlockLocation(target.getLocation()));
		if (magicSign == null) {
			p.sendMessage(ChatColor.RED + "This is not a MagicSign hence it can not be unmasked!");
			return true;
		}

		Sign targetSign = (Sign) target.getState();

		for (int i = 0; i < magicSign.getLines().length; i++) {
			targetSign.setLine(i, magicSign.getLines()[i]);
		}
		targetSign.update();

		p.sendMessage("The sign has been unmasked!");

		return true;
	}

}
