package com.hepolite.pangaea.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerAllowFlightEvent;
import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pangaea.movement.IMovementModifier;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;

public class SkillHold extends SkillCastTriggered implements IMovementModifier
{
	public SkillHold()
	{
		super("Hold", true);
		setTickRate(10);
		Pangaea.getInstance().getMovementManager().registerModifier((IMovementModifier) this);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();

		float range = getSettings().getFloat(race.getData().getName() + "." + getName() + ".range");
		int duration = (int) (20.0f * getSettings().getFloat(race.getData().getName() + "." + getName() + ".duration"));
		LivingEntity entity = EntityHelper.getEntityInSight(player, range, player);
		if (entity == null)
		{
			Chat.message(player, ChatColor.RED + "There was no valid target in front of you!");
			return false;
		}

		if (entity instanceof Player)
		{
			Player target = (Player) entity;
			Database.getPlayerData(target).set(getName(), true, duration);
			Pangaea.getInstance().getMovementManager().forceUpdatePlayerSpeeds();
		}
		else
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 10, true), true);
		// Chat.message(player, ChatColor.RED + "There was no valid target in front of you!");

		return true;
	}

	@Override
	public float getGroundModifier(Player player)
	{
		return (Database.getPlayerData(player).has(getName()) ? -1000.0f : 0.0f);
	}

	@Override
	public float getFlightModifier(Player player)
	{
		return (Database.getPlayerData(player).has(getName()) ? -1000.0f : 0.0f);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerFlightCheck(PlayerAllowFlightEvent event)
	{
		if (Database.getPlayerData(event.getPlayer()).has(getName()))
			event.setCanFly(true);
	}

	@Override
	public void onTick()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (Database.getPlayerData(player).has(getName()))
			{
				player.setAllowFlight(true);
				player.setFlying(true);
			}
		}
	}
}
