package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;

public class InstructionAccount extends Instruction
{
	public InstructionAccount()
	{
		super("account", "pangaea.system");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("<player> <account> {SYSTEM ONLY}");
		list.add("<player> <account> force {SYSTEM ONLY}");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		Player player = getPlayer(sender, arguments, 0);
		if (player == null || arguments.size() < 2)
			return true;
		int accountId = 0;
		try
		{
			accountId = Integer.parseInt(arguments.get(1));
		}
		catch (Exception e)
		{
			return true;
		}

		if (player.hasPermission("pangaea.admin") || (arguments.size() >= 3 && arguments.get(2).equalsIgnoreCase("force")))
			Pangaea.getInstance().getAccountManager().changeAccountForcefully(player, accountId);
		else
			Pangaea.getInstance().getAccountManager().changeAccount(player, accountId);
		return false;
	}
}
