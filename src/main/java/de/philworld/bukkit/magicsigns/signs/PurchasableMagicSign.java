package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSigns;

/**
 * This is a {@link MagicSign} that allows to be payed for usage. The price has
 * to be on the last line of the sign.
 */
public abstract class PurchasableMagicSign extends MagicSign {

	protected double price = 0;

	/**
	 * Creates a new purchasable magic sign by setting the price of this sign.
	 * The price has to be on the last line of the sign.
	 *
	 * @param sign
	 * @param lines
	 * @throws InvalidSignException
	 */
	public PurchasableMagicSign(Block sign, String[] lines)
			throws InvalidSignException {
		super(sign, lines);

		if (!lines[3].isEmpty()) {
			price = Double.parseDouble(lines[3]);
			if (price < 0) {
				throw new InvalidSignException(
						"The sign price may not be lower than zero!");
			}
		}
	}

	/**
	 * Gets the price for this sign.
	 *
	 * @return The price.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Withdraws the price of this sign from the bank account of the player.
	 *
	 * @param p
	 *            The player to withdraw the money from.
	 * @return True if the transaction suceeded or if no economy plugin was
	 *         found, else false.
	 */
	public boolean withdrawPlayer(Player p) {
		if (MagicSigns.economy != null) {
			if (MagicSigns.economy.has(p.getName(), price)) {
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
