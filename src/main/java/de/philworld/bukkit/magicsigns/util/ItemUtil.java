package de.philworld.bukkit.magicsigns.util;

import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.magicsigns.MagicSigns;

public class ItemUtil {

	public static boolean isRepairable(ItemStack is) {
		if (is == null || MagicSigns.getSpoutWrapper() != null && MagicSigns.getSpoutWrapper().isCustom(is))
			return false;
		return MaterialUtil.isRepairable(is.getType());
	}

	public static void repair(ItemStack is) {
		is.setDurability((short) 0);
	}

}
