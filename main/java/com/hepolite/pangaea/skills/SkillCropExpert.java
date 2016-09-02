package com.hepolite.pangaea.skills;

import java.util.ArrayList;
import java.util.List;

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

public class SkillCropExpert extends Skill
{
	public SkillCropExpert()
	{
		super("Crop Expert");
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
		if (material != Material.MELON_STEM && material != Material.PUMPKIN_STEM)
			return;

		if (grow(event.getClickedBlock()))
			player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
	}

	/** Grows a melon or pumpkin from a fully grown stem */
	@SuppressWarnings("deprecation")
	private final boolean grow(Block block)
	{
		if (block.getData() != 7)
			return false;

		Material type = (block.getType() == Material.MELON_STEM ? Material.MELON_BLOCK : Material.PUMPKIN);
		List<Block> blocks = new ArrayList<Block>();
		blocks.add(block.getRelative(1, 0, 0));
		blocks.add(block.getRelative(-1, 0, 0));
		blocks.add(block.getRelative(0, 0, 1));
		blocks.add(block.getRelative(0, 0, -1));
		while (!blocks.isEmpty())
		{
			Block currentBlock = blocks.remove(random.nextInt(blocks.size()));
			Material ground = currentBlock.getRelative(0, -1, 0).getType();
			if (currentBlock.getType() != Material.AIR || (ground != Material.GRASS && ground != Material.DIRT))
				continue;
			currentBlock.setType(type);
			return true;
		}
		return false;
	}
}
