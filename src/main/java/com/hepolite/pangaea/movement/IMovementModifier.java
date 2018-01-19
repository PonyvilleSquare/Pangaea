package com.hepolite.pangaea.movement;

import org.bukkit.entity.Player;

public interface IMovementModifier
{
	/** Returns a multiplier for the ground speed of the given player */
	public float getGroundModifier(Player player);
	
	/** Returns a multiplier for the flight speed of the given player */
	public float getFlightModifier(Player player);
}
