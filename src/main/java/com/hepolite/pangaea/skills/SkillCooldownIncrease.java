package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.entity.Player;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillCooldownIncrease extends SkillCastTriggered
{
	public SkillCooldownIncrease(String name)
	{
		super(name, false);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();

		float cooldown = getSettings().getFloat(race.getData().getName() + "." + getName() + ".cooldown");
		List<String> skills = getSettings().getStringList(race.getData().getName() + "." + getName() + ".skills");
		for (String skillName : skills)
		{
			PlayerSkill current = SkillAPIHelper.getSkill(player, skillName);
			if (current != null)
				current.addCooldown(cooldown);
		}
		return true;
	}
}
