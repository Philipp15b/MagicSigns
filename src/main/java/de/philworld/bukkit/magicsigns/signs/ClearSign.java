package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * A sign that clears player's inventory.
 *
 * If <code>[all]</code> is on the second line, it will clear the whole
 * inventory, otherwise only the hot bar.
 *
 */
@BuildPermission("magicsigns.clear.create")
@UsePermission("magicsigns.clear.use")
public class ClearSign extends PurchasableMagicSign {

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Clear]");
	}

	/**
	 * Whether to clear the whole inventory or just the hot bar.
	 */
	private boolean clearAll = false;

	public ClearSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		if (lines[1].equalsIgnoreCase("[all]")) {
			clearAll = true;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRightClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();

		Inventory inventory = p.getInventory();

		if (clearAll) {
			inventory.clear();
		} else {
			for (int i = 9; i < 36; i++) {
				inventory.setItem(i, new ItemStack(Material.AIR));
			}
		}

		p.updateInventory();

		MSMsg.INVENTORY_CLEARED.send(p);
	}

}
