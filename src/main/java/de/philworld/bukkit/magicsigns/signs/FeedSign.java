package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MSMsg;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

/**
 * A sign that increases the food level of a player by a certain amount.
 */
@MagicSignInfo(
		name = "Feed",
		friendlyName = "Feed sign",
		description = "A sign that increases the food level of a player by a certain amount.",
		buildPerm = "magicsigns.feed.create",
		usePerm = "magicsigns.feed.use")
public class FeedSign extends PurchasableMagicSign {

	public static final int MAX_FOOD_LEVEL = 6;

	private int feedAmount = MAX_FOOD_LEVEL;

	public FeedSign(BlockLocation location, String[] lines) throws InvalidSignException {
		super(location, lines);

		if (!lines[1].isEmpty()) {
			try {
				feedAmount = new Integer(lines[1]);
			} catch (NumberFormatException e) {
				throw new InvalidSignException("Line 2 must be a number or empty!");
			}
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();

		int newFoodLevel = p.getFoodLevel() + feedAmount;

		if (newFoodLevel > MAX_FOOD_LEVEL) {
			p.setFoodLevel(MAX_FOOD_LEVEL);
		} else {
			p.setFoodLevel(newFoodLevel);
		}

		MSMsg.FEED_SUCCESS.send(p);
	}

}
