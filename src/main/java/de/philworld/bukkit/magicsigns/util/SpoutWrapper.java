package de.philworld.bukkit.magicsigns.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.getspout.spout.Spout;
import org.getspout.spoutapi.inventory.SpoutItemStack;

public class SpoutWrapper {

	public static SpoutWrapper get(PluginManager pm) {
		Spout spout = (Spout) pm.getPlugin("Spout");
		if (spout == null)
			return null;
		return new SpoutWrapper();
	}

	public boolean isCustom(ItemStack is) {
		return new SpoutItemStack(is).isCustomItem();
	}
}
