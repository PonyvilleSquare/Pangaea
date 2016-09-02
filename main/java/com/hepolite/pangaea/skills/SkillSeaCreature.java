package com.hepolite.pangaea.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.entities.EntityFish;
import com.hepolite.pillar.settings.Settings;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillSeaCreature extends SkillMovement
{
	public SkillSeaCreature()
	{
		super("Sea Creature");
		setTickRate(1200);
	}

	@Override
	protected float getModifier(Player player, String group)
	{
		if (player.getEyeLocation().getBlock().isLiquid())
			return super.getModifier(player, "Water." + group);
		return super.getModifier(player, "Air." + group);
	}

	/** Handle the spawning of fish in the vicinty of the player */
	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		Settings settings = Pangaea.getInstance().getEntityManager().getSettings();
		int min = settings.getInt("Fish.minSpawn");
		int max = settings.getInt("Fish.maxSpawn");
		int repeats = min + (max - min > 0 ? random.nextInt(max - min) : 0);
		for (int i = 0; i < repeats; i++)
		{
			double px = 32.0 * random.nextDouble();
			double py = 8.0 * random.nextDouble() - 4.0;
			double pz = 32.0 - px;
			Location location = player.getLocation().add(px - 16.0, py, pz - 16.0);
			boolean valid = true;
			check: for (int x = -2; x <= 2; x++)
				for (int y = -2; y <= 2; y++)
					for (int z = -2; z <= 2; z++)
						if (!location.getBlock().getRelative(x, y, z).isLiquid())
						{
							valid = false;
							break check;
						}
			if (!valid)
				continue;

			EntityFish fish = new EntityFish();
			Pangaea.getInstance().getEntityManager().spawnEntity(fish, location);
		}
	}
}
