package com.hepolite.pangaea.skills;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pangaea.utility.TeleportHelper;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillDimensionJump extends SkillCastTriggered
{
	public SkillDimensionJump(String name)
	{
		super(name, true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();
		PlayerSkill skill = event.getSkill();

		World surface = Bukkit.getWorld(getSettings().getString(race.getData().getName() + "." + getName() + ".overworld"));
		World nether = Bukkit.getWorld(getSettings().getString(race.getData().getName() + "." + getName() + ".nether"));
		if (surface == null || nether == null)
		{
			Chat.message(player, "&cERROR: Was unable to located the destination world. Please contact the administration to resolve this issue.");
			return false;
		}

		float radius = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".radius");
		List<LivingEntity> entities = new LinkedList<LivingEntity>();
		if (radius == 0.0f)
			entities.add(player);
		else
			entities = EntityHelper.getEntitiesInRange(player.getLocation(), radius);
		if (entities.size() == 0)
		{
			Chat.message(player, "&cDidn't you want to teleport even yourself...?");
			return false;
		}

		Location location = player.getLocation();
		Location destination = null;
		if (player.getWorld() == nether)
			destination = new Location(surface, location.getX() * 8.0, location.getY(), location.getZ() * 8.0);
		else
			destination = new Location(nether, location.getX() / 8.0, location.getY() / 2.0, location.getZ() / 8.0);
		destination = TeleportHelper.getSafeLocation(destination);

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 0.7f, 0.0f);
		for (LivingEntity entity : entities)
			entity.teleport(destination);
		player.getWorld().playSound(destination, Sound.ENTITY_ENDERMEN_TELEPORT, 0.7f, 0.0f);
		return true;
	}
}
