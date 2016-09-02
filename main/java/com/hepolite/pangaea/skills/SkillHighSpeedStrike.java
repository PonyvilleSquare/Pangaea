package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillHighSpeedStrike extends Skill
{
	public SkillHighSpeedStrike()
	{
		super("High Speed Strike");
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerGiveDamage(EntityDamageByEntityEvent event)
	{
		if (event.getCause() != DamageCause.ENTITY_ATTACK || !(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;

		double modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".modifier");
		double speed = Pangaea.getInstance().getSkillManager().getPlayerVelocity(player).length();
		event.setDamage(event.getDamage() * (1.0f + speed * modifier));
	}
}
