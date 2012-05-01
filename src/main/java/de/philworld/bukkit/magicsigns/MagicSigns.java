package de.philworld.bukkit.magicsigns;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.philworld.bukkit.magicsigns.coloredsigns.ColoredSigns;
import de.philworld.bukkit.magicsigns.config.MagicSignSerializationProxy;
import de.philworld.bukkit.magicsigns.locks.Lock;
import de.philworld.bukkit.magicsigns.signedit.SignEdit;
import de.philworld.bukkit.magicsigns.signs.ClearSign;
import de.philworld.bukkit.magicsigns.signs.CreativeModeSign;
import de.philworld.bukkit.magicsigns.signs.FeedSign;
import de.philworld.bukkit.magicsigns.signs.HealSign;
import de.philworld.bukkit.magicsigns.signs.HealthSign;
import de.philworld.bukkit.magicsigns.signs.LevelSign;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.signs.RocketSign;
import de.philworld.bukkit.magicsigns.signs.SpeedSign;
import de.philworld.bukkit.magicsigns.signs.SurvivalModeSign;
import de.philworld.bukkit.magicsigns.signs.TeleportSign;
import de.philworld.bukkit.magicsigns.signs.command.CommandSign;
import de.philworld.bukkit.magicsigns.signs.command.ConsoleCommandSign;
import de.philworld.bukkit.magicsigns.signs.permission.PermissionSign;

public class MagicSigns extends JavaPlugin {

	private static Economy economy = null;
	private static MagicSigns instance;
	private static Permission permission = null;
	private static Set<Class<? extends MagicSign>> registeredSignTypes = new HashSet<Class<? extends MagicSign>>();

	static {
		registeredSignTypes.add(CommandSign.class);
		registeredSignTypes.add(ConsoleCommandSign.class);
		registeredSignTypes.add(SpeedSign.class);
		registeredSignTypes.add(HealSign.class);
		registeredSignTypes.add(HealthSign.class);
		registeredSignTypes.add(ClearSign.class);
		registeredSignTypes.add(TeleportSign.class);
		registeredSignTypes.add(RocketSign.class);
		registeredSignTypes.add(LevelSign.class);
		registeredSignTypes.add(CreativeModeSign.class);
		registeredSignTypes.add(SurvivalModeSign.class);
		registeredSignTypes.add(FeedSign.class);
		registeredSignTypes.add(PermissionSign.class);
	}

	/**
	 * @return the Vault economy; can be null
	 */
	public static Economy getEconomy() {
		return economy;
	}

	/**
	 * @return the Vault permission; can be null
	 */
	public static Permission getPermission() {
		return permission;
	}

	public static Set<Class<? extends MagicSign>> getRegisteredSignTypes() {
		return registeredSignTypes;
	}

	/**
	 * @return the current running instance of MagicSigns. Can be null if the
	 *         plugin hasn't started yet.
	 */
	public static MagicSigns inst() {
		return instance;
	}

	private ColoredSigns coloredSigns;
	private SignEdit signEdit;
	private SignManager signManager;
	private FileConfiguration signsDb;
	private File signsDbFile;

	/**
	 * @return the currently running {@link ColoredSigns} of this plugin.
	 */
	public ColoredSigns getColoredSigns() {
		return coloredSigns;
	}

	/**
	 * @return the currently running {@link SignEdit} of this plugin.
	 */
	public SignEdit getSignEdit() {
		return signEdit;
	}

	/**
	 * @return the currently running {@link SignManager} of this plugin.
	 */
	public SignManager getSignManager() {
		return signManager;
	}

	@Override
	public void onDisable() {
		// Disable saving configuration because no sign is currently modifying
		// the config and user's modified config is overwritten every server
		// shutdown.
		// saveConfiguration();
		try {
			getSignEdit().save();
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Error saving EditModes:", e);
		}
		saveSigns();
	}

	@Override
	public void onEnable() {

		instance = this;

		signManager = new SignManager(this);
		signEdit = new SignEdit(this);
		coloredSigns = new ColoredSigns(this);

		loadConfiguration();

		// register all sign types
		for (Class<? extends MagicSign> signType : registeredSignTypes) {
			getSignManager().registerSignType(signType);
		}

		// and then load them from the config
		loadSigns();

		if (setupEconomy()) {
			getLogger().log(Level.INFO, "Using Vault for economy.");
		} else {
			getLogger().log(Level.INFO,
					"Vault was not found, all signs will be free!");
		}

		if (setupPermissions()) {
			getLogger().log(Level.INFO, "Using Vault for permissions.");
		} else {
			getLogger()
					.log(Level.INFO,
							"Vault was not found, permission signs will not work (Permissions in general will work though!)");
		}

		getCommand("ms").setExecutor(new MagicSignsCommandExecutor(this));

		getServer().getPluginManager().registerEvents(
				new MagicSignsListener(this), this);

		// start metrics
		try {
			new Metrics(this).start();
		} catch (IOException e) {
			getLogger().log(Level.WARNING,
					"Error enabling Metrics for MagicSigns:", e);
		}
	}

	private void loadConfiguration() {
		ConfigurationSerialization
				.registerClass(MagicSignSerializationProxy.class);
		ConfigurationSerialization.registerClass(Lock.class);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	@SuppressWarnings("unchecked")
	private void loadSigns() {
		getSignManager().loadConfig(getConfig());
		saveConfig();

		signsDbFile = new File(getDataFolder(), "signs.db.yml");
		signsDb = YamlConfiguration.loadConfiguration(signsDbFile);

		List<MagicSignSerializationProxy> list = (List<MagicSignSerializationProxy>) signsDb
				.get("magic-signs");

		if (list == null) {
			list = new LinkedList<MagicSignSerializationProxy>();
		}

		// migrate from old config file.
		if (getConfig().get("magic-signs") != null) {
			getLogger()
					.log(Level.INFO,
							"Found list of signs in main config file. MagicSigns are now saved in signs.db.yml! Copying...");
			list.addAll((List<? extends MagicSignSerializationProxy>) getConfig()
					.get("magic-signs"));
			getConfig().set("magic-signs", null);
		}

		if (list == null || list.isEmpty())
			return;

		for (MagicSignSerializationProxy proxy : list) {
			try {
				getSignManager().registerSign(proxy.getMagicSign());
			} catch (Throwable e) {
				getLogger().log(
						Level.WARNING,
						"Error loading Magic Sign from config: "
								+ e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("unused")
	private void saveConfiguration() {
		getSignManager().saveConfig(getConfig());
		saveConfig();
	}

	private void saveSigns() {
		// reset list first
		signsDb.set("magic-signs", null);

		List<MagicSignSerializationProxy> signList = new LinkedList<MagicSignSerializationProxy>();
		for (MagicSign sign : getSignManager().getSigns()) {
			signList.add(sign.serialize());
		}

		signsDb.set("magic-signs", signList);
		try {
			signsDb.save(signsDbFile);
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Error saving MagicSigns:", e);
		}
	}

	private boolean setupEconomy() {
		try {
			RegisteredServiceProvider<Economy> economyProvider = getServer()
					.getServicesManager().getRegistration(
							net.milkbowl.vault.economy.Economy.class);

			if (economyProvider == null)
				return false;

			economy = economyProvider.getProvider();

			return true;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}

	private boolean setupPermissions() {
		try {
			RegisteredServiceProvider<Permission> economyProvider = getServer()
					.getServicesManager().getRegistration(
							net.milkbowl.vault.permission.Permission.class);

			if (economyProvider == null)
				return false;

			permission = economyProvider.getProvider();

			return true;
		} catch (NoClassDefFoundError e) {
			return false;
		}
	}

}
