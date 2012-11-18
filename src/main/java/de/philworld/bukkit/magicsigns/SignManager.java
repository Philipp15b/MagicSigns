package de.philworld.bukkit.magicsigns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	private static class SignType {
		public final Class<? extends MagicSign> clazz;
		public final Method takeAction;
		public final Constructor<? extends MagicSign> constructor;
		public final String buildPermission;
		@SuppressWarnings("unused") // TODO
		public final String usePermission;
		
		private SignType(Class<? extends MagicSign> clazz, Method takeAction, Constructor<? extends MagicSign> constructor, String buildPermission, String usePermission) {
			this.clazz = clazz;
			this.takeAction = takeAction;
			this.constructor = constructor;
			this.buildPermission = buildPermission;
			this.usePermission = usePermission;
		}
	}
	
	private List<SignType> signTypes = new ArrayList<SignType>(MagicSigns.getIncludedSignTypes().size());
	private Map<Location, MagicSign> signs = new HashMap<Location, MagicSign>();
	private MagicSigns plugin;
	private ConfigurationSection config;

	public SignManager(MagicSigns plugin, ConfigurationSection config) {
		this.plugin = plugin;
		this.config = config;
	}

	/**
	 * Get all registered {@link MagicSign}s in a list.
	 *
	 * @return List of {@link MagicSign}s
	 */
	public List<MagicSign> getSigns() {
		return new ArrayList<MagicSign>(signs.values());
	}

	/**
	 * Adds a new sign type. It must extend MagicSign and override the static
	 * method <code>takeAction()</code>. The class must have a
	 * {@link MagicSignInfo} annotation.
	 *
	 * @throws IllegalArgumentException
	 *             if the sign type doesnt have a {@link MagicSignInfo}
	 *             annotation or no {@link MagicSign#takeAction(Sign, String[])}
	 *             method.
	 * @param clazz
	 */
	public void registerSignType(Class<? extends MagicSign> clazz) {
		// get takeAction method
		Method takeAction;
		try {
			takeAction = clazz.getMethod("takeAction", Block.class, String[].class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("The sign type '"
					+ clazz.getName()
					+ "' must have a static takeAction(Sign, String[]) method!");
		} catch (SecurityException e) {
			throw new IllegalArgumentException("The sign type '"
					+ clazz.getName()
					+ "' must have a static takeAction(Sign, String[]) method!");
		}
		
		// get constructor
		Constructor<? extends MagicSign> constructor;
		try {
			constructor = clazz.getConstructor(Block.class, String[].class);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("The sign type '"
					+ clazz.getName()
					+ "' must have a constructor with arguments Block and String[]!");
		} catch (SecurityException e) {
			throw new IllegalArgumentException("The sign type '"
					+ clazz.getName()
					+ "' must have a constructor with arguments Block and String[]!");
		}
		
		// get permissions
		MagicSignInfo annotation = clazz.getAnnotation(MagicSignInfo.class);
		if (annotation == null)
			throw new IllegalArgumentException("The sign type '"
					+ clazz.getName()
					+ "' must have a MagicSignInfo annotation!");
		String buildPerm = annotation.buildPerm();
		String usePerm = annotation.usePerm();
		

		// load the config into this sign type
		try {
			clazz.getMethod("loadConfig", ConfigurationSection.class)
					.invoke(null, config);
		} catch (Throwable e) {
			getLogger().log(
					Level.WARNING,
					"Error loading config into sign type " + clazz.getName()
							+ "!", e);
		}

		signTypes.add(new SignType(clazz, takeAction, constructor, buildPerm, usePerm));
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
				if (!(Boolean) signType.takeAction.invoke(null, sign, lines))
					continue;

				// check for build permissions
				if (p != null && !p.hasPermission(signType.buildPermission)) {
					if (event != null) {
						event.setCancelled(true);
					} else {
						sign.breakNaturally();
					}
					MSMsg.NO_PERMISSION.send(p);
					return;
				}

				MagicSign magicSign = signType.constructor.newInstance(sign, lines);

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
									+ signType.clazz.getCanonicalName() + ": "
									+ e.getTargetException().getMessage(),
							e.getTargetException());
			} catch (Throwable e) {
				getLogger().log(
						Level.WARNING,
						"Error registering sign of type "
								+ signType.clazz.getCanonicalName(), e);
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
	 * @param loc The location
	 * @return true if it exists, else false
	 */
	public boolean containsSign(Location loc) {
		return signs.containsKey(loc);
	}

	/**
	 * Returns a {@link MagicSign} by a given location
	 *
	 * @param loc The location
	 * @return the magic sign.
	 */
	public MagicSign getSign(Location loc) {
		return signs.get(loc);
	}

	/**
	 * Removes a sign by a given location.
	 *
	 * @param loc The location of this sign.
	 * @return true if the sign was found and deleted
	 */
	public boolean removeSign(Location loc) {
		return signs.remove(loc) != null;
	}

	/**
	 * Set the configuration used by all signs in this sign manager.
	 *
	 * @param section
	 */
	public void setConfig(ConfigurationSection config) {
		this.config = config;
	}

	public ConfigurationSection getConfig() {
		return config;
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
		for (SignType type : signTypes) {
			try {
				section = (ConfigurationSection) type.clazz.getMethod("saveConfig",
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