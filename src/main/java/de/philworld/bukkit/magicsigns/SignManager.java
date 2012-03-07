package de.philworld.bukkit.magicsigns;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * Manages all signs and sign types.
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
 * The manager will automatically check all sign types and if some take action,
 * it will instantiate new objects of them.
 */
public class SignManager {

	private Set<Class<? extends MagicSign>> signTypes = new HashSet<Class<? extends MagicSign>>();
	private Map<Location, MagicSign> signs = new HashMap<Location, MagicSign>();
	private MagicSigns plugin;

	public SignManager(MagicSigns plugin) {
		this.plugin = plugin;
	}

	/**
	 * Get all {@link MagicSign}s in a collection
	 *
	 * @return Collection of {@link MagicSign}s
	 */
	public Collection<MagicSign> getSigns() {
		return signs.values();
	}

	/**
	 * Adds a new sign type. It must extend MagicSign and override the static
	 * method <code>takeAction()</code>. The class can also contain permission
	 * annotations ({@link BuildPermission}, {@link UsePermission}).
	 *
	 * @param signType
	 */
	public void registerSignType(Class<? extends MagicSign> signType) {
		signTypes.add(signType);
	}

	public void registerSign(Block sign, String[] lines) {
		registerSign(sign, lines, null, null);
	}

	/**
	 * Generates a MagicSign from a block if some MagicSign takesAction (
	 * <code>takeAction()</code>) and registers it.
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
		for (Class<? extends MagicSign> signType : signTypes) {

			Method takeAction = null;

			// get takeAction static method
			try {
				takeAction = signType.getMethod("takeAction", Block.class,
						lines.getClass());
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(
						"Could not find static method takeAction(Block, String[])");
			}

			// invoke takeAction
			try {

				if ((Boolean) takeAction.invoke(null, sign, lines)) {

					// check for build permissions
					if (p != null) {
						BuildPermission buildPerm = signType
								.getAnnotation(BuildPermission.class);

						if (buildPerm != null) {
							if (!p.hasPermission(buildPerm.value())) {
								sign.breakNaturally();
								throw new PermissionException();
							}
						}
					}

					MagicSign magicSign = signType.getConstructor(Block.class,
							lines.getClass()).newInstance(sign, lines);

					// call onCreate()
					if (event != null)
						magicSign.onCreate(event);

					// add the sign to the list
					registerSign(magicSign);

					if (p != null)
						MSMsg.SIGN_CREATED.send(p);

					return;
				}

			} catch (PermissionException e) {
				MSMsg.NO_PERMISSION.send(p);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof InvalidSignException) {
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

	/**
	 * Registers a MagicSign directly.
	 *
	 * @param sign
	 */
	public void registerSign(MagicSign sign) {
		signs.put(sign.getLocation(), sign);
	}

	/**
	 * Returns if a sign at the given location is registered.
	 *
	 * @param loc
	 *            The location
	 * @return true if it exists, else false
	 */
	public boolean containsSign(Location loc) {
		return signs.containsKey(loc);
	}

	/**
	 * Returns a {@link MagicSign} by a given location
	 *
	 * @param loc
	 *            The location
	 * @return the magic sign.
	 */
	public MagicSign getSign(Location loc) {
		if (containsSign(loc)) {
			return signs.get(loc);
		}
		return null;
	}

	/**
	 * Removes a sign by a given location.
	 *
	 * @param loc
	 *            - The location of this sign.
	 * @return true if the sign was found and deleted
	 */
	public boolean removeSign(Location loc) {
		if (signs.containsKey(loc)) {
			signs.remove(loc);
			return true;
		}
		return false;
	}

	/**
	 * Load the configuration into all currently registered sign types.
	 *
	 * @param section
	 */
	public void loadConfig(ConfigurationSection section) {
		for (Class<? extends MagicSign> type : signTypes) {
			try {
				type.getMethod("loadConfig", ConfigurationSection.class)
						.invoke(null, section);
			} catch (Throwable e) {
				getLogger().log(Level.WARNING,
						"Error loading config: " + e.getMessage(), e);
			}

		}
	}

	/**
	 * Save the configuration of all sign types to the given
	 * {@link ConfigurationSection}
	 *
	 * @param section
	 *            ConfigurationSection to save the data to.
	 * @return The modified ConfigurationSection
	 */
	public ConfigurationSection saveConfig(ConfigurationSection section) {
		for (Class<? extends MagicSign> type : signTypes) {
			try {
				section = (ConfigurationSection) type.getMethod("saveConfig",
						ConfigurationSection.class).invoke(null, section);
			} catch (Throwable e) {
				getLogger().log(Level.WARNING,
						"Error saving config: " + e.getMessage(), e);
			}
		}
		return section;
	}

	/**
	 * Get the logger.
	 *
	 * @return the logger
	 */
	private Logger getLogger() {
		return plugin.getLogger();
	}
}