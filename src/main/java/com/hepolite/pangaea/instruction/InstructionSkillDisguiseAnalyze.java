package com.hepolite.pangaea.instruction;

import java.util.List;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.skills.SkillDisguise;

public class InstructionSkillDisguiseAnalyze extends Instruction
{
	public InstructionSkillDisguiseAnalyze()
	{
		super("analyze", "pangaea.admin");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		boolean foundAny = false;
		SkillDisguise skill = (SkillDisguise) Pangaea.getInstance().getSkillManager().getSkill("Disguise");
		for (Player player : Bukkit.getOnlinePlayers())
		{
			Disguise disguise = skill.getPlayerDisguise(player);
			if (disguise != null)
			{
				foundAny = true;
				String message = String.format("&cPlayer &6%s&c is disguised as &6%s&c", player.getName(), disguise.getType().toReadable());
				if (disguise.isPlayerDisguise())
					message = String.format("%s [&6%s&c]", message, ((PlayerDisguise) disguise).getName());
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			}
		}
		if (!foundAny)
			sender.sendMessage(ChatColor.RED + "No players are online, or disguised");
		return false;
	}
}
