package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.hunger.HungerManager;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillAmbientFeeding extends Skill
{
	public SkillAmbientFeeding()
	{
		super("Ambient Feeding");
		setTickRate(600);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		float range = getSettings().getFloat(race.getData().getName() + "." + getName() + ".range");
		List<LivingEntity> nearbyEntities = EntityHelper.getEntitiesInRange(player.getLocation(), range);
		nearbyEntities.remove(player);
		if (nearbyEntities.size() == 0)
			return;

		// Can't drain from Changelings, but all other entities are fine
		LivingEntity entity = nearbyEntities.get(random.nextInt(nearbyEntities.size()));
		if (entity instanceof Player)
		{
			PlayerClass targetRace = SkillAPIHelper.getRace((Player) entity);
			if (targetRace == null || targetRace.getData().getName().equalsIgnoreCase("Changeling"))
				return;
		}

		float amount = getSettings().getFloat(race.getData().getName() + "." + getName() + ".amount");
		HungerManager manager = Pangaea.getInstance().getHungerManager();
		manager.changeHunger(player, amount);
		manager.changeSaturation(player, amount);
	}
}
