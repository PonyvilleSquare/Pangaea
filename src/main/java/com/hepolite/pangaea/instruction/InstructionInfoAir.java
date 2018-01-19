package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.skills.SkillAquaticLifeform;
import com.hepolite.pillar.chat.Chat;

public class InstructionInfoAir extends Instruction
{
	public InstructionInfoAir()
	{
		super("air", "pangaea.basic");
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

		float remainingAir = ((SkillAquaticLifeform) Pangaea.getInstance().getSkillManager().getSkill("Aquatic Lifeform")).getRemainingAir(player);
		Chat.message(player, String.format("Oxygen %.0f/%d", remainingAir, player.getMaximumAir()));
		return false;
	}
}
