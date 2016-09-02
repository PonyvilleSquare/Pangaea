package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.chat.Chat;
import com.sucy.skill.api.player.PlayerSkill;

public abstract class SkillCastTriggered extends Skill
{
	private final boolean countAsCast;

	/** If countAsCast is set to true, the skill will assume that SkillAPI does not handle cooldowns or mana consumption; this will then be done by the skill itself. Set to false if any component of the skill has "count as cast" set to true in the skill editor */
	public SkillCastTriggered(String name, boolean countAsCast)
	{
		super(name);
		this.countAsCast = countAsCast;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onCastSkill(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerSkill skill = event.getSkill();
		if (skill.getData().getName().equals(getName()))
		{
			if (countAsCast)
			{
				if (skill.isOnCooldown())
				{
					Chat.message(player, String.format("&6%s &4cooldown - &6%d &4seconds left", getName(), skill.getCooldown()));
					return;
				}
			}

			if (onCast(event) && countAsCast)
			{
				skill.startCooldown();
				SkillAPIHelper.broadcast(player, skill);
				SkillAPIHelper.consumeMana(player, skill);
			}
		}
	}

	/** Invoked whenever the skill is cast by any player. Returns true if the skill was successfully cast */
	protected abstract boolean onCast(PlayerCastSkillEvent event);
}
