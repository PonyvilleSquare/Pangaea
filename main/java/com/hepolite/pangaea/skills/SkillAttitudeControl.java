package com.hepolite.pangaea.skills;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillAttitudeControl extends SkillMovement
{
	public SkillAttitudeControl()
	{
		super("Attitude Control");
		setTickRate(1);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		if (!player.getAllowFlight() || player.isFlying())
			return;
		if (!player.getLocation().getBlock().getRelative(0, -1, 0).getType().isSolid())
		{
			handleDeadlyFalls(player);
			handleFastFalls(player);
		}
	}

	/** Handles the prevention of death by fall damage */
	private final void handleDeadlyFalls(Player player)
	{
		if (Math.max(0.01, player.getHealth() - 6.0) > Pangaea.getInstance().getMovementManager().calculateFallDamage(player))
			return;

		int X = (int) Math.floor(player.getLocation().getX() - 0.5);
		int Y = player.getLocation().getBlockY();
		int Z = (int) Math.floor(player.getLocation().getZ() - 0.5);
		for (int y = Y; y > Math.max(0, Y - 30); y--)
		{
			Block blockA = player.getWorld().getBlockAt(X, y, Z);
			Block blockB = player.getWorld().getBlockAt(X + 1, y, Z);
			Block blockC = player.getWorld().getBlockAt(X, y, Z + 1);
			Block blockD = player.getWorld().getBlockAt(X + 1, y, Z + 1);
			if (blockA.getType().isSolid() || blockB.getType().isSolid() || blockC.getType().isSolid() || blockD.getType().isSolid())
			{
				player.setFlying(true);
				break;
			}
		}
	}

	/** Handle too fast falls prevention */
	private final void handleFastFalls(Player player)
	{
		if (player.isSneaking())
			return;

		if (Pangaea.getInstance().getMovementManager().calculateFallDamage(player) > player.getMaxHealth())
			player.setFlying(true);
	}
}
