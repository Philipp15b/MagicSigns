package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.economy.Price;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;

/**
 * This is a {@link MagicSign} that allows to be payed for usage. The price has
 * to be on the last line of the sign.
 */
public abstract class PurchasableMagicSign extends MagicSign {

	protected final Price price;

	/**
	 * Creates a new purchasable magic sign by setting the price of this sign.
	 * The price has to be on the last line of the sign.
	 */
	public PurchasableMagicSign(Location location, String[] lines)
			throws InvalidSignException {
		super(location, lines);

		if (!lines[3].isEmpty()) {
			try {
				price = Price.valueOf(lines[3]);
			} catch (IllegalArgumentException e) {
				throw new InvalidSignException(e.getMessage());
			}
		} else {
			price = null;
		}
	}

	/**
	 * Gets the price for this sign.
	 * 
	 * @return The price.
	 */
	public Price getPrice() {
		return price;
	}

	public boolean isFree() {
		if (price == null)
			return true;
		return price.isFree();
	}

	/**
	 * Withdraws the price of this sign from the bank account of the player.
	 * 
	 * @param p
	 *            The player to withdraw the money from.
	 * @return True if the transaction suceeded or if no economy plugin was
	 *         found, else false.
	 */
	public boolean withdrawPlayer(Player p) throws PermissionException {
		if (price == null)
			return true;
		return price.withdrawPlayer(p);
	}

}
