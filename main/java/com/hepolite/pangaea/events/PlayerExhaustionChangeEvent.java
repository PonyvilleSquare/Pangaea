package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerExhaustionChangeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled = false;

	private final Player player;
	private float oldExhaustion, newExhaustion;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerExhaustionChangeEvent(final Player player, float oldExhaustion, float newExhaustion)
	{
		this.player = player;
		this.oldExhaustion = oldExhaustion;
		this.newExhaustion = newExhaustion;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Returns the old exhaustion value the player had */
	public final float getOldExhaustion()
	{
		return oldExhaustion;
	}

	/** Returns the new exhaustion value the player had */
	public final float getNewExhaustion()
	{
		return newExhaustion;
	}

	/** Sets the new saturation value the player will have */
	public final void setNewExhaustion(float newExhaustion)
	{
		this.newExhaustion = newExhaustion;
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
