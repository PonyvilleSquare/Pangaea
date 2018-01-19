package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillNaturalConnection extends Skill
{
	public SkillNaturalConnection()
	{
		super("Natural Connection");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMobSpawn(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() != SpawnReason.BREEDING)
			return;

		List<Player> nearbyPlayers = EntityHelper.getPlayersInRange(event.getLocation(), 15.0f);
		for (Player player : nearbyPlayers)
		{
			PlayerClass race = SkillAPIHelper.getRace(player);
			PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
			if (skill == null || race == null)
				continue;

			float chance = getSettings().getFloat(race.getData().getName() + "." + getName() + ".chance");
			int attempts = getSettings().getInt(race.getData().getName() + "." + getName() + ".attempts");
			spawn(event, chance, attempts);
		}
	}

	/** Spawns a few animals at the given location */
	private final void spawn(CreatureSpawnEvent event, float chance, int attempts)
	{
		for (int i = 0; i < attempts; i++)
		{
			if (random.nextFloat() < chance)
			{
				Entity child = event.getLocation().getWorld().spawnEntity(event.getLocation(), event.getEntityType());
				if (child instanceof Ageable)
					((Ageable) child).setBaby();
			}
		}
	}
}
