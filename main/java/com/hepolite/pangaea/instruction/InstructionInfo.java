package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;

public class InstructionInfo extends Instruction
{
	public InstructionInfo()
	{
		super("info", "pangaea.basic");
		registerInstruction(new InstructionInfoHunger());
		registerInstruction(new InstructionInfoAir());
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("<topic>");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		return false;
	}
}
