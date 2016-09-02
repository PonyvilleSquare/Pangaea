package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerHungerUpdateMaxEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private float maxHunger;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerHungerUpdateMaxEvent(final Player player, float maxHunger)
	{
		this.player = player;
		this.maxHunger = maxHunger;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Sets the new hunger value the player will have */
	public final void setMaxHunger(float maxHunger)
	{
		this.maxHunger = maxHunger;
	}

	/** Returns the maximum hunger value the player can have */
	public final float getMaxHunger()
	{
		return maxHunger;
	}
}
