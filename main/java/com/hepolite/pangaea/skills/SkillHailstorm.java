package com.hepolite.pangaea.skills;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.utility.ParticleEffect;
import com.hepolite.pangaea.utility.ParticleEffect.ParticleType;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillHailstorm extends SkillProduce
{
	public SkillHailstorm()
	{
		super("Hailstorm");
		setTickRate(1);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);
		if (!data.has(getName()))
			return;

		if (tickNumber % 20 == 0)
			handleDamage(player, race, skill);
		handleVisuals(player, race, skill, tickNumber);
	}

	@Override
	protected void onProduceGoods(Player player)
	{
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null)
			return;

		int duration = getSettings().getInt(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".duration");
		Database.getPlayerData(player).set(getName(), true, duration);
	}

	/** Processes the damage of the hailstorm */
	private final void handleDamage(Player player, PlayerClass race, PlayerSkill skill)
	{
		float radius = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".radius");
		float damage = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".damage");
		for (LivingEntity entity : EntityHelper.getEntitiesInRange(player.getLocation(), radius))
		{
			if (player != entity)
				entity.damage(damage, player);
		}
	}

	/** Handles the visuals of the hailstorm */
	private final void handleVisuals(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		if (tickNumber % 11 == 0)
			player.getWorld().playSound(player.getLocation(), Sound.WEATHER_RAIN, 0.35f, 2.0f);
		float radius = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".radius");
		ParticleEffect.play(ParticleType.SNOWBALL, player.getLocation(), 0.05f, 16, radius);
	}
}
