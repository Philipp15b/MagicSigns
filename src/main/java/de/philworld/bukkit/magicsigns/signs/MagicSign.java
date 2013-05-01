package de.philworld.bukkit.magicsigns.signs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.config.Configuration;
import de.philworld.bukkit.magicsigns.config.MagicSignSerializationProxy;
import de.philworld.bukkit.magicsigns.config.annotation.AnnotationConfiguration;
import de.philworld.bukkit.magicsigns.locks.Lock;
import de.philworld.bukkit.magicsigns.locks.PlayerLock;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

/**
 * This is the parent class for every magic sign. Subclasses of MagicSign must
 * override the static {@link #takeAction(Sign, String[])} method (which throws
 * an {@link Exception} in the default implementation) and should override
 * {@link #onRightClick(PlayerInteractEvent)}. For magic signs that require
 * configuration, they can override {@link #loadConfig(ConfigurationSection)}.
 * 
 * @see AnnotationConfiguration ConfigurationBase for a simple base
 *      configuration class.
 */
public abstract class MagicSign {

	public static Configuration getConfig() {
		return null;
	}

	private final BlockLocation location;
	private final String[] lines;
	private Lock lock = null;
	private Map<String, PlayerLock> playerLocks = null;

	/**
	 * Create a new instance of the MagicSign
	 * 
	 * @param sign
	 * @param lines
	 * @throws InvalidSignException
	 */
	public MagicSign(BlockLocation location, String[] lines) throws InvalidSignException {
		this.location = location;
		this.lines = lines;
	}

	public String getDescription() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);
		return signInfo != null ? signInfo.description() : null;
	}

	public String getFriendlyName() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);
		return signInfo != null ? signInfo.friendlyName() : null;
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
	public BlockLocation getLocation() {
		return location;
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
			}
			PlayerLock lock = new PlayerLock(this.lock);
			playerLocks.put(p.getName(), lock);
			return lock;
		}
		playerLocks = new HashMap<String, PlayerLock>();
		PlayerLock lock = new PlayerLock(this.lock);
		playerLocks.put(p.getName(), lock);
		return lock;
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
	 * Get the use permission for this {@link MagicSign}.
	 */
	public String getUsePermission() {
		MagicSignInfo signInfo = getClass().getAnnotation(MagicSignInfo.class);
		return signInfo != null ? signInfo.usePerm() : null;
	}

	/**
	 * Returns if this sign is masked (the sign shows a different text than this
	 * MagicSign uses for its work).
	 * 
	 * <p>
	 * <b>Always returns true if the world/chunk is not loaded!</b>
	 * 
	 * @return True if its masked, else false.
	 */
	public boolean isMasked() {
		Block block = getLocation().getBlockAt();
		if (block == null || block.getChunk() == null || !block.getChunk().isLoaded())
			return true;
		Sign sign = (Sign) block.getState();
		String[] currentLines = sign.getLines();
		return !Arrays.equals(currentLines, getLines());
	}

	/**
	 * Called on every right click on the sign.
	 */
	public void onRightClick(PlayerInteractEvent event) {
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
