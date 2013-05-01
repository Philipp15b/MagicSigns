package de.philworld.bukkit.magicsigns.locks;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.util.BlockLocation;
import de.philworld.bukkit.magicsigns.util.MSMsg;
import de.philworld.bukkit.magicsigns.util.MaterialUtil;

public class MagicSignsLockCommandExecutor {

	public boolean lock(Player p, String label, String[] args) {

		if (args.length >= 2 && !args[1].isEmpty()) {
			return applyLock(p, label, args);
		}

		Block target = p.getTargetBlock(null, 30);
		if (target == null || !MaterialUtil.isSign(target.getType())) {
			MSMsg.POINT_AT_SIGN.send(p);
			return true;
		}

		MagicSign magicSign = MagicSigns.inst().getSignManager().getSign(new BlockLocation(target.getLocation()));

		if (magicSign == null) {
			MSMsg.NOT_MAGIC_SIGN.send(p);
			return true;
		}

		if (magicSign.getLock() != null) {
			Lock lock = magicSign.getLock();
			p.sendMessage(ChatColor.BLUE + "This sign has a lock:");
			p.sendMessage(ChatColor.BLUE + "    Maximum Uses: " + lock.getMaxUses());
			p.sendMessage(ChatColor.BLUE + "    Period between clicks: " + lock.getPeriod());
			p.sendMessage(ChatColor.GOLD + "To remove this lock, type " + ChatColor.GRAY + "/" + label + " unlock");
		} else {
			p.sendMessage(ChatColor.BLUE + "This sign has no lock.");
		}

		p.sendMessage(ChatColor.GOLD + "To set a new lock, type " + ChatColor.GRAY + "/" + label
				+ " lock max:5,period:2");
		p.sendMessage(ChatColor.GRAY + "max:" + ChatColor.GOLD + " stands for the maximum uses for a player");
		p.sendMessage(ChatColor.GRAY + "period" + ChatColor.GOLD
				+ " stands for the delay between each click on the sign (in seconds)");

		return true;
	}

	public boolean applyLock(Player p, String label, String[] args) {
		String lock = args[1];
		String[] lockParts = lock.split(",");

		int maxuses = -1;
		int period = 0;
		for (String lockPart : lockParts) {
			if (lockPart.contains("max:")) {
				maxuses = Integer.parseInt(lockPart.split("max:")[1]);
			} else if (lockPart.contains("period:")) {
				period = Integer.parseInt(lockPart.split("period:")[1]);
			}
		}

		Lock signLock = new Lock(period, maxuses);

		Block target = p.getPlayer().getTargetBlock(null, 20);

		if (target == null || !MaterialUtil.isSign(target.getType())) {
			MSMsg.POINT_AT_SIGN.send(p);
			return true;
		}

		MagicSign magicSign = MagicSigns.inst().getSignManager().getSign(new BlockLocation(target.getLocation()));

		if (magicSign == null) {
			MSMsg.NOT_MAGIC_SIGN.send(p);
			return true;
		}

		magicSign.setLock(signLock, true);

		p.sendMessage(ChatColor.GREEN + "This sign now has a lock with a period of " + period + ", maximum uses of "
				+ maxuses);

		return true;
	}

	public boolean unlock(Player p, String label) {

		Block target = p.getTargetBlock(null, 30);
		if (target == null || !MaterialUtil.isSign(target.getType())) {
			MSMsg.POINT_AT_SIGN.send(p);
			return true;
		}

		MagicSign magicSign = MagicSigns.inst().getSignManager().getSign(new BlockLocation(target.getLocation()));

		if (magicSign == null) {
			MSMsg.NOT_MAGIC_SIGN.send(p);
			return true;
		}

		if (magicSign.getLock() != null) {
			magicSign.setLock(null, false);
			magicSign.removePlayerLocks();
			p.sendMessage(ChatColor.GREEN + "The lock has been removed from this sign!");
			return true;
		}
		p.sendMessage(ChatColor.RED + "This sign does not even have a lock!");
		return true;
	}
}
