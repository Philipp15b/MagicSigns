package de.philworld.bukkit.magicsigns.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MacroUtil {

	public static final String MACRO_START = "$";
	public static final String MACRO_END = "$";
	public static final String DELIMITER = " && ";

	/**
	 * Formats the string by splitting it by the {@link MacroUtil#DELIMITER} and
	 * inserting the given macros. Macros will be formatted recursively; this
	 * allows macros contain macros.
	 *
	 * @param text
	 *            The text to format
	 * @param macros
	 *            The macros
	 * @return The list of formatted elements; can be empty if there are only spaces.
	 */
	public static List<String> format(String text,
			Map<String, List<String>> macros) {
		String[] elements = text.split(DELIMITER);

		List<String> resultList = new LinkedList<String>();

		for (String element : elements) {

			if(element.replaceAll(" ", "").isEmpty())
				continue;

			// insert macros
			boolean hasFoundMacro = false;
			for (Map.Entry<String, List<String>> entry : macros.entrySet()) {
				String macroName = entry.getKey();
				List<String> macroValue = entry.getValue();

				// if the macro was found insert the commands of this macro
				if (element.contains(MACRO_START + macroName + MACRO_END)) {
					hasFoundMacro = true;

					for (String macroCmd : macroValue) {
						resultList.addAll(format(macroCmd, macros));
					}
					break;
				}
			}

			// if no macro was found, just insert this element into the list
			if (!hasFoundMacro) {
				resultList.add(element);
			}
		}

		return resultList;
	}

}
