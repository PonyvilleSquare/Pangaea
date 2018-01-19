package com.hepolite.pangaea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerEatEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean isCancelled = false;

	private final Player player;
	private final ItemStack item;

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	public PlayerEatEvent(final Player player, final ItemStack item)
	{
		this.player = player;
		this.item = item;
	}

	/** Returns the player that is associated with this event */
	public final Player getPlayer()
	{
		return player;
	}

	/** Returns the item that is associated with this event */
	public final ItemStack getItem()
	{
		return item;
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
