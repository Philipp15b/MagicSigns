package de.philworld.bukkit.magicsigns.config.annotation;

import static org.junit.Assert.assertEquals;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.Test;

public class AnnotationConfigurationTest {

	@SettingBase("test-base")
	private static class LocalConfiguration extends AnnotationConfiguration {
		@Setting("foo") public boolean foo = false;
		@Setting("bar") public String bar = null;
		public String notAnnotated = "notAnnotated";
		@Setting("nope") public String doesntExist = "doesntExist";
	}

	@Test
	public void test() {
		ConfigurationSection section = new MemoryConfiguration();
		ConfigurationSection testBase = section.createSection("test-base");
		testBase.set("foo", true);
		testBase.set("bar", "foo");
		LocalConfiguration config = new LocalConfiguration();
		config.load(section);

		assertEquals(true, config.foo);
		assertEquals("foo", config.bar);
		assertEquals("notAnnotated", config.notAnnotated);
		assertEquals("doesntExist", config.doesntExist);
	}

}
