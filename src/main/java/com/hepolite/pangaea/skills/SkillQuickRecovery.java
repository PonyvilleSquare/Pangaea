package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.hepolite.pangaea.events.PlayerAirChangeEvent;

public class SkillQuickRecovery extends SkillMovement
{
	public SkillQuickRecovery()
	{
		super("Quick Recovery");
	}

	/** Handles the case where the player's oxygen is restored */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerAirChange(PlayerAirChangeEvent event)
	{
		float change = event.getNewAir() - event.getOldAir();
		if (change <= 0.0f)
			return;

		float modifier = getModifier(event.getPlayer(), "Regen");
		event.setNewAir(event.getOldAir() + change * (1.0f + modifier));
	}

	@Override
	protected float getModifier(Player player, String group)
	{
		if (group.equalsIgnoreCase("Regen"))
			return super.getModifier(player, group);
		else if (!player.getLocation().getBlock().isLiquid())
			return super.getModifier(player, group);
		else
			return 0.0f;
	}
}
