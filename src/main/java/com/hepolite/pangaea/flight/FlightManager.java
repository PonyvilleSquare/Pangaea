package com.hepolite.pangaea.flight;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerAllowFlightEvent;
import com.hepolite.pangaea.hunger.HungerNode;
import com.hepolite.pangaea.hunger.HungerSettings;
import com.hepolite.pangaea.movement.MovementSettings;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.hepolite.pillar.listener.Listener;
import com.sucy.skill.api.player.PlayerClass;

public class FlightManager extends Listener
{
	private final HashSet<UUID> exhaustedPlayers = new HashSet<UUID>();
	private final HashSet<UUID> warnedPlayers = new HashSet<UUID>();

	@Override
	public void onTick()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			handleFlightPermission(player);
		}
	}

	/** Performs the flight allowing check for the given player */
	public final void handleFlightPermission(Player player)
	{
		PermissionUser user = PermissionsEx.getUser(player);
		boolean permBasic = user.has("pangaea.basic.flight", player.getWorld().getName());
		boolean permAdmin = user.has("pangaea.admin.flight", player.getWorld().getName()) || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
		player.setAllowFlight(permBasic || permAdmin);

		if (!permAdmin && player.getAllowFlight())
		{
			warnPlayerOfExhaustion(player);
			PlayerAllowFlightEvent event = new PlayerAllowFlightEvent(player, !checkFlightInWater(player) && !checkFlightVeryExhausted(player) && !checkFlightMildlyExhausted(player) && !checkFlightPotionEffects(player) && !checkFlightSpaceRequirements(player));
			post(event);
			if (!event.canFly())
			{
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		}
	}

	/** Performs the exhaustion subroutine for flight permissions */
	private final boolean checkFlightMildlyExhausted(Player player)
	{
		String raceName = SkillAPIHelper.getRaceName(player);
		HungerNode node = ((HungerSettings) Pangaea.getInstance().getHungerManager().getSettings()).getNode(player);
		MovementSettings settings = (MovementSettings) Pangaea.getInstance().getMovementManager().getSettings();

		float grounded = settings.getFloat(settings.getPath(raceName, "Default", "Exhaustion.grounded"));
		if (grounded > node.exhaustion)
			return false;

		for (int y = 0; y < 4; y++)
			if (player.getLocation().getBlock().getRelative(0, -y, 0).getType().isSolid())
				return true;
		return false;
	}

	/** Performs the extreme exhaustion subroutine for flight permissions */
	private final boolean checkFlightVeryExhausted(Player player)
	{
		String raceName = SkillAPIHelper.getRaceName(player);
		HungerNode node = ((HungerSettings) Pangaea.getInstance().getHungerManager().getSettings()).getNode(player);
		MovementSettings settings = (MovementSettings) Pangaea.getInstance().getMovementManager().getSettings();

		float failing = settings.getFloat(settings.getPath(raceName, "Default", "Exhaustion.failing"));
		return (failing <= node.exhaustion);
	}

	/** Performs a check on the player's exhaustion values, will warn the player for relevant levels */
	private final void warnPlayerOfExhaustion(Player player)
	{
		String raceName = SkillAPIHelper.getRaceName(player);
		HungerNode node = ((HungerSettings) Pangaea.getInstance().getHungerManager().getSettings()).getNode(player);
		MovementSettings settings = (MovementSettings) Pangaea.getInstance().getMovementManager().getSettings();

		float grounded = settings.getFloat(settings.getPath(raceName, "Default", "Exhaustion.grounded"));
		if (grounded > node.exhaustion)
		{
			if (exhaustedPlayers.contains(player.getUniqueId()))
			{
				Chat.message(player, "&bYou are no longer too tired to take off again!");
				exhaustedPlayers.remove(player.getUniqueId());
			}
		}
		else
		{
			if (!exhaustedPlayers.contains(player.getUniqueId()))
			{
				Chat.message(player, "&cYou are now too tired to take off again if you land!");
				exhaustedPlayers.add(player.getUniqueId());
			}
		}
		float warning = settings.getFloat(settings.getPath(raceName, "Default", "Exhaustion.warning"));
		if (warning > node.exhaustion)
		{
			if (warnedPlayers.contains(player.getUniqueId()))
			{
				Chat.message(player, "&bYou no longer feel like rest is critical, but you're still tired");
				warnedPlayers.remove(player.getUniqueId());
			}
		}
		else
		{
			if (!warnedPlayers.contains(player.getUniqueId()))
			{
				Chat.message(player, "&cYou're going to need to rest soon!");
				warnedPlayers.add(player.getUniqueId());
			}
		}
	}

	/** Perform the potion effect subroutine for flight permissions */
	private final boolean checkFlightPotionEffects(Player player)
	{
		PlayerClass race = SkillAPIHelper.getRace(player);
		if (race == null)
			return true;
		MovementSettings settings = (MovementSettings) Pangaea.getInstance().getMovementManager().getSettings();
		List<PotionEffectType> types = settings.getPotionEffectTypes(settings.getPath(race.getData().getName(), "Default", "Flight.disabledByEffects"));
		for (PotionEffect effect : player.getActivePotionEffects())
		{
			if (types.contains(effect.getType()))
				return true;
		}
		return false;
	}

	/** Performs the in-fluid check subroutine for flight permissions */
	private final boolean checkFlightInWater(Player player)
	{
		PlayerData data = Database.getPlayerData(player);
		if (data.has("FlightManager.invertAir") && ((String) data.get("FlightManager.invertAir")).equals("true")) // This is weird simply because SkillAPI is only able to store strings via the Database command
			return !player.getEyeLocation().getBlock().isLiquid();
		else
			return player.getEyeLocation().getBlock().isLiquid();
	}

	/** Performs the space check subroutine for flight permissions */
	private final boolean checkFlightSpaceRequirements(Player player)
	{
		Block block = player.getLocation().getBlock();
		boolean mask[] = new boolean[27];
		for (int x = -1; x <= 1; x++)
			for (int y = 0; y <= 1; y++)
				for (int z = -1; z <= 1; z++)
					mask[index(x + 1, y, z + 1)] = !isBlockSolid(block.getRelative(x, y, z));

		// Figure out if the area is open or not
		for (int X = 0; X <= 1; X++)
			for (int Z = 0; Z <= 1; Z++)
			{
				if (mask[index(X, 0, Z)] && mask[index(X, 0, Z + 1)] && mask[index(X, 1, Z)] && mask[index(X, 1, Z + 1)] && mask[index(X + 1, 0, Z)] && mask[index(X + 1, 0, Z + 1)] && mask[index(X + 1, 1, Z)] && mask[index(X + 1, 1, Z + 1)])
					return false;
			}
		return true;
	}

	/** Returns true if the block is considered solid */
	private final boolean isBlockSolid(Block block)
	{
		Material type = block.getType();
		return (type != Material.STEP && type != Material.WOOD_STEP && type.isSolid());
	}

	private final int index(int x, int y, int z)
	{
		return z + 3 * (y + 3 * x);
	}
}
