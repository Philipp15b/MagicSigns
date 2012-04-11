package de.philworld.bukkit.magicsigns.signedit;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.magicsigns.MagicSignsListener;

public class SignEditListener implements Listener {

	public final SignEdit signEdit;

	public SignEditListener(SignEdit signEdit) {
		this.signEdit = signEdit;
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		// check that we are only using signs that are placed against other
		// signs.
		if (!isSign(event.getBlockPlaced().getType())
				|| !isSign(event.getBlockAgainst().getType()))
			return;

		EditMode mode = signEdit.getEditMode(event.getPlayer());
		if (mode == EditMode.NONE)
			return;

		if (mode == EditMode.MASK_MAGIC_SIGNS
				&& !signEdit.plugin.isMagicSign(event.getBlockAgainst()
						.getLocation()))
			return;

		signEdit.registerEditSign(event.getBlockPlaced().getLocation(), event
				.getBlockAgainst().getLocation());
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
	public void onSignChange(SignChangeEvent event) {
		if (!signEdit.isTempSign(event.getBlock()))
			return;

		Sign tempSign = (Sign) event.getBlock().getState();
		Sign targetSign = (Sign) signEdit.getTargetBlock(event.getBlock())
				.getState();

		// copy from the tempSign to targetSign
		for (int i = 0; i < event.getLines().length; i++) {
			targetSign.setLine(i, event.getLines()[i]);
		}

		targetSign.update();

		// if the EditMode is modify, delete the old MagicSign
		if (signEdit.getEditMode(event.getPlayer()) == EditMode.MODIFY
				&& signEdit.plugin.isMagicSign(targetSign.getLocation())) {
			signEdit.plugin.signManager.removeSign(targetSign.getLocation());
		}

		// remove the temporary sign
		event.getBlock().setType(Material.AIR);
		signEdit.unregisterEditSign(tempSign.getLocation());

		// and give it back
		event.getPlayer().getInventory()
				.addItem(new ItemStack(Material.SIGN, 1));
		event.getPlayer().updateInventory();

		event.setCancelled(true);

		// TODO: call SignChangeEvent to register modified MagicSigns etc.
	}

	/**
	 * Helper method for {@link MagicSignsListener} for not to handle the
	 * interaction when this plugin would edit it.
	 *
	 * @param event
	 * @return
	 */
	public boolean willEditMagicSign(PlayerInteractEvent event) {
		if (isSign(event.getClickedBlock().getType())
				&& event.getItem() != null && isSign(event.getItem().getType())) {
			if (signEdit.getEditMode(event.getPlayer()) == EditMode.NONE)
				return false;
			else
				return true;
		} else {
			return false;
		}
	}

	private boolean isSign(Material material) {
		return material == Material.SIGN || material == Material.SIGN_POST
				|| material == Material.WALL_SIGN;
	}

}
