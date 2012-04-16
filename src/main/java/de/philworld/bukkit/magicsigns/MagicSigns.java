package de.philworld.bukkit.magicsigns;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.philworld.bukkit.magicsigns.config.MagicSignSerializationProxy;
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

	private static MagicSigns instance;
	private static Economy economy = null;
	private static Permission permission = null;

	/**
	 * Get the current instance.
	 *
	 * @return Current MagicSigns instance.
	 */
	public static MagicSigns inst() {
		return instance;
	}

	public SignManager signManager;
	public SignEdit signEdit;
	private File signsDbFile;
	private FileConfiguration signsDb;

	@Override
	public void onEnable() {

		instance = this;

		signManager = new SignManager(this);
		signEdit = new SignEdit(this);

		loadConfiguration();

		// register all sign types
		signManager.registerSignType(CommandSign.class);
		signManager.registerSignType(ConsoleCommandSign.class);
		signManager.registerSignType(SpeedSign.class);
		signManager.registerSignType(HealSign.class);
		signManager.registerSignType(HealthSign.class);
		signManager.registerSignType(ClearSign.class);
		signManager.registerSignType(TeleportSign.class);
		signManager.registerSignType(RocketSign.class);
		signManager.registerSignType(LevelSign.class);
		signManager.registerSignType(CreativeModeSign.class);
		signManager.registerSignType(SurvivalModeSign.class);
		signManager.registerSignType(FeedSign.class);
		signManager.registerSignType(PermissionSign.class);

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
			getLogger().log(Level.INFO,
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

	@Override
	public void onDisable() {
		saveConfiguration();
		try {
			signEdit.save();
		} catch (IOException e) {
			getLogger().log(Level.WARNING, "Error saving EditModes:", e);
		}
		saveSigns();
	}

	/**
	 * Public alias for {@link SignManager#registerSignType(Class)}
	 *
	 * @see SignManager#registerSignType(Class)
	 * @param signType
	 */
	public void registerSignType(Class<? extends MagicSign> signType) {
		signManager.registerSignType(signType);
	}

	/**
	 * Checks if the block at the given Location is a MagicSign.
	 *
	 * @param loc
	 * @return True if its a MagicSign, else false
	 */
	public boolean isMagicSign(Location loc) {
		return signManager.containsSign(loc);
	}

	private void loadConfiguration() {
		ConfigurationSerialization
				.registerClass(MagicSignSerializationProxy.class);
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private void saveConfiguration() {
		signManager.saveConfig(getConfig());
		saveConfig();
	}

	@SuppressWarnings("unchecked")
	private void loadSigns() {
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
				signManager.registerSign(proxy.getMagicSign());
			} catch (Throwable e) {
				getLogger().log(
						Level.WARNING,
						"Error loading Magic Sign from config: "
								+ e.getMessage(), e);
			}
		}

		signManager.loadConfig(getConfig());
		saveConfig();
	}

	private void saveSigns() {
		// reset list first
		signsDb.set("magic-signs", null);

		List<MagicSignSerializationProxy> signList = new LinkedList<MagicSignSerializationProxy>();
		for (MagicSign sign : signManager.getSigns()) {
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

	/**
	 * @return the Vault economy
	 */
	public static Economy getEconomy() {
		return economy;
	}

	/**
	 * @return the Vault permission
	 */
	public static Permission getPermission() {
		return permission;
	}

}
