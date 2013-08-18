package de.philworld.bukkit.magicsigns.dev;

import java.util.EnumSet;

import de.philworld.bukkit.magicsigns.util.EnchantmentEx;

/**
 * Prints out a HTML table from enchantment names and abbreviations.
 */
public class EnchantmentPrinter {

	public static void main(String[] args) {
		// awesome! html!
		System.out.println("<table>");
		System.out.println("    <thead>");
		System.out.println("        <tr><th>Enchantment</th><th>Names</th></tr>");
		System.out.println("    </thead>");
		System.out.println("    <tbody>");
		for (EnchantmentEx e : EnumSet.allOf(EnchantmentEx.class)) {
			System.out.print("    <tr><td>" + e.name + "</td><td>");
			for (int i = 0; i < e.lookupNames.length; i++) {
				System.out.print("<code>" + e.lookupNames[i] + "</code>");
				if (i != e.lookupNames.length - 1)
					System.out.print(", ");
			}
			System.out.println("</td></tr>");
		}
		System.out.println("    </tbody>");
		System.out.println("</table>");
	}

}
