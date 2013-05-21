package de.philworld.bukkit.magicsigns.signs;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.powermock.reflect.Whitebox;

import com.google.common.base.Joiner;

import de.philworld.bukkit.magicsigns.config.MacroConfiguration;

public abstract class MacroTest {

	private final Class<? extends MagicSign> clazz;
	private final String methodName;
	private final Map<String, List<String>> macros = new HashMap<String, List<String>>();

	public MacroTest(Class<? extends MagicSign> clazz, String methodName) {
		this.clazz = clazz;
		this.methodName = methodName;
	}

	@Before
	public void before() throws ClassNotFoundException {
		Class<Object> localConfig = Whitebox.getInnerClassType(clazz, "LocalConfiguration");
		Object mockConfig = mock(localConfig);
		when(((MacroConfiguration) mockConfig).getMacros()).thenReturn(macros);
		Whitebox.setInternalState(clazz, "config", mockConfig);
	}

	@After
	public void after() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ClassNotFoundException {
		Class<Object> localConfig = Whitebox.getInnerClassType(clazz, "LocalConfiguration");
		Whitebox.setInternalState(clazz, "config", Whitebox.getConstructor(localConfig).newInstance());
	}

	protected void addMacro(String name, String... values) {
		macros.put(name, Arrays.asList(values));
	}

	protected void test(String input, String... expected) throws Exception {
		List<String> commands = Whitebox.invokeMethod(clazz, methodName, input);
		Joiner joiner = Joiner.on('\n');
		assertEquals(joiner.join(expected), joiner.join(commands));
	}

}
