package com.hepolite.pangaea.instruction;

import org.bukkit.entity.Player;

import com.hepolite.pillar.database.Database;

public class InstructionSkillCarrySeaPony extends InstructionSkillCarry
{
	public InstructionSkillCarrySeaPony()
	{
		super("carry_seapony");
	}

	@Override
	protected void setRider(Player carrier, Player rider)
	{
		Database.getPlayerData(carrier).set("Carry (Sea Pony)", rider.getUniqueId());
	}
}
