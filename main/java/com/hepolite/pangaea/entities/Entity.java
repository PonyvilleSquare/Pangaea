package com.hepolite.pangaea.entities;

import java.util.Random;

import org.bukkit.Location;

import com.hepolite.pangaea.Pangaea;

public abstract class Entity
{
	protected Location location;

	protected final Random random = new Random();

	/** Invoked when the entity is first spawned */
	public abstract void onSpawn();

	/** Invoked when the entity is despawned */
	public abstract void onDespawn();

	/** Main processing point; will be called each tick */
	public abstract void onTick();

	// ////////////////////////////////////////////////////

	/** Sets the location of the entity */
	public final void setLocation(Location location)
	{
		this.location = location.clone();
	}

	/** Returns the location of the entity */
	public final Location getLocation()
	{
		return location.clone();
	}

	/** Marks the entity for despawning */
	public final void despawn()
	{
		Pangaea.getInstance().getEntityManager().despawnEntity(this);
	}

}
