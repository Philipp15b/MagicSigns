package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.config.ConfigurationBase;
import de.philworld.bukkit.magicsigns.config.Setting;
import de.philworld.bukkit.magicsigns.config.SettingBase;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * A sign that can modify player's velocity.
 *
 * It accepts also an vector (comma-separated) for velocity on the second line.
 * For example: <code>0,300,0</code> to make a huge jump in the air.
 *
 */
@MagicSignInfo(
		friendlyName = "Rocket sign",
		description = "A sign that can modify player's velocity.",
		buildPerm = "magicsigns.rocket.create",
		usePerm = "magicsigns.rocket.use")
public class RocketSign extends PurchasableMagicSign {

	public static LocalConfiguration config;

	@SettingBase("rocket")
	private static class LocalConfiguration extends ConfigurationBase {
		@Setting("defaultVelocity")
		public String defaultVelocity = "0,200,0";
	}

	public static void loadConfig(ConfigurationSection section) {
		config = new LocalConfiguration();
		config.load(section);
	}

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Rocket]");
	}

	private Vector velocity;

	public RocketSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		String vector = (lines[1].isEmpty()) ? config.defaultVelocity
				: lines[1];

		String[] parts = vector.split(",");

		velocity = new Vector(new Integer(parts[0]), new Integer(parts[1]),
				new Integer(parts[2]));
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().setVelocity(velocity);
		MSMsg.ROCKETED.send(event.getPlayer());
	}

}
