package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pillar.chat.Chat;

public class InstructionSetName extends Instruction
{
	public InstructionSetName()
	{
		super("Name", "pangaea.admin");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("[name]");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		Player player = getPlayer(sender);
		if (player == null)
			return true;

		if (arguments.size() == 0)
		{
			Pangaea.getInstance().getRoleplayManager().setName(player, null);
			Chat.message(player, ChatColor.RED + "Removed your custom name");
		}
		else
		{
			String name = ChatColor.translateAlternateColorCodes('&', getArguments(arguments, 0)) + ChatColor.RESET;
			Pangaea.getInstance().getRoleplayManager().setName(player, name);
			Chat.message(player, ChatColor.AQUA + "Your name is now " + ChatColor.RESET + name);
		}

		return false;
	}
}
