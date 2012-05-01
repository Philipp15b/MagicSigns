package de.philworld.bukkit.magicsigns.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import de.philworld.bukkit.magicsigns.MagicSigns;

/**
 * A configuration that can save Macros ({@code Map<String, List<String>>}).
 */
public class MacroConfiguration extends ConfigurationBase {

	private final String configKey;
	private final Map<String, List<String>> macros = new HashMap<String, List<String>>();

	public MacroConfiguration(String configKey) {
		this.configKey = configKey;
	}

	@Override
	public void load(ConfigurationSection section) {
		section = section.getConfigurationSection(configKey);
		if (section != null) {
			Map<String, Object> values = section.getValues(false);

			// convert Map<String, Object> to Map<String, List<String>>
			for (Map.Entry<String, Object> entry : values.entrySet()) {
				String key = entry.getKey();
				try {

					@SuppressWarnings("unchecked")
					List<String> macroValue = (List<String>) entry.getValue();
					getMacros().put(key, macroValue);

				} catch (ClassCastException e) {
					MagicSigns
							.inst()
							.getLogger()
							.log(Level.WARNING,
									"Config value of '"
											+ configKey
											+ "."
											+ key
											+ "' must by of type 'List<String>', found instead '"
											+ entry.getValue().getClass()
													.getName() + "'!");
				}
			}
		}
	}

	@Override
	public ConfigurationSection save(ConfigurationSection section) {
		return section; // nothing changes at runtime
	}

	/**
	 * @return the macros
	 */
	public Map<String, List<String>> getMacros() {
		return macros;
	}

}
