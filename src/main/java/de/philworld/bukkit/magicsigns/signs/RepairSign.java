package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.util.BlockLocation;
import de.philworld.bukkit.magicsigns.util.ItemUtil;

@MagicSignInfo(
		name = "Repair",
		friendlyName = "Repair sign",
		description = "Repairs armor or tools",
		buildPerm = "magicsigns.repair.create",
		usePerm = "magicsigns.repair.use")
public class RepairSign extends PurchasableMagicSign {

	private final boolean all;

	public RepairSign(BlockLocation location, String[] lines) throws InvalidSignException {
		super(location, lines);
		all = lines[1].trim().equalsIgnoreCase("all");
	}

	@Override
	public boolean withdrawPlayer(Player p) throws PermissionException {
		if (!all) {
			ItemStack holding = p.getItemInHand();
			if (holding == null || !ItemUtil.isRepairable(holding)) {
				throw new PermissionException(ChatColor.RED + "Please hold a repairable item in your hand to repair!");
			}
		} else {
			boolean found = false;
			for (ItemStack is : p.getInventory()) {
				if (found = ItemUtil.isRepairable(is))
					break;
			}
			if (!found)
				throw new PermissionException(ChatColor.RED + "You don't have any repairable items!");
		}
		return super.withdrawPlayer(p);
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		if (!all) {
			ItemUtil.repair(event.getPlayer().getItemInHand());
		} else {
			int i = 0;
			for (ItemStack is : event.getPlayer().getInventory()) {
				if (!ItemUtil.isRepairable(is))
					continue;
				i++;
				ItemUtil.repair(is);
			}
			event.getPlayer().sendMessage(ChatColor.GREEN + "Repaired " + i + " items!");
		}
	}
}
