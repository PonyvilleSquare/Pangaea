package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillMovementDash extends SkillCastTriggered
{
	public SkillMovementDash(String name, boolean countAsCast)
	{
		super(name, countAsCast);
		setTickRate(1);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);
		if (data.has("Hold"))
			return;
		if (data.has(getName()))
		{
			if (data.getLifetime(getName()) == 1)
				onEnd(player);
			onTick(player, race, skill, tickNumber);
			Vector velocity = (Vector) data.get(getName());
			if (velocity != null)
				player.setVelocity(velocity);
		}
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();

		float speed = getSettings().getFloat(race.getData().getName() + "." + getName() + ".speed");
		int duration = getSettings().getInt(race.getData().getName() + "." + getName() + ".duration");
		PlayerData data = Database.getPlayerData(player);
		data.set(getName(), player.getLocation().getDirection().multiply(speed), duration);
		onStart(player, race, event.getSkill());
		return true;
	}

	/** Immediately terminates the dash */
	protected void stop(Player player)
	{
		PlayerData data = Database.getPlayerData(player);
		data.remove(getName());
		onEnd(player);
	}

	/** Invoked every tick while the movement dash is in use */
	protected void onTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
	}

	/** Invoked on the start of the dash */
	protected void onStart(Player player, PlayerClass race, PlayerSkill skill)
	{
	}

	/** Invoked on the start of the dash */
	protected void onEnd(Player player)
	{
	}
}
