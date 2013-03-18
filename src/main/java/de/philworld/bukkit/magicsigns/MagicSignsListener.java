package de.philworld.bukkit.magicsigns;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.locks.PlayerLock;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.signs.PurchasableMagicSign;
import de.philworld.bukkit.magicsigns.util.MSMsg;

public class MagicSignsListener implements Listener {

	private final SignManager manager;
	private final MagicSigns plugin;

	public MagicSignsListener(MagicSigns plugin) {
		this.plugin = plugin;
		manager = plugin.getSignManager();
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
			if (plugin.getSignEdit().getListener().willEditMagicSign(event))
				return;

			MagicSign sign = manager.getSign(loc);
			try {

				if (sign.getUsePermission() != null) {
					if (!event.getPlayer().hasPermission(
							sign.getUsePermission())) {
						throw new PermissionException();
					}
				}

				if (sign.getLock() != null
						&& sign.getPlayerLock(event.getPlayer()) != null) {
					PlayerLock pLock = sign.getPlayerLock(event.getPlayer());
					long time = System.currentTimeMillis() / 1000;
					if (pLock.isUsable(time)) {
						pLock.touch(time);
					} else {
						throw new PermissionException(ChatColor.RED
								+ pLock.getErrorMessage(time));
					}
				}

				if (sign instanceof PurchasableMagicSign) {
					PurchasableMagicSign pSign = (PurchasableMagicSign) sign;
					if (!pSign.isFree()) {
						if (!pSign.withdrawPlayer(event.getPlayer())) {
							MSMsg.NOT_ENOUGH_MONEY.send(event.getPlayer(),
									pSign.getPrice().toString());
							return;
						}
						MSMsg.PAID_SIGN.send(event.getPlayer(), pSign
								.getPrice().toString());
					}
				}

				sign.onRightClick(event);

				event.setCancelled(true);

			} catch (PermissionException e) {
				e.send(event.getPlayer());
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
