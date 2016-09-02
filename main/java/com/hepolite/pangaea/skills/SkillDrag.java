package com.hepolite.pangaea.skills;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillDrag extends SkillCastTriggered
{
	public SkillDrag()
	{
		super("Drag", true);
		setTickRate(10);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);
		if (!data.has(getName()))
			return;

		LivingEntity target = (LivingEntity) data.get(getName());
		if (target != null && target.isValid())
		{
			Vector delta = player.getLocation().subtract(target.getLocation()).toVector();
			if (target instanceof Player)
				((Player) target).setFlying(false);
			target.setVelocity(delta.multiply(0.25f));
		}
		else
			data.remove(getName());
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();
		PlayerSkill skill = event.getSkill();

		LivingEntity target = EntityHelper.getEntityInSight(player, 5.0f, player);
		if (target == null)
		{
			Chat.message(player, "&cFound no targets in front of you");
			return false;
		}

		int duration = getSettings().getInt(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".duration");
		Database.getPlayerData(player).set(getName(), target, duration);
		return true;
	}
}
