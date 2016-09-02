package com.hepolite.pangaea.skills;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.utility.Damager;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillThunder extends SkillCastTriggered
{
	public SkillThunder()
	{
		super("Thunder", false);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerSkill skill = event.getSkill();
		PlayerClass race = event.getRace();

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
		Damager.createLightningStrike(aimLocation, damage, radius, false);
		return false;
	}
}
