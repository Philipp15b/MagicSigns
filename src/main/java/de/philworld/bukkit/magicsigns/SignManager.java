package de.philworld.bukkit.magicsigns;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

import de.philworld.bukkit.magicsigns.signs.MagicSign;

public class SignManager {

	private final Logger logger;
	private final Map<String, SignType> signTypes = new HashMap<String, SignType>();
	public Map<Location, MagicSign> signs = new HashMap<Location, MagicSign>();
	private ConfigurationSection config;

	public SignManager(Logger logger, ConfigurationSection config) {
		this.logger = logger;
		this.config = config;
	}

	/**
	 * Adds a new sign type. It must extend MagicSign and must have a
	 * {@link MagicSignInfo} annotation.
	 * 
	 * @throws InvocationTargetException
	 * 
	 * @throws IllegalArgumentException
	 *             if the sign type doesnt have a {@link MagicSignInfo}
	 *             annotation or no {@link MagicSign#takeAction(Sign, String[])}
	 *             method.
	 */
	public void registerSignType(Class<? extends MagicSign> clazz)
			throws InvocationTargetException {
		SignType signType = new SignType(clazz);
		try {
			signType.loadConfig(config);
		} catch (Exception e) {
			throw new InvocationTargetException(e,
					"Error loading config into sign type "
							+ signType.getCanonicalName() + "!");
		}
		signTypes.put(signType.getName().toLowerCase(), signType);
	}

	public SignType getSignType(String name) {
		return signTypes.get(name.toLowerCase());
	}

	public void registerSign(MagicSign sign) {
		signs.put(sign.getLocation(), sign);
	}

	public MagicSign getSign(Location loc) {
		return signs.get(loc);
	}

	public boolean removeSign(Location loc) {
		return signs.remove(loc) != null;
	}

	public ConfigurationSection getConfig() {
		return config;
	}

	/**
	 * Reloads the given config into all currently registered sign types and
	 * recreates all MagicSigns.
	 */
	public void reloadConfig(ConfigurationSection config) {
		this.config = config;
		for (SignType signType : signTypes.values()) {
			try {
				signType.loadConfig(config);
			} catch (Exception e) {
				logger.log(
						Level.WARNING,
						"Error loading config into sign type "
								+ signType.getCanonicalName() + "!", e);
			}
		}
		Map<Location, MagicSign> oldSigns = signs;
		signs = new HashMap<Location, MagicSign>(oldSigns.size());
		for (MagicSign sign : oldSigns.values()) {
			try {
				registerSign(sign.serialize().getMagicSign());
			} catch (Exception e) {
				logger.log(
						Level.SEVERE,
						"Error loading Magic Sign from config: "
								+ e.getMessage(), e);
			}
		}
	}

	/**
	 * Save the configuration of all sign types to the current
	 * {@link ConfigurationSection}
	 */
	public void saveConfig() {
		for (SignType type : signTypes.values()) {
			try {
				type.saveConfig(config);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error saving config for sign type "
						+ type.getCanonicalName() + ": " + e.getMessage(), e);
			}
		}
	}
}