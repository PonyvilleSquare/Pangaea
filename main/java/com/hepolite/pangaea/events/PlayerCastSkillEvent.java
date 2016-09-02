package com.hepolite.pangaea.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class PlayerCastSkillEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final PlayerClass race;
	private final PlayerSkill skill;
	private final List<String> arguments;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerCastSkillEvent(final Player player, final PlayerClass race, final PlayerSkill skill, final List<String> arguments)
	{
		this.player = player;
		this.race = race;
		this.skill = skill;
		this.arguments = arguments;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Returns the race that is associated with this event */
	public final PlayerClass getRace()
	{
		return race;
	}
	
	/** Returns the skill that is associated with this event */
	public final PlayerSkill getSkill()
	{
		return skill;
	}

	/** Returns the arguments that is associated with this event */
	public final List<String> getArguments()
	{
		return arguments;
	}
}
