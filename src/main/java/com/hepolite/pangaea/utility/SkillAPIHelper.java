package com.hepolite.pangaea.utility;

import java.util.Collection;
import java.util.LinkedList;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ManaCost;
import com.sucy.skill.api.enums.ManaSource;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillAPIHelper
{
	/** Returns the currently active account of the player */
	public final static PlayerData getPlayerAccount(OfflinePlayer player)
	{
		PlayerAccounts accounts = SkillAPI.getPlayerAccountData(player);
		return accounts == null ? null : accounts.getActiveData();
	}

	/** Returns the race of the given player; returns null if the player has no race */
	public final static PlayerClass getRace(OfflinePlayer player)
	{
		PlayerAccounts accounts = SkillAPI.getPlayerAccountData(player);
		return accounts == null ? null : getRace(accounts.getActiveData());
	}
	
	/** Returns the name of the race of the given player; returns an empty string if the player has no race */
	public final static String getRaceName(OfflinePlayer player)
	{
		PlayerClass race = getRace(player);
		return (race == null ? "" : race.getData().getName());
	}

	/** Returns the race of the given player data; returns null if the player data has no race */
	public final static PlayerClass getRace(PlayerData data)
	{
		return data == null ? null : data.getClass("race");
	}

	/** Returns all skills the player has it unlocked */
	public final static Collection<PlayerSkill> getSkills(OfflinePlayer player)
	{
		return player == null ? null : getSkills(SkillAPI.getPlayerData(player));
	}

	/** Returns all skills the player has it unlocked */
	public final static Collection<PlayerSkill> getSkills(PlayerData data)
	{
		Collection<PlayerSkill> skills = new LinkedList<PlayerSkill>();
		for (PlayerSkill skill : data.getSkills())
		{
			if (skill.getLevel() > 0 || skill.getData().getMaxLevel() <= 0)
				skills.add(skill);
		}
		return skills;
	}

	/** Returns the skill object, if the player has it unlocked */
	public final static PlayerSkill getSkill(OfflinePlayer player, String name)
	{
		return player == null ? null : getSkill(SkillAPI.getPlayerData(player), name);
	}

	/** Returns the skill object, if the player data has it unlocked */
	public final static PlayerSkill getSkill(PlayerData data, String name)
	{
		PlayerSkill skill = (data == null ? null : data.getSkill(name));
		return (skill == null || (skill.getLevel() <= 0 && skill.getData().getMaxLevel() > 0) ? null : skill);
	}

	/** Returns the frequency at which mana regenerates, in ticks */
	public final static int getManaRegenFrequency()
	{
		return SkillAPI.getSettings().getGainFreq();
	}

	/** Consumes the given mana for the given skill */
	public final static void consumeMana(OfflinePlayer player, PlayerSkill skill)
	{
		PlayerData data = SkillAPI.getPlayerData(player);
		if (data != null)
			data.useMana(skill.getManaCost(), ManaCost.SKILL_CAST);
	}

	/** Consumes the given mana */
	public final static void consumeMana(OfflinePlayer player, double amount)
	{
		PlayerData data = SkillAPI.getPlayerData(player);
		if (data != null)
			data.useMana(amount, ManaCost.SKILL_EFFECT);
	}

	/** Grants the given player some mana */
	public final static void giveMana(OfflinePlayer player, double amount)
	{
		PlayerData data = SkillAPI.getPlayerData(player);
		if (data != null)
			data.giveMana(amount, ManaSource.SKILL);
	}

	/** Broadcasts that the given skill was used by the given player */
	public static void broadcast(Player player, PlayerSkill skill)
	{
		if (skill != null && player != null)
			skill.getData().sendMessage(player, SkillAPI.getSettings().getMessageRadius());
	}
}
