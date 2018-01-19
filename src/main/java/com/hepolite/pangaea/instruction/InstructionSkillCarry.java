package com.hepolite.pangaea.instruction;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.hepolite.pillar.utility.EntityHelper;

public class InstructionSkillCarry extends Instruction
{
	public InstructionSkillCarry()
	{
		super("carry", "pangaea.system");
	}

	protected InstructionSkillCarry(String name)
	{
		super(name, "pangaea.system");
	}

	@Override
	public void addArgumentUsage(List<String> list)
	{
		list.add("<player> [accept] {SYSTEM ONLY}");
	}

	@Override
	public boolean onInvoke(CommandSender sender, List<String> arguments)
	{
		Player player = getPlayer(sender, arguments, 0);
		if (player == null)
			return true;

		if (arguments.size() < 2)
			handleRequest(player);
		else
			handleAccept(player);
		return false;
	}

	/** Sends a request to whomever is in front of the sender */
	private final void handleRequest(Player player)
	{
		Player target = EntityHelper.getPlayerInSight(player, 5.0f, player);
		if (target == null)
			Chat.message(player, "&bFound no players in front of you to carry");
		else
		{
			if (Database.getPlayerData(target).has("Instruction.Carry"))
				Chat.message(player, "&cThat player has received a request already");
			else
			{
				Chat.message(player, String.format("&bSent request to %s", target.getName()));
				Chat.message(target, String.format("&b%s wants to carry you. Use /carryaccept to be carried", player.getName()));
				Database.getPlayerData(target).set("Instruction.Carry", new Object[] { this, player.getUniqueId() }, 300);
			}
		}
	}

	/** Sends a request to whomever is in front of the sender */
	private final void handleAccept(Player player)
	{
		PlayerData data = Database.getPlayerData(player);
		if (!data.has("Instruction.Carry"))
		{
			Chat.message(player, "&cNo player has requested to pick you up, or request timed out");
			return;
		}
		Object[] objects = (Object[]) data.get("Instruction.Carry");
		Player carrier = Bukkit.getPlayer((UUID) objects[1]);
		if (carrier == null)
		{
			Chat.message(player, "&fThe one who wanted to carry you left you all alone :c");
			return;
		}
		if (carrier.getLocation().distance(player.getLocation()) < 5.0)
		{
			Chat.message(carrier, String.format("&b%s accepted your request", player.getName()));
			((InstructionSkillCarry) objects[0]).setRider(carrier, player);
		}
		else
		{
			Chat.message(carrier, String.format("&cYou are too far away from %s!", player.getName()));
			Chat.message(player, String.format("&cYou are too far away from %s!", carrier.getName()));
		}
	}

	/** Sets the rider */
	protected void setRider(Player carrier, Player rider)
	{
		carrier.setPassenger(rider);
	}
}
