package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;

public class SkillAbsorb extends SkillProduce
{
	public SkillAbsorb()
	{
		super("Absorb");
	}

	@Override
	protected void onProduceGoods(Player player)
	{
		PlayerClass race = SkillAPIHelper.getRace(player);
		float mana = getSettings().getFloat(race.getData().getName() + "." + getName() + ".mana");
		SkillAPIHelper.giveMana(player, mana);
	}
}
