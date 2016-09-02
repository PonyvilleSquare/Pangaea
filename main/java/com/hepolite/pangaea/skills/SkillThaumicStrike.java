package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import com.hepolite.pangaea.events.PlayerImpactGroundEvent;
import com.hepolite.pangaea.utility.ParticleEffect;
import com.hepolite.pangaea.utility.ParticleEffect.ParticleType;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillThaumicStrike extends SkillMovementDash
{
	public SkillThaumicStrike()
	{
		super("Thaumic Strike", false);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTakeDamage(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		PlayerData data = Database.getPlayerData(player);
		if (data.has(getName()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerHitGround(PlayerImpactGroundEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;
		PlayerData data = Database.getPlayerData(player);
		if (!data.has(getName()))
			return;

		double damage = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".damage");
		float range = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".range");
		List<LivingEntity> entities = EntityHelper.getEntitiesInRange(player.getLocation(), range);

		for (LivingEntity entity : entities)
		{
			if (entity == player)
				continue;
			entity.damage(damage, player);
		}
		ParticleEffect.play(ParticleType.SNOWBALL, player.getLocation(), 0.1f, 26, 1.5f);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 0.5f, -0.9f);
		stop(player);
	}
}
