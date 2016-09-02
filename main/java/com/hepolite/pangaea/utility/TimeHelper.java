package com.hepolite.pangaea.utility;

import org.bukkit.World;

public class TimeHelper
{
	/** Returns true if it is day in the given world */
	public final static boolean isDay(World world)
	{
		long time = world.getTime();
		return (time > 0 && time < 12000);
	}

	/** Returns true if the sun is up in the given world */
	public final static boolean isSunUp(World world)
	{
		long time = world.getTime();
		return (time > 500 && time < 11500);
	}

	/** Returns true if the moon is up in the given world */
	public final static boolean isMoonUp(World world)
	{
		long time = world.getTime();
		return (time > 12500 && time < 23500);
	}
}
