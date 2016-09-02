package com.hepolite.pangaea.skills;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import me.libraryaddict.disguise.disguisetypes.AnimalColor;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SheepWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.WolfWatcher;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerAllowFlightEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pangaea.utility.TimeHelper;
import com.hepolite.pillar.utility.Damager;
import com.sucy.skill.api.player.PlayerClass;

public class SkillDisguise extends Skill
{
	private final HashMap<UUID, Disguise> playerDisguises = new HashMap<UUID, Disguise>();
	private final HashSet<UUID> playersHadDisguise = new HashSet<UUID>();

	private int timer = 0;

	public SkillDisguise()
	{
		super("Disguise");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDisguise(DisguiseEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();
		if (playerDisguises.containsKey(uuid))
			playersHadDisguise.add(uuid);
		playerDisguises.put(player.getUniqueId(), event.getDisguise());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerUndisguise(UndisguiseEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		UUID uuid = player.getUniqueId();
		if (playersHadDisguise.contains(uuid))
			playersHadDisguise.remove(uuid);
		else
			playerDisguises.remove(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDisconnect(PlayerQuitEvent event)
	{
		playerDisguises.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerFlightCheck(PlayerAllowFlightEvent event)
	{
		Player player = event.getPlayer();
		Disguise disguise = playerDisguises.get(player.getUniqueId());
		if (disguise == null)
			return;
		EntityType type = disguise.getType().getEntityType();
		if (type != EntityType.BAT && type != EntityType.BLAZE && type != EntityType.CHICKEN && type != EntityType.ENDER_DRAGON && type != EntityType.GHAST && type != EntityType.PLAYER)
		{
			event.setCanFly(false);
			return;
		}
		if (!disguise.isPlayerDisguise())
			return;
		PlayerDisguise d = (PlayerDisguise) disguise;
		@SuppressWarnings("deprecation")
		Player target = Bukkit.getPlayer(d.getName());
		PlayerClass race = SkillAPIHelper.getRace(target);
		String raceName = (race == null ? "" : race.getData().getName());
		if (raceName.equals("Bat Pony") || raceName.equals("Changeling") || raceName.equals("Dragon") || raceName.equals("Pegasus") || raceName.equals("Gryphon"))
			return;
		event.setCanFly(false);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEntityEvent event)
	{
		if (!(event.getRightClicked() instanceof Player))
			return;
		Player player = event.getPlayer();
		Player target = (Player) event.getRightClicked();

		Disguise disguise = playerDisguises.get(target.getUniqueId());
		ItemStack item = player.getInventory().getItemInMainHand();
		if (disguise == null || item == null)
			return;

		// Regular cows give milk when a bucket is applied on them
		if (item.getType() == Material.BUCKET && disguise.getType() == DisguiseType.COW)
		{
			target.sendMessage(ChatColor.RED + "Someone milked you! How disturbing!");
			Pangaea.getInstance().getHungerManager().changeSaturation(target, -20.0f);

			item.setType(Material.MILK_BUCKET);
		}
		// Mushroom cows give mushroom soup when bowls are applied on them
		else if (item.getType() == Material.BOWL && disguise.getType() == DisguiseType.MUSHROOM_COW)
		{
			target.sendMessage(ChatColor.RED + "Someone harvested mushrooms from you!");
			Pangaea.getInstance().getHungerManager().changeSaturation(target, -70.0f);

			item.setAmount(item.getAmount());
			if (item.getAmount() <= 0)
			{
				item.setType(Material.MUSHROOM_SOUP);
				item.setAmount(1);
			}
			else
				player.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
		}
		// Coloring wolves and sheep
		else if (item.getType() == Material.INK_SACK && (disguise.getType() == DisguiseType.SHEEP || disguise.getType() == DisguiseType.WOLF))
		{
			AnimalColor color = (disguise.getType() == DisguiseType.SHEEP ? ((SheepWatcher) disguise.getWatcher()).getColor() : ((WolfWatcher) disguise.getWatcher()).getCollarColor());
			AnimalColor newColor = AnimalColor.getColor(15 - item.getDurability());

			if (!color.equals(newColor))
			{
				target.sendMessage(ChatColor.RED + "You were colored by someone! Your new color is " + newColor.toString().toLowerCase() + "!");

				if (disguise.getType() == DisguiseType.SHEEP)
					((SheepWatcher) disguise.getWatcher()).setColor(newColor);
				else if (disguise.getType() == DisguiseType.WOLF)
					((WolfWatcher) disguise.getWatcher()).setCollarColor(newColor);
				item.setAmount(item.getAmount());
				if (item.getAmount() <= 0)
					item = null;
			}
		}
		// Shearing sheep yields wool
		else if (item.getType() == Material.SHEARS && disguise.getType() == DisguiseType.SHEEP)
		{
			SheepWatcher sheep = (SheepWatcher) disguise.getWatcher();
			if (!sheep.isSheared())
			{
				target.sendMessage(ChatColor.RED + "You were sheared by someone! Your wool was taken away!");
				Pangaea.getInstance().getHungerManager().changeSaturation(target, -25.0f);

				sheep.setSheared(true);
				item.setDurability((short) (item.getDurability() + 1));
				if (item.getDurability() >= item.getType().getMaxDurability())
					item = null;
				Random random = new Random();
				ItemStack wool = new ItemStack(Material.WOOL, 1 + random.nextInt(2), (short) sheep.getColor().getId());
				target.getWorld().dropItemNaturally(target.getLocation(), wool);
			}
		}

		player.getInventory().setItemInMainHand(item);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Disguise disguise = playerDisguises.get(event.getPlayer().getUniqueId());
		if (disguise != null && disguise.getType() != DisguiseType.PLAYER)
		{
			if (timer % 50 == 0)
				event.getPlayer().sendMessage(ChatColor.RED + "You are unable to pick up items in your current form");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPlaceBlock(BlockPlaceEvent event)
	{
		if (event.getPlayer() == null)
			return;
		Disguise disguise = playerDisguises.get(event.getPlayer().getUniqueId());
		if (disguise != null && disguise.getType() != DisguiseType.PLAYER)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "You are unable to place in your current form");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		if (event.getPlayer() == null)
			return;
		Disguise disguise = playerDisguises.get(event.getPlayer().getUniqueId());
		if (disguise != null && disguise.getType() != DisguiseType.PLAYER)
		{
			event.getPlayer().sendMessage(ChatColor.RED + "You are unable to break blocks in your current form");
			event.setCancelled(true);
		}
	}

	@Override
	public void onTick()
	{
		timer++;
		List<UUID> playersToRemove = new LinkedList<UUID>();
		for (Entry<UUID, Disguise> entry : playerDisguises.entrySet())
		{
			Player player = Bukkit.getPlayer(entry.getKey());
			if (player == null)
				playersToRemove.add(entry.getKey());
			else
				process(player, entry.getValue());
		}
		for (UUID uuid : playersToRemove)
			playerDisguises.remove(uuid);
		playersToRemove.clear();
	}

	/** Process the player, checking against all conditions and everything that is relevant */
	private final void process(Player player, Disguise disguise)
	{
		if (timer % 20 == 0)
		{
			EntityType type = disguise.getType().getEntityType();
			handleBurning(player, type);
			handleDrowning(player, type);
			handleRaining(player, type);
		}
	}

	/** Checks if the player should be burning */
	private final void handleBurning(Player player, EntityType type)
	{
		if ((type != EntityType.SKELETON && type != EntityType.ZOMBIE) || !TimeHelper.isSunUp(player.getWorld()))
			return;
		if (player.getLocation().getBlock().getLightFromSky() == 15)
		{
			ItemStack helmet = player.getInventory().getHelmet();
			if (helmet != null && helmet.getType() != Material.AIR)
				return;
			if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
				return;

			player.setFireTicks(Math.max(player.getFireTicks(), 60));
			Damager.doDamage(1.0, player, DamageCause.FIRE_TICK);
		}
	}

	/** Checks if the player should be damaged by rain */
	private final void handleRaining(Player player, EntityType type)
	{
		if (type != EntityType.ENDERMAN && type != EntityType.BLAZE)
			return;
		if (player.getLocation().getBlock().getLightFromSky() == 15 && player.getWorld().hasStorm())
			Damager.doDamage(2.0, player, DamageCause.DROWNING);
	}

	/** Checks if the player should be drowning or not */
	private final void handleDrowning(Player player, EntityType type)
	{
		if (type == EntityType.SQUID)
		{
			if (player.getLocation().getBlock().isLiquid())
				player.setRemainingAir(300);
			else
			{
				player.setRemainingAir(0);
				Damager.doDamage(1.0, player, DamageCause.DROWNING);
			}
		}
	}
}
