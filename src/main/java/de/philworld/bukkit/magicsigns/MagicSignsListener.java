package de.philworld.bukkit.magicsigns;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import de.philworld.bukkit.magicsigns.locks.PlayerLock;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.signs.MagicSign;
import de.philworld.bukkit.magicsigns.signs.PurchasableMagicSign;
import de.philworld.bukkit.magicsigns.util.BlockLocation;
import de.philworld.bukkit.magicsigns.util.MaterialUtil;

public class MagicSignsListener implements Listener {

	private final MagicSigns plugin;
	private final SignManager manager;

	public MagicSignsListener(MagicSigns plugin) {
		this.plugin = plugin;
		manager = plugin.getSignManager();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent event) {
		if (plugin.lazyLoader != null)
			plugin.lazyLoader.loadChunk(event.getChunk());
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		if (plugin.getSignEdit().isTempSign(event.getBlock()))
			return;

		String typeName = getSignTypeFor(event.getLines()[0]);
		if (typeName == null)
			return;
		SignType signType = manager.getSignType(typeName);
		if (signType == null)
			return;

		// check for build permissions
		if (!event.getPlayer().hasPermission(signType.getBuildPermission())) {
			event.setCancelled(true);
			MSMsg.NO_PERMISSION.send(event.getPlayer());
			return;
		}

		try {
			MagicSign magicSign = signType.newInstance(new BlockLocation(event.getBlock().getLocation()),
					event.getLines());

			manager.registerSign(magicSign);
			event.setLine(0, ChatColor.BLUE + "[" + typeName + "]");

			MSMsg.SIGN_CREATED.send(event.getPlayer());
			return;
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof InvalidSignException) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + e.getCause().getMessage());
				return;
			}
			plugin.getLogger().log(Level.SEVERE, "Error registering Magic sign:", e.getTargetException());
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error registering Magic sign:", e);
		}

		event.getPlayer().sendMessage(ChatColor.RED + "An error occured while creating this sign :(");
	}

	/**
	 * Parses a MagicSign header and extracts the name. Leading and trailing
	 * whitespace is allowed and the name must be enclosed within <code>[</code>
	 * and <code>]</code>.
	 */
	static String getSignTypeFor(String line) {
		int start = -1;
		for (int i = 0; i < line.length(); i++) {
			switch (line.charAt(i)) {
			case ' ':
			case '\t':
				if (start != -1)
					return null;
				break;
			case '[':
				start = i + 1;
				break;
			case ']':
				if (start != -1) {
					return line.substring(start, i);
				}
				return null;
			default:
				if (start == -1)
					return null;
			}
		}
		return null;
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.hasBlock() || event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null
				|| !MaterialUtil.isSign(event.getClickedBlock().getType())) {
			return;
		}

		MagicSign sign = manager.getSign(new BlockLocation(event.getClickedBlock().getLocation()));
		if (sign == null)
			return;

		// if the plugin will edit this sign, don't allow interaction.
		if (plugin.getSignEdit().getListener().willEditMagicSign(event))
			return;

		event.setCancelled(true);

		Player p = event.getPlayer();
		try {
			if (sign.getUsePermission() != null && !p.hasPermission(sign.getUsePermission()))
				throw new PermissionException();

			if (sign.getLock() != null && sign.getPlayerLock(p) != null) {
				PlayerLock pLock = sign.getPlayerLock(p);
				long time = System.currentTimeMillis() / 1000;
				if (pLock.isUsable(time)) {
					pLock.touch(time);
				} else {
					throw new PermissionException(ChatColor.RED + pLock.getErrorMessage(time));
				}
			}

			sign.beforeRightClick(event);

			if (sign instanceof PurchasableMagicSign) {
				PurchasableMagicSign pSign = (PurchasableMagicSign) sign;
				if (!pSign.isFree()) {
					if (pSign.withdrawPlayer(p)) {
						MSMsg.PAID_SIGN.send(p, pSign.getPrice().toString());
					} else {
						MSMsg.NOT_ENOUGH_MONEY.send(p, pSign.getPrice().toString());
						return;
					}
				}
			}

			sign.onRightClick(event);
		} catch (PermissionException e) {
			e.send(event.getPlayer());
		}
	}

	/**
	 * Removes broken signs from the list.
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if (MaterialUtil.isSign(event.getBlock().getType()))
			manager.removeSign(new BlockLocation(event.getBlock().getLocation()));
	}

}
