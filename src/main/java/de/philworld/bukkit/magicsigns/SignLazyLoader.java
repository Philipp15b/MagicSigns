package de.philworld.bukkit.magicsigns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Chunk;

import de.philworld.bukkit.magicsigns.config.MagicSignSerializationProxy;
import de.philworld.bukkit.magicsigns.util.ChunkLocation;

/**
 * Provides chunk-based lazy loading for {@link MagicSignSerializationProxy}s.
 */
class SignLazyLoader {

	private final SignManager manager;
	private final Map<ChunkLocation, List<MagicSignSerializationProxy>> serialized = new HashMap<ChunkLocation, List<MagicSignSerializationProxy>>();

	public SignLazyLoader(SignManager manager, List<MagicSignSerializationProxy> items) {
		this.manager = manager;
		for (MagicSignSerializationProxy sign : items) {
			ChunkLocation pos = sign.getChunkVector();
			if (!serialized.containsKey(pos))
				serialized.put(pos, new ArrayList<MagicSignSerializationProxy>());
			serialized.get(pos).add(sign);
		}
	}

	public List<MagicSignSerializationProxy> getAllQueued() {
		List<MagicSignSerializationProxy> list = new ArrayList<MagicSignSerializationProxy>();
		for (List<MagicSignSerializationProxy> chunked : serialized.values()) {
			list.addAll(chunked);
		}
		return list;
	}

	public void loadChunk(Chunk chunk) {
		ChunkLocation pos = new ChunkLocation(chunk);
		List<MagicSignSerializationProxy> proxies = serialized.get(pos);
		if (proxies == null)
			return;
		for (MagicSignSerializationProxy proxy : proxies) {
			try {
				manager.registerSign(proxy.getMagicSign());
			} catch (Exception e) {
				MagicSigns.inst().getLogger().log(Level.SEVERE, "Unable to load MagicSign!", e);
			}
		}
		serialized.remove(pos);
	}

}
