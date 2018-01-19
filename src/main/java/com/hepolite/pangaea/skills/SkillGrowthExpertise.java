package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillGrowthExpertise extends Skill
{
	public SkillGrowthExpertise()
	{
		super("Growth Expertise");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || event.getAction() != Action.RIGHT_CLICK_BLOCK || item.getType() != Material.INK_SACK || item.getDurability() != (short) 15)
			return;
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null)
			return;
		Material material = event.getClickedBlock().getType();
		if (material != Material.CACTUS && material != Material.SUGAR_CANE_BLOCK)
			return;

		if (grow(event.getClickedBlock()))
			player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
	}

	/** Grows a block of sugar cane or cactus from another block */
	private final boolean grow(Block block)
	{
		int count = 0;
		Block currentBlock = block;
		while (currentBlock.getType() == block.getType())
		{
			count++;
			currentBlock = currentBlock.getRelative(0, -1, 0);
		}
		currentBlock = block.getRelative(0, 1, 0);
		while (currentBlock.getType() == block.getType())
		{
			count++;
			currentBlock = currentBlock.getRelative(0, 1, 0);
		}

		// Perform the actual growth
		if (count <= 4 && currentBlock.getType() == Material.AIR)
		{
			currentBlock.setType(block.getType());
			return true;
		}
		return false;
	}
}
