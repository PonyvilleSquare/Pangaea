package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerImpactGroundEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final double speed;
	private final double distance;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerImpactGroundEvent(final Player player, double speed, double distance)
	{
		this.player = player;
		this.speed = speed;
		this.distance = distance;
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
	
	/** Returns the distance the player has fallen */
	public final double getDistance()
	{
		return distance;
	}
}
