package de.philworld.bukkit.magicsigns.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import de.philworld.bukkit.magicsigns.MagicSign;

/**
 * Proxy for serializing a MagicSign.
 */
public class MagicSignSerializationProxy implements ConfigurationSerializable {

	private World world;
	private final int x;
	private final int y;
	private final int z;
	private final String type;

	public MagicSignSerializationProxy(MagicSign magicSign) {
		this.world = magicSign.getLocation().getWorld();
		this.x = magicSign.getLocation().getBlockX();
		this.y = magicSign.getLocation().getBlockY();
		this.z = magicSign.getLocation().getBlockZ();
		this.type = magicSign.getClass().getName();
	}

	public MagicSignSerializationProxy(Map<String, Object> map) {
		this.world = Bukkit.getServer().getWorld((String) map.get("world"));
		this.x = (Integer) map.get("x");
		this.y = (Integer) map.get("y");
		this.z = (Integer) map.get("z");
		this.type = (String) map.get("type");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("world", this.world.getName());
		map.put("x", this.x);
		map.put("y", this.y);
		map.put("z", this.z);
		map.put("type", this.type);
		return map;
	}

	public static MagicSignSerializationProxy deserialize(
			Map<String, Object> map) {
		return new MagicSignSerializationProxy(map);
	}

	/**
	 * Get the Magic Sign behind this proxy.
	 * 
	 * @return MagicSign
	 * @throws Throwable
	 *             - Error when instantiating new Magic Sign of this type.
	 */
	public MagicSign getMagicSign() throws Throwable {
		Location loc = new Location(this.world, this.x, this.y, this.z);
		Block block = this.world.getBlockAt(loc);

		if (block.getState() instanceof Sign) {
			Sign state = (Sign) block.getState();
			String[] lines = state.getLines();

			return (MagicSign) Class.forName(this.type)
					.getConstructor(Block.class, lines.getClass())
					.newInstance(block, lines);
		} else {
			throw new InvalidConfigException("No sign found at coordinates: X("
					+ block.getLocation().getBlockX() + ") Y("
					+ block.getLocation().getBlockY() + ") Z("
					+ block.getLocation().getBlockZ() + ") in world '"
					+ block.getLocation().getWorld().getName()
					+ "' ! The sign config will be deleted on server shutdown!");
		}
	}

}
