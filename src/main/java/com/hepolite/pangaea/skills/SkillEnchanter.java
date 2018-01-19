package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillEnchanter extends Skill
{
	public SkillEnchanter()
	{
		super("Enchanter");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemEnchanted(EnchantItemEvent event)
	{
		Player player = event.getEnchanter();
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null)
			return;
		// Change the 0 to 1 if the lowest cost should be 0 levels
		player.setLevel(player.getLevel() + Math.min(0 + event.whichButton(), skill.getLevel()));
	}
}
