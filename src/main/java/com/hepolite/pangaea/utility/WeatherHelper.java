package com.hepolite.pangaea.utility;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class WeatherHelper
{
	/** Returns true if it can rain in the given biome */
	private static final boolean rainsInBiome(Biome biome)
	{
		switch (biome)
		{
		case BEACHES:
		case BIRCH_FOREST:
		case BIRCH_FOREST_HILLS:
		case DEEP_OCEAN:
		case EXTREME_HILLS:
		case EXTREME_HILLS_WITH_TREES:
		case FOREST:
		case FOREST_HILLS:
		case JUNGLE:
		case JUNGLE_EDGE:
		case JUNGLE_HILLS:
		case MUSHROOM_ISLAND:
		case MUSHROOM_ISLAND_SHORE:
		case MUTATED_BIRCH_FOREST:
		case MUTATED_BIRCH_FOREST_HILLS:
		case MUTATED_EXTREME_HILLS:
		case MUTATED_EXTREME_HILLS_WITH_TREES:
		case MUTATED_FOREST:
		case MUTATED_JUNGLE:
		case MUTATED_JUNGLE_EDGE:
		case MUTATED_PLAINS:
		case MUTATED_REDWOOD_TAIGA:
		case MUTATED_SWAMPLAND:
		case MUTATED_TAIGA:
		case OCEAN:
		case PLAINS:
		case RIVER:
		case ROOFED_FOREST:
		case SMALLER_EXTREME_HILLS:
		case STONE_BEACH:
		case SWAMPLAND:
			return true;
		default:
			return false;
		}
	}

	/** Returns true if it can snow in the given biome */
	@SuppressWarnings("unused")
	private static final boolean snowsInBiome(Biome biome)
	{
		switch (biome)
		{
		case COLD_BEACH:
		case FROZEN_OCEAN:
		case FROZEN_RIVER:
		case ICE_FLATS:
		case ICE_MOUNTAINS:
		case MUTATED_ICE_FLATS:
		case MUTATED_REDWOOD_TAIGA:
		case MUTATED_REDWOOD_TAIGA_HILLS:
		case MUTATED_TAIGA:
		case MUTATED_TAIGA_COLD:
		case REDWOOD_TAIGA:
		case REDWOOD_TAIGA_HILLS:
		case TAIGA:
		case TAIGA_COLD:
		case TAIGA_COLD_HILLS:
		case TAIGA_HILLS:
			return true;
		default:
			return false;
		}
	}

	/** Returns true if it can rain hits the given location */
	public static final boolean isRaining(Location location)
	{
		World world = location.getWorld();
		if (!world.hasStorm())
			return false;
		Block highest = world.getHighestBlockAt(location);
		if (highest.getY() > location.getBlockY())
			return false;
		return rainsInBiome(world.getBiome(location.getBlockX(), location.getBlockZ()));
	}
}
