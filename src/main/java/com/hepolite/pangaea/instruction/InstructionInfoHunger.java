package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.hunger.HungerNode;
import com.hepolite.pangaea.hunger.HungerSettings;
import com.hepolite.pillar.chat.Chat;

public class InstructionInfoHunger extends Instruction
{
	public InstructionInfoHunger()
	{
		super("hunger", "pangaea.basic");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		Player player = getPlayer(sender, arguments, 0);
		if (player == null)
			return true;
		HungerNode node = ((HungerSettings) Pangaea.getInstance().getHungerManager().getSettings()).getNode(player);
		Chat.message(player, String.format("Hunger %.0f/%.0f, saturation %.0f, exhaustion %.0f", node.hunger, node.max, node.saturation, node.exhaustion));
		return false;
	}
}
