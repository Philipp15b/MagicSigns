package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;

/**
 * A sign that applies the speed potion effect to players.
 *
 * The second line may contain a duration and the third line may contain an
 * amplifier.
 *
 */
@BuildPermission("magicsigns.speed.create")
@UsePermission("magicsigns.speed.use")
public class SpeedSign extends PurchasableMagicSign {

	private final int duration;
	private final int amplifier;

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Speed]");
	}

	public SpeedSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		if (lines[1].isEmpty()) {
			throw new InvalidSignException("Line 2 must contain the duration!");
		}
		if (lines[2].isEmpty()) {
			throw new InvalidSignException("Line 3 must contain an amplifier!");
		}

		duration = new Integer(lines[1]);
		amplifier = new Integer(lines[2]);
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().addPotionEffect(
				new PotionEffect(PotionEffectType.SPEED, duration, amplifier),
				true);
	}

}
