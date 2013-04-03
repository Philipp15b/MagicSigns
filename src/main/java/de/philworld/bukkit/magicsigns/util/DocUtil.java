package de.philworld.bukkit.magicsigns.util;

import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.signs.MagicSign;

public class DocUtil {

	public static void main(String[] args) {
		System.out.println(getSignPermissions());
	}

	public static String getSignPermissions() {
		StringBuilder builder = new StringBuilder();
		builder.append("# -----------------------\n# MagicSigns Permissions\n# -----------------------\n");
		for (Class<? extends MagicSign> clazz : MagicSigns
				.getIncludedSignTypes()) {
			MagicSignInfo annotation = clazz.getAnnotation(MagicSignInfo.class);
			if (annotation != null) {
				builder.append("# ").append(annotation.friendlyName())
						.append("\n");
				builder.append(" - ").append(annotation.buildPerm())
						.append("\n");
				builder.append(" - ").append(annotation.usePerm()).append("\n");
			}
		}
		return builder.toString();
	}

}