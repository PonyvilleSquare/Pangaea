package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.hepolite.pangaea.events.PlayerAirChangeEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillEndurance extends Skill
{
	public SkillEndurance()
	{
		super("Endurance");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerAirChange(PlayerAirChangeEvent event)
	{
		if (event.getNewAir() >= event.getOldAir())
			return;

		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null)
			return;

		float change = event.getNewAir() - event.getOldAir();
		float modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel());
		event.setNewAir(event.getOldAir() + change * Math.max(0.0f, 1.0f + modifier));
	}
}
