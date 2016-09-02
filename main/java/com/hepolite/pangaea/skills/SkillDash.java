package com.hepolite.pangaea.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillDash extends SkillMovementDash
{
	public SkillDash()
	{
		super("Dash", false);
	}

	@Override
	public void onTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		LivingEntity target = EntityHelper.getEntityInSight(player, 8.0f, player);
		if (target == null)
			return;

		double damage = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".damage");
		target.damage(damage, player);
		stop(player);
	}

	// Make it toggleable whether to stop at hitting a target or not?
	// Knock the enemy back

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		if (player.getPassenger() == null)
			return super.onCast(event);
		Chat.message(player, "&cYou can't do this when having someone on your back");
		return false;
	}
}
