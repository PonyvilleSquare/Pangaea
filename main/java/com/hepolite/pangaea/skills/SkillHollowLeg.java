package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.hepolite.pangaea.events.PlayerHungerUpdateMaxEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillHollowLeg extends Skill
{
	public SkillHollowLeg()
	{
		super("Hollow Leg");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerEat(PlayerHungerUpdateMaxEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null)
			return;

		float modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel());
		event.setMaxHunger(event.getMaxHunger() + modifier);
	}
}
