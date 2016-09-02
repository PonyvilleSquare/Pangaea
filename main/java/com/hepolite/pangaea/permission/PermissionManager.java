package com.hepolite.pangaea.permission;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.core.Manager;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.settings.Settings;
import com.sucy.skill.api.classes.RPGClass;
import com.sucy.skill.api.event.PlayerAccountChangeEvent;
import com.sucy.skill.api.event.PlayerClassChangeEvent;
import com.sucy.skill.api.player.PlayerClass;

public class PermissionManager extends Manager
{
	public PermissionManager()
	{
		super(new Settings(Pangaea.getInstance(), "Permissions")
		{
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangeClass(PlayerClassChangeEvent event)
	{
		handlePerms(event.getPlayerData().getPlayerName(), event.getPreviousClass(), event.getNewClass());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangeClass(PlayerAccountChangeEvent event)
	{
		PlayerClass oldRace = SkillAPIHelper.getRace(event.getPreviousAccount());
		PlayerClass newRace = SkillAPIHelper.getRace(event.getNewAccount());
		handlePerms(event.getAccountData().getPlayerName(), oldRace == null ? null : oldRace.getData(), newRace == null ? null : newRace.getData());
	}

	/** Handles the permissions given the old and new class */
	private final void handlePerms(String player, RPGClass oldClass, RPGClass newClass)
	{
		if (oldClass != null && settings.has(oldClass.getName()))
			run(String.format("pex user %s remove %s", player, settings.getString(oldClass.getName())));
		if (newClass == null)
			run(String.format("pex user %s remove %s", player, settings.getString("Professed")));
		else
		{
			if (settings.has(newClass.getName()))
				run(String.format("pex user %s add %s", player, settings.getString(newClass.getName())));
			run(String.format("pex user %s add %s", player, settings.getString("Professed")));
		}
	}

	/** Runs the given string as a command from the console */
	private final void run(String command)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}
}
