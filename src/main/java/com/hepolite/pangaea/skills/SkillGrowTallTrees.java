package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SkillGrowTallTrees extends Skill
{
	public SkillGrowTallTrees()
	{
		super("Grow Tall Trees");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || event.getAction() != Action.RIGHT_CLICK_BLOCK || item.getType() != Material.INK_SACK || item.getDurability() != (short) 15)
			return;
		Material material = event.getClickedBlock().getType();
		if (material != Material.SAPLING)
			return;

		if (player.isSneaking() && grow(event.getClickedBlock()))
			player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
	}

	/** Grows a tree from a sapling */
	@SuppressWarnings("deprecation")
	private final boolean grow(Block block)
	{
		TreeType type;
		switch (block.getData())
		{
		case 0:
			type = TreeType.BIG_TREE;
			break;
		case 1:
			type = TreeType.TALL_REDWOOD;
			break;
		case 2:
			type = TreeType.TALL_BIRCH;
			break;
		case 3:
			type = TreeType.JUNGLE;
			break;
		default:
			type = null;
		}

		if (type == null)
			return false;
		return block.getWorld().generateTree(block.getLocation(), type);
	}
}
