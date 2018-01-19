package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveGroundEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final Player player;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerLeaveGroundEvent(final Player player)
	{
		this.player = player;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}
}
