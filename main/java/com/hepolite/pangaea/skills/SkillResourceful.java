package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillResourceful extends Skill
{
	public SkillResourceful()
	{
		super("Resourceful");
		setTickRate(SkillAPIHelper.getManaRegenFrequency());
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		double modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel());
		race.getPlayerData().giveMana(race.getData().getManaRegen() * modifier);
	}
}
