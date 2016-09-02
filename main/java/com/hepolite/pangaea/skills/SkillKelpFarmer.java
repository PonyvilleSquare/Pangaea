package com.hepolite.pangaea.skills;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.settings.Settings;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillKelpFarmer extends Skill
{
	private final HashMap<Location, Kelp> kelps = new HashMap<Location, Kelp>();
	private final ArrayList<Kelp> kelpList = new ArrayList<Kelp>();

	private int timer = 0;
	private int index = 0;

	public SkillKelpFarmer()
	{
		super("Kelp Farmer");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPlaceBlock(BlockPlaceEvent event)
	{
		Block block = event.getBlock();
		if (block.getType() != Material.SAPLING || !block.getRelative(0, 1, 0).isLiquid())
			return;
		Player player = event.getPlayer();
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null)
			return;

		Kelp kelp = generateKelp(block);
		if (kelp == null)
			return;
		float minChance = getSettings().getFloat("Generic." + getName() + ".minChance");
		float maxChance = getSettings().getFloat("Generic." + getName() + ".maxChance");
		kelp.spreadChance = minChance + random.nextFloat() * (maxChance - minChance);
		float minAmount = getSettings().getFloat("Generic." + getName() + ".minAmount");
		float maxAmount = getSettings().getFloat("Generic." + getName() + ".maxAmount");
		kelp.spreadAmount = Math.round(minAmount + random.nextFloat() * (maxAmount - minAmount));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		Location location = event.getBlock().getLocation();
		Kelp kelp = getKelp(location);
		if (kelp == null)
			return;
		dropKelp(kelp);
		removeKelp(kelp);
	}

	@Override
	public void onTick()
	{
		if (timer++ % 100 != 0)
			return;

		int count = Math.min(100, kelpList.size());
		while (--count >= 0)
		{
			if (++index >= kelpList.size())
				index = 0;
			Kelp kelp = kelpList.get(index);
			if (!kelp.isLoaded())
				continue;
			attemptSpread(kelpList.get(index));
		}
	}

	@Override
	public void onReload(Settings settings)
	{
		Set<String> keys = settings.getKeys(getName());
		for (String key : keys)
		{
			Location location = settings.parseSimpleLocation(key);
			if (location == null)
				continue;
			String path = getName() + "." + key + ".";

			Kelp kelp = new Kelp(location);
			kelp.spreadChance = settings.getFloat(path + "spreadChance");
			kelp.spreadAmount = settings.getInt(path + "spreadLeft");

			String[] children = settings.getString(path + "children").split("=");
			for (String child : children)
				kelp.addChild(Integer.parseInt(child));

			addKelp(kelp);
		}
	}

	@Override
	public void onSave(Settings settings)
	{
		for (Kelp kelp : kelpList)
		{
			String path = getName() + "." + settings.writeSimpleLocation(kelp.location) + ".";
			settings.set(path + "spreadChance", kelp.spreadChance);
			settings.set(path + "spreadLeft", kelp.spreadAmount);

			String children = "" + kelp.children.get(0);
			for (int i = 1; i < kelp.children.size(); i++)
				children += "=" + kelp.children.get(i);
			settings.set(path + "children", children);
		}
	}

	// //////////////////////////////////////////////////////////////

	/** Generates a piece of kelp at the specified location; returns null if the generation failed */
	private final Kelp generateKelp(Block block)
	{
		if (block.getRelative(0, -1, 0).getType() != Material.DIRT)
			return null;

		Collection<Vector> shape = generateKelpShape();
		if (!verifyKelpShape(block, shape))
			return null;
		return placeKelp(block, shape);
	}

	/** Generates the shape of kelp, returns a list containing the relative offset for kelp blocks */
	private final Collection<Vector> generateKelpShape()
	{
		int dx = random.nextBoolean() ? 1 : -1;
		int dz = random.nextBoolean() ? 1 : -1;
		int height = 7 + random.nextInt(8);
		float bendChance = 0.1f + 0.05f * random.nextFloat();

		Collection<Vector> kelpBlocks = new LinkedList<Vector>();
		int x = 0, z = 0;
		for (int i = 0; i < height; i++)
		{
			kelpBlocks.add(new Vector(x, i, z));
			if (random.nextFloat() < bendChance)
				x += dx;
			if (random.nextFloat() < bendChance)
				z += dz;
		}
		return kelpBlocks;
	}

	/** Checks that the given list of vectors are all valid blocks for kelp to generate; returns true if all positions are valid */
	private final boolean verifyKelpShape(Block origin, Collection<Vector> shape)
	{
		for (Vector vector : shape)
		{
			if (vector.getBlockX() == 0 && vector.getBlockY() == 0 && vector.getBlockZ() == 0)
				continue;
			Block block = origin.getRelative(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
			if (!block.isLiquid() || !block.getRelative(0, 1, 0).isLiquid())
				return false;
		}
		return true;
	}

	/** Actually generates the kelp structure from the given shape */
	@SuppressWarnings("deprecation")
	private final Kelp placeKelp(Block origin, Collection<Vector> shape)
	{
		Kelp kelp = new Kelp(origin.getLocation());
		for (Vector vector : shape)
		{
			Block block = origin.getRelative(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
			block.setType(Material.LEAVES);
			block.setData((byte) (4 + random.nextInt(2)));
			kelp.addChild(vector);
		}
		return addKelp(kelp);
	}

	/** Adds the given kelp to the system; returns the same kelp as proided */
	private final Kelp addKelp(Kelp kelp)
	{
		if (kelp == null)
			return null;

		kelpList.add(kelp);
		for (int index : kelp.children)
		{
			Location location = kelp.location.clone().add(kelp.getPositionFromIndex(index));
			kelps.put(location, kelp);
		}
		return kelp;
	}

	/** Removes the given kelp from the system */
	private final void removeKelp(Kelp kelp)
	{
		kelpList.remove(kelp);
		for (int index : kelp.children)
		{
			Location location = kelp.location.clone().add(kelp.getPositionFromIndex(index));
			if (location.getBlock().getType() == Material.LEAVES)
				location.getBlock().setType(Material.STATIONARY_WATER);
			kelps.remove(location);
		}
		getData().set(getName() + "." + getData().writeSimpleLocation(kelp.location), null);
	}

	/** Returns the kelp at the given location, if there is any there */
	private final Kelp getKelp(Location location)
	{
		return kelps.get(location);
	}

	/** Drops the kelp from the given kelp structure */
	private final void dropKelp(Kelp kelp)
	{
		ItemStack item = getSettings().getItem("Generic." + getName() + ".drop");
		item.setAmount(2 + random.nextInt(1 + kelp.children.size() / 4));
		kelp.location.getWorld().dropItemNaturally(kelp.location, item);
	}

	/** Attempts to spread the kelp to nearby regions */
	public final void attemptSpread(Kelp kelp)
	{
		if (random.nextFloat() >= kelp.spreadChance || kelp.spreadAmount <= 0)
			return;
		Kelp newKelp = generateKelp(findSpreadLocation(kelp).getBlock());
		if (newKelp == null)
			return;
		kelp.spreadAmount--;
		newKelp.spreadAmount = Math.max(0, kelp.spreadAmount - 1);
		newKelp.spreadChance = kelp.spreadChance;
	}

	/** Find a random position that is to be used to spread kelp to other places */
	private final Location findSpreadLocation(Kelp kelp)
	{
		int x = kelp.location.getBlockX() + random.nextInt(9) - 4;
		int y = kelp.location.getBlockY() + 4;
		int z = kelp.location.getBlockZ() + random.nextInt(9) - 4;
		Location location = new Location(kelp.location.getWorld(), x, y, z);

		Block block = location.getBlock();
		int count = 9;
		while (--count >= 0 && block.getRelative(0, -1, 0).isLiquid())
			block = block.getRelative(0, -1, 0);

		return block.getLocation();
	}

	// //////////////////////////////////////////////////////////////

	/** Useful structure to keep track of kelp */
	public final static class Kelp
	{
		public final Location location;
		public final ArrayList<Integer> children = new ArrayList<Integer>();
		public float spreadChance = 0.0f;
		public int spreadAmount = 0;

		public Kelp(Location location)
		{
			this.location = location.clone();
		}

		/** Adds another block as a child over the piece of kelp */
		public final void addChild(Vector offset)
		{
			addChild(getIndexFromPosition(offset));
		}

		/** Adds another block as a child over the piece of kelp */
		public final void addChild(int index)
		{
			children.add(index);
		}

		/** Calculates an index from a position; valid positions range from -127 to 128, inclusive */
		public final int getIndexFromPosition(Vector vector)
		{
			return (vector.getBlockX() + 127) << 16 | (vector.getBlockY() + 127) << 8 | (vector.getBlockZ() + 127);
		}

		/** Returns the position for the given position index */
		public final Vector getPositionFromIndex(int index)
		{
			int x = (index >> 16) - 127;
			int y = ((index >> 8) & 0xFF) - 127;
			int z = (index & 0xFF) - 127;
			return new Vector(x, y, z);
		}

		/** Returns true if the kelp is in a loaded chunk */
		public final boolean isLoaded()
		{
			return location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4);
		}
	}
}
