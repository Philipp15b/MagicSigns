package de.philworld.bukkit.magicsigns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.philworld.bukkit.magicsigns.config.Configuration;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

public class SignType {

	private final Class<? extends MagicSign> clazz;
	private final Constructor<? extends MagicSign> constructor;
	private final String buildPermission;

	public SignType(Class<? extends MagicSign> clazz) {
		this.clazz = clazz;

		try {
			constructor = clazz.getConstructor(BlockLocation.class, String[].class);
		} catch (NoSuchMethodException e) {
			throw mustHaveException(clazz, "constructor with arguments BlockLocation and String[]", e);
		} catch (SecurityException e) {
			throw mustHaveException(clazz, "constructor with arguments Location and String[]", e);
		}

		MagicSignInfo annotation = clazz.getAnnotation(MagicSignInfo.class);
		if (annotation == null)
			throw mustHaveException(clazz, "MagicSignInfo annotation", null);
		buildPermission = annotation.buildPerm();
	}

	private static IllegalArgumentException mustHaveException(Class<? extends MagicSign> clazz, String what,
			Exception cause) {
		return new IllegalArgumentException("The sign type '" + clazz.getCanonicalName() + "' must have a " + what
				+ "!", cause);
	}

	public String getName() {
		return clazz.getAnnotation(MagicSignInfo.class).name();
	}

	public MagicSign newInstance(BlockLocation location, String[] lines) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return constructor.newInstance(location, lines);
	}

	public Configuration getConfig() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		return (Configuration) clazz.getMethod("getConfig").invoke(null);
	}

	public String getBuildPermission() {
		return buildPermission;
	}

	public String getCanonicalName() {
		return clazz.getCanonicalName();
	}

}