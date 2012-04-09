package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.MSMsg;

/**
 * Adds a specific amount of levels to a player.
 *
 */
@MagicSignInfo(
		friendlyName = "Level sign",
		description = "Adds a specific amount of levels to a player.",
		buildPerm = "magicsigns.level.create",
		usePerm = "magicsigns.level.use")
public class LevelSign extends PurchasableMagicSign {

	public static boolean takeAction(Block sign, String[] lines) {
		return lines[0].equalsIgnoreCase("[Level]");
	}

	private int additionalLevels = 1;

	public LevelSign(Block sign, String[] lines) throws InvalidSignException {
		super(sign, lines);

		if (!lines[1].isEmpty()) {
			additionalLevels = new Integer(lines[1]);
		}
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		p.setLevel(p.getLevel() + additionalLevels);
		MSMsg.LEVEL_ADDED.send(p, "" + additionalLevels);
	}

}
