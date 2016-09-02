package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSaturationChangeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled = false;

	private final Player player;
	private float oldSaturation, newSaturation;
	private final float hunger;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerSaturationChangeEvent(final Player player, float oldSaturation, float newSaturation, float hunger)
	{
		this.player = player;
		this.oldSaturation = oldSaturation;
		this.newSaturation = newSaturation;
		this.hunger = hunger;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Returns the old saturation value the player had */
	public final float getOldSaturation()
	{
		return oldSaturation;
	}

	/** Returns the new saturation value the player had */
	public final float getNewSaturation()
	{
		return newSaturation;
	}

	/** Sets the new saturation value the player will have */
	public final void setNewSaturation(float newSaturation)
	{
		this.newSaturation = newSaturation;
	}
	
	/** Returns the hunger value the player has */
	public final float getHunger()
	{
		return hunger;
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
