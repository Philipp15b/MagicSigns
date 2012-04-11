package de.philworld.bukkit.magicsigns.signedit;

import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.signs.MagicSign;

/**
 * Represents the mode signs (especially {@link MagicSign}s) are edited.
 */
public enum EditMode {

	/**
	 * This is a combination of {@link EditMode#MASK_MAGIC_SIGNS} and
	 * {@link EditMode#MODIFY}. This means {@link MagicSign}s will be masked and
	 * normal signs will be modified.
	 */
	AUTO("magicsigns.edit.auto"),

	/**
	 * Write an alternate value on {@link MagicSign}s, but keep them MagicSigns.
	 * Users will see the mask text, but the MagicSign will work as before.
	 *
	 * <p>
	 * Other signs wont be modified.
	 */
	MASK_MAGIC_SIGNS("magicsigns.edit.mask"),

	/**
	 * Modify all signs. {@link MagicSign}s as well as other signs will be
	 * <b>modified which is equal to destroy and recreate the sign</b> with new
	 * text.
	 */
	MODIFY("magicsigns.edit.modify"),

	/**
	 * Don't edit anything.
	 */
	NONE(null);

	private final String permission;

	EditMode(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

	public boolean hasPermission(Player p) {
		if (getPermission() != null) {
			return p.hasPermission(getPermission());
		} else {
			return true;
		}
	}

}
