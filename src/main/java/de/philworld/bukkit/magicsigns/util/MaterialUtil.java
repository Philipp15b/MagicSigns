package de.philworld.bukkit.magicsigns.util;

import org.bukkit.Material;

public class MaterialUtil {

	public static boolean isSign(Material material) {
		return material == Material.SIGN || material == Material.SIGN_POST
				|| material == Material.WALL_SIGN;
	}

}
