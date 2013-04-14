package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

/**
 * A sign that applies the speed potion effect to players.
 * 
 * The second line may contain a duration and the third line may contain an
 * amplifier.
 * 
 */
@MagicSignInfo(
		name = "Speed",
		friendlyName = "Speed sign",
		description = "A sign that applies the speed potion effect to players.",
		buildPerm = "magicsigns.speed.create",
		usePerm = "magicsigns.speed.use")
public class SpeedSign extends PurchasableMagicSign {

	private final int duration;
	private final int amplifier;

	public SpeedSign(BlockLocation location, String[] lines)
			throws InvalidSignException {
		super(location, lines);

		if (lines[1].isEmpty()) {
			throw new InvalidSignException("Line 2 must contain the duration!");
		}
		if (lines[2].isEmpty()) {
			throw new InvalidSignException("Line 3 must contain an amplifier!");
		}

		try {
			duration = new Integer(lines[1]);
		} catch (NumberFormatException e) {
			throw new InvalidSignException(
					"The duration on line 2 must be a number!");
		}
		try {
			amplifier = new Integer(lines[2]);
		} catch (NumberFormatException e) {
			throw new InvalidSignException(
					"The amplifier on line 3 must be a number!");
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().addPotionEffect(
				new PotionEffect(PotionEffectType.SPEED, duration, amplifier),
				true);
	}

}
