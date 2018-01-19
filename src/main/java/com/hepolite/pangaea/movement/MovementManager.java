package com.hepolite.pangaea.movement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.core.Manager;
import com.hepolite.pangaea.events.PlayerImpactGroundEvent;
import com.hepolite.pangaea.events.PlayerLeaveGroundEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.utility.Damager;
import com.sucy.skill.api.player.PlayerClass;

public class MovementManager extends Manager
{
	private final Random random = new Random();
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
	private final HashSet<UUID> airbornePlayers = new HashSet<UUID>();
	private final HashMap<UUID, Integer> playerHeights = new HashMap<UUID, Integer>();

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
	private final boolean isPlayerAirborne(Player player)
	{
		return airbornePlayers.contains(player.getUniqueId());
	}

	/** Sets the airborne state for players */
	private final void setPlayerAirborneState(Player player, boolean isFlying)
	{
		if (isFlying)
			airbornePlayers.add(player.getUniqueId());
		else
			airbornePlayers.remove(player.getUniqueId());
	}

	/** Sets the previous known non-falling height for the given player */
	public final void setPlayerHeight(Player player, int height)
	{
		playerHeights.put(player.getUniqueId(), height);
	}

	/** Returns the previously known non-falling height for the given player */
	public final int getPlayerHeight(Player player)
	{
		UUID uuid = player.getUniqueId();
		return playerHeights.containsKey(uuid) ? playerHeights.get(uuid) : player.getLocation().getBlockY();
	}

	/** Handle the case of players impacting with the ground */
	private final void updatePlayerGroundImpact()
	{
		outer: for (Player player : Bukkit.getOnlinePlayers())
		{
			// boolean isPlayerOnGround = MovementHelper.isPlayerCompletelyOnGround(player);
			boolean isPlayerOnGround = player.isOnGround();
			Vector velocity = Pangaea.getInstance().getSkillManager().getPlayerVelocity(player);
			if (isPlayerOnGround)
			{
				int oldPlayerHeight = getPlayerHeight(player);
				setPlayerHeight(player, player.getLocation().getBlockY());
				if (!isPlayerAirborne(player))
					continue;

				// Special interaction with slime blocks; ignore fall damage when landing on them
				for (int i = 0; i < 10; i++)
					if (player.getLocation().getBlock().getRelative(0, -i, 0).getType() == Material.SLIME_BLOCK)
						continue outer;

				if (velocity != null && velocity.getY() < 0.0)
					post(new PlayerImpactGroundEvent(player, velocity.getY(), oldPlayerHeight - player.getLocation().getBlockY()));
				setPlayerAirborneState(player, false);
			}
			else
			{
				if (!isPlayerAirborne(player))
					post(new PlayerLeaveGroundEvent(player));
				setPlayerAirborneState(player, true);
			}
			if (player.isGliding())
			{
				double v = Math.max(0.0, Math.abs(velocity.getY()));
				setPlayerHeight(player, player.getLocation().getBlockY() + (int) (v * v / (2.0 * 0.08)));
			}
			if (player.isFlying() || (player.getLocation().getBlock().isLiquid() && Math.abs(velocity.getY()) < 0.6) || player.getLocation().getBlock().getType() == Material.LADDER || player.getLocation().getBlock().getType() == Material.VINE)
				setPlayerHeight(player, player.getLocation().getBlockY());
		}
	}

	/** Used to calculate the fall damage, will intercept the old system in favor of a custom system */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerImpactGround(PlayerImpactGroundEvent event)
	{
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;
		if (Database.getPlayerData(player).has("MovementManager.fallDamageBlock"))
			return;

		double damage = calculateFallDamage(player, event.getDistance());
		if (player.getLocation().getBlock().isLiquid())
			damage = 0.5 * damage - 8.0;
		if (damage > 0.0)
		{
			ignoreNextFallEvent = true;
		//	List<String> list = settings.getStringList("General.fallDeathMessages");
		//	String message = list.size() == 0 ? "<player> fell from a high place" : list.get(random.nextInt(list.size()));
		//	Damager.setNextDeathMessage(message);
			Damager.doDamage(damage, player, DamageCause.FALL);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_SMALL_FALL, 1.0f, 1.0f);
			ignoreNextFallEvent = false;
		}
	}

	/** Used to detect whenever a player teleport, will give them fall damage immunity every time they teleport */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		resetFallHeight(event.getPlayer(), event.getTo());
	}

	/** Used to detect whenever a player use a portal, will give them fall damage immunity every time they teleport */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event)
	{
		resetFallHeight(event.getPlayer(), event.getTo());
	}

	/** Intercepts the fall damage event, allowing the custom damage system to be prioritized */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTakeFallDamage(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER || event.getCause() != DamageCause.FALL || ignoreNextFallEvent)
			return;
		event.setCancelled(true);
	}

	/** Calculates the fall damage the given player will receive */
	public final double calculateFallDamage(Player player, double fallDistance)
	{
		// Vector velocity = Pangaea.getInstance().getSkillManager().getPlayerVelocity(player);
		// if (velocity == null)
		// return 0.0;
		// int startHeight = getPlayerFlightHeight(player);
		// int currentHeight = player.getLocation().getBlockY();

		// double v = Math.abs(velocity.getY());
		// double v = Math.sqrt(2.0 * 0.08 * (double) Math.max(startHeight - currentHeight, 0));
		double v = Math.sqrt(2.0 * 0.08 * Math.max(fallDistance, 0.0));
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

	/** Resets the fall stats for the given player, doing as much as possible to prevent random deaths */
	public final void resetFallHeight(Player player, Location location)
	{
		if (location != null)
			setPlayerHeight(player, location.getBlockY());
		else
			setPlayerHeight(player, player.getLocation().getBlockY());
		Database.getPlayerData(player).set("MovementManager.fallDamageBlock", true, 3);
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

	/** Force updates all player movement speeds */
	public final void forceUpdatePlayerSpeeds()
	{
		updatePlayerSpeeds();
	}

	/** Force updates the given player's movement speeds */
	public final void forceUpdatePlayerSpeeds(Player player)
	{
		updatePlayerSpeeds(player);
	}

	/** Handles the speed of all players */
	private final void updatePlayerSpeeds()
	{
		for (Player player : Bukkit.getOnlinePlayers())
			updatePlayerSpeeds(player);
	}

	/** Updates the player's speed */
	private final void updatePlayerSpeeds(Player player)
	{
		MovementSettings settings = (MovementSettings) this.settings;
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
