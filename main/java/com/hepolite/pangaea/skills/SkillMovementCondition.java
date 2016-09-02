package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;

import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;

public class SkillMovementCondition extends SkillMovement
{
	public SkillMovementCondition(String name)
	{
		super(name);
	}

	@Override
	protected float getModifier(Player player, String group)
	{
		PlayerData data = Database.getPlayerData(player);
		if (data.has(getName()))
			return super.getModifier(player, group);
		return 0.0f;
	}
}
