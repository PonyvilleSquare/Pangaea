package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.hepolite.pangaea.events.PlayerSaturationChangeEvent;
import com.hepolite.pangaea.utility.TimeHelper;

public class SkillNightProwler extends SkillMovement
{
	public SkillNightProwler()
	{
		super("Night Prowler");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDealDamage(EntityDamageByEntityEvent event)
	{
		if (!(event.getDamager() instanceof Player))
			return;
		float modifier = getModifier((Player) event.getDamager(), "Damage");
		if (modifier != 0.0f)
			event.setDamage(Math.max(0.0, event.getDamage() * (1.0f + modifier)));
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLoseSaturation(PlayerSaturationChangeEvent event)
	{
		Player player = event.getPlayer();
		if (event.getNewSaturation() < event.getOldSaturation())
		{
			float change = event.getNewSaturation() - event.getOldSaturation();
			event.setNewSaturation(event.getOldSaturation() + (1.0f + getModifier(player, "Hunger")) * change);
		}
	}

	@Override
	protected float getModifier(Player player, String group)
	{
		if (player.getLocation().getY() <= 0.0 || player.getLocation().getY() >= 250.0)
			return 0.0f;
		if (TimeHelper.isMoonUp(player.getWorld()) && player.getLocation().getBlock().getLightFromSky() > 2)
			return super.getModifier(player, group);
		return 0.0f;
	}
}
