package de.philworld.bukkit.magicsigns.signs;

import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerInteractEvent;

import de.philworld.bukkit.magicsigns.InvalidSignException;
import de.philworld.bukkit.magicsigns.MagicSignInfo;
import de.philworld.bukkit.magicsigns.util.BlockLocation;

@MagicSignInfo(
		name = "Survival",
		friendlyName = "Survival Mode sign",
		description = "A sign that sets the player's game mode to Creative Mode.",
		buildPerm = "magicsigns.survival.create",
		usePerm = "magicsigns.survival.use")
public class SurvivalModeSign extends PurchasableMagicSign {

	public SurvivalModeSign(BlockLocation location, String[] lines)
			throws InvalidSignException {
		super(location, lines);
	}

	@Override
	public void onRightClick(PlayerInteractEvent event) {
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
	}

}
