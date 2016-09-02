package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillNightVision extends SkillCastTriggered
{
	public SkillNightVision()
	{
		super("Night Vision", false);
		setTickRate(200);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		if (Database.getPlayerData(player).has(getName()))
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 500, 0, true), true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = Database.getPlayerData(player);
		if (data.has(getName()))
		{
			Chat.message(player, "&fToggled Night Vision &coff&f. Please wait for your eyes to adapt...");
			data.remove(getName());
		}
		else
		{
			Chat.message(player, "&fToggled Night Vision &bon&f. Please wait for your eyes to adapt...");
			data.set(getName(), true);
		}
		return true;
	}
}
