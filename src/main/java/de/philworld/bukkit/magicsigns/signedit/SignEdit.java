package de.philworld.bukkit.magicsigns.signedit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.MagicSigns;
import de.philworld.bukkit.magicsigns.permissions.PermissionException;

public class SignEdit {

	public static final String UNMASK_PERMISSION = "magicsigns.edit.unmask";

	final MagicSigns plugin;

	/**
	 * This Map saves the location of the sign that contains the new content as
	 * the key. The target sign's location is the value.
	 * 
	 * <p>
	 * TempSign => TargetSign
	 */
	private final Map<Location, Location> editSigns = new HashMap<Location, Location>();
	private final PlayerEditMode editMode;
	private final SignEditListener listener = new SignEditListener(this);
	private final SignEditCommandExecutor cmdExecutor = new SignEditCommandExecutor(
			this);

	public SignEdit(MagicSigns plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager()
				.registerEvents(getListener(), plugin);

		editMode = new PlayerEditMode(new File(plugin.getDataFolder(),
				"edit-modes.db.yml"));
	}

	public void save() throws IOException {
		editMode.save();
	}

	/**
	 * Returns if this sign is just temporary to edit another sign.
	 * 
	 * @param sign
	 *            The block to check.
	 * @return True if this sign is temporary, else false
	 */
	public boolean isTempSign(Block sign) {
		return editSigns.containsKey(sign.getLocation());
	}

	/**
	 * Returns if this sign is currently edited.
	 * 
	 * @param sign
	 *            The block to check.
	 * @return True if this sign is currently edited, else false
	 */
	public boolean isEdited(Block sign) {
		return editSigns.containsValue(sign.getLocation());
	}

	/**
	 * Get the block that is edited by the given sign.
	 * 
	 * @param sign
	 *            The block to get the edited block of.
	 * @return The edited block if the given sign is a temporary sign, else
	 *         null.
	 */
	public Block getTargetBlock(Block sign) {
		Location edited = editSigns.get(sign.getLocation());
		return edited != null ? edited.getBlock() : null;
	}

	/**
	 * Get the temporary edit sign for this block.
	 * 
	 * @param sign
	 *            The block to get the temporary edit block for.
	 * @return The temporary sign if the sign is edited, else null.
	 */
	public Block getTempEditBlock(Block sign) {
		for (Entry<Location, Location> entry : editSigns.entrySet()) {
			if (entry.getValue().equals(sign.getLocation())) {
				return entry.getKey().getBlock();
			}
		}
		return null;
	}

	/**
	 * Sets the {@link EditMode} for this player.
	 * 
	 * @param p
	 *            The player
	 * @param mode
	 *            The {@link EditMode}
	 * @throws PermissionException
	 *             if the player has not enough permissions.
	 */
	public void setEditMode(Player p, EditMode mode) throws PermissionException {
		setEditMode(p, mode, true);
	}

	/**
	 * Sets the {@link EditMode} for this player.
	 * 
	 * @param p
	 *            The player
	 * @param mode
	 *            The {@link EditMode}
	 * @param checkPermissions
	 *            Whether to check if the player has enough permissions.
	 * @throws PermissionException
	 *             If the player has not enough permissions and
	 *             {@code checkPermissions} is set to true.
	 */
	public void setEditMode(Player p, EditMode mode, boolean checkPermissions)
			throws PermissionException {
		if (mode.hasPermission(p)) {
			editMode.setEditMode(p, mode);
		} else {
			throw new PermissionException();
		}
	}

	/**
	 * Get the {@link EditMode} for this player.
	 * 
	 * @param p
	 *            The player.
	 * @return The {@link EditMode} for this player.
	 */
	public EditMode getEditMode(Player p) {
		if (editMode.getEditMode(p) != null) {
			return editMode.getEditMode(p);
		}
		if (EditMode.AUTO.hasPermission(p)) {
			return EditMode.AUTO;
		} else if (EditMode.MASK_MAGIC_SIGNS.hasPermission(p)) {
			return EditMode.MASK_MAGIC_SIGNS;
		} else if (EditMode.MODIFY.hasPermission(p)) {
			return EditMode.MODIFY;
		} else {
			return EditMode.NONE;
		}
	}

	/**
	 * Registers a new temporary edit sign.
	 * 
	 * @param editSign
	 *            The temporary edit sign to edit the target.
	 * @param target
	 *            The targetSign that is edited by the editSign.
	 */
	void registerEditSign(Location editSign, Location target) {
		editSigns.put(editSign, target);
	}

	/**
	 * Unregisters a temporary edit sign.
	 * 
	 * @param loc
	 *            The location of the edit sign.
	 */
	void unregisterEditSign(Location loc) {
		editSigns.remove(loc);
	}

	/**
	 * @return the listener
	 */
	public SignEditListener getListener() {
		return listener;
	}

	/**
	 * @return the cmdExecutor
	 */
	public SignEditCommandExecutor getCmdExecutor() {
		return cmdExecutor;
	}

}
