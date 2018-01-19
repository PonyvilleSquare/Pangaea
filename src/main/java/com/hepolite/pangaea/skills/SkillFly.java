package com.hepolite.pangaea.skills;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerLeaveGroundEvent;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;

public class SkillFly extends SkillMovement
{
	public SkillFly(String name)
	{
		super(name);
	}

	/** Makes sure that players who log in in mid-air, and can fly, actually do fly instead of falling to their deaths */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLogin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		Pangaea.getInstance().getFlightManager().handleFlightPermission(player);
		if (!player.getAllowFlight())
			return;
		Block block = player.getLocation().getBlock();
		if (!block.getType().isSolid() && !block.getRelative(0, -1, 0).getType().isSolid())
			player.setFlying(true);
	}

	/** Handle alternative takeoff method for players who want something else */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeaveGround(PlayerLeaveGroundEvent event)
	{
		Player player = event.getPlayer();
		if (!player.getAllowFlight() || player.getVelocity().getY() < 0.0)
			return;

		PlayerData data = Database.getPlayerData(player);
		if (data.has("Fly.system") && ((String) data.get("Fly.system")).equalsIgnoreCase("custom"))
			player.setFlying(true);
	}
}
