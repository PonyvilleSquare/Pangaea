package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillTendAnimals extends Skill
{
	public SkillTendAnimals()
	{
		super("Tend Animals");
		setTickRate(1200);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		float chance = getSettings().getFloat(race.getData().getName() + "." + getName() + ".chance");
		float range = getSettings().getFloat(race.getData().getName() + "." + getName() + ".range");
		int max = getSettings().getInt(race.getData().getName() + "." + getName() + ".max");
		drop(player.getLocation(), chance, range, max);
	}

	/** Drops loot from animals around the given location; returns true if something was dropped */
	@SuppressWarnings("deprecation")
	private final boolean drop(Location location, float chance, float range, int max)
	{
		int count = 0;
		List<LivingEntity> entities = EntityHelper.getEntitiesInRange(location, range);
		for (LivingEntity entity : entities)
		{
			if (chance < random.nextFloat())
				continue;
			count++;
			if (entity instanceof Chicken)
				entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.FEATHER, 1));
			else if (entity instanceof Cow)
				entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.LEATHER, 1));
			else if (entity instanceof Sheep)
				entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.WOOL, 1, ((Sheep) entity).getColor().getWoolData()));
			else
				count--;
			if (count >= max)
				break;
		}
		return count > 0;
	}
}
