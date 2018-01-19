package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;

public class InstructionDebug extends Instruction
{
	public InstructionDebug()
	{
		super("debug", "pangaea.admin");
		registerInstruction(new InstructionDebugItem());
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		return false;
	}
}
