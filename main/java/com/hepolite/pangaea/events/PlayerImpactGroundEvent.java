package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerImpactGroundEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final double speed;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerImpactGroundEvent(final Player player, double speed)
	{
		this.player = player;
		this.speed = speed;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Returns the speed the player hit the ground with */
	public final double getSpeed()
	{
		return speed;
	}
}
