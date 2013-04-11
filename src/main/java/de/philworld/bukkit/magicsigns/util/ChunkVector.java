package de.philworld.bukkit.magicsigns.util;

import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkVector {

	public final int x;
	public final int z;
	public final World world;

	public ChunkVector(Chunk chunk) {
		x = chunk.getX();
		z = chunk.getZ();
		world = chunk.getWorld();
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
		ChunkVector other = (ChunkVector) obj;
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
