package de.philworld.bukkit.magicsigns.dev;

import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.signs.MagicSign;

public class PermissionPrinter {

	public static void main(String[] args) {
		System.out.println("# -----------------------\n# MagicSigns Permissions\n# -----------------------");
		for (Class<? extends MagicSign> clazz : MagicSigns.getIncludedSignTypes()) {
			MagicSignInfo annotation = clazz.getAnnotation(MagicSignInfo.class);
			if (annotation == null)
				continue;
			System.out.println("# " + annotation.friendlyName() + "\n - " + annotation.buildPerm() + "\n - "
					+ annotation.usePerm());
		}
	}

}