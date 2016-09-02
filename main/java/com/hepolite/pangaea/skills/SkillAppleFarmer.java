package com.hepolite.pangaea.skills;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.settings.Settings;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillAppleFarmer extends Skill
{
	private final HashMap<Location, AppleTree> appleTrees = new HashMap<Location, AppleTree>();

	private int timer = 0;

	public SkillAppleFarmer()
	{
		super("Apple Farmer");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (item == null || item.getType() == Material.AIR)
			examine(player, event.getClickedBlock().getLocation());
		else
		{
			if (item.getType() == Material.APPLE)
			{
				Material material = event.getClickedBlock().getType();
				if (material != Material.GRASS && material != Material.DIRT)
					return;

				if (grow(player, event.getClickedBlock().getRelative(0, 1, 0)))
					player.getInventory().removeItem(new ItemStack(Material.APPLE, 1));
			}
			else if (item.getType() == Material.INK_SACK && item.getDurability() == (short) 15)
			{
				if (fertilize(player, event.getClickedBlock().getLocation()))
					player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
			}
		}
	}

	@Override
	public void onTick()
	{
		if (timer++ % 1200 != 0)
			return;

		List<Location> locationsToRemove = new LinkedList<Location>();
		for (Entry<Location, AppleTree> entry : appleTrees.entrySet())
		{
			Location location = entry.getKey();
			if (location == null || location.getWorld() == null)
			{
				locationsToRemove.add(location);
				continue;
			}
			if (!location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4))
				continue;

			if (location.getBlock().getType() != Material.LOG)
				locationsToRemove.add(location);
			else
				drop(entry.getKey(), entry.getValue());
		}
		for (Location location : locationsToRemove)
		{
			appleTrees.remove(location);
			getData().set(getName() + "." + getData().writeSimpleLocation(location), null);
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
			float yield = settings.getFloat(path + "yield");
			float fertilizer = settings.getFloat(path + "fertilizer");
			appleTrees.put(location, new AppleTree(yield, fertilizer));
		}
	}

	@Override
	public void onSave(Settings settings)
	{
		for (Entry<Location, AppleTree> entry : appleTrees.entrySet())
		{
			String path = getName() + "." + settings.writeSimpleLocation(entry.getKey()) + ".";
			settings.set(path + "yield", entry.getValue().yield);
			settings.set(path + "fertilizer", entry.getValue().fertilizer);
		}
	}

	// //////////////////////////////////////////////////////////

	/** Grows apple trees at the given location */
	private final boolean grow(Player player, Block block)
	{
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null)
			return false;

		if (!block.getWorld().generateTree(block.getLocation(), TreeType.BIG_TREE))
			return false;

		float minYield = getSettings().getFloat(race.getData().getName() + "." + getName() + ".minYield");
		float maxYield = getSettings().getFloat(race.getData().getName() + "." + getName() + ".maxYield");
		float yield = minYield + random.nextFloat() * (maxYield - minYield);

		appleTrees.put(block.getLocation(), new AppleTree(yield, 0.0f));
		return true;
	}

	/** Fertilizes the tree at the given location */
	private final boolean fertilize(Player player, Location location)
	{
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null)
			return false;
		AppleTree tree = appleTrees.get(location);
		if (tree == null || tree.fertilizer >= 0.99f)
			return false;

		float fertilizer = getSettings().getFloat(race.getData().getName() + "." + getName() + ".fertilizer");
		tree.fertilizer = Math.min(1.0f, tree.fertilizer + fertilizer);
		return true;
	}

	/** Examines the tree at the given location */
	private final void examine(Player player, Location location)
	{
		AppleTree tree = appleTrees.get(location);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || tree == null)
			return;

		if (tree.fertilizer <= 0.1f)
			Chat.message(player, String.format("Yield: &b%.0f%%&f &fFertilizer: &c%.0f%%&f", tree.yield * 10000.0f, tree.fertilizer * 100.0f));
		else
			Chat.message(player, String.format("Yield: &b%.0f%%&f &fFertilizer: &b%.0f%%&f", tree.yield * 10000.0f, tree.fertilizer * 100.0f));
	}

	/** Performs a drop at the specified location */
	private final void drop(Location location, AppleTree tree)
	{
		float yield = tree.yield * (1.0f + tree.fertilizer);

		// Find a few nearby leaf blocks
		for (int x = -3; x <= 3; x++)
			for (int z = -3; z <= 3; z++)
				for (int y = 1; y <= 8; y++)
				{
					Block block = location.getBlock().getRelative(x, y, z);
					if (block.getType() == Material.LEAVES && random.nextFloat() < yield)
					{
						tree.fertilizer *= 0.95f;
						block.getWorld().dropItemNaturally(block.getLocation().add(0.5, -1.5, 0.5), new ItemStack(Material.APPLE, 1));
						break;
					}
				}
	}

	/** Stores information about each individual tree */
	public final static class AppleTree
	{
		public float yield = 1.0f;			// Amount multiplier for drops
		public float fertilizer = 0.0f;		// How productive the tree is

		public AppleTree(float yield, float fertilizer)
		{
			this.yield = yield;
			this.fertilizer = fertilizer;
		}
	}
}
