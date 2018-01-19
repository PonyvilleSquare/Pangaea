package com.hepolite.pangaea.utility;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class MovementHelper
{
	/** Returns true if the player is on the ground */
	public static boolean isPlayerCompletelyOnGround(Player player)
	{
		if (player.isOnGround())
			return true;
		double check = (player.getPlayer().getLocation().getY() - player.getPlayer().getLocation().getBlockY());
		return check % getRelativeBlockHeight(player.getLocation().getBlock()) == 0 || check <= 0.002;
	}

	/** Returns the relative height a player would have when standing on the given block */
	public static Double getRelativeBlockHeight(Block block)
	{
		switch (block.getType())
		{
		case ACACIA_FENCE:
		case ACACIA_FENCE_GATE:
		case BIRCH_FENCE:
		case BIRCH_FENCE_GATE:
		case DARK_OAK_FENCE:
		case DARK_OAK_FENCE_GATE:
		case FENCE:
		case FENCE_GATE:
		case IRON_FENCE:
		case JUNGLE_FENCE:
		case JUNGLE_FENCE_GATE:
		case NETHER_FENCE:
		case SPRUCE_FENCE:
		case SPRUCE_FENCE_GATE:
		case COBBLE_WALL:
			return 0.5;
		case GRASS_PATH:
		case SOIL:
		case CACTUS:
			return 0.9375;
		case SOUL_SAND:
		case CHEST:
		case ENDER_CHEST:
		case TRAPPED_CHEST:
			return 0.875;
		case CHORUS_PLANT:
			return 0.8125;
		case ENCHANTMENT_TABLE:
			return 0.75;
		case BED_BLOCK:
			return 0.5625;
		case SKULL:
			return 0.25;
		case WATER_LILY:
			return 0.09375;
		default:
			return 0.0625;
		}
	}
}
