package com.hepolite.pangaea.instruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Instruction
{
	// Control variables
	public final String name;
	public final String permission;

	private final HashMap<String, Instruction> subInstructions = new HashMap<String, Instruction>();

	/** Initializes an instruction */
	public Instruction(String name, String permission)
	{
		this.name = name;
		this.permission = permission;
	}

	/** Registrates a subinstruction for this instruction */
	public final void registerInstruction(Instruction instruction)
	{
		if (instruction == null)
			return;
		subInstructions.put(instruction.name.toLowerCase(), instruction);
	}

	/** Returns the instruction with the given name, or null if none with the given name was found */
	public final Instruction getInstruction(String name)
	{
		if (name == null)
			return null;
		return subInstructions.get(name.toLowerCase());
	}

	/** Inserts all of the subinstructions into the given map */
	public final void getInstructions(String base, Map<String, Instruction> map)
	{
		if (name != null)
			map.put(base + " " + name, this);
		for (Instruction instruction : subInstructions.values())
			instruction.getInstructions(base + (name == null ? "" : " " + name), map);
	}

	/** On command handling, called every time the instruction is invoked. Returns true if something went wrong */
	public boolean onCommand(CommandSender sender, List<String> arguments)
	{
		if (arguments.size() != 0)
		{
			Instruction instruction = getInstruction(arguments.get(0));
			if (instruction != null)
			{
				arguments.remove(0);
				return instruction.onCommand(sender, arguments);
			}
		}
		if (sender.hasPermission(permission))
			return onInvoke(sender, arguments);
		return false;
	}

	/** The actual invoking of the instruction, does the work. Returns true if something went wrong */
	public abstract boolean onInvoke(CommandSender sender, List<String> arguments);

	/** Invoked to add all the possible ways to use the instruction to the given list */
	public abstract void addArgumentUsage(List<String> list);

	// /////////////////////////////////////////////////////////////////////////////////////////
	// HELPER METHODS // HELPER METHODS // HELPER METHODS // HELPER METHODS // HELPER METHODS //
	// /////////////////////////////////////////////////////////////////////////////////////////

	/** Returns an argument from the list of arguments, provided the index is valid */
	protected String getArgument(List<String> arguments, int index)
	{
		if (arguments == null || index >= arguments.size())
			return null;
		return arguments.get(index);
	}

	/** Returns an string from the list of arguments, provided the index is valid. Adds a space between all arguments */
	protected String getArguments(List<String> arguments, int start)
	{
		String string = "";
		if (arguments == null)
			return string;
		for (int i = start; i < arguments.size(); i++)
			string += arguments.get(i) + " ";
		return string;
	}

	/** Returns a player object from the sender, the player name as the given argument. Returns null if neither is a player */
	protected Player getPlayer(CommandSender sender, List<String> arguments, int playerArgumentIndex)
	{
		if (arguments == null || arguments.size() <= playerArgumentIndex)
			return getPlayer(sender, null);
		return getPlayer(sender, arguments.get(playerArgumentIndex));
	}

	/** Returns a player object from the sender, or the string, if the string matches a player. Returns null if neither is a player */
	protected Player getPlayer(CommandSender sender, String playerName)
	{
		Player player = getPlayer(playerName);
		if (player == null)
		{
			if (playerName == null && sender instanceof Player)
				return (Player) sender;
			else
				return null;
		}
		return player;
	}

	/** Returns a player object from the given command sender */
	protected Player getPlayer(CommandSender sender)
	{
		if (sender instanceof Player)
			return (Player) sender;
		return null;
	}

	/** Returns a player object from the given string */
	@SuppressWarnings("deprecation")
	protected Player getPlayer(String playerName)
	{
		if (playerName == null)
			return null;
		return Bukkit.getPlayer(playerName);
	}
}
