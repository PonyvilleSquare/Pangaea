package com.hepolite.pangaea.entities;

import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.Location;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.core.Manager;
import com.hepolite.pillar.settings.Settings;

public class EntityManager extends Manager
{
	private final Collection<Entity> entities = new LinkedList<Entity>();
	private final Collection<Entity> entitiesToAdd = new LinkedList<Entity>();
	private final Collection<Entity> entitiesToRemove = new LinkedList<Entity>();

	public EntityManager()
	{
		super(new Settings(Pangaea.getInstance(), "Entities")
		{
		});
	}

	@Override
	public void onTick()
	{
		entities.removeAll(entitiesToRemove);
		entitiesToRemove.clear();
		entities.addAll(entitiesToAdd);
		entitiesToAdd.clear();

		for (Entity entity : entities)
			entity.onTick();
	}

	/** Spawns in a custom entity object and the given location */
	public final void spawnEntity(Entity entity, Location location)
	{
		if (entity != null)
		{
			entity.setLocation(location);
			entity.onSpawn();
			entitiesToAdd.add(entity);
		}
	}

	/** Removes a custom entity object */
	public final void despawnEntity(Entity entity)
	{
		if (entity != null)
		{
			entity.onDespawn();
			entitiesToRemove.add(entity);
		}
	}
}
