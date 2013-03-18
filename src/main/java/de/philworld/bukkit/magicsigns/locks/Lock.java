package de.philworld.bukkit.magicsigns.locks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * A lock for a certain object that saves the maximum uses and a lock period
 * between the uses.
 * 
 * <ul>
 * <li>If the period is zero, there is no delay between each usage.
 * <li>If maximum uses equals -1, there are unlimited usages.
 * </ul>
 * 
 * @see PlayerLock PlayerLock - Saves the lock data for each player.
 */
public class Lock implements ConfigurationSerializable {

	public static abstract class ConfigKeys {
		public static final String MAX_USES = "maxUses";
		public static final String PERIOD = "period";
	}

	public static Lock valueOf(Map<String, Object> data) {
		int period = (Integer) data.get(ConfigKeys.PERIOD);
		int maxUses = (Integer) data.get(ConfigKeys.MAX_USES);
		return new Lock(period, maxUses);
	}

	private final int maxUses;
	private final int period;

	/**
	 * Creates a Lock that does not allow usage at all.
	 */
	public Lock() {
		maxUses = 0;
		period = 0;
	}

	public Lock(int period, int maxUses) {
		this.period = period;
		this.maxUses = maxUses;
	}

	public int getMaxUses() {
		return maxUses;
	}

	public int getPeriod() {
		return period;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(ConfigKeys.PERIOD, period);
		data.put(ConfigKeys.MAX_USES, maxUses);
		return data;
	}

}
