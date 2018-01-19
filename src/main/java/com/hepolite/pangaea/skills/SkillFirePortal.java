package com.hepolite.pangaea.skills;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.sucy.skill.api.player.PlayerClass;

public class SkillFirePortal extends SkillCastTriggered
{
	public SkillFirePortal()
	{
		super("Fire Portal", true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();

		int range = getSettings().getInt(race.getData().getName() + "." + getName() + ".range");
		Location destination = findDestination(player, range);
		if (destination == null)
		{
			Chat.message(player, "&cCouldn't find a destination, no open block within range");
			return false;
		}

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 0.7f, 0.0f);
		player.teleport(destination);
		player.getWorld().playSound(destination, Sound.ENTITY_ENDERMEN_TELEPORT, 0.7f, 0.0f);
		return true;
	}

	/** Returns an open destination for the given player */
	private final Location findDestination(Player player, int range)
	{
		boolean foundSolidBlock = false;
		Location foundLocation = null;

		BlockIterator iterator = new BlockIterator(player.getEyeLocation(), 0.0, range);
		while (iterator.hasNext())
		{
			Block block = iterator.next();
			if (block.getType().isSolid())
				foundSolidBlock = true;
			else if (foundSolidBlock && !block.getRelative(0, 1, 0).getType().isSolid())
			{
				foundLocation = block.getLocation();
				break;
			}
		}
		if (foundLocation == null)
			return null;
		Location destination = player.getEyeLocation();
		destination.setX(foundLocation.getX() + 0.5);
		destination.setY(foundLocation.getY());
		destination.setZ(foundLocation.getZ() + 0.5);
		return destination;
	}
}
