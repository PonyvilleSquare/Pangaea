package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;

public class InstructionSkillFly extends Instruction
{
	public InstructionSkillFly()
	{
		super("fly", "pangaea.system");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("<player> <system> {SYSTEM ONLY}");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		Player player = getPlayer(sender, arguments, 0);
		if (player == null || arguments.size() < 2)
			return true;

		String system = arguments.get(1);
		if (system.equalsIgnoreCase("vanilla"))
		{
			Database.getPlayerData(player).remove("Fly.system");
			Chat.message(player, "You can now start flying by doubletapping the jump key");
		}
		else if (system.equalsIgnoreCase("custom"))
		{
			Database.getPlayerData(player).set("Fly.system", "custom");
			Chat.message(player, "You can now start flying by simply jumping");
		}
		else
			Chat.message(player, "&cUnable to understand what system you mean by '" + system + "'...");

		return false;
	}
}
