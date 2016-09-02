package com.hepolite.pangaea.movement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.core.Manager;
import com.hepolite.pangaea.events.PlayerImpactGroundEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.utility.Damager;
import com.sucy.skill.api.player.PlayerClass;

public class MovementManager extends Manager
{
	private int timer = 0;

	public MovementManager()
	{
		super(new MovementSettings());

		registerModifier(new FallDamageModifier());
	}

	@Override
	public void onTick()
	{
		timer++;
		if (timer % 100 == 0)
			updatePlayerSpeeds();
		updatePlayerGroundImpact();
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	// MOVEMENT // MOVEMENT // MOVEMENT // MOVEMENT // MOVEMENT // MOVEMENT // MOVEMENT //
	// ///////////////////////////////////////////////////////////////////////////////////

	// Player ground state
	private final HashSet<UUID> flyingPlayers = new HashSet<UUID>();

	private boolean ignoreNextFallEvent = false;

	// List of all the modifiers that can modify fall damage
	private final List<IFallDamageModifier> fallDamageModifiers = new ArrayList<IFallDamageModifier>();

	/** Registers a movement modifier to the system */
	public final void registerModifier(IFallDamageModifier modifier)
	{
		if (modifier != null)
			fallDamageModifiers.add(modifier);
	}

	/** Returns the true if the player was flying */
	private final boolean isPlayerFlying(Player player)
	{
		return flyingPlayers.contains(player.getUniqueId());
	}

	/** Sets the airborne state for players */
	private final void setPlayerFlightState(Player player, boolean isFlying)
	{
		if (isFlying)
			flyingPlayers.add(player.getUniqueId());
		else
			flyingPlayers.remove(player.getUniqueId());
	}

	/** Handle the case of players impacting with the ground */
	@SuppressWarnings("deprecation")
	private final void updatePlayerGroundImpact()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (player.isOnGround())
			{
				if (!isPlayerFlying(player))
					continue;

				// Special interaction with slime blocks; ignore fall damage when landing on them
				Block block = player.getLocation().getBlock().getRelative(0, -1, 0);
				if (block.getType() == Material.SLIME_BLOCK)
					continue;

				Vector velocity = Pangaea.getInstance().getSkillManager().getPlayerVelocity(player);
				if (velocity != null && velocity.getY() < 0.0)
					post(new PlayerImpactGroundEvent(player, velocity.getY()));
				setPlayerFlightState(player, false);
			}
			else
				setPlayerFlightState(player, true);
		}
	}

	/** Used to calculate the fall damage, will intercept the old system in favor of a custom system */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerImpactGround(PlayerImpactGroundEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;

		double damage = calculateFallDamage(player);
		if (damage > 0.0)
		{
			// if (Database.getPlayerData(player).has("MovementManager.falldamagenegation"))
			// {
			// Database.getPlayerData(player).remove("MovementManager.falldamagenegation");
			// return;
			// }

			ignoreNextFallEvent = true;
			Damager.doDamage(damage, player, DamageCause.FALL);
			ignoreNextFallEvent = false;
		}
	}

	/** Used to detect whenever a player use a portal, will give them fall damage immunity every time they teleport */
	// @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	// public void onPlayerTeleport(PlayerPortalEvent event)
	// {
	// This is just a hack-ish way of doing this, might want to rethink how this is done in the future
	// Field used under "onPlayerImpactGround"
	// Database.getPlayerData(event.getPlayer()).set("MovementManager.falldamagenegation", true);
	// }

	/** Intercepts the fall damage event, allowing the custom damage system to be prioritized */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTakeFallDamage(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER || event.getCause() != DamageCause.FALL || ignoreNextFallEvent)
			return;
		event.setCancelled(true);
	}

	/** Calculates the fall damage the given player will receive */
	public final double calculateFallDamage(Player player)
	{
		Vector velocity = Pangaea.getInstance().getSkillManager().getPlayerVelocity(player);
		if (velocity == null)
			return 0.0;

		double v = Math.abs(velocity.getY());
		final double minV = Math.sqrt(2.0 * 0.08 * 4.0);	// 0.8
		final double maxV = Math.sqrt(2.0 * 0.08 * 23.0);	// 1.92
		double damage = 20.0 * (v - minV) / (maxV - minV);
		if (damage <= 0.0)
			return 0.0;

		float flat = 0.0f, multiplicative = 0.0f;
		for (IFallDamageModifier modifier : fallDamageModifiers)
		{
			flat += modifier.getFlat(player);
			multiplicative += modifier.getMultiplier(player);
		}
		return damage * (1.0 + multiplicative) + flat;
	}

	// ///////////////////////////////////////////////////////////////////////////////////

	// List of all the modifiers that can modify speeds
	private final List<IMovementModifier> movementModifiers = new ArrayList<IMovementModifier>();

	/** Registers a movement modifier to the system */
	public final void registerModifier(IMovementModifier modifier)
	{
		if (modifier != null)
			movementModifiers.add(modifier);
	}

	/** Handles the speed of players */
	private final void updatePlayerSpeeds()
	{
		MovementSettings settings = (MovementSettings) this.settings;
		for (Player player : Bukkit.getOnlinePlayers())
		{
			PlayerClass race = SkillAPIHelper.getRace(player);
			float walkSpeed = settings.getFloat(settings.getPath(race == null ? "" : race.getData().getName(), "Default", "Ground.baseSpeed"));
			float flySpeed = settings.getFloat(settings.getPath(race == null ? "" : race.getData().getName(), "Default", "Flight.baseSpeed"));
			float walkModifier = 1.0f;
			float flyModifier = 1.0f;

			for (IMovementModifier modifier : movementModifiers)
			{
				walkModifier += modifier.getGroundModifier(player);
				flyModifier += modifier.getFlightModifier(player);
			}

			player.setWalkSpeed(Math.max(0.0f, Math.min(0.999f, walkSpeed * walkModifier)));
			player.setFlySpeed(Math.max(0.0f, Math.min(0.999f, flySpeed * flyModifier)));
		}
	}
}
