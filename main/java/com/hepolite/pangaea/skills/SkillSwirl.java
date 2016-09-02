package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillSwirl extends SkillCastTriggered
{
	public SkillSwirl()
	{
		super("Swirl", true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerSkill skill = event.getSkill();
		PlayerClass race = event.getRace();

		float damage = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".damage");
		float range = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".range");

		List<LivingEntity> entities = EntityHelper.getEntitiesInRange(player.getLocation(), range);
		for (LivingEntity entity : entities)
		{
			if (entity == player || !entity.getLocation().getBlock().isLiquid() || !EntityHelper.isPathClear(player, entity))
				continue;
			entity.damage(damage, player);
		}

		// ParticleEffect.play(ParticleType.SNOW, player.getLocation(), 0.08f, 220, range);
		// ParticleEffect.play(ParticleType.EXPLOSION_NORMAL, player.getLocation(), 0.08f, 30, range);
		return true;
	}
}
