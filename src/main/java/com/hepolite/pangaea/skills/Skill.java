package com.hepolite.pangaea.skills;

import java.util.Random;

import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pillar.listener.Listener;
import com.hepolite.pillar.settings.Settings;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class Skill extends Listener
{
	// Control variables
	private final String name;
	private int tickRate = -1;

	protected final Random random = new Random();

	public Skill(final String name)
	{
		super();
		this.name = name;
	}

	/** Returns the name of the skill */
	public final String getName()
	{
		return name;
	}

	/** Returns the skill settings */
	protected final SkillSettings getSettings()
	{
		return (SkillSettings) Pangaea.getInstance().getSkillManager().getSettings();
	}

	/** Returns the skill data settings */
	protected final Settings getData()
	{
		return getSettings().getSkillData();
	}

	// /////////////////////////////////////////////////////////////////

	/** Sets the tick rate for the skill; set to -1 to disable skill ticking */
	protected final void setTickRate(int rate)
	{
		tickRate = rate;
	}
	
	/** Returns the tick rate of the skill */
	public final int getTickRate()
	{
		return tickRate;
	}

	// /////////////////////////////////////////////////////////////////

	/** Invoked on skill tick, if relevant */
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
	}

	/** Invoked when the skill data is being saved */
	public void onSave(Settings settings)
	{
	}

	/** Invoked when the skill data is being reloaded */
	public void onReload(Settings settings)
	{
	}
}
