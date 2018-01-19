package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerAllowFlightEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillCling extends SkillMovement
{
	public SkillCling()
	{
		super("Cling");
		setTickRate(1);
	}

	@Override
	protected float getModifier(Player player, String group)
	{
		return (isInCrampedSpace(player) ? super.getModifier(player, group) : 0.0f);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onPlayerFlightCheck(PlayerAllowFlightEvent event)
	{
		Player player = event.getPlayer();
		if (SkillAPIHelper.getSkill(player, getName()) != null && isInCrampedSpace(player))
			event.setCanFly(true);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		Pangaea.getInstance().getMovementManager().forceUpdatePlayerSpeeds(player);
	}

	/** Checks if the player is in a cramped space */
	private final boolean isInCrampedSpace(Player player)
	{
		Block main = player.getLocation().getBlock();
		if (main.isLiquid() || !player.isFlying())
			return false;

		for (int y = 0; y <= 1; y++)
		{
			Material typeA = main.getRelative(1, y, 0).getType();
			Material typeB = main.getRelative(-1, y, 0).getType();
			Material typeC = main.getRelative(0, y, 1).getType();
			Material typeD = main.getRelative(0, y, -1).getType();
			if ((typeA.isSolid() && typeB.isSolid()) || (typeC.isSolid() && typeD.isSolid()))
				return true;
		}
		return false;
	}
}
