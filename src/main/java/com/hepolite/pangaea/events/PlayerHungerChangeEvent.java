package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerHungerChangeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled = false;

	private final Player player;
	private float oldHunger, newHunger;
	private final float maxHunger;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerHungerChangeEvent(final Player player, float oldHunger, float newHunger, float maxHunger)
	{
		this.player = player;
		this.oldHunger = oldHunger;
		this.newHunger = newHunger;
		this.maxHunger = maxHunger;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Returns the old hunger value the player had */
	public final float getOldHunger()
	{
		return oldHunger;
	}

	/** Returns the new hunger value the player had */
	public final float getNewHunger()
	{
		return newHunger;
	}

	/** Sets the new hunger value the player will have */
	public final void setNewHunger(float newHunger)
	{
		this.newHunger = newHunger;
	}

	/** Returns the maximum hunger value the player can have */
	public final float getMaxHunger()
	{
		return maxHunger;
	}

	/** Returns whether the event has been cancelled or not */
	@Override
	public boolean isCancelled()
	{
		return isCancelled;
	}

	/** Assigns the canceling state of the event */
	@Override
	public void setCancelled(boolean cancelled)
	{
		isCancelled = cancelled;
	}
}
