package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.hepolite.pangaea.events.PlayerExhaustionChangeEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillCarry extends SkillMovement
{
	public SkillCarry()
	{
		super("Carry");
	}

	protected SkillCarry(String name)
	{
		super(name);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerGainExhaustion(PlayerExhaustionChangeEvent event)
	{
		Player player = event.getPlayer();
		if (player.getPassenger() == null || event.getNewExhaustion() <= event.getOldExhaustion())
			return;
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;

		float modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".exhaustionModifier");
		float change = event.getNewExhaustion() - event.getOldExhaustion();
		event.setNewExhaustion(event.getOldExhaustion() + (1.0f + modifier) * change);
	}

	@Override
	protected float getModifier(Player player, String group)
	{
		if (player.getPassenger() == null)
			return 0.0f;
		return super.getModifier(player, group);
	}
}
