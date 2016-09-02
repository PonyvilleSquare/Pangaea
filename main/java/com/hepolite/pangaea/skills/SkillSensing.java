package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillSensing extends SkillCastTriggered
{
	public SkillSensing()
	{
		super("Sensing", false);
		setTickRate(10);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);
		if (data.has(getName()))
		{
			if (data.getLifetime(getName()) <= 10)
				Chat.message(player, "&cYou couldn't hear anything interesting...");
			else if (process(player, (float) data.get(getName())))
				data.remove(getName());
		}
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		if (event.getArguments().size() < 2)
			return false;
		try
		{
			float range = Float.parseFloat(event.getArguments().get(0));
			int duration = Integer.parseInt(event.getArguments().get(1));
			Database.getPlayerData(event.getPlayer()).set(getName(), range, duration);
		}
		catch (Exception e)
		{
		}
		return true;
	}

	/** Processes the given player */
	private final boolean process(Player player, float range)
	{
		// Get all nearby players, and check if any of them are moving
		List<Player> players = EntityHelper.getPlayersInRange(player.getLocation(), range);
		for (Player p : players)
		{
			if (Pangaea.getInstance().getSkillManager().getPlayerVelocity(p).lengthSquared() > 0.007)
			{
				if (p == player)
					Chat.message(player, "&cYou heard yourself move");
				else
					Chat.message(player, "&cYou heard something move " + getDirection(player.getLocation(), p.getLocation()) + " you");
				return true;
			}
		}

		// Get all nearby entities, and check if any of them are moving
		List<LivingEntity> entities = EntityHelper.getEntitiesInRange(player.getLocation(), range);
		for (LivingEntity entity : entities)
		{
			if (entity instanceof Player)
				continue;
			if (entity.getVelocity().lengthSquared() != 0.0)
			{
				Chat.message(player, "&cYou heard something move " + getDirection(player.getLocation(), entity.getLocation()) + " you");
				return true;
			}
		}

		return false;
	}

	/** Returns a name for the direction from start to end; assumes start is the player's eye location */
	private final String getDirection(Location start, Location end)
	{
		Vector delta = end.subtract(start).toVector();
		int yaw = (int) (180.0 / Math.PI * Math.atan2(delta.getZ(), delta.getX()) - start.getYaw() + 90.0) % 360;
		if (yaw < -180)
			yaw += 360;
		if (yaw > 180)
			yaw -= 360;
		int pitch = (int) (180.0 / Math.PI * Math.atan2(delta.getY(), Math.sqrt(delta.getX() * delta.getX() + delta.getZ() * delta.getZ())));

		if (pitch > 60)
			return "above";
		else if (pitch < -60)
			return "below";
		else
		{
			String dir = "";

			if (Math.abs(yaw) < 60)
			{
				dir = "behind";
				if (Math.abs(yaw) > 30)
					dir += " and " + (yaw < 0 ? "right" : "left") + " of";
			}
			else if (Math.abs(yaw) > 120)
			{
				dir = "in front";
				if (Math.abs(yaw) < 150)
					dir += " and " + (yaw < 0 ? "right" : "left");
				dir += " of";
			}
			else
				dir = "to the " + (yaw < 0 ? "right" : "left") + " of";

			if (pitch > 30)
				dir += ", and slightly above,";
			else if (pitch < -30)
				dir += ", and slightly below,";
			return dir;
		}
	}
}
