package de.philworld.bukkit.magicsigns;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import de.philworld.bukkit.magicsigns.config.InvalidConfigException;
import de.philworld.bukkit.magicsigns.config.MagicSignSerializationProxy;
import de.philworld.bukkit.magicsigns.signs.ClearSign;
import de.philworld.bukkit.magicsigns.signs.CommandSign;
import de.philworld.bukkit.magicsigns.signs.ConsoleCommandSign;
import de.philworld.bukkit.magicsigns.signs.CreativeModeSign;
import de.philworld.bukkit.magicsigns.signs.FeedSign;
import de.philworld.bukkit.magicsigns.signs.HealSign;
import de.philworld.bukkit.magicsigns.signs.HealthSign;
import de.philworld.bukkit.magicsigns.signs.LevelSign;
import de.philworld.bukkit.magicsigns.signs.RocketSign;
import de.philworld.bukkit.magicsigns.signs.SpeedSign;
import de.philworld.bukkit.magicsigns.signs.SurvivalModeSign;
import de.philworld.bukkit.magicsigns.signs.TeleportSign;

public class MagicSigns extends JavaPlugin {

	public SignHandler signHandler = new SignHandler(this);
	private FileConfiguration config;

	@Override
	public void onEnable() {

		loadConfiguration();

		signHandler.registerSignType(CommandSign.class);
		signHandler.registerSignType(ConsoleCommandSign.class);
		signHandler.registerSignType(SpeedSign.class);
		signHandler.registerSignType(HealSign.class);
		signHandler.registerSignType(HealthSign.class);
		signHandler.registerSignType(ClearSign.class);
		signHandler.registerSignType(TeleportSign.class);
		signHandler.registerSignType(RocketSign.class);
		signHandler.registerSignType(LevelSign.class);
		signHandler.registerSignType(CreativeModeSign.class);
		signHandler.registerSignType(SurvivalModeSign.class);
		signHandler.registerSignType(FeedSign.class);

		loadSigns();

		getServer().getPluginManager().registerEvents(signHandler, this);
	}

	@Override
	public void onDisable() {
		saveSigns();
	}

	/**
	 * Loads the configuration and inserts the defaults.
	 */
	public void loadConfiguration() {
		ConfigurationSerialization
		.registerClass(MagicSignSerializationProxy.class);
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
	}

	@SuppressWarnings("unchecked")
	private void loadSigns() {
		List<MagicSignSerializationProxy> list = (List<MagicSignSerializationProxy>) config
				.get("magic-signs");

		if (list == null || list.isEmpty())
			return;

		for (MagicSignSerializationProxy proxy : list) {
			try {
				signHandler.registerSign(proxy.getMagicSign());
			} catch (InvalidConfigException e) {
				getLogger().log(Level.WARNING, e.getMessage());
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof InvalidSignException) {
					getLogger().log(
							Level.WARNING,
							"Tried to load invalid sign: "
									+ e.getTargetException().getMessage());
				} else {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void saveSigns() {
		// reset list first
		config.set("magic-signs", null);

		List<MagicSignSerializationProxy> signList = new LinkedList<MagicSignSerializationProxy>();
		for (MagicSign sign : signHandler.getSigns()) {
			signList.add(sign.serialize());
		}
		config.set("magic-signs", signList);
		saveConfig();
	}

}
