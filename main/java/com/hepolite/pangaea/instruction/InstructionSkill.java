package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;

public class InstructionSkill extends Instruction
{
	public InstructionSkill()
	{
		super("skill", "pangaea.system");
		registerInstruction(new InstructionSkillCarry());
		registerInstruction(new InstructionSkillCarrySeaPony());
		registerInstruction(new InstructionSkillDisguise());
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("<skill> [arguments] {SYSTEM ONLY}");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		return false;
	}
}
