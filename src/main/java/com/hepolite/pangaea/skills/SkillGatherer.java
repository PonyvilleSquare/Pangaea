package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.LootHelper;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillGatherer extends Skill
{
	public SkillGatherer()
	{
		super("Gatherer");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null)
			return;
		
		Block block = event.getBlock();
		Material type = block.getType();
		if (type != Material.LONG_GRASS)
			return;
		
		String path = SkillAPIHelper.getRaceName(player) + "." + getName();
		String section = LootHelper.getLootSection(getSettings(), path);
		ItemStack item = getSettings().getItem(path + "." + section);
		if (item == null)
			return;

		block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), item);
	}
}
