package com.hepolite.pangaea.instruction;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.settings.Settings;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class InstructionSkillDisguise extends Instruction
{
	private final LinkedList<IReplacer> replacers;

	public InstructionSkillDisguise()
	{
		super("disguise", "pangaea.system");
		replacers = new LinkedList<IReplacer>();
		replacers.add(new BabyReplacer());
		replacers.add(new HideSelfReplacer());
		replacers.add(new ColorReplacer());
		replacers.add(new TypeReplacer());
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("<player> [arguments] {SYSTEM ONLY}");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		if (arguments.size() == 0)
			return true;
		Player player = getPlayer(sender, arguments, 0);
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, "Disguise");
		if (player == null || race == null || skill == null)
			return true;
		arguments.remove(0);

		String type = validate(player, race, skill, arguments);
		if (type == null)
			return false;
		player.performCommand(replaceArguments(type, buildCommand(arguments)));
		return false;
	}

	/** validates the argument list */
	private final String validate(Player player, PlayerClass race, PlayerSkill skill, List<String> arguments)
	{
		if (arguments.size() == 0)
		{
			Chat.message(player, "&cYou need to specify what you want to disguise as");
			return null;
		}
		String type = arguments.get(0).toLowerCase().replaceAll("_", "");
		Settings settings = Pangaea.getInstance().getSkillManager().getSettings();

		// Special case for slimes and magma cubes
		if (type.equalsIgnoreCase("slime") || type.equalsIgnoreCase("magmacube"))
		{
			int maxSize = settings.getInt(race.getData().getName() + ".Disguise.maxSlimeSize");
			if (!isSlimeSizeAllowed(maxSize, type, arguments))
			{
				Chat.message(player, "&cYou're unable to take such a large body");
				return null;
			}
		}
		// Special check to prevent invisible disguises
		for (String string : arguments)
		{
			if (string.equalsIgnoreCase("setInvisible"))
			{
				Chat.message(player, "&cPerhaps a potion of invisibility works...?");
				return null;
			}
		}

		List<String> types = settings.getStringList(race.getData().getName() + ".Disguise.blocked");
		if (types.contains(type))
			Chat.message(player, "&cIt's impossible to disguise as '" + type + "'");
		else if (type.equalsIgnoreCase("player") && skill.getLevel() <= 1)
			Chat.message(player, "&cYou don't know how to do this yet");
		else
			return type;
		return null;
	}

	// /////////////////////////////////////////////////////////////////////////////

	/** Verify that the disguise structure isn't invalid slime size */
	private final boolean isSlimeSizeAllowed(int maxSize, String type, List<String> arguments)
	{
		// Use this to ensure that the size is set at least once; any player-defined sizes will override this setting
		arguments.add(1, "setSize 1");

		String[] parts = replaceArguments(type, buildCommand(arguments)).split(" ");
		for (int i = 0; i < parts.length; i++)
		{
			if (parts[i].equalsIgnoreCase("setSize") && i < parts.length - 1)
			{
				int number = 0;
				try
				{
					number = Integer.parseInt(parts[i + 1]);
				}
				catch (Exception e)
				{
				}
				if (number > maxSize)
					return false;
			}
		}
		return true;
	}

	// /////////////////////////////////////////////////////////////////////////////

	/** Performs a replacement of various parts of the disguise command */
	private final String replaceArguments(String type, String command)
	{
		for (IReplacer replacer : replacers)
			command = replacer.replace(type, command);
		while (command.contains("  "))
			command = command.replaceAll("  ", " ");
		return command;
	}

	/** Builds the final command */
	private final String buildCommand(List<String> arguments)
	{
		String command = "libsdisguises:disguise";
		for (String string : arguments)
			command += " " + string;
		return command;
	}

	// /////////////////////////////////////////////////////////////////////////////

	private interface IReplacer
	{
		/** Takes the input string and replaces it with something, based on what the entity type is */
		public String replace(String type, String input);
	}

	private final class BabyReplacer implements IReplacer
	{
		@Override
		public String replace(String type, String input)
		{
			return input.replaceAll("-b", " baby ");
		}
	}

	private final class HideSelfReplacer implements IReplacer
	{
		@Override
		public String replace(String type, String input)
		{
			input = input.replaceAll("-h", " setViewSelfDisguise false ");
			return input;
		}
	}

	private final class ColorReplacer implements IReplacer
	{
		@Override
		public String replace(String type, String input)
		{
			if (type.equalsIgnoreCase("horse") || type.equalsIgnoreCase("sheep"))
				input = input.replaceAll("-c:", " setColor ");
			else if (type.equalsIgnoreCase("wolf"))
				input = input.replaceAll("-c:", " setCollarColor ");
			return input;
		}
	}

	private final class TypeReplacer implements IReplacer
	{
		@Override
		public String replace(String type, String input)
		{
			if (type.equalsIgnoreCase("horse"))
				input = input.replaceAll("-t:", " setStyle ");
			else if (type.equalsIgnoreCase("ocelot") || type.equalsIgnoreCase("rabbit"))
				input = input.replaceAll("-t:", " setType ");
			else if (type.equalsIgnoreCase("villager"))
				input = input.replaceAll("-t:", " setProfession ");
			return input;
		}
	}
}
