package de.philworld.bukkit.magicsigns.util;

import java.util.EnumSet;
import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableMap;
import com.sk89q.util.StringUtil;

public enum EnchantmentEx {

	PROTECTION(Enchantment.PROTECTION_ENVIRONMENTAL, "protection", "protect", "p"),
	FIRE_PROTECTION(Enchantment.PROTECTION_FIRE, "fireprotection", "fireprotect", "fp"),
	FEATHER_FALLING(Enchantment.PROTECTION_FALL, "featherfalling", "feather", "f"),
	BLAST_PROTECTION(Enchantment.PROTECTION_EXPLOSIONS, "blastprotection", "blast", "b"),
	PROJECTILE_PROTECTION(Enchantment.PROTECTION_PROJECTILE, "projectileprotection", "projectile", "proj"),
	RESPIRATION(Enchantment.OXYGEN, "respiration", "resp", "r"),
	AQUA_AFFINITY(Enchantment.WATER_WORKER, "aquaaffinity", "aqua", "aa"),
	THORNS(Enchantment.THORNS, "thorns", "t"),
	SHARPNESS(Enchantment.DAMAGE_ALL, "sharpness", "sharp", "damage", "d"),
	SMITE(Enchantment.DAMAGE_UNDEAD, "smite"),
	BANE_OF_ARTHROPODS(Enchantment.DAMAGE_ARTHROPODS, "baneofarthropods", "baneof", "bane", "arthrophods", "arthro",
			"ba"),
	KNOCKBACK(Enchantment.KNOCKBACK, "knockback", "knock"),
	FIRE_ASPECT(Enchantment.FIRE_ASPECT, "fireaspect", "fire", "fa"),
	LOOTING(Enchantment.LOOT_BONUS_MOBS, "looting", "loot", "l"),
	EFFICIENCY(Enchantment.DIG_SPEED, "efficiency", "eff", "e"),
	SILK_TOUCH(Enchantment.SILK_TOUCH, "silktouch", "silk", "st"),
	UNBREAKING(Enchantment.DURABILITY, "unbreaking", "unbreak", "durability", "dura", "ub", "u"),
	FORTUNE(Enchantment.LOOT_BONUS_BLOCKS, "fortune", "for"),
	POWER(Enchantment.ARROW_DAMAGE, "power", "arrow", "pow"),
	PUNCH(Enchantment.ARROW_KNOCKBACK, "punch", "arrowknockback", "akb"),
	FLAME(Enchantment.ARROW_FIRE, "flame", "flamearrow", "fla"),
	INFINITY(Enchantment.ARROW_INFINITE, "infinity", "arrowinfinite", "ai", "i");

	private static final Map<String, EnchantmentEx> lookup;

	static {
		ImmutableMap.Builder<String, EnchantmentEx> builder = new ImmutableMap.Builder<String, EnchantmentEx>();
		for (EnchantmentEx e : EnumSet.allOf(EnchantmentEx.class)) {
			for (String name : e.lookupNames) {
				builder.put(name, e);
			}
		}
		lookup = builder.build();
	}

	public final String name;
	public final Enchantment enchantment;
	public final String[] lookupNames;

	public static EnchantmentEx lookup(String name) {
		return StringUtil.lookup(lookup, name, true);
	}

	EnchantmentEx(Enchantment enchantment, String... lookupNames) {
		this.name = toNiceName(name());
		this.enchantment = enchantment;
		this.lookupNames = new String[lookupNames.length + 1];
		this.lookupNames[0] = name;
		System.arraycopy(lookupNames, 0, this.lookupNames, 1, lookupNames.length);
	}

	public int getStartLevel() {
		return enchantment.getStartLevel();
	}

	public int getMaxLevel() {
		return enchantment.getMaxLevel();
	}

	public boolean canEnchantItem(ItemStack is) {
		return enchantment.canEnchantItem(is);
	}

	/**
	 * Converts an enum name to a camel-cased name with spaces.
	 */
	private static String toNiceName(String enumName) {
		StringBuilder sb = new StringBuilder(enumName.length());
		String[] parts = enumName.split("_");
		for (int i = 0; i < parts.length; i++) {
			String s = parts[i];
			sb.append(Character.toUpperCase(s.charAt(0)));
			if (s.length() > 1) {
				sb.append(s.substring(1).toLowerCase());
			}
			if (i != parts.length - 1)
				sb.append(" ");
		}
		return sb.toString();
	}

}
