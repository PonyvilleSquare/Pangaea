package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;

public class SkillLeechGroup extends SkillCastTriggered
{
	public SkillLeechGroup()
	{
		super("Leech Group", true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();

		float range = getSettings().getFloat(race.getData().getName() + "." + getName() + ".range");
		float radius = getSettings().getFloat(race.getData().getName() + "." + getName() + ".radius");
		LivingEntity target = EntityHelper.getEntityInSight(player, range, player);
		if (target == null)
			return false;

		List<LivingEntity> entities = EntityHelper.getEntitiesInRange(target.getLocation(), radius);
		entities.remove(player);
		if (entities.size() == 0)
			return false;

		for (LivingEntity entity : entities)
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1, -2));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 1, entities.size() - 1));
		return true;
	}
}
