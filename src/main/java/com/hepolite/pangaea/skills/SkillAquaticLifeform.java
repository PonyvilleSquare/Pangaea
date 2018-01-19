package com.hepolite.pangaea.skills;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.events.PlayerAirChangeEvent;
import com.hepolite.pangaea.events.PlayerSaturationChangeEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.utility.Damager;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillAquaticLifeform extends Skill
{
	private final HashMap<UUID, Float> playerAir = new HashMap<UUID, Float>();
	private final HashMap<UUID, Integer> playerWarnings = new HashMap<UUID, Integer>();
	private final HashMap<UUID, BossBar> playerOxygenBars = new HashMap<UUID, BossBar>();

	public SkillAquaticLifeform()
	{
		super("Aquatic Lifeform");
		setTickRate(20);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		initializeAir(player);
		updateAir(player, race.getData().getName() + "." + getName() + ".");
	}

	/** Handles the initial setting of player air, if needed */
	private final void initializeAir(Player player)
	{
		UUID uuid = player.getUniqueId();
		if (!playerAir.containsKey(uuid))
			playerAir.put(uuid, (float) player.getMaximumAir());
		if (!playerWarnings.containsKey(uuid))
			playerWarnings.put(uuid, 0);
		if (!playerOxygenBars.containsKey(uuid))
			playerOxygenBars.put(uuid, Bukkit.createBossBar("Oxygen", BarColor.BLUE, BarStyle.SEGMENTED_10));
	}

	/** Clears the data for the given player */
	private final void clearAirData(Player player)
	{
		UUID uuid = player.getUniqueId();
		playerAir.remove(uuid);
		playerWarnings.remove(uuid);
	}

	/** Handles updating the air of the given player */
	private final void updateAir(Player player, String path)
	{
		if (player.getEyeLocation().getBlock().isLiquid())
			changeAir(player, getSettings().getFloat(path + "airGain"));
		else
			changeAir(player, -getSettings().getFloat(path + "airLoss"));

		if (getRemainingAir(player) <= 1.0f)
		{
			Damager.setNextDeathMessage("<player> was unable to breathe in air");
			Damager.doDamage(1.0, player, DamageCause.DROWNING);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
		}

		player.setRemainingAir((int) Math.round(getRemainingAir(player)));
	}

	/** Changes the remaining air for the player */
	private final void changeAir(Player player, float amount)
	{
		if (amount < 0)
		{
			float waterBreathingLevel = 0.0f;
			if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING))
				waterBreathingLevel += 10.0f;
			if (player.getInventory().getHelmet() != null)
				waterBreathingLevel += 0.5f * (float) player.getInventory().getHelmet().getEnchantmentLevel(Enchantment.OXYGEN);
			amount *= 1.0f / (1.0f + waterBreathingLevel);
		}

		float oldAir = getRemainingAir(player);
		float newAir = Math.max(0.0f, Math.min(player.getMaximumAir(), oldAir + amount));
		if (newAir == oldAir)
			return;

		PlayerAirChangeEvent event = new PlayerAirChangeEvent(player, oldAir, newAir);
		post(event);
		if (!event.isCancelled())
		{
			UUID uuid = player.getUniqueId();
			BossBar bar = playerOxygenBars.get(uuid);
			float air = Math.max(0.0f, Math.min(player.getMaximumAir(), event.getNewAir()));
			playerAir.put(uuid, air);
			bar.setProgress(air / (float) player.getMaximumAir());
			handleWarnings(player, air);

			if (air >= player.getMaximumAir())
				bar.removePlayer(player);
			else
				bar.addPlayer(player);
		}
	}

	/** Returns the air the player has left */
	public final float getRemainingAir(Player player)
	{
		UUID uuid = player.getUniqueId();
		return playerAir.containsKey(uuid) ? playerAir.get(uuid) : player.getRemainingAir();
	}

	/** Handle player warnings */
	private final void handleWarnings(Player player, float newAir)
	{
		float fraction = newAir / (float) player.getMaximumAir();
		int warnLevel = getWarningLevel(player);
		if (fraction < 0.02f && warnLevel < 6)
		{
			Chat.message(player, ChatColor.RED + "Need water, now!");
			setWarningLevel(player, 6);
		}
		else if (fraction < 0.06f && warnLevel < 5)
		{
			Chat.message(player, ChatColor.RED + "You feel as if you're about to suffocate!");
			setWarningLevel(player, 5);
		}
		else if (fraction < 0.1f && warnLevel < 4)
		{
			Chat.message(player, ChatColor.RED + "You feel that your body is running very low on oxygen!");
			setWarningLevel(player, 4);
		}
		else if (fraction < 0.2f && warnLevel < 3)
		{
			Chat.message(player, ChatColor.WHITE + "It's very uncomfortable being out of water for this long!");
			setWarningLevel(player, 3);
		}
		else if (fraction < 0.35f && warnLevel < 2)
		{
			Chat.message(player, ChatColor.WHITE + "You've been out of water for quite some time now...");
			setWarningLevel(player, 2);
		}
		else if (fraction < 0.5f && warnLevel < 1)
		{
			Chat.message(player, ChatColor.WHITE + "You're starting to feeling a little bit dizzy...");
			setWarningLevel(player, 1);
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0, true), true);
		}
		else if (fraction >= 0.1f && warnLevel > 5)
			setWarningLevel(player, 5);
		else if (fraction >= 0.25f && warnLevel > 3)
			setWarningLevel(player, 3);
		else if (fraction >= 0.4f && warnLevel > 2)
			setWarningLevel(player, 2);
		else if (fraction >= 0.55f && warnLevel > 1)
			setWarningLevel(player, 1);
		else if (fraction >= 0.75f && warnLevel > 0)
		{
			Chat.message(player, ChatColor.AQUA + "The coolness of the water is refreshing, you feel much better now!");
			setWarningLevel(player, 0);
		}
	}

	/** Returns the previous warn level for the player */
	private final int getWarningLevel(Player player)
	{
		UUID uuid = player.getUniqueId();
		return playerWarnings.containsKey(uuid) ? playerWarnings.get(uuid) : 0;
	}

	/** Sets the warn level for the given player */
	private final void setWarningLevel(Player player, int level)
	{
		playerWarnings.put(player.getUniqueId(), level);
	}

	// ////////////////////////////////////////////////////////////////////////

	/** Prevents players who die from suffocation to continuously suffocate when they respawn */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		clearAirData(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerLoseSaturation(PlayerSaturationChangeEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null || race == null || !player.getLocation().getBlock().isLiquid())
			return;

		if (event.getNewSaturation() < event.getOldSaturation())
		{
			float modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".energySaving");
			float change = event.getNewSaturation() - event.getOldSaturation();
			event.setNewSaturation(event.getOldSaturation() + (1.0f - modifier) * change);
		}
	}
}
