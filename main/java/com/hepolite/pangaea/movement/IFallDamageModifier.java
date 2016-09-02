package com.hepolite.pangaea.movement;

import org.bukkit.entity.Player;

public interface IFallDamageModifier
{
	/** Returns a multiplicative modifier for the damage the given player receives */
	public float getMultiplier(Player player);
	
	/** Returns a flat modifier for the damage the given player receives */
	public float getFlat(Player player);
}
