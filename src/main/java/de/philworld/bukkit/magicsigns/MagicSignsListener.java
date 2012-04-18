package de.philworld.bukkit.magicsigns;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.signs.PurchasableMagicSign;
import de.philworld.bukkit.magicsigns.util.MSMsg;

public class MagicSignsListener implements Listener {

	private SignManager manager;
	private MagicSigns plugin;

	public MagicSignsListener(MagicSigns plugin) {
		this.plugin = plugin;
		this.manager = plugin.getSignManager();
	}

	/**
	 * Adds every created/changed sign to the plugin's signHandler.
	 *
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		if (plugin.getSignEdit().isTempSign(event.getBlock()))
			return;

		try {
			manager.registerSign(event.getBlock(), event.getLines(),
					event.getPlayer(), event);
		} catch (Exception e) {
			getLogger().log(Level.SEVERE,
					"Could not add new sign to SignHandler", e);
		}
	}

	/**
	 * Calls <code>playerInteract()</code> on the MagicSigns.
	 *
	 * @param event
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()
				|| !(event.hasBlock()
						&& event.getAction() == Action.RIGHT_CLICK_BLOCK
						&& event.getClickedBlock() != null && event
						.getClickedBlock().getState() instanceof Sign)) {
			return;
		}

		Location loc = event.getClickedBlock().getLocation();

		if (manager.containsSign(loc)) {

			// if the plugin will edit this sign, don't allow interaction.
			if (plugin.getSignEdit().listener.willEditMagicSign(event))
				return;

			MagicSign sign = manager.getSign(loc);
			try {

				if (sign.getUsePermission() != null) {
					if (!event.getPlayer().hasPermission(
							sign.getUsePermission())) {
						throw new PermissionException();
					}
				}

				if (sign instanceof PurchasableMagicSign) {
					PurchasableMagicSign pSign = (PurchasableMagicSign) sign;
					if (!pSign.isFree()) {
						if (!pSign.withdrawPlayer(event.getPlayer())) {
							MSMsg.NOT_ENOUGH_MONEY.send(event.getPlayer());
							return;
						} else {
							MSMsg.PAID_SIGN.send(event.getPlayer());
						}
					}
				}

				sign.onRightClick(event);

				event.setCancelled(true);

			} catch (PermissionException e) {
				MSMsg.NO_PERMISSION.send(event.getPlayer());
			}
		}
	}

	/**
	 * Removes broken signs from the list.
	 *
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if (manager.containsSign(event.getBlock().getLocation())) {
			manager.removeSign(event.getBlock().getLocation());
		}
	}

	private Logger getLogger() {
		return plugin.getLogger();
	}
}
