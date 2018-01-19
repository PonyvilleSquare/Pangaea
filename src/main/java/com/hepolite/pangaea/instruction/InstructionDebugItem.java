package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.settings.Settings;

public class InstructionDebugItem extends Instruction
{
	public InstructionDebugItem()
	{
		super("item", "pangaea.admin");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("[-w]");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		Player player = getPlayer(sender);
		if (player == null)
			return true;

		Settings settings = new Settings(Pangaea.getInstance(), "Debug"){};
		settings.initialize();
		if ("-w".equals(getArgument(arguments, 0)))
		{
			Chat.message(player, "&cLoaded the stored item from the debug config file");
			player.getInventory().setItemInMainHand(settings.getItem("item"));
		}
		else
		{
			Chat.message(player, "&cStored the held item in the debug config file");
			settings.set("item", player.getInventory().getItemInMainHand());
			settings.save();
		}
		return false;
	}
}
