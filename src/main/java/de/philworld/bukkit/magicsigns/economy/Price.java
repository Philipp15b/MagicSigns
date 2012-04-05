package de.philworld.bukkit.magicsigns.economy;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.util.InventoryUtil;

public abstract class Price {

	public static Price valueOf(String text) throws IllegalArgumentException,
			NumberFormatException {
		// the price is an item
		if (text.startsWith("i:")) {
			String[] result = text.split("i:");
			if (result.length != 2)
				throw new IllegalArgumentException(
						"Invalid price format for an item! Format: `i:ITEMNAME`");
			return Price.Item.valueOf(result[1]);
		} else if (text.startsWith("lvl:")) {
			String[] result = text.split("i:");
			if (result.length != 2)
				throw new IllegalArgumentException(
						"Invalid price format for levels! Format: `lvl:10`");
			return Price.Level.valueOf(result[1]);
		} else { // its just money
			return Price.VaultEconomy.valueOf(text);
		}
	}

	/**
	 * Returns if the player can pay this price.
	 *
	 * @param p
	 *            The player
	 * @return True if the player can pay this, else false
	 */
	public abstract boolean has(Player p);

	/**
	 * Withdraw an amount from a player.
	 *
	 * @param p
	 *            The player
	 * @return True if the transaction succeeded, else false.
	 */
	public abstract boolean withdrawPlayer(Player p);

	/**
	 * A price that uses Vault's economy.
	 *
	 */
	public static class VaultEconomy extends Price {

		public static VaultEconomy valueOf(String text)
				throws IllegalArgumentException, NumberFormatException {
			double money = Double.parseDouble(text);
			if (money < 0)
				throw new IllegalArgumentException(
						"The price may not be lower than zero!");
			return new Price.VaultEconomy(money);
		}

		private final double price;

		public VaultEconomy(double price) {
			this.price = price;
		}

		public double getPrice() {
			return price;
		}

		@Override
		public boolean has(Player p) {
			return MagicSigns.economy.has(p.getName(), price);
		}

		@Override
		public boolean withdrawPlayer(Player p) {
			if (MagicSigns.economy != null) {
				if (has(p)) {
					if (MagicSigns.economy.withdrawPlayer(p.getName(), price)
							.transactionSuccess()) {
						return true;
					}
				}
				return false;
			}
			return true;
		}

	}

	/**
	 * A price that uses items.
	 *
	 */
	public static class Item extends Price {

		public static Item valueOf(String text) throws NumberFormatException {
			String[] result = text.split(":");
			Material material = Material.getMaterial(result[0]);
			int amount;
			if (result.length == 1)
				amount = 1;
			else
				amount = Integer.parseInt(result[1]);
			return new Item(material, amount);
		}

		private final Material material;
		private final int amount;

		public Item(Material material, int amount) {
			this.material = material;
			this.amount = amount;
		}

		public ItemStack getItems() {
			return new ItemStack(material, amount);
		}

		@Override
		public boolean has(Player p) {
			return p.getInventory().contains(material, amount);
		}

		@SuppressWarnings("deprecation")
		@Override
		public boolean withdrawPlayer(Player p) {
			if (has(p)) {
				InventoryUtil.removeItems(p.getInventory(), material, amount);
				p.updateInventory();
				return true;
			}
			return false;
		}

	}

	public static class Level extends Price {

		public static Level valueOf(String text) throws NumberFormatException {
			return new Level(Integer.valueOf(text));
		}

		private final int level;

		public Level(int level) {
			this.level = level;
		}

		@Override
		public boolean has(Player p) {
			return p.getLevel() >= level;
		}

		@Override
		public boolean withdrawPlayer(Player p) {
			if (has(p)) {
				p.setLevel(p.getLevel() - level);
				return true;
			}
			return false;
		}

	}

}
