package com.hepolite.pangaea.skills;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.BlockIterator;

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
		Database.getPlayerData(player).set(getName(), 20);
		for (LivingEntity entity : entities)
		{
			entity.teleport(destination);
			if (entity instanceof Player)
				Database.getPlayerData((Player) entity).set(getName(), 20);
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
			if (block.getType().isSolid())
			{
				// Check if it is possible to land on top of the block or not
				if (previous != null && !previous.getRelative(0, 1, 0).getType().isOccluding() && !block.getRelative(0, 1, 0).getType().isSolid() && !block.getRelative(0, 2, 0).getType().isSolid())
					openLocation = block.getRelative(0, 1, 0).getLocation();
				foundLocation = openLocation;

				// Allow teleporting through certain materials
				Material type = block.getType();
				if (type != Material.THIN_GLASS && type != Material.STAINED_GLASS_PANE && type != Material.GLASS && type != Material.STAINED_GLASS && type != Material.IRON_FENCE && type != Material.FENCE && type != Material.FENCE_GATE)
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

	/** Intercepts the fall damage event, canceling any fall damage when teleporting */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTakeFallDamage(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER || event.getCause() != DamageCause.FALL)
			return;
		if (Database.getPlayerData((Player) event.getEntity()).has(getName()))
			event.setCancelled(true);
	}
}
