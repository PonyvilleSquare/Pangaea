package com.hepolite.pangaea.instruction;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.hepolite.pangaea.Pangaea;

public class InstructionHelp extends Instruction
{
	public InstructionHelp()
	{
		super("help", "pangaea.basic");
		registerInstruction(new InstructionHelpDisguise());
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		// Grab all instructions that exists
		TreeMap<String, Instruction> map = new TreeMap<String, Instruction>();
		Pangaea.getInstance().getInstructionManager().getBase().getInstructions("/pangaea", map);

		// Build a list of instructions the user can use
		List<String> list = new LinkedList<String>();
		for (Entry<String, Instruction> entry : map.entrySet())
		{
			if (entry.getValue().permission == null)
				continue;
			if (sender.hasPermission(entry.getValue().permission))
			{
				List<String> uses = new LinkedList<String>();
				entry.getValue().addArgumentUsage(uses);
				for (String use : uses)
					list.add(entry.getKey() + " " + use);
			}
		}

		// Display all commands
		for (String entry : list)
			sender.sendMessage(ChatColor.WHITE + entry);
		return false;
	}
}
