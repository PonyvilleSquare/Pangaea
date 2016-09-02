package com.hepolite.pangaea.skills;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillDamageResistance extends Skill
{
	public SkillDamageResistance(final String name)
	{
		super(name);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTakeDamage(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		Player player = (Player) event.getEntity();

		float modifier = getModifier(player);
		if (modifier != 0.0f)
			event.setDamage(Math.max(0.0, event.getDamage() * (1.0f - modifier)));
	}

	/** Returns the modifier for the damage taken */
	protected float getModifier(Player player)
	{
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null)
			return 0.0f;
		return getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel());
	}
}
