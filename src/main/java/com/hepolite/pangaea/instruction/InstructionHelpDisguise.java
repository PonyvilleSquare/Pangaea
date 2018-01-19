package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pillar.chat.Chat;

public class InstructionHelpDisguise extends Instruction
{
	public InstructionHelpDisguise()
	{
		super("disguise", "pangaea.basic");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		Player player = getPlayer(sender);
		if (player == null)
			return true;

		Chat.message(player, "");
		Chat.message(player, "&n&9Disguise command helper&r&9:");
		Chat.message(player, "&b-h        &fHides your disguise from yourself");
		Chat.message(player, "&b-b        &fSets the disguise to be a baby");
		Chat.message(player, "&b-c:color  &fSets the color of your disguise");
		Chat.message(player, "&b-t:type   &fSets the type of your disguise");
		Chat.message(player, "");
		Chat.message(player, "&n&9Examples&r&9:");
		Chat.message(player, "&bHiding disguise &f/dis Cow -h");
		Chat.message(player, "&bBlue baby sheep &f/dis Sheep -b -c:blue");
		Chat.message(player, "&bUnique horse    &f/dis Horse -c:chestnut -t:white_dots");
		Chat.message(player, "&bVillager        &f/dis Villager -t:blacksmith");
		Chat.message(player, "");
		Chat.message(player, "&n&9More help&r&9:");
		Chat.message(player, "&b/dhelp         &fLibsDisguises help menu");
		Chat.message(player, "&b/dhelp <topic> &fLibsDisguises help menu");
		Chat.message(player, "");
		Chat.message(player, "&fAll &bLibsDisguises&f settings will also work");
		Chat.message(player, "");
		return false;
	}
}
