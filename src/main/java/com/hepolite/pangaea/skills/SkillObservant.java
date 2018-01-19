package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.sucy.skill.api.player.PlayerClass;

public class SkillObservant extends SkillCastTriggered
{
	public SkillObservant()
	{
		super("Observant", true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();

		List<EntityType> types = getSettings().getEntityTypes(race.getData().getName() + "." + getName() + ".types");
		EntityType type = null;
		try
		{
			type = EntityType.valueOf(event.getArguments().get(0).toUpperCase());
		}
		catch (Exception e)
		{
			Chat.message(player, "&fNot sure what it is that you're looking for...");
		}
		if (!types.contains(type))
			return false;

		player.getWorld().spawnEntity(player.getLocation(), type);
		Chat.message(player, String.format("&bYou found a %s!", type.toString().toLowerCase()));
		return true;
	}
}
