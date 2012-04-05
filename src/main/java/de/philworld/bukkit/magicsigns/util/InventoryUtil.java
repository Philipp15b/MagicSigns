package de.philworld.bukkit.magicsigns.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

	/**
	 * Removes a specific amount of items from an inventory.
	 *
	 * @param inv
	 *            The inventory
	 * @param type
	 *            The material
	 * @param amount
	 *            The amount
	 */
	public static void removeItems(Inventory inv, Material type,
			int amount) {
		for (ItemStack is : inv.getContents()) {
			if (is != null && is.getType() == type) {
				int newamount = is.getAmount() - amount;
				if (newamount > 0) {
					is.setAmount(newamount);
					break;
				} else {
					inv.remove(is);
					amount = -newamount;
					if (amount == 0)
						break;
				}
			}
		}
	}

}
