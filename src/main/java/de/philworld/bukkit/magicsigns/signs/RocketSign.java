package de.philworld.bukkit.magicsigns.signs;

import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MSMsg;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.config.Configuration;
import de.philworld.bukkit.magicsigns.config.annotation.AnnotationConfiguration;
import de.philworld.bukkit.magicsigns.config.annotation.Setting;
import de.philworld.bukkit.magicsigns.config.annotation.SettingBase;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

/**
 * A sign that can modify player's velocity.
 * 
 * It accepts also an vector (comma-separated) for velocity on the second line.
 * For example: <code>0,300,0</code> to make a huge jump in the air.
 * 
 */
@MagicSignInfo(
		name = "Rocket",
		friendlyName = "Rocket sign",
		description = "A sign that can modify player's velocity.",
		buildPerm = "magicsigns.rocket.create",
		usePerm = "magicsigns.rocket.use")
public class RocketSign extends PurchasableMagicSign {

	private static LocalConfiguration config = new LocalConfiguration();

	@SettingBase("rocket")
	private static class LocalConfiguration extends AnnotationConfiguration {
		@Setting("defaultVelocity") public String defaultVelocity = "0,2,0";

		@Override
		public void load(ConfigurationSection section) {
			super.load(section);
			try {
				Vector v = parseVelocity(config.defaultVelocity);
				if (v.length() > 100)
					MagicSigns
							.inst()
							.getLogger()
							.warning(
									"The default velocity of a Rocket sign may only have a vector length of 100, "
											+ "Bukkit won't let you move that fast!");
			} catch (InvalidSignException e) {
				MagicSigns.inst().getLogger()
						.log(Level.WARNING, "Invalid Rocket sign default velocity: " + e.getMessage(), e);
			}
		}
	}

	public static Configuration getConfig() {
		return config;
	}

	private final Vector velocity;

	public RocketSign(BlockLocation location, String[] lines) throws InvalidSignException {
		super(location, lines);

		String vector = (lines[1].isEmpty()) ? config.defaultVelocity : lines[1];
		velocity = parseVelocity(vector);
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().setVelocity(velocity);
		MSMsg.ROCKETED.send(event.getPlayer());
	}

	private static Vector parseVelocity(String vector) throws InvalidSignException {
		String[] parts = vector.split(",");

		try {
			if (parts.length == 3) {
				return new Vector(new Integer(parts[0]), new Integer(parts[1]), new Integer(parts[2]));
			} else if (parts.length == 1) {
				return new Vector(0, new Integer(parts[0]), 0);
			} else {
				throw new InvalidSignException("Make sure you specify the velocity like this: 10,20,30 (x,y,z)");
			}
		} catch (NumberFormatException e) {
			throw new InvalidSignException("Make sure you specify the velocity like this: 10,20,30 (x,y,z)");
		}
	}

}
