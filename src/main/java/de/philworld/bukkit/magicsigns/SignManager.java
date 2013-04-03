package de.philworld.bukkit.magicsigns;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * Manages sign types and their instances, {@link MagicSign}s.
 * 
 * <h2>Usage</h2>
 * 
 * <pre>
 * <code>
 *   SignManager manager = new SignManager();
 *   manager.registerSignType(<? extends MagicSign> myMagicSignClass);
 *   manager.registerSign(Block mysign, String[] lines); // for every sign thats created or changed.
 * </code>
 * </pre>
 * 
 * <p>
 * The manager will automatically check all sign types and if some take action,
 * it will instantiate new objects of them.
 * 
 * <p>
 * Note: Sign types don't have to be registered before using them. This means
 * you can register a single MagicSign with
 * {@link SignManager#registerSign(MagicSign)} but not automatically create
 * other signs of this type. This allows loading MagicSigns from the config
 * before another plugin has registered this sign type.
 */
public class SignManager {

	private final List<SignType> signTypes = new ArrayList<SignType>(MagicSigns
			.getIncludedSignTypes().size());
	public Map<Location, MagicSign> signs = new HashMap<Location, MagicSign>();
	private final MagicSigns plugin;
	private ConfigurationSection config;

	public SignManager(MagicSigns plugin, ConfigurationSection config) {
		this.plugin = plugin;
		this.config = config;
	}

	/**
	 * Adds a new sign type. It must extend MagicSign and override the static
	 * method <code>takeAction()</code>. The class must have a
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
		signTypes.add(signType);
	}

	/**
	 * Checks all registered sign types with the static method
	 * {@link MagicSign#takeAction(Sign, String[])} if a new MagicSign of the
	 * type should be created.
	 * 
	 * <ul>
	 * <li>Add player if you want to check for permissions
	 * <li>Add event if you want to call onCreate() on the new sign.
	 * </ul>
	 * 
	 * @param sign
	 *            Sign Block
	 * @param lines
	 *            Lines on the sign
	 * @param p
	 *            Player for permission checks; can be null
	 * @param event
	 *            SignChangeEvent to call onCreate(); can be null
	 */
	public void registerSign(Block sign, String[] lines, Player p,
			SignChangeEvent event) {
		for (SignType signType : signTypes) {
			// invoke takeAction
			try {
				if (!signType.takeAction(sign, lines))
					continue;

				// check for build permissions
				if (p != null
						&& !p.hasPermission(signType.getBuildPermission())) {
					if (event != null) {
						event.setCancelled(true);
					} else {
						sign.breakNaturally();
					}
					MSMsg.NO_PERMISSION.send(p);
					return;
				}

				MagicSign magicSign = signType.newInstance(sign, lines);

				if (event != null)
					magicSign.onCreate(event);

				registerSign(magicSign);

				if (p != null)
					MSMsg.SIGN_CREATED.send(p);

				return;
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof InvalidSignException) {
					if (event != null) {
						event.setCancelled(true);
					} else {
						sign.breakNaturally();
					}

					if (p != null) {
						p.sendMessage(ChatColor.RED
								+ e.getTargetException().getMessage());
					} else {
						getLogger().log(
								Level.WARNING,
								"Invalid sign: "
										+ e.getTargetException().getMessage(),
								e.getTargetException());
					}
				} else
					getLogger().log(
							Level.WARNING,
							"Error registering Magic sign of type "
									+ signType.getCanonicalName() + ": "
									+ e.getTargetException().getMessage(),
							e.getTargetException());
			} catch (Throwable e) {
				getLogger().log(
						Level.WARNING,
						"Error registering sign of type "
								+ signType.getCanonicalName(), e);
			}

		}
	}

	public void registerSign(MagicSign sign) {
		signs.put(sign.getLocation(), sign);
	}

	/**
	 * Returns if a sign at the given location is registered.
	 */
	public boolean hasSign(Location loc) {
		return signs.containsKey(loc);
	}

	/**
	 * Returns a {@link MagicSign} by a given location
	 */
	public MagicSign getSign(Location loc) {
		return signs.get(loc);
	}

	/**
	 * Removes a sign by a given location.
	 * 
	 * @return true if the sign was found and deleted
	 */
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
		for (SignType signType : signTypes) {
			try {
				signType.loadConfig(config);
			} catch (Exception e) {
				getLogger().log(
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
				getLogger().log(
						Level.WARNING,
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
		for (SignType type : signTypes) {
			try {
				type.saveConfig(config);
			} catch (Exception e) {
				getLogger().log(
						Level.WARNING,
						"Error saving config for sign type "
								+ type.getCanonicalName() + ": "
								+ e.getMessage(), e);
			}
		}
	}

	private Logger getLogger() {
		return plugin.getLogger();
	}
}