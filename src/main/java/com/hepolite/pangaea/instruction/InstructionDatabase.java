package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;

public class InstructionDatabase extends Instruction
{
	public InstructionDatabase()
	{
		super("database", "pangaea.system");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("<player> <name> <value> [duration] {SYSTEM ONLY}");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		if (arguments.size() < 3)
			return true;
		PlayerData data = Database.getPlayerData(getPlayerUUID(arguments.get(0)));
		if (data != null)
		{
			int duration = -1;
			if (arguments.size() >= 4)
				duration = Integer.parseInt(arguments.get(3));
			data.set(arguments.get(1).replaceAll("_", " "), arguments.get(2), duration);
		}
		return false;
	}
}
