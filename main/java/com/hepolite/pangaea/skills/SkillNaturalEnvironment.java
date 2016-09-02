package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.hunger.HungerNode;
import com.hepolite.pangaea.hunger.HungerSettings;
import com.hepolite.pillar.utility.Damager;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillNaturalEnvironment extends Skill
{
	public SkillNaturalEnvironment()
	{
		super("Natural Environment");
		setTickRate(100);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		if (player.getHealth() >= player.getMaxHealth())
			return;
		if (!player.getLocation().getBlock().isLiquid() || !player.getEyeLocation().getBlock().isLiquid())
			return;

		float cost = getSettings().getFloat(race.getData().getName() + "." + getName() + ".cost");
		HungerNode node = ((HungerSettings) Pangaea.getInstance().getHungerManager().getSettings()).getNode(player);
		if (node.hunger + node.saturation < cost)
			return;

		float heal = getSettings().getFloat(race.getData().getName() + "." + getName() + ".heal");
		Damager.doHeal(heal, player, RegainReason.CUSTOM);
		Pangaea.getInstance().getHungerManager().changeSaturation(player, -cost);
	}
}
