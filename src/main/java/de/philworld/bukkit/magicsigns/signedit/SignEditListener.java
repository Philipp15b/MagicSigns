package de.philworld.bukkit.magicsigns.signedit;

import static de.philworld.bukkit.magicsigns.util.MaterialUtil.isSign;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.philworld.bukkit.magicsigns.util.BlockLocation;

public class SignEditListener implements Listener {

	private final SignEdit signEdit;

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

		// only allow MagicSigns if mode is mask.
		if (mode == EditMode.MASK_MAGIC_SIGNS
				&& signEdit.plugin.getSignManager()
						.getSign(
								new BlockLocation(event.getBlockAgainst()
										.getLocation())) == null)
			return;

		signEdit.registerEditSign(event.getBlockPlaced().getLocation(), event
				.getBlockAgainst().getLocation());
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignChange(SignChangeEvent event) {
		// only temporary edit signs
		if (!signEdit.isTempSign(event.getBlock()))
			return;

		Sign tempSign = (Sign) event.getBlock().getState();
		Sign targetSign = (Sign) signEdit.getTargetBlock(event.getBlock())
				.getState();

		EditMode playerEditMode = signEdit.getEditMode(event.getPlayer());

		// whether to mask a MagicSign. If true, don't call SignChangeEvent.
		boolean maskMagicSign = ((playerEditMode == EditMode.MASK_MAGIC_SIGNS || playerEditMode == EditMode.AUTO) && signEdit.plugin
				.getSignManager().getSign(
						new BlockLocation(targetSign.getLocation())) != null);

		SignChangeEvent signChange = null;
		String[] newLines = event.getLines();

		if (!maskMagicSign) {
			// delete the old MagicSign if the EditMode is modify.
			if (playerEditMode == EditMode.MODIFY
					&& signEdit.plugin.getSignManager().getSign(
							new BlockLocation(targetSign.getLocation())) != null) {
				signEdit.plugin.getSignManager().removeSign(
						new BlockLocation(targetSign.getLocation()));
			}

			// call a new SignChangeEvent to inform other plugins
			signChange = new SignChangeEvent(targetSign.getLocation()
					.getBlock(), event.getPlayer(), newLines);

			Bukkit.getServer().getPluginManager().callEvent(signChange);

			newLines = signChange.getLines();
		} else {
			// when masking, make sure signs are colored
			signEdit.plugin.getColoredSigns().getListener().onSignChange(event);
			newLines = event.getLines();
		}

		if (maskMagicSign || (signChange != null && !signChange.isCancelled())) {
			// copy from the tempSign to targetSign
			for (int i = 0; i < newLines.length; i++) {
				targetSign.setLine(i, newLines[i]);
			}

			targetSign.update();
		}

		// remove the temporary sign
		event.getBlock().setType(Material.AIR);
		signEdit.unregisterEditSign(tempSign.getLocation());

		// and give it back
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.getPlayer().getInventory()
					.addItem(new ItemStack(Material.SIGN, 1));
			event.getPlayer().updateInventory();
		}

		event.setCancelled(true);
	}

	/**
	 * If this listener would handle a interaction with a sign. This should be
	 * used not to react to a {@link PlayerInteractEvent}.
	 */
	public boolean willEditMagicSign(PlayerInteractEvent event) {
		if (signEdit.getEditMode(event.getPlayer()) == EditMode.NONE)
			return false;
		if (isSign(event.getClickedBlock().getType())
				&& event.getItem() != null && isSign(event.getItem().getType())) {
			return true;
		}
		return false;
	}

}
