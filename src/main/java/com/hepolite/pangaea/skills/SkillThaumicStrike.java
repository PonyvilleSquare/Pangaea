package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

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
		// TODO: This particle effect isn't working
		//ParticleEffect.play(ParticleType.SNOWBALL, player.getLocation(), 0.1f, 30, 3.5f);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_LARGE_BLAST, 0.5f, -0.9f);
		player.setVelocity(new Vector());
		stop(player);
	}

	@Override
	protected void onTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);

		// Calculate the velocity of the player
		Vector velocity = (Vector) data.get(getName());
		double direction = Math.atan2(velocity.getZ(), velocity.getX());
		double zAngle = Math.atan2(velocity.getY(), Math.sqrt(velocity.getX() * velocity.getX() + velocity.getZ() * velocity.getZ()));

		float angle = getSettings().getFloat(race.getData().getName() + "." + getName() + ".angle") * (float) Math.PI / 180.0f;
		float factor = getSettings().getFloat(race.getData().getName() + "." + getName() + ".rate");
		float speed = getSettings().getFloat(race.getData().getName() + "." + getName() + ".speed");

		angle = Math.min((float) zAngle, angle);

		speed = (float) velocity.length() * (1.0f - factor) + speed * factor;
		double x = speed * Math.cos(direction) * Math.cos(angle);
		double y = speed * Math.sin(angle);
		double z = speed * Math.sin(direction) * Math.cos(angle);
		velocity = (velocity.multiply(1.0f - factor)).add((new Vector(x, y, z)).multiply(factor));

		data.set(getName(), velocity, data.getLifetime(getName()));
	}
}
