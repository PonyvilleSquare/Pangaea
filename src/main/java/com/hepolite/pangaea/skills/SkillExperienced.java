package com.hepolite.pangaea.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.utility.Damager;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillExperienced extends Skill
{
	public SkillExperienced()
	{
		super("Experienced");
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onDealDamage(EntityDamageByEntityEvent event)
	{
		if (!(event.getDamager() instanceof Player) || event.getCause() != DamageCause.ENTITY_ATTACK)
			return;
		Player player = (Player) event.getDamager();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;

		float modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel());
		double damage = event.getDamage() * modifier;
		event.setDamage(Math.max(0.0, event.getDamage() - damage));
		if (event.getEntity() instanceof LivingEntity && damage >= 0.0)
			Damager.doDamage(damage, (LivingEntity) event.getEntity(), player, DamageCause.CUSTOM);
	}
}
