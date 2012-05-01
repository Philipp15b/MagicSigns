package de.philworld.bukkit.magicsigns.permissions;


public class PermissionException extends Exception {
	private static final long serialVersionUID = 8335769240781472705L;

	public PermissionException() {
		super();
	}

	public PermissionException(String text) {
		super(text);
	}
}
