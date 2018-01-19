package com.hepolite.pangaea.instruction;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InstructionManager
{
	private Instruction instructions = new Instruction(null, null)
	{
		@Override
		public boolean onInvoke(CommandSender sender, List<String> arguments)
		{
			return false;
		}

		@Override
		public void addArgumentUsage(List<String> list)
		{
		}
	};

	public InstructionManager()
	{
		registerInstruction(new InstructionDebug());
		registerInstruction(new InstructionCast());
		registerInstruction(new InstructionDatabase());
		registerInstruction(new InstructionHelp());
		registerInstruction(new InstructionInfo());
		registerInstruction(new InstructionReload());
		registerInstruction(new InstructionSkill());
		registerInstruction(new InstructionAccount());
		registerInstruction(new InstructionSet());
	}

	/** Registers another instruction to the system */
	public final void registerInstruction(Instruction instruction)
	{
		instructions.registerInstruction(instruction);
	}

	/** Returns the base instruction of the system; all other instructions are a child of this one */
	public final Instruction getBase()
	{
		return instructions;
	}

	/** Basic entry point for executing commands */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!"pangaea".equalsIgnoreCase(label))
			return false;

		// Convert argument array to list
		List<String> arguments = new LinkedList<String>();
		for (String argument : args)
			arguments.add(argument);
		String instructionName = null;
		if (arguments.size() != 0)
			instructionName = arguments.remove(0);

		Instruction instruction = instructions.getInstruction(instructionName);
		if (instruction == null)
		{
			sender.sendMessage(ChatColor.RED + "Invalid parameters for the command. Try /pangaea help");
			return false;
		}
		instruction.onCommand(sender, arguments);
		return true;
	}
}
