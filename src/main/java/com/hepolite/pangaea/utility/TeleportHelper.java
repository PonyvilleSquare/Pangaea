package com.hepolite.pangaea.utility;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class TeleportHelper
{
	// Control variables
	private final static Random random = new Random();

	/** Returns a safe location near the given location */
	public static Location getSafeLocation(Location location)
	{
		World world = location.getWorld();
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		int recursionsLeft = 20;
		while (--recursionsLeft > 0)
		{
			// Find a safe spot in the vicinity
			boolean foundSpot = false;

			for (int side = 0; side < 2; side++)
			{
				boolean up = (side == 0);
				for (int i = 0; i < 40; i++)
				{
					// Get some blocks in the vicinity that gives a lot of information
					int Y = i * (up ? 1 : -1);
					Block lowerBlock = world.getBlockAt(x, y - 1 + Y, z);
					Block currentBlock = world.getBlockAt(x, y + Y, z);
					Block upperBlock = world.getBlockAt(x, y + 1 + Y, z);

					if (lowerBlock.getType().isSolid() && currentBlock.getType() == Material.AIR && upperBlock.getType() == Material.AIR)
					{
						foundSpot = true;
						y += Y;
						break;
					}
				}
				if (foundSpot)
					break;
			}
			if (foundSpot)
				break;
			x += random.nextInt(17) / 2 - 8;
			z += random.nextInt(17) / 2 - 8;
		}
		return new Location(world, (double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
	}
}
