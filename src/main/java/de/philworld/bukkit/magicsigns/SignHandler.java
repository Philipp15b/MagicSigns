package de.philworld.bukkit.magicsigns;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * Handles all signs and sign types.
 * 
 * <h2>Usage</h2>
 * 
 * <pre>
 * <code>
 *   SignHandler handler = new SignHandler();
 *   handler.registerSignType(<? extends MagicSign> myMagicSignClass);
 *   handler.registerSign(Block mysign); // for every sign thats created or changed.
 * </code>
 * </pre>
 * 
 * The handler will automatically check all sign types and if some take action,
 * it will instantiate new objects of them.
 */
public class SignHandler implements Listener {

	private Set<Class<? extends MagicSign>> signTypes = new HashSet<Class<? extends MagicSign>>();
	private Map<Location, MagicSign> signs = new HashMap<Location, MagicSign>();
	private MagicSigns plugin;

	public SignHandler(MagicSigns plugin) {
		this.plugin = plugin;
	}

	public Collection<MagicSign> getSigns() {
		return signs.values();
	}

	/**
	 * Adds a new sign type. It must extend MagicSign and override the static
	 * method <code>takeAction()</code>. The class can also contain permission
	 * annotations ({@link BuildPermission}, {@link UsePermission}).
	 * 
	 * @param signType
	 */
	public void registerSignType(Class<? extends MagicSign> signType) {
		signTypes.add(signType);
	}

	public void registerSign(Block sign, String[] lines) {
		registerSign(sign, lines, null, null);
	}

	/**
	 * Generates a MagicSign from a block if some MagicSign takesAction (
	 * <code>takeAction()</code>) and registers it.
	 * <ul>
	 * <li>Add player if you want to check for permissions
	 * <li>Add event if you want to call onCreate() on the new sign.
	 * </ul>
	 * 
	 * @param sign
	 *            Sign Block
	 * @param lines
	 *            Lines on the sign
	 * @param p
	 *            Player for permission checks; can be null
	 * @param event
	 *            SignChangeEvent to call onCreate(); can be null
	 */
	public void registerSign(Block sign, String[] lines, Player p,
			SignChangeEvent event) {
		for (Class<? extends MagicSign> signType : signTypes) {

			Method takeAction = null;

			// get takeAction static method
			try {
				takeAction = signType.getMethod("takeAction", Block.class,
						lines.getClass());
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException(
						"Could not find static method takeAction(Block, String[])");
			}

			// invoke takeAction
			try {

				if ((Boolean) takeAction.invoke(null, sign, lines)) {

					// check for build permissions
					if (p != null) {
						BuildPermission buildPerm = signType
								.getAnnotation(BuildPermission.class);

						if (buildPerm != null) {
							if (!p.hasPermission(buildPerm.value())) {
								sign.breakNaturally();
								throw new PermissionException();
							}
						}
					}

					MagicSign magicSign = signType.getConstructor(Block.class,
							lines.getClass()).newInstance(sign, lines);

					// call onCreate()
					if (event != null)
						magicSign.onCreate(event);

					// add the sign to the list
					registerSign(magicSign);

					if (p != null)
						MSMsg.SIGN_CREATED.send(p);

					return;
				}

			} catch (PermissionException e) {
				MSMsg.NO_PERMISSION.send(p);
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof InvalidSignException) {
					if (p != null) {
						p.sendMessage(ChatColor.RED
								+ e.getTargetException().getMessage());
					} else {
						getLogger().log(
								Level.WARNING,
								"Invalid sign: "
										+ e.getTargetException().getMessage(),
										e.getTargetException());
					}
				} else
					getLogger().log(
							Level.WARNING,
							"Error registering Magic sign of type "
									+ signType.getCanonicalName() + ": "
									+ e.getTargetException().getMessage(),
									e.getTargetException());
			} catch (Throwable e) {
				getLogger().log(
						Level.WARNING,
						"Error registering sign of type "
								+ signType.getCanonicalName(), e);
			}

		}
	}

	/**
	 * Registers a MagicSign directly.
	 * 
	 * @param sign
	 */
	public void registerSign(MagicSign sign) {
		signs.put(sign.getLocation(), sign);
	}

	// ----------------
	// EVENT HANDLERS
	// ----------------

	/**
	 * Adds every created/changed sign to the plugin's signHandler.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		try {
			registerSign(event.getBlock(), event.getLines(), event.getPlayer(),
					event);
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
		if (signs.containsKey(loc)) {

			MagicSign sign = signs.get(loc);

			try {

				// check for UsePermission annotation.
				UsePermission perm = sign.getClass().getAnnotation(
						UsePermission.class);

				if (perm != null) {
					if (!event.getPlayer().hasPermission(perm.value())) {
						throw new PermissionException();
					}
				}

				sign.onRightClick(event);

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
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		if (signs.containsKey(event.getBlock().getLocation())) {
			signs.remove(event.getBlock().getLocation());
		}

	}

	private Logger getLogger() {
		return plugin.getLogger();
	}
}