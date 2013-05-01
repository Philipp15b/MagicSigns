package de.philworld.bukkit.magicsigns.signs;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

public class TeleportSignTest extends AbstractSignTest {

	@Test
	public void test() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		TeleportSign sign = createSign(TeleportSign.class, "99,100,101", "30,5", "");
		assertEquals(LOCATION.world, sign.destination.world);
		assertEquals(99, sign.destination.x);
		assertEquals(100, sign.destination.y);
		assertEquals(101, sign.destination.z);

		assertEquals(30, sign.destination.yaw);
		assertEquals(5, sign.destination.pitch);
	}
}
