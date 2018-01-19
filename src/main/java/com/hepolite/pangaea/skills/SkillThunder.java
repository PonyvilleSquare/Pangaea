package com.hepolite.pangaea.skills;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.utility.Damager;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillThunder extends SkillProduce
{
	public SkillThunder()
	{
		super("Thunder");
	}

	@Override
	protected void onProduceGoods(Player player)
	{
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		PlayerClass race = SkillAPIHelper.getRace(player);

		int range = 125;
		Location aimLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(range));
		for (BlockIterator it = new BlockIterator(player.getEyeLocation(), 0.0, range); it.hasNext();)
		{
			Block block = it.next();
			if (block.getType().isSolid())
			{
				aimLocation = block.getLocation();
				break;
			}
		}
		LivingEntity target = EntityHelper.getEntityInSight(player.getEyeLocation(), aimLocation, player);
		if (target != null)
			aimLocation = target.getLocation();

		float damage = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".damage");
		float radius = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".radius");
		Damager.createLightningStrike(aimLocation, damage, radius, false, player);

		// Turn all nearby creepers to charged creepers
		for (LivingEntity entity : EntityHelper.getEntitiesInRange(aimLocation, radius))
		{
			if (entity instanceof Creeper)
				((Creeper) entity).setPowered(true);
		}
	}
}
