package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;

public class InstructionSet extends Instruction
{
	public InstructionSet()
	{
		super("Set", "pangaea.admin");
		registerInstruction(new InstructionSetName());
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
