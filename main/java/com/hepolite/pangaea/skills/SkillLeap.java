package com.hepolite.pangaea.skills;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillLeap extends SkillMovementDash
{
	public SkillLeap()
	{
		super("Leap", true);
	}

	@Override
	protected void onStart(Player player, PlayerClass race, PlayerSkill skill)
	{
		int protectionDuration = getSettings().getInt(race.getData().getName() + "." + getName() + ".protectionDuration");
		float protection = getSettings().getFloat(race.getData().getName() + "." + getName() + ".protection");
		Database.getPlayerData(player).set(getName() + ".modifier", protection, protectionDuration);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTakeDamage(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER || event.getCause() != DamageCause.FALL)
			return;
		Player player = (Player) event.getEntity();
		PlayerData data = Database.getPlayerData(player);
		if (!data.has(getName() + ".modifier"))
			return;

		float modifier = (float) data.get(getName() + ".modifier");
		event.setDamage(Math.max(0.0, event.getDamage() - modifier));
	}
}
