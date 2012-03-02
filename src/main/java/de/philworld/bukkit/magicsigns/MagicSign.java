package de.philworld.bukkit.magicsigns;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.config.MagicSignSerializationProxy;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;

/**
 * Parent class for every magic sign. Uses the Behavior pattern.
 */
public abstract class MagicSign {

	public Block sign;

	/**
	 * <i><b>Important:</b> Must be overridden!</i>
	 */
	public static boolean takeAction(Sign sign, String[] lines) {
		throw new UnsupportedOperationException();
	}

	public MagicSign(Block sign, String[] lines) throws InvalidSignException {
		this.sign = sign;
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

	public MagicSignSerializationProxy serialize() {
		return new MagicSignSerializationProxy(this);
	}

	public Location getLocation() {
		return sign.getLocation();
	}

}
