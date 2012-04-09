package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * A sign that adds some health to a player.
 *
 * @see {@link HealthSign} - Sets the health directly to a value.
 */
@MagicSignInfo(
		friendlyName = "Heal sign",
		description = "A sign that adds some health to a player.",
		buildPerm = "magicsigns.heal.create",
		usePerm = "magicsigns.heal.use")
public class HealSign extends PurchasableMagicSign {

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Heal]");
	}

	private int healAmount = 20;

	public HealSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		if (!lines[1].isEmpty()) {
			healAmount = new Integer(lines[1]);
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();

		int newHealth = p.getHealth() + healAmount;

		if (newHealth > p.getMaxHealth()) {
			event.getPlayer().setHealth(p.getMaxHealth());
		} else {
			p.setHealth(newHealth);
		}

		MSMsg.HEAL_SUCCESS.send(p);
	}

}
