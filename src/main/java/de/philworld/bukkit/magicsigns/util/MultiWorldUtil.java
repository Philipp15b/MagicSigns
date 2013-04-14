package de.philworld.bukkit.magicsigns.util;

import java.io.File;

import org.bukkit.Bukkit;

public class MultiWorldUtil {

	public static File getDataFolder(String name) {
		return new File(Bukkit.getWorldContainer(), name);
	}

	public static File getDataFile(String name) {
		return new File(getDataFolder(name) + File.separator + "level.dat");
	}

	public static boolean exists(String name) {
		return getDataFile(name).exists();
	}

	public static boolean isLoaded(String name) {
		return Bukkit.getServer().getWorld(name) != null;
	}
}
