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
		player.setLevel(player.getLevel() + Math.min(1 + event.whichButton(), skill.getLevel()));
	}
}
