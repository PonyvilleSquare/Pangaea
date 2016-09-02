package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.hepolite.pangaea.Pangaea;

public class InstructionReload extends Instruction
{
	public InstructionReload()
	{
		super("reload", "pangaea.admin");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		Pangaea.getInstance().getHungerManager().getSettings().reload();
		Pangaea.getInstance().getMovementManager().getSettings().reload();
		Pangaea.getInstance().getSkillManager().getSettings().reload();
		Pangaea.getInstance().getPermissionManager().getSettings().reload();
		sender.sendMessage(ChatColor.AQUA + "Reloaded all Pangaea configurations!");
		return false;
	}
}
