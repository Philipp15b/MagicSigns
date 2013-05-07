package de.philworld.bukkit.magicsigns.permissions;

import org.bukkit.entity.Player;

import de.philworld.bukkit.magicsigns.MSMsg;

public class PermissionException extends Exception {
	private static final long serialVersionUID = 8335769240781472705L;

	public PermissionException() {
		super();
	}

	public PermissionException(String text) {
		super(text);
	}

	/**
	 * Returns the message of this Exception. If no one was defined, a default
	 * one is returned.
	 */
	@Override
	public String getMessage() {
		return super.getMessage() != null ? super.getMessage() : MSMsg.NO_PERMISSION.toString();
	}

	public void send(Player p) {
		p.sendMessage(getMessage());
	}
}
