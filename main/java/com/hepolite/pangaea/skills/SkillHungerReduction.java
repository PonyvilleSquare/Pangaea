package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.hepolite.pangaea.events.PlayerSaturationChangeEvent;

public class SkillHungerReduction extends SkillMovement
{
	public SkillHungerReduction(String name)
	{
		super(name);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLoseSaturation(PlayerSaturationChangeEvent event)
	{
		Player player = event.getPlayer();
		if (player.isFlying() && event.getNewSaturation() < event.getOldSaturation())
		{
			float change = event.getNewSaturation() - event.getOldSaturation();
			event.setNewSaturation(event.getOldSaturation() + (1.0f + getModifier(player, "Flight")) * change);
		}
	}
}
