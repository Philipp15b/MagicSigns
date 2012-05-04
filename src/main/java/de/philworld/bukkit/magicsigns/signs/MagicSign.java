package de.philworld.bukkit.magicsigns.signs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spout.api.util.config.ConfigurationNode;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.config.ConfigurationBase;
import de.philworld.bukkit.magicsigns.config.MagicSignSerializationProxy;
import de.philworld.bukkit.magicsigns.locks.Lock;
import de.philworld.bukkit.magicsigns.locks.PlayerLock;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;

/**
 * This is the parent class for every magic sign. Subclasses of MagicSign must
 * override the static {@link #takeAction(Sign, String[])} method (which
 * throws an {@link Exception} in the default implementation) and should
 * override {@link #onRightClick(PlayerInteractEvent)}. For magic signs that
 * require configuration, they can override
 * {@link #loadConfig(ConfigurationSection)}.
 *
 * @see ConfigurationBase ConfigurationBase for a simple base configuration class.
 */
public abstract class MagicSign {

	/**
	 * Contains the configuration of this magic sign.
	 */
	private static ConfigurationBase config = null;

	/**
	 * Initializes the configuration.
	 *
	 * @param node
	 *            {@link ConfigurationNode} the config.
	 */
	public static void loadConfig(ConfigurationSection section) {
	}

	public static void saveConfig(ConfigurationSection section) {
		if (config != null) {
			config.save(section);
		}
	}

	/**
	 * Returns if a new Magic Sign should be created of this sign.
	 *
	 * <i><b>Important:</b> Must be overridden!</i>
	 */
	public static boolean takeAction(Sign sign, String[] lines) {
		throw new UnsupportedOperationException(
				"The static method takeAction() must be overridden!");
	}

	private final String[] lines;
	private Lock lock = null;
	private Map<String, PlayerLock> playerLocks = null;
	private final Block sign;

	/**
	 * Create a new instance of the MagicSign
	 *
	 * @param sign
	 * @param lines
	 * @throws InvalidSignException
	 */
	public MagicSign(Block sign, String[] lines) throws InvalidSignException {
		this.sign = sign;
		this.lines = lines;
	}

	public String getDescription() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);

		if (signInfo != null) {
			return signInfo.description();
		} else {
			return null;
		}
	}

	public String getFriendlyName() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);

		if (signInfo != null) {
			return signInfo.friendlyName();
		} else {
			return null;
		}
	}

	/**
	 * @return the lines
	 */
	public String[] getLines() {
		return lines;
	}

	/**
	 * Get the location of this sign
	 *
	 * @return Location
	 */
	public Location getLocation() {
		return getSign().getLocation();
	}

	/**
	 * @return the lock; can be null if there is no lock.
	 */
	public Lock getLock() {
		return lock;
	}

	/**
	 * Get the player lock for this sign. If there is no player lock registered,
	 * a new one is returned.
	 *
	 * @param p
	 *            The player
	 * @return The lock
	 */
	public PlayerLock getPlayerLock(Player p) {
		if (playerLocks != null) {
			if (playerLocks.containsKey(p.getName())) {
				return playerLocks.get(p.getName());
			} else {
				PlayerLock lock = new PlayerLock(this.lock);
				playerLocks.put(p.getName(), lock);
				return lock;
			}
		} else {
			playerLocks = new HashMap<String, PlayerLock>();
			PlayerLock lock = new PlayerLock(this.lock);
			playerLocks.put(p.getName(), lock);
			return lock;
		}
	}

	/**
	 * Get all player locks.
	 *
	 * @return The player locks; can be null.
	 */
	public Map<String, PlayerLock> getPlayerLocks() {
		return playerLocks;
	}

	/**
	 * @return the sign
	 */
	public Block getSign() {
		return sign;
	}

	/**
	 * Get the use permission for this {@link MagicSign}.
	 *
	 * @param sign
	 *            the MagicSign
	 * @return The use permission.
	 */
	public String getUsePermission() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);

		if (signInfo != null) {
			return signInfo.usePerm();
		} else {
			return null;
		}
	}

	/**
	 * Returns if this sign is masked (the sign shows a different text than this
	 * MagicSign uses for its work).
	 *
	 * @return True if its masked, else false.
	 */
	public boolean isMasked() {
		Sign sign = (Sign) this.getSign().getState();
		String[] currentLines = sign.getLines();
		return !Arrays.equals(currentLines, getLines());
	}

	/**
	 * Called every time the sign is created, but not when its loaded from
	 * config.
	 *
	 * @param event
	 */
	public void onCreate(SignChangeEvent event) {
		return;
	}

	/**
	 * Called on every right click on the sign.
	 *
	 * @param event
	 * @throws PermissionException
	 */
	public void onRightClick(PlayerInteractEvent event)
			throws PermissionException {
		return;
	}

	/**
	 * Removes all registered player locks.
	 *
	 * @see {@link #setLock(Lock, boolean)} to delete only locks of the current
	 *      lock type, not special player locks.
	 */
	public void removePlayerLocks() {
		playerLocks = null;
	}

	/**
	 * Return the serialization proxy.
	 *
	 * @return {@link MagicSignSerializationProxy}
	 */
	public MagicSignSerializationProxy serialize() {
		return new MagicSignSerializationProxy(this);
	}

	/**
	 * @param lock
	 *            the lock to set
	 * @param deleteOld
	 *            whether to delete the old saved {@link PlayerLock}s of the
	 *            type of the old lock.
	 */
	public void setLock(Lock lock, boolean deleteOld) {
		if (deleteOld && playerLocks != null) {
			for (Entry<String, PlayerLock> entry : playerLocks.entrySet()) {
				PlayerLock playerLock = entry.getValue();
				if (playerLock.getLock().equals(this.lock))
					playerLocks.remove(entry.getKey());
			}
		}
		this.lock = lock;
	}

	/**
	 * Set the player lock.
	 *
	 * @param p
	 *            The player
	 * @param lock
	 *            The lock; can be null to remove
	 */
	public void setPlayerLock(Player p, PlayerLock lock) {
		if (lock != null) {
			if (playerLocks == null)
				playerLocks = new HashMap<String, PlayerLock>();
			playerLocks.put(p.getName(), lock);
		} else {
			if (playerLocks == null)
				return;
			playerLocks.remove(p.getName());
			if (playerLocks.isEmpty())
				playerLocks = null;
		}
	}

	public void setPlayerLocks(Map<String, PlayerLock> playerLocks) {
		this.playerLocks = playerLocks;
	}

}
