package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pangaea.utility.ParticleEffect;
import com.hepolite.pangaea.utility.ParticleEffect.ParticleType;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillSwirl extends SkillCastTriggered
{
	public SkillSwirl()
	{
		super("Swirl", true);
		setTickRate(1);
	}
	
	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);
		if (!data.has(getName()))
			return;

		Location location = player.getLocation();
		location.setYaw(location.getYaw() + 360.0f / 7.0f);
		player.teleport(location);
		player.setVelocity(Pangaea.getInstance().getSkillManager().getPlayerVelocity(player));
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
		
		Database.getPlayerData(event.getPlayer()).set(getName(), true, 7);

		ParticleEffect.play(ParticleType.SNOWBALL, player.getLocation(), 0.08f, 150, 0.5f * range);
		// ParticleEffect.play(ParticleType.EXPLOSION_NORMAL, player.getLocation(), 0.08f, 30, range);
		return true;
	}
}
