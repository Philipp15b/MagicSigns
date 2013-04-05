package de.philworld.bukkit.magicsigns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import de.philworld.bukkit.magicsigns.signs.MagicSign;

public class SignType {

	private final Class<? extends MagicSign> clazz;
	private final Constructor<? extends MagicSign> constructor;
	private final String buildPermission;

	public SignType(Class<? extends MagicSign> clazz) {
		this.clazz = clazz;

		try {
			constructor = clazz.getConstructor(Location.class, String[].class);
		} catch (NoSuchMethodException e) {
			throw mustHaveException(clazz,
					"constructor with arguments Location and String[]", e);
		} catch (SecurityException e) {
			throw mustHaveException(clazz,
					"constructor with arguments Location and String[]", e);
		}

		MagicSignInfo annotation = clazz.getAnnotation(MagicSignInfo.class);
		if (annotation == null)
			throw mustHaveException(clazz, "MagicSignInfo annotation", null);
		buildPermission = annotation.buildPerm();
	}

	private static IllegalArgumentException mustHaveException(
			Class<? extends MagicSign> clazz, String what, Exception cause) {
		return new IllegalArgumentException("The sign type '"
				+ clazz.getCanonicalName() + "' must have a " + what + "!",
				cause);
	}

	public String getName() {
		return clazz.getAnnotation(MagicSignInfo.class).name();
	}

	public MagicSign newInstance(Location location, String[] lines)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		return constructor.newInstance(location, lines);
	}

	public void loadConfig(ConfigurationSection config)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		clazz.getMethod("loadConfig", ConfigurationSection.class).invoke(null,
				config);
	}

	public void saveConfig(ConfigurationSection config)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		clazz.getMethod("saveConfig", ConfigurationSection.class).invoke(null,
				config);
	}

	public String getBuildPermission() {
		return buildPermission;
	}

	public String getCanonicalName() {
		return clazz.getCanonicalName();
	}

}