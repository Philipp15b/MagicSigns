package de.philworld.bukkit.magicsigns.config.annotation;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;

import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.config.Configuration;
import de.philworld.bukkit.magicsigns.config.InvalidConfigException;
import de.philworld.bukkit.magicsigns.signs.RocketSign;

/**
 * Base class for the configurations that uses annotations on attributes to save
 * the configuration.
 * 
 * @see RocketSign RocketSign#LocalConfiguration for example usage.
 */
public abstract class AnnotationConfiguration implements Configuration {

	/**
	 * Set all properties annotated with {@link Setting} to the value in the
	 * {@link Setting}.
	 */
	@Override
	public void load(ConfigurationSection section) {
		if (getClass().isAnnotationPresent(SettingBase.class)) {
			String base = getClass().getAnnotation(SettingBase.class).value();
			section = section.getConfigurationSection(base);
			if (section == null)
				return;
		}

		for (Field field : getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(Setting.class)) {
				continue;
			}

			field.setAccessible(true);

			String key = field.getAnnotation(Setting.class).value();
			Object value = section.get(key);

			try {
				if (value != null) {
					try {
						field.set(this, value);
					} catch (IllegalArgumentException e) {
						throw new InvalidConfigException("Config value of '" + section.getCurrentPath() + "." + key
								+ "' must be assignable from '" + field.getType().getName() + "', found instead '"
								+ value.getClass().getName() + "'! Using default for this key.");
					}
				}
			} catch (IllegalAccessException e) {
				getLogger().log(Level.SEVERE, "Error loading config:", e);
			} catch (InvalidConfigException e) {
				getLogger().log(Level.SEVERE, "Error loading config:", e);
			}
		}

	}

	/**
	 * Save all contents of fields annotated with {@link Setting} to the node.
	 */
	@Override
	public void save(ConfigurationSection section) {
		if (getClass().isAnnotationPresent(SettingBase.class)) {
			String base = getClass().getAnnotation(SettingBase.class).value();

			if (section.getConfigurationSection(base) == null) {
				section.createSection(base);
			}
			section = section.getConfigurationSection(base);
		}

		for (Field field : getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(Setting.class)) {
				continue;
			}

			String key = field.getAnnotation(Setting.class).value();
			try {
				section.set(key, field.get(this));
			} catch (IllegalAccessException e) {
				getLogger().log(Level.SEVERE, "Error saving config: " + e.getMessage(), e);
			}
		}
	}

	private Logger getLogger() {
		return MagicSigns.inst().getLogger();
	}

}