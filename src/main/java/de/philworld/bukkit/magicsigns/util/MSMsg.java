package de.philworld.bukkit.magicsigns.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum MSMsg {

	NO_PERMISSION(ChatColor.RED + "You don't have permission to do this."),
	SIGN_CREATED(ChatColor.GREEN + "Your new Magic Sign has been created!"),
	HEAL_SUCCESS(ChatColor.GREEN + "You have been healed. Do you already feel refreshed?"),
	ROCKETED(ChatColor.GREEN + "Whoosh!"),
	FEED_SUCCESS(ChatColor.GREEN + "Nom nom nom nom..."),
	LEVEL_ADDED(ChatColor.GREEN + "You have been taught % levels."),
	TELEPORT_SUCCESS(ChatColor.GREEN + "You have been teleported."),
	INVENTORY_CLEARED(ChatColor.GREEN + "Your inventory has been cleared."),
	NOT_ENOUGH_MONEY("You can't pay this sign. Make sure you have %."),
	PAID_SIGN("You paid % to use this sign."),
	POINT_AT_SIGN(ChatColor.RED + "Please point at a sign!"),
	NOT_MAGIC_SIGN(ChatColor.GREEN + "This is just a normal sign!");

	private String msg;

	private MSMsg(String msg) {
		this.msg = msg;
	}

	public String get() {
		return msg;
	}

	public String get(String s) {
		return msg.replace("%", s);
	}

	public void send(CommandSender sender) {
		sender.sendMessage(msg);
	}

	public void send(CommandSender sender, String s) {
		sender.sendMessage(get(s));
	}

	/**
	 * Returns the message.
	 * 
	 * @returns the content of this message.
	 */
	@Override
	public String toString() {
		return get();
	}

}
