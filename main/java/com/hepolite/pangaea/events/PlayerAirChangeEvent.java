package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerAirChangeEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled = false;

	private final Player player;
	private float oldAir, newAir;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerAirChangeEvent(final Player player, float oldAir, float newAir)
	{
		this.player = player;
		this.oldAir = oldAir;
		this.newAir = newAir;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Returns the old air value the player had */
	public final float getOldAir()
	{
		return oldAir;
	}

	/** Returns the new air value the player had */
	public final float getNewAir()
	{
		return newAir;
	}

	/** Sets the new air value the player will have */
	public final void setNewAir(float newAir)
	{
		this.newAir = newAir;
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
