package com.hepolite.pangaea.rp;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.core.Manager;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.settings.Settings;
import com.sucy.skill.api.player.PlayerClass;

public class RoleplayManager extends Manager
{
	private final HashMap<UUID, String> playerNames = new HashMap<UUID, String>();
	
	private int tick = 0;

	public RoleplayManager()
	{
		super(new Settings(Pangaea.getInstance(), "Roleplay")
		{
		});
	}

	@Override
	public void onTick()
	{
		if (tick++ % 20 != 0)
			return;
		
		for (Player player : Bukkit.getOnlinePlayers())
		{
			String name = getName(player);
			if (name != null)
				player.setDisplayName(name);
		}
	}

	/** Assigns the given name to the given player, or removes the name if the name is null */
	public final void setName(Player player, String name)
	{
		if (player != null)
		{
			setName(player.getUniqueId(), name);
			PlayerClass race = SkillAPIHelper.getRace(player);
			if (race == null)
				player.setDisplayName(player.getName());
			else
				player.setDisplayName(race.getData().getPrefixColor() + "[" + race.getData().getName() + "] " + ChatColor.RESET + player.getName());
		}
	}

	/** Assigns the given name to the given player, or removes the name if the name is null */
	public final void setName(UUID player, String name)
	{
		if (name == null)
			playerNames.remove(player);
		else
			playerNames.put(player, name);
	}

	/** Returns the custom name for the given player, or null if there is no custom name */
	public final String getName(Player player)
	{
		return player == null ? null : getName(player.getUniqueId());
	}

	/** Returns the custom name for the given player, or null if there is no custom name */
	public final String getName(UUID player)
	{
		return playerNames.get(player);
	}

	/** Used to handle the case where players die */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDie(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		Player killer = player.getKiller();
		String playerName = getName(player);
		String killerName = getName(killer);

		String message = event.getDeathMessage();
		if (playerName != null)
			message = message.replaceAll(player.getName(), playerName);
		if (killerName != null)
			message = message.replaceAll(killer.getName(), killerName);
		event.setDeathMessage(message);
	}
}
