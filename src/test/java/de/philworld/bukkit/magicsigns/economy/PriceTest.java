package de.philworld.bukkit.magicsigns.economy;

import static org.junit.Assert.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

public class PriceTest {

	@Test
	public void testVaultEconomyPrice() {
		Price price = Price.valueOf("255.54");
		assertFalse(price.isFree());
		assertTrue(price instanceof Price.VaultEconomy);
		Price.VaultEconomy vprice = (Price.VaultEconomy) price;
		assertEquals(255.54, vprice.getPrice(), 0);

		vprice = (Price.VaultEconomy) Price.valueOf("0");
		assertTrue(vprice.isFree());
		assertEquals(0, vprice.getPrice(), 0);
	}

	@Test
	public void testInvalidVaultEconomyPrice() {
		try {
			Price.valueOf("hello!!");
			fail("Invalid price should throw an exception!");
		} catch (IllegalArgumentException e) {
			assertEquals("Make sure to insert a real price! Example: 23.5", e.getMessage());
		}
	}

	@Test
	public void testItemPrice() {
		// price without amount
		Price price = Price.valueOf("i:cobblestone");
		assertFalse(price.isFree());
		assertTrue(price instanceof Price.Item);
		Price.Item iprice = (Price.Item) price;
		ItemStack stack = iprice.getItems();
		assertEquals(Material.COBBLESTONE, stack.getType());
		assertEquals(1, stack.getAmount());

		// price with amount
		iprice = (Price.Item) Price.valueOf("i:milkbucket:63");
		assertFalse(price.isFree());
		stack = iprice.getItems();
		assertEquals(Material.MILK_BUCKET, stack.getType());
		assertEquals(63, stack.getAmount());

		// price with amount of zero
		iprice = (Price.Item) Price.valueOf("i:milkbucket:0");
		assertTrue(iprice.isFree());
		stack = iprice.getItems();
		assertEquals(Material.MILK_BUCKET, stack.getType());
		assertEquals(0, stack.getAmount());
	}

	@Test
	public void testInvalidItemPrice() {
		try {
			Price.valueOf("i:cobblestone:abc");
			fail("Invalid item price should throw exception!");
		} catch (IllegalArgumentException e) {
			assertEquals("The amount is not a number! Please insert a valid number.", e.getMessage());
		}

		try {
			Price.valueOf("i:thisitemwillnotexist:64");
			fail("Invalid item price should throw exception!");
		} catch (IllegalArgumentException e) {
			assertEquals("Could not find material!", e.getMessage());
		}
	}

	@Test
	public void testLevelPrice() {
		Price price = Price.valueOf("lvl:25");
		assertFalse(price.isFree());
		assertTrue(price instanceof Price.Level);
		Price.Level lprice = (Price.Level) price;
		assertEquals(25, lprice.getLevel());

		lprice = (Price.Level) Price.valueOf("lvl:0");
		assertTrue(lprice.isFree());
		assertEquals(0, lprice.getLevel());
	}

	@Test
	public void testInvalidLevelPrice() {
		try {
			Price.valueOf("lvl:0abc");
			fail("Invalid price should  throw an exception!");
		} catch (IllegalArgumentException e) {
			assertEquals("Make sure the level is a real number!", e.getMessage());
		}
	}

}
