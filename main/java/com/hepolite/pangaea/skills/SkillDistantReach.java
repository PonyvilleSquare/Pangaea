package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.utility.EntityHelper;

public class SkillDistantReach extends SkillCastTriggered
{
	public SkillDistantReach()
	{
		super("Distant Reach", true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		Location end = player.getEyeLocation();
		end = end.add(end.getDirection().multiply(Float.parseFloat(event.getArguments().get(0))));
		List<Item> items = EntityHelper.getItemsInSight(player.getEyeLocation(), end);
		for (Item item : items)
		{
			item.teleport(player);
			break;
		}
		if (items.size() == 0)
			Chat.message(player, ChatColor.RED + "Found no items to teleport to you");
		return items.size() != 0;
	}
}
