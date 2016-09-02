package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillFarmer extends Skill
{
	public SkillFarmer()
	{
		super("Farmer");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (player.isSneaking() || skill == null || race == null)
			return;
		Block block = event.getBlock();
		Material material = block.getType();
		if (material != Material.POTATO && material != Material.CARROT && material != Material.CROPS && material != Material.BEETROOT_BLOCK)
			return;

		int size = skill.getLevel();
		for (int x = -size; x <= size; x++)
			for (int z = -size; z <= size; z++)
			{
				Block newBlock = block.getRelative(x, 0, z);
				Material current = newBlock.getType();
				if (current == Material.POTATO || current == Material.CARROT || current == Material.CROPS || current == Material.BEETROOT_BLOCK)
				{
					@SuppressWarnings("deprecation")
					Byte meta = newBlock.getData();
					if (meta == 7 || (meta == 3 && material == Material.BEETROOT_BLOCK)) // The meta of fully grown crops is 7 for normal crops, 3 for beets
					{
						newBlock.breakNaturally(player.getInventory().getItemInMainHand());
						newBlock.setType(Material.AIR);
					}
				}
			}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPlaceBlock(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (player.isSneaking() || skill == null || race == null)
			return;
		Block block = event.getBlock();
		Material material = block.getType();

		Material seeds = null;
		switch (material)
		{
		case CROPS:
			seeds = Material.SEEDS;
			break;
		case POTATO:
			seeds = Material.POTATO_ITEM;
			break;
		case CARROT:
			seeds = Material.CARROT_ITEM;
			break;
		case BEETROOT_BLOCK:
			seeds = Material.BEETROOT_SEEDS;
			break;
		default:
			return;
		}

		int size = skill.getLevel();
		for (int x = -size; x <= size; x++)
			for (int z = -size; z <= size; z++)
			{
				if (block.getRelative(x, -1, z).getType() != Material.SOIL || block.getRelative(x, 0, z).getType() != Material.AIR)
					continue;
				if (player.getInventory().contains(seeds))
				{
					block.getRelative(x, 0, z).setType(material);
					player.getInventory().removeItem(new ItemStack(seeds, 1));
				}
				else
					break;
			}
	}
}
