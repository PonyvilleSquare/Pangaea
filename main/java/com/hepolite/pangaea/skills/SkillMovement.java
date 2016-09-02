package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.movement.IFallDamageModifier;
import com.hepolite.pangaea.movement.IMovementModifier;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillMovement extends Skill implements IMovementModifier, IFallDamageModifier
{
	public SkillMovement(final String name)
	{
		super(name);
		Pangaea.getInstance().getMovementManager().registerModifier((IMovementModifier) this);
		Pangaea.getInstance().getMovementManager().registerModifier((IFallDamageModifier) this);
	}

	@Override
	public final float getGroundModifier(Player player)
	{
		return getModifier(player, "Ground");
	}

	@Override
	public final float getFlightModifier(Player player)
	{
		return getModifier(player, "Flight");
	}
	
	@Override
	public float getMultiplier(Player player)
	{
		return getModifier(player, "Multiplier");
	}

	@Override
	public float getFlat(Player player)
	{
		return getModifier(player, "Flat");
	}

	/** Returns the modifier for the given group (May be either "Ground" or "Flight" for speed, or "Flat" or "Multiplier" for fall damage) */
	protected float getModifier(Player player, String group)
	{
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null)
			return 0.0f;
		return getSettings().getFloat(race.getData().getName() + "." + getName() + "." + group + ".Level " + skill.getLevel());
	}
}
