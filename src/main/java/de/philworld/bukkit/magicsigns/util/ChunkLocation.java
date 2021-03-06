package de.philworld.bukkit.magicsigns.util;

import org.bukkit.Chunk;

public class ChunkLocation {

	@SuppressWarnings("unused")
	public static ChunkLocation fromLocation(String world, int x, int y, int z) {
		return new ChunkLocation(world, x >> 4, z >> 4);
	}

	public final String world;
	public final int x;
	public final int z;

	public ChunkLocation(Chunk chunk) {
		this.world = chunk.getWorld().getName();
		this.x = chunk.getX();
		this.z = chunk.getZ();
	}

	public ChunkLocation(String world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((world == null) ? 0 : world.hashCode());
		result = prime * result + x;
		result = prime * result + z;
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
		ChunkLocation other = (ChunkLocation) obj;
		if (world == null) {
			if (other.world != null)
				return false;
		} else if (!world.equals(other.world))
			return false;
		if (x != other.x)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

}
