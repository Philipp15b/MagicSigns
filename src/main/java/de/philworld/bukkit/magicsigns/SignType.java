package de.philworld.bukkit.magicsigns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import de.philworld.bukkit.magicsigns.signs.MagicSign;

public class SignType {

	private final Class<? extends MagicSign> clazz;
	private final Method takeAction;
	private final Constructor<? extends MagicSign> constructor;
	private final String buildPermission;

	public SignType(Class<? extends MagicSign> clazz) {
		this.clazz = clazz;

		try {
			takeAction = clazz.getMethod("takeAction", Block.class,
					String[].class);
		} catch (NoSuchMethodException e) {
			throw mustHaveException(clazz,
					"static takeAction(Sign, String[]) method", e);
		} catch (SecurityException e) {
			throw mustHaveException(clazz,
					"static takeAction(Sign, String[]) method", e);
		}

		try {
			constructor = clazz.getConstructor(Block.class, String[].class);
		} catch (NoSuchMethodException e) {
			throw mustHaveException(clazz,
					"constructor with arguments Block and String[]", e);
		} catch (SecurityException e) {
			throw mustHaveException(clazz,
					"constructor with arguments Block and String[]", e);
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

	public boolean takeAction(Block sign, String[] lines)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		return (Boolean) takeAction.invoke(null, sign, lines);
	}

	public MagicSign newInstance(Block sign, String[] lines)
			throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		return constructor.newInstance(sign, lines);
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