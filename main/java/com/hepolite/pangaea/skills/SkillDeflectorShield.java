package com.hepolite.pangaea.skills;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.database.Database;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillDeflectorShield extends Skill
{
	public SkillDeflectorShield()
	{
		super("Deflector Shield");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTakeDamage(EntityDamageByEntityEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		Player player = (Player) event.getEntity();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null || !Database.getPlayerData(player).has(getName()))
			return;

		float chance = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".chance");
		if (random.nextFloat() < chance && (event.getCause() == DamageCause.PROJECTILE || event.getDamager().getLocation().distance(player.getLocation()) > 8.0))
			event.setCancelled(true);
	}
}
