package de.philworld.bukkit.magicsigns.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * A block location with the world name instead of a world instance.
 */
public class BlockLocation {

	public final String world;
	public final int x;
	public final int y;
	public final int z;

	public BlockLocation(String world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockLocation(World world, int x, int y, int z) {
		this.world = world.getName();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockLocation(Location loc) {
		this(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public World getWorld() {
		return Bukkit.getServer().getWorld(world);
	}

	public Location toLocation() {
		World w = getWorld();
		if (w == null)
			return null;
		return new Location(w, x, y, z);
	}

	public Block getBlockAt() {
		World w = getWorld();
		if (w == null)
			return null;
		return w.getBlockAt(x, y, z);
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + ((world == null) ? 0 : world.hashCode());
		result = 31 * result + x;
		result = 31 * result + y;
		result = 31 * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockLocation other = (BlockLocation) obj;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		if (x != other.x || y != other.y || z != other.z)
			return false;
		return true;
	}

}
