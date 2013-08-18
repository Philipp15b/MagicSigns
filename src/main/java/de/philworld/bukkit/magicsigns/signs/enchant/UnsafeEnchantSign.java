package de.philworld.bukkit.magicsigns.signs.enchant;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

@MagicSignInfo(
		name = "UEnchant",
		friendlyName = "Unsafe Enchant sign",
		description = "A sign that allows unsafely enchanting items.",
		buildPerm = "magicsigns.uenchant.create",
		usePerm = "magicsigns.uenchant.use")
public class UnsafeEnchantSign extends EnchantSign {

	// the only difference between those two signs is that this one does allow
	// arbitrary high levels. The EnchantSign constructor leaves out that check
	// if it's an UnsafeEnchantSign.

	public UnsafeEnchantSign(BlockLocation location, String[] lines) throws InvalidSignException {
		super(location, lines);
	}

}
