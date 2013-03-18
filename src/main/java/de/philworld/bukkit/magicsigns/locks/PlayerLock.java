package de.philworld.bukkit.magicsigns.locks;

import java.util.HashMap;
import java.util.Map;

/**
 * This saves the lock data for each player & object: how many uses are
 * remaining and on which time the object is usable again.
 */
public class PlayerLock {

	public static abstract class ConfigKeys {
		public static final String NEXT_ALLOWED_TIME = "nextAllowedTime";
		public static final String REMAINING_USES = "remainingUses";
	}

	public static PlayerLock valueOf(Map<String, Object> map, Lock lock) {
		long nextAllowedTime = Long.parseLong(((String) map
				.get(ConfigKeys.NEXT_ALLOWED_TIME)).split("LONG-")[1]);
		int period = (Integer) map.get(ConfigKeys.REMAINING_USES);
		return new PlayerLock(nextAllowedTime, period, lock);
	}

	private final Lock lock;
	private long nextAllowedTime = 0;
	private int remainingUses;

	public PlayerLock(Lock lock) {
		this.lock = lock;
		remainingUses = lock.getMaxUses();
	}

	public PlayerLock(long nextAllowedTime, int remainingUses, Lock lock) {
		this.nextAllowedTime = nextAllowedTime;
		this.remainingUses = remainingUses;
		this.lock = lock;
	}

	public Lock getLock() {
		return lock;
	}

	public long getNextAllowedTime() {
		return nextAllowedTime;
	}

	public int getRemainingUses() {
		return remainingUses;
	}

	/**
	 * Returns if this object is no longer necessary: The 'NextAllowedTime' has
	 * passed and remaining uses equal the maximum uses in the this object's
	 * {@link Lock}.
	 * 
	 * @param currentTime
	 *            The current server time
	 * @return If its obsolete, else false.
	 */
	public boolean isObsolete(long currentTime) {
		return nextAllowedTime <= currentTime
				&& remainingUses == lock.getMaxUses();
	}

	/**
	 * If the object is usable now.
	 * 
	 * @return If it's usable, else false.
	 */
	public boolean isUsable(long currentTime) {
		if (remainingUses == -1 || remainingUses > 0) {
			if (nextAllowedTime <= currentTime) {
				return true;
			}
		}
		return false;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> serialized = new HashMap<String, Object>();
		serialized.put(ConfigKeys.NEXT_ALLOWED_TIME, "LONG-" + nextAllowedTime);
		serialized.put(ConfigKeys.REMAINING_USES, remainingUses);
		return serialized;
	}

	public void setNextAllowedTick(long nextAllowedTick) {
		nextAllowedTime = nextAllowedTick;
	}

	public void setRemainingUses(int remainingUses) {
		this.remainingUses = remainingUses;
	}

	/**
	 * 'Touch' this object: Decrement the remaining usages and set the new next
	 * allowed tick.
	 * 
	 * @param currentTime
	 *            The current server time.
	 * @throws IllegalStateException
	 *             If {@link #isUsable(long)} returns false.
	 */
	public void touch(long currentTime) throws IllegalStateException {
		if (!isUsable(currentTime))
			throw new IllegalStateException(
					"This lock is currently not usable!");
		if (remainingUses != -1)
			--remainingUses;
		nextAllowedTime = currentTime + lock.getPeriod();
	}

	/**
	 * Returns a user friendly error message.
	 */
	public String getErrorMessage(long currentTime) {
		if (isUsable(currentTime))
			return null;

		String msg;
		if (remainingUses == 0) {
			msg = "You dont have any remaining uses!";
		} else if (currentTime < nextAllowedTime) {
			msg = "You have to wait " + (nextAllowedTime - currentTime)
					+ " seconds until you can use this again!";
		} else {
			msg = "Something weird happened.";
		}

		return msg;
	}

}
