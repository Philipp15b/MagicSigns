package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.permissions.BuildPermission;
import de.philworld.bukkit.magicsigns.permissions.UsePermission;

@BuildPermission("magicsigns.creative.create")
@UsePermission("magicsigns.creative.use")
public class CreativeModeSign extends PurchasableMagicSign {

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Creative]");
	}

	public CreativeModeSign(Block sign, String[] lines)
			throws InvalidSignException {
		super(sign, lines);
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().setGameMode(GameMode.CREATIVE);
	}

}
