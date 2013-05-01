package de.philworld.bukkit.magicsigns.signs;

import java.lang.reflect.InvocationTargetException;

import de.philworld.bukkit.magicsigns.SignType;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

abstract class AbstractSignTest {

	protected static final BlockLocation LOCATION = new BlockLocation("myworld", 1, 1, 1);

	@SuppressWarnings("unchecked")
	protected static <T extends MagicSign> T createSign(Class<T> clazz, String line2, String line3, String line4)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SignType signType = new SignType(clazz);
		String[] lines = { "", line2, line3, line4 };
		return (T) signType.newInstance(LOCATION, lines);
	}

}
