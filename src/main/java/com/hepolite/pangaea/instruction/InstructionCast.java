package com.hepolite.pangaea.instruction;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.listener.ListenerManager;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class InstructionCast extends Instruction
{
	public InstructionCast()
	{
		super("cast", "pangaea.system");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("<player> <skill> [arguments] {SYSTEM ONLY}");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		if (arguments.size() < 2)
			return true;
		Player player = getPlayer(arguments.remove(0));
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, arguments.remove(0).replaceAll("_", " "));
		if (player == null || race == null || skill == null)
			return true;

		PlayerCastSkillEvent event = new PlayerCastSkillEvent(player, race, skill, arguments);
		ListenerManager.post(event);
		return false;
	}
}
