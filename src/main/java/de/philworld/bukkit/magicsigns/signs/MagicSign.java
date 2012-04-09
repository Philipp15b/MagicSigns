package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.config.ConfigurationBase;
import de.philworld.bukkit.magicsigns.config.MagicSignSerializationProxy;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;

/**
 * Parent class for every magic sign. Uses the Behavior pattern.
 */
public abstract class MagicSign {

	private static ConfigurationBase config = null;

	/**
	 * Returns if a new Magic Sign should be created of this sign.
	 *
	 * <i><b>Important:</b> Must be overridden!</i>
	 */
	public static boolean takeAction(Sign sign, String[] lines) {
		throw new UnsupportedOperationException(
				"The static method takeAction() must be overridden!");
	}

	/**
	 * Initializes the configuration.
	 *
	 * @param node
	 *            {@link ConfigurationNode} the config.
	 */
	public static void loadConfig(ConfigurationSection section) {
	}

	public static void saveConfig(ConfigurationSection section) {
		if (config != null) {
			config.save(section);
		}
	}

	public Block sign;
	public String[] lines;

	/**
	 * Create a new instance of the MagicSign
	 *
	 * @param sign
	 * @param lines
	 * @throws InvalidSignException
	 */
	public MagicSign(Block sign, String[] lines) throws InvalidSignException {
		this.sign = sign;
		this.lines = lines;
	}

	/**
	 * Called every time the sign is created, but not when its loaded from
	 * config.
	 *
	 * @param event
	 */
	public void onCreate(SignChangeEvent event) {
		return;
	}

	/**
	 * Called on every right click on the sign.
	 *
	 * @param event
	 * @throws PermissionException
	 */
	public void onRightClick(PlayerInteractEvent event)
			throws PermissionException {
		return;
	}

	/**
	 * Return the serialization proxy.
	 *
	 * @return {@link MagicSignSerializationProxy}
	 */
	public MagicSignSerializationProxy serialize() {
		return new MagicSignSerializationProxy(this);
	}

	/**
	 * Get the location of this sign
	 *
	 * @return Location
	 */
	public Location getLocation() {
		return sign.getLocation();
	}

	/**
	 * Get the use permission for this {@link MagicSign}.
	 *
	 * @param sign
	 *            the MagicSign
	 * @return The use permission.
	 */
	public String getUsePermission() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);

		if (signInfo != null) {
			return signInfo.usePerm();
		} else {
			return null;
		}
	}

	public String getFriendlyName() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);

		if (signInfo != null) {
			return signInfo.friendlyName();
		} else {
			return null;
		}
	}

	public String getDescription() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);

		if (signInfo != null) {
			return signInfo.description();
		} else {
			return null;
		}
	}

}
