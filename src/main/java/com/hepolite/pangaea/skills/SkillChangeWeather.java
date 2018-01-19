package com.hepolite.pangaea.skills;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;

public class SkillChangeWeather extends SkillCastTriggered
{
	public SkillChangeWeather()
	{
		super("Change Weather", true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		if (event.getArguments().size() < 1)
			return false;
		Player player = event.getPlayer();
		String weather = event.getArguments().get(0);

		boolean didSomething = true;
		World world = player.getWorld();
		if (weather.equals("sun"))
		{
			world.setStorm(false);
			world.setThundering(false);
			Chat.message(player, ChatColor.AQUA + "Changed weather to sun");
		}
		else if (weather.equals("rain"))
		{
			world.setStorm(true);
			world.setThundering(false);
			Chat.message(player, ChatColor.AQUA + "Changed weather to rain");
		}
		else if (weather.equals("thunder"))
		{
			world.setStorm(true);
			world.setThundering(true);
			Chat.message(player, ChatColor.AQUA + "Changed weather to thunder");
		}
		else
		{
			Chat.message(player, ChatColor.RED + "Can't recognize the weather '" + weather + "'");
			didSomething = false;
		}
		return didSomething;
	}
}
