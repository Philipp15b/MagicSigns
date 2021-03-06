package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.economy.Price;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

/**
 * This is a {@link MagicSign} that allows to be payed for usage. The price has
 * to be on the last line of the sign.
 */
public abstract class PurchasableMagicSign extends MagicSign {

	private final Price price;

	/**
	 * Creates a new purchasable magic sign by setting the price of this sign.
	 * The price has to be on the last line of the sign.
	 */
	public PurchasableMagicSign(BlockLocation location, String[] lines) throws InvalidSignException {
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
	 * @throws PermissionException
	 */
	public boolean withdrawPlayer(Player p) throws PermissionException {
		if (price == null)
			return true;
		return price.withdrawPlayer(p);
	}

}
