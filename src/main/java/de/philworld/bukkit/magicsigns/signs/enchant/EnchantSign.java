package de.philworld.bukkit.magicsigns.signs.enchant;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.PurchasableMagicSign;
import de.philworld.bukkit.magicsigns.util.BlockLocation;
import de.philworld.bukkit.magicsigns.util.EnchantmentEx;

@MagicSignInfo(
		name = "Enchant",
		friendlyName = "Enchant sign",
		description = "A sign that allows enchanting items.",
		buildPerm = "magicsigns.enchant.create",
		usePerm = "magicsigns.enchant.use")
public class EnchantSign extends PurchasableMagicSign {

	private final EnchantmentEx enchantment;
	private final int level;
	private final boolean set;

	public EnchantSign(BlockLocation location, String[] lines) throws InvalidSignException {
		super(location, lines);

		String[] parts = lines[1].split(":");
		enchantment = EnchantmentEx.lookup(parts[0]);
		if (enchantment == null)
			throw new InvalidSignException("Could not find enchantment " + ChatColor.BLUE + lines[1] + ChatColor.WHITE
					+ "!");

		if (parts.length > 1 && !parts[1].trim().isEmpty()) {
			try {
				if (parts[1].charAt(0) == '=') {
					level = Integer.parseInt(parts[1].substring(1));
					set = true;
				} else {
					level = Integer.parseInt(parts[1]);
					set = level == 0;
				}

			} catch (NumberFormatException e) {
				throw new InvalidSignException("The enchantment level is not a number!");
			}
			if (level < 0)
				throw new InvalidSignException("The enchantment level may not be less than zero!");
			if (level > enchantment.getMaxLevel() && !(this instanceof UnsafeEnchantSign))
				throw new InvalidSignException("The maximum level for " + enchantment.name + " is "
						+ enchantment.getMaxLevel() + "!");
		} else {
			level = enchantment.getStartLevel();
			set = false;
		}

	}

	@Override
	public void beforeRightClick(PlayerInteractEvent event) throws PermissionException {
		ItemStack is = event.getPlayer().getItemInHand();
		if (is == null) {
			throw new PermissionException(ChatColor.RED + "You must hold an enchantable item in your hand!");
		} else if ((set && is.getEnchantmentLevel(enchantment.enchantment) == level)
				|| (!set && is.containsEnchantment(enchantment.enchantment) && is
						.getEnchantmentLevel(enchantment.enchantment) >= level)) {
			throw new PermissionException(ChatColor.RED + "This item is already enchanted with " + ChatColor.BLUE
					+ format() + ChatColor.GREEN + "!");
		} else if (!enchantment.canEnchantItem(is)) {
			throw new PermissionException(ChatColor.RED + "This item cannot be enchanted with " + ChatColor.BLUE
					+ enchantment.name + ChatColor.RED + "!");
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		if (level != 0) {
			event.getPlayer().getItemInHand().addUnsafeEnchantment(enchantment.enchantment, level);
			event.getPlayer().sendMessage(
					ChatColor.GREEN + "Your item is now enchanted with " + ChatColor.BLUE + format() + ChatColor.GREEN
							+ "!");
		} else {
			event.getPlayer().getItemInHand().removeEnchantment(enchantment.enchantment);
			event.getPlayer().sendMessage(
					ChatColor.GREEN + "Removed " + ChatColor.BLUE + enchantment.name + " " + ChatColor.GREEN
							+ " from your item!");
		}
	}

	private String format() {
		String levelStr;
		if (level == 1)
			levelStr = "I";
		else if (level == 2)
			levelStr = "II";
		else if (level == 3)
			levelStr = "III";
		else if (level == 4)
			levelStr = "IV";
		else if (level == 5)
			levelStr = "V";
		else
			levelStr = Integer.toString(level);
		return enchantment.name + " " + levelStr;
	}
}
