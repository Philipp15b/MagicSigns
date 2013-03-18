package de.philworld.bukkit.magicsigns.config;

import org.bukkit.configuration.ConfigurationSection;

public interface Configuration {

	public void load(ConfigurationSection section);

	public ConfigurationSection save(ConfigurationSection section);

}
