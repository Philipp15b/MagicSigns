package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.BlockLocation;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * A sign that clears player's inventory.
 * 
 * If <code>[all]</code> is on the second line, it will clear the whole
 * inventory, otherwise only the hot bar.
 * 
 */
@MagicSignInfo(
		name = "Clear",
		friendlyName = "Clear sign",
		description = "A sign that clears player's inventory.",
		buildPerm = "magicsigns.clear.create",
		usePerm = "magicsigns.clear.use")
public class ClearSign extends PurchasableMagicSign {

	/**
	 * Whether to clear the whole inventory or just the hot bar.
	 */
	private boolean clearAll = false;

	public ClearSign(BlockLocation location, String[] lines) throws InvalidSignException {
		super(location, lines);

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
