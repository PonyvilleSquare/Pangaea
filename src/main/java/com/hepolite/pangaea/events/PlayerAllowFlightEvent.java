package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAllowFlightEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private boolean allow = true;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerAllowFlightEvent(final Player player, boolean allow)
	{
		this.player = player;
		this.allow = allow;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Returns true if the player is allowed to fly */
	public final boolean canFly()
	{
		return allow;
	}

	/** Returns true if the player is allowed to fly */
	public final void setCanFly(boolean allow)
	{
		this.allow = allow;
	}

}
