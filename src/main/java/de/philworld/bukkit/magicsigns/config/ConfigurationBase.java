package de.philworld.bukkit.magicsigns.config;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.config.ConfigurationNode;

import de.philworld.bukkit.magicsigns.MagicSigns;

/**
 * Base class for the configurations.
 *
 */
public abstract class ConfigurationBase {

	/**
	 * Set all properties annotated with {@link Setting} to the value in the
	 * {@link ConfigurationNode}.
	 *
	 * If the field does not exist in the node, it will be set to the current
	 * value in the field.
	 *
	 * @param section
	 */
	public void load(ConfigurationSection section) {
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

			field.setAccessible(true);

			String key = field.getAnnotation(Setting.class).value();
			Object value = section.get(key);

			try {
				if (value != null) {
					try {
						field.set(this, value);
					} catch (IllegalArgumentException e) {
						throw new InvalidConfigException("Config value of '"
								+ section.getCurrentPath() + "." + key
								+ "' must be assignable from '"
								+ field.getType().getName()
								+ "', found instead '"
								+ value.getClass().getName()
								+ "'! Using default for this key.");
					}
				} else {
					section.set(key, field.get(this));
				}
			} catch (IllegalAccessException e) {
				getLogger().log(Level.WARNING, "Error loading config:", e);
			} catch (InvalidConfigException e) {
				getLogger().log(Level.WARNING, "Error loading config:", e);
			}
		}

	}

	/**
	 * Save all contents of fields annotated with {@link Setting} to the node.
	 *
	 * @param section
	 */
	public ConfigurationSection save(ConfigurationSection section) {
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
				getLogger().log(Level.WARNING,
						"Error saving config: " + e.getMessage(), e);
			}
		}
		return section;
	}

	private Logger getLogger() {
		return MagicSigns.inst().getLogger();
	}

}