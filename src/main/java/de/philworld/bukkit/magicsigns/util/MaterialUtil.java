package de.philworld.bukkit.magicsigns.util;

import org.bukkit.Material;

public class MaterialUtil {

	/**
	 * Returns if the Material is either a {@link Material#SIGN},
	 * {@link Material#SIGN_POST} or a {@link Material#WALL_SIGN}.
	 */
	public static boolean isSign(Material material) {
		return material == Material.SIGN || material == Material.SIGN_POST
				|| material == Material.WALL_SIGN;
	}

}
