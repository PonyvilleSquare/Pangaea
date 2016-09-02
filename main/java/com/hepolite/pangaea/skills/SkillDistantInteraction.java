package com.hepolite.pangaea.skills;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;

public class SkillDistantInteraction extends SkillCastTriggered
{
	private final HashMap<Location, Integer> timers = new HashMap<Location, Integer>();

	public SkillDistantInteraction()
	{
		super("Distant Interaction", true);
	}

	@Override
	public void onTick()
	{
		super.onTick();
		handleButtons();
	}

	/** Restores the buttons that has been pressed back to normal after some delay */
	@SuppressWarnings("deprecation")
	private final void handleButtons()
	{
		List<Location> locations = new LinkedList<Location>();
		for (Entry<Location, Integer> entry : timers.entrySet())
		{
			if (entry.getValue() <= 0)
				locations.add(entry.getKey());
			timers.put(entry.getKey(), entry.getValue() - 1);
		}
		for (Location location : locations)
		{
			Block block = location.getBlock();
			block.setData((byte) (block.getData() & ~0x8));
			block.getState().update(true);
			timers.remove(location);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();

		BlockIterator iterator = new BlockIterator(player.getEyeLocation(), 0.0, Integer.parseInt(event.getArguments().get(0)));
		while (iterator.hasNext())
		{
			final Block block = iterator.next();
			Material material = block.getType();
			if (material.isOccluding())
				return false;
			if (material == Material.WOOD_BUTTON || material == Material.STONE_BUTTON)
			{
				block.setData((byte) (block.getData() | 0x8));
				block.getState().update(true);
				timers.put(block.getLocation(), 30);
				return true;
			}
			else if (material == Material.LEVER)
			{
				block.setData((byte) (block.getData() ^ 0x8));
				block.getState().update(true);
				return true;
			}
		}
		return false;
	}
}
