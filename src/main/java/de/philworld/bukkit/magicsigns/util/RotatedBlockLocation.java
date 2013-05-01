package de.philworld.bukkit.magicsigns.util;

import org.bukkit.Location;
import org.bukkit.World;

public class RotatedBlockLocation extends BlockLocation {

	public final int yaw;
	public final int pitch;

	public RotatedBlockLocation(String world, int x, int y, int z, int yaw, int pitch) {
		super(world, x, y, z);
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public Location toLocation() {
		World w = getWorld();
		if (w == null)
			return null;
		return new Location(getWorld(), x, y, z, yaw, pitch);
	}

}
