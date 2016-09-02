package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pangaea.events.PlayerSaturationChangeEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillWaterWorker extends SkillCastTriggered
{
	public SkillWaterWorker()
	{
		super("Water Worker", false);
		setTickRate(200);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		if (Database.getPlayerData(player).has(getName()) && isPlayerSubmerged(player))
		{
			int level = getSettings().getInt(race.getData().getName() + "." + getName() + ".workSpeed");
			player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 500, level, true), true);
		}
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerData data = Database.getPlayerData(player);
		if (data.has(getName()))
		{
			Chat.message(player, "&fToggled Water Worker &coff&f. Please wait for your body to adapt...");
			data.remove(getName());
		}
		else
		{
			Chat.message(player, "&fToggled Water Worker &bon&f. Please wait for your body to adapt...");
			data.set(getName(), true);
		}
		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLoseSaturation(PlayerSaturationChangeEvent event)
	{
		Player player = event.getPlayer();
		if (!Database.getPlayerData(player).has(getName()) || !isPlayerSubmerged(player))
			return;
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null)
			return;

		if (event.getNewSaturation() < event.getOldSaturation())
		{
			float modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".energySpending");
			float change = event.getNewSaturation() - event.getOldSaturation();
			event.setNewSaturation(event.getOldSaturation() + (1.0f + modifier) * change);
		}
	}

	/** Return true when the player is submerged */
	private final boolean isPlayerSubmerged(Player player)
	{
		return player.getLocation().getBlock().isLiquid() && player.getEyeLocation().getBlock().isLiquid();
	}
}
