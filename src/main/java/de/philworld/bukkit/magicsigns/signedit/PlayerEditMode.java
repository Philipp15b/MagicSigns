package de.philworld.bukkit.magicsigns.signedit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerEditMode {

	private final File file;
	private final FileConfiguration config;
	private Map<String, EditMode> editModes = new HashMap<String, EditMode>();

	public PlayerEditMode(File file) {
		this.file = file;
		this.config = YamlConfiguration.loadConfiguration(file);

		if (config.contains("edit-modes")) {
			Map<String, Object> values = config.getConfigurationSection(
					"edit-modes").getValues(false);
			// convert Map<String, Object> to Map<String, EditMode>
			for (Entry<String, Object> entry : values.entrySet()) {
				editModes.put(entry.getKey(),
						EditMode.valueOf((String) entry.getValue()));
			}
		}
	}

	public EditMode getEditMode(Player p) {
		return getEditMode(p.getName());
	}

	public EditMode getEditMode(String playername) {
		EditMode mode = editModes.get(playername);
		if (mode != null) {
			return mode;
		} else {
			return EditMode.NONE;
		}
	}

	public void setEditMode(Player p, EditMode mode) {
		setEditMode(p.getName(), mode);
	}

	public void setEditMode(String playername, EditMode mode) {
		if (mode != EditMode.NONE) {
			editModes.put(playername, mode);
		} else {
			if (editModes.containsKey(playername))
				editModes.remove(playername);
		}
	}

	public void save() throws IOException {
		// convert Map<String, EditMode> to Map<String, String>
		Map<String, String> editModesAsString = new HashMap<String, String>();
		for (Entry<String, EditMode> entry : editModes.entrySet()) {
			editModesAsString.put(entry.getKey(), entry.getValue().toString());
		}
		config.set("edit-modes", editModesAsString);
		config.save(file);
	}

}
