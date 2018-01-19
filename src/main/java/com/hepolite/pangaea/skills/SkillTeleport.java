package com.hepolite.pangaea.skills;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillTeleport extends SkillCastTriggered
{
	public SkillTeleport(String name)
	{
		super(name, true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();
		PlayerSkill skill = event.getSkill();

		if (Database.getPlayerData(player).has("Hold"))
		{
			Chat.message(player, "&cYou are being held in place!");
			return false;
		}

		if (player.getEyeLocation().getBlock().isLiquid())
		{
			Chat.message(player, "&cYou can't do this from here!");
			return false;
		}

		int range = getSettings().getInt(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".range");
		Location destination = findDestination(player, range);
		if (destination == null)
		{
			Chat.message(player, "&cCouldn't find a destination, no solid block within range");
			return false;
		}

		float radius = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".radius");
		List<LivingEntity> entities = new LinkedList<LivingEntity>();
		if (radius == 0.0f)
			entities.add(player);
		else
			entities = EntityHelper.getEntitiesInRange(player.getLocation(), radius);
		if (entities.size() == 0)
		{
			Chat.message(player, "&cDidn't you want to teleport even yourself...?");
			return false;
		}

		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 0.7f, 0.0f);
		for (LivingEntity entity : entities)
		{
			entity.teleport(destination);
			if (entity instanceof Player)
				Pangaea.getInstance().getMovementManager().setPlayerHeight((Player) entity, destination.getBlockY());
		}
		player.getWorld().playSound(destination, Sound.ENTITY_ENDERMEN_TELEPORT, 0.7f, 0.0f);
		return true;
	}

	/** Returns an open destination for the given player */
	private final Location findDestination(Player player, int range)
	{
		Location openLocation = null;
		Location foundLocation = null;

		Block previous = null;
		BlockIterator iterator = new BlockIterator(player.getEyeLocation(), 0.0, range);
		while (iterator.hasNext())
		{
			Block block = iterator.next();
			if (!block.getType().isSolid() && !block.getRelative(0, 1, 0).getType().isSolid())
				openLocation = block.getLocation();
			if (block.getType().isSolid() || block.isLiquid())
			{
				// Check if it is possible to land on top of the block or not
				if (previous != null)
				{
					Block relative = previous.getRelative(0, 1, 0);
					if ((!relative.getType().isSolid() || canTeleportThrough(relative)) && !block.getRelative(0, 1, 0).getType().isSolid() && !block.getRelative(0, 2, 0).getType().isSolid())
						openLocation = block.getRelative(0, 1, 0).getLocation();
					foundLocation = openLocation;
				}

				// Allow teleporting through certain materials
				if (!canTeleportThrough(block))
					break;
			}
			previous = block;
		}
		if (foundLocation == null)
			return null;
		Location destination = player.getEyeLocation();
		destination.setX(foundLocation.getX() + 0.5);
		destination.setY(foundLocation.getY());
		destination.setZ(foundLocation.getZ() + 0.5);
		return destination;
	}

	/** Returns true if the block can be teleported through */
	private final boolean canTeleportThrough(Block block)
	{
		String type = block.getType().toString().toLowerCase();
		return type.contains("glass") || type.contains("fence") || type.contains("door");
	}
}
