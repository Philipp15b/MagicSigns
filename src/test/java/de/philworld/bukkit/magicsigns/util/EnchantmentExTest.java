package de.philworld.bukkit.magicsigns.util;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class EnchantmentExTest {

	@Test
	public void testNoDuplicateNames() {
		Map<String, EnchantmentEx> declarations = new HashMap<String, EnchantmentEx>();
		for (EnchantmentEx e : EnumSet.allOf(EnchantmentEx.class)) {
			for (String name : e.lookupNames) {
				EnchantmentEx before;
				name = name.replace("[ _]", "");
				if ((before = declarations.put(name, e)) != null)
					throw new AssertionError(name + " (declared in " + e.name + ") was already declared in "
							+ before.name);
			}
		}
	}

}
