package com.hepolite.pangaea.skills;

import java.util.HashMap;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerAllowFlightEvent;
import com.hepolite.pangaea.hunger.HungerSettings;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pangaea.utility.TimeHelper;
import com.hepolite.pangaea.utility.WeatherHelper;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.utility.Damager;
import com.shampaggon.crackshot.events.WeaponPrepareShootEvent;

public class SkillDisguise extends Skill
{
	private int timer = 0;

	public SkillDisguise()
	{
		super("Disguise");

		registerDisguiseInteraction(DisguiseType.COW, new DisguiseInteractionCow());
		registerDisguiseInteraction(DisguiseType.MUSHROOM_COW, new DisguiseInteractionMushroomCow());
		registerDisguiseInteraction(DisguiseType.SHEEP, new DisguiseInteractionSheep());
		registerDisguiseInteraction(DisguiseType.WOLF, new DisguiseInteractionWolf());
	}

	@Override
	public void onTick()
	{
		timer++;
		updatePlayerDisguises();
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////////
	// DISGUISE MAIN SETTINGS // DISGUISE MAIN SETTINGS // DISGUISE MAIN SETTINGS // DISGUISE MAIN SETTINGS //
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////

	// The current disguises players have
	private final HashMap<UUID, Disguise> playerDisguises = new HashMap<UUID, Disguise>();
	private Disguise tempDisguise = null;	// Used because LibsDisguises is stupid and fires DisguiseEvent BEFORE UndisguiseEvent...

	/** Sets the current player disguise */
	private final void setPlayerDisguise(Player player, Disguise disguise)
	{
		UUID uuid = player.getUniqueId();
		if (disguise == null)
		{
			if (tempDisguise != null)
				playerDisguises.put(uuid, tempDisguise);
			else
				playerDisguises.remove(uuid);
			tempDisguise = null;
		}
		else
		{
			if (playerDisguises.containsKey(uuid))
				tempDisguise = disguise;
			playerDisguises.put(uuid, disguise);
		}
	}

	/** Returns the disguise the player has, or null if the player has no disguise */
	public final Disguise getPlayerDisguise(Player player)
	{
		return player == null ? null : playerDisguises.get(player.getUniqueId());
	}

	/** Removes disguises from all offline players */
	private final void updatePlayerDisguises()
	{
		List<UUID> playersToRemove = new LinkedList<UUID>();
		for (Entry<UUID, Disguise> entry : playerDisguises.entrySet())
		{
			Player player = Bukkit.getPlayer(entry.getKey());
			if (player == null)
				playersToRemove.add(entry.getKey());
			else
				handleDisguiseAbilities(player, entry.getValue());
		}
		for (UUID uuid : playersToRemove)
			playerDisguises.remove(uuid);
		playersToRemove.clear();
	}

	// ///////////////////////////////////////////////////// EVENT HANDLING MAIN SETTINGS

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDisguise(DisguiseEvent event)
	{
		if (event.getEntity() instanceof Player)
			setPlayerDisguise((Player) event.getEntity(), event.getDisguise());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerUndisguise(UndisguiseEvent event)
	{
		if (event.getEntity() instanceof Player)
			setPlayerDisguise((Player) event.getEntity(), null);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// DISGUISE ABILITIES // DISGUISE ABILITIES // DISGUISE ABILITIES // DISGUISE ABILITIES // DISGUISE ABILITIES //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Returns a string list of the given key in the config, for the given player */
	private final List<String> getStringList(Player player, String setting)
	{
		return getSettings().getStringList(SkillAPIHelper.getRaceName(player) + "." + getName() + "." + setting);
	}

	/** Low-level method to handle disguise abilities */
	private final void handleDisguiseAbilities(Player player, Disguise disguise)
	{
		if (timer % 20 != 0)
			return;
		if (isAllowedGeneric(player, disguise, "harmedBySun"))
			handleBurning(player);
		if (isAllowedGeneric(player, disguise, "harmedByWater"))
			handleRaining(player);
		if (isAllowedGeneric(player, disguise, "harmedByAir"))
			handleDrowning(player);
	}

	/** Checks if the given player is allowed to fly with the given disguise */
	private final boolean isAllowedToFly(Player player, Disguise disguise)
	{
		if (disguise.isPlayerDisguise())
		{
			@SuppressWarnings("deprecation")
			OfflinePlayer target = Bukkit.getOfflinePlayer(((PlayerDisguise) disguise).getName());
			if (!target.hasPlayedBefore())
				return false;

			for (String flySkill : getStringList(player, "flySkills"))
			{
				if (SkillAPIHelper.getSkill(target, flySkill) != null)
					return true;
			}
		}
		else
			return isAllowedGeneric(player, disguise, "flyMobs");
		return false;
	}

	/** Checks if the player is allowed to perform the given task, using the given disguise */
	private final boolean isAllowedGeneric(Player player, Disguise disguise, String task)
	{
		String type = disguise.getType().toReadable().replaceAll(" ", "");
		for (String string : getStringList(player, task))
		{
			if (type.equalsIgnoreCase(string))
				return true;
		}
		return false;
	}

	/** Checks if the player is allowed to use items */
	private final boolean isAllowedUseItem(Player player)
	{
		Disguise disguise = getPlayerDisguise(player);
		return disguise == null || isAllowedGeneric(player, disguise, "useItem");
	}

	// /////////////////////////////////////////////////////

	/** Checks if the player should be burning */
	private final void handleBurning(Player player)
	{
		if (!TimeHelper.isSunUp(player.getWorld()))
			return;
		if (player.getLocation().getBlock().getLightFromSky() == 15)
		{
			ItemStack helmet = player.getInventory().getHelmet();
			if (helmet != null && helmet.getType() != Material.AIR)
				return;
			if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))
				return;

			player.setFireTicks(Math.max(player.getFireTicks(), 60));
			Damager.setNextDeathMessage("<player> had an insufficient sunblock factor");
			Damager.doDamage(1.0, player, DamageCause.FIRE_TICK);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
		}
	}

	/** Checks if the player should be damaged by rain */
	private final void handleRaining(Player player)
	{
		Location location = player.getLocation();
		Material type = location.getBlock().getType();
		if (WeatherHelper.isRaining(location))
		{
			Damager.setNextDeathMessage("<player> was unable to find shelter from the rain");
			Damager.doDamage(2.0, player, DamageCause.DROWNING);
			player.getWorld().playSound(location, Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
		}
		if (type == Material.STATIONARY_WATER || type == Material.WATER)
		{
			Damager.setNextDeathMessage("<player> could not handle water");
			Damager.doDamage(2.0, player, DamageCause.DROWNING);
			player.getWorld().playSound(location, Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
		}
	}

	/** Checks if the player should be drowning or not */
	private final void handleDrowning(Player player)
	{
		if (player.getLocation().getBlock().isLiquid())
			player.setRemainingAir(300);
		else
		{
			player.setRemainingAir(0);
			Damager.setNextDeathMessage("<player> suffocated in air");
			Damager.doDamage(1.0, player, DamageCause.DROWNING);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
		}
	}

	// ///////////////////////////////////////////////////// EVENT HANDLING DISGUISE ABILITIES

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerFlightCheck(PlayerAllowFlightEvent event)
	{
		Player player = event.getPlayer();
		Disguise disguise = getPlayerDisguise(player);
		if (disguise != null && !isAllowedToFly(player, disguise))
			event.setCanFly(false);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		Disguise disguise = getPlayerDisguise(player);
		if (disguise != null && !isAllowedGeneric(player, disguise, "itemPickup"))
		{
			if (timer % 50 == 0)
				player.sendMessage(ChatColor.RED + "You are unable to pick up items in your current form");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBreakBlock(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		Disguise disguise = getPlayerDisguise(player);
		if (disguise != null && !isAllowedGeneric(player, disguise, "blockBreak"))
		{
			player.sendMessage(ChatColor.RED + "You are unable to break blocks in your current form");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPlaceBlock(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		Disguise disguise = getPlayerDisguise(player);
		if (disguise != null && !isAllowedGeneric(player, disguise, "blockPlace"))
		{
			player.sendMessage(ChatColor.RED + "You are unable to place that in your current form");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerUseItem(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item != null && ((HungerSettings) Pangaea.getInstance().getHungerManager().getSettings()).canPlayerEat(player, item))
			return;

		if (!isAllowedUseItem(player))
		{
			player.sendMessage(ChatColor.RED + "You are unable to use that in your current form");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerFireWeapon(WeaponPrepareShootEvent event)
	{
		Player player = event.getPlayer();
		if (!isAllowedUseItem(player))
		{
			player.sendMessage(ChatColor.RED + "You are unable to use that in your current form");
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTakeDamage(EntityDamageByEntityEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		Player player = (Player) event.getEntity();
		Disguise disguise = getPlayerDisguise(player);
		if (disguise != null && isAllowedGeneric(player, disguise, "additionalDamageMobs"))
		{
			float modifier = getSettings().getFloat(SkillAPIHelper.getRaceName(player) + "." + getName() + ".additionalDamage");
			event.setDamage(event.getDamage() * (1.0f + modifier));
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////////////////
	// DISGUISE INTERACTIONS // DISGUISE INTERACTIONS // DISGUISE INTERACTIONS // DISGUISE INTERACTIONS //
	// ///////////////////////////////////////////////////////////////////////////////////////////////////

	private final HashMap<DisguiseType, DisguiseInteraction> disguiseInteractions = new HashMap<>();

	/** Registers a disguise interaction to the system */
	private final void registerDisguiseInteraction(DisguiseType type, DisguiseInteraction interaction)
	{
		disguiseInteractions.put(type, interaction);
	}

	/** Returns an interaction for the given disguise type */
	private final DisguiseInteraction getDisguiseInteraction(DisguiseType type)
	{
		return disguiseInteractions.get(type);
	}

	/** Used to handle players interacting with different types of disguises */
	private abstract class DisguiseInteraction
	{
		protected final Random random = new Random();

		/** Invoked whenever a player right-clicks a disguised player */
		public abstract void onInteract(Player disguised, Player observer);

		/** Returns the item the given player is holding */
		protected final ItemStack getPlayerMainItem(Player player)
		{
			return player.getInventory().getItemInMainHand();
		}

		/** Sets the item the given player is holding */
		protected final void setPlayerMainItem(Player player, ItemStack item)
		{
			player.getInventory().setItemInMainHand(item);
		}
	}

	/** Used to handle player interaction with dyeable disguises */
	private abstract class DisguiseInteractionDyeable extends DisguiseInteraction
	{
		@Override
		public void onInteract(Player disguised, Player observer)
		{
			Disguise disguise = getPlayerDisguise(disguised);
			ItemStack item = getPlayerMainItem(observer);
			if (item == null || item.getType() != Material.INK_SACK)
				return;

			AnimalColor color = getColor(disguise);
			AnimalColor newColor = AnimalColor.getColor(15 - item.getDurability());

			if (!color.equals(newColor))
			{
				Chat.message(disguised, ChatColor.RED + "You were colored by someone! Your new color is " + newColor.toString().toLowerCase() + "!");

				setColor(disguise, newColor);
				item.setAmount(item.getAmount() - 1);
				if (item.getAmount() <= 0)
					item = null;
				setPlayerMainItem(observer, item);
			}
		}

		/** Returns the color of the disguise */
		protected abstract AnimalColor getColor(Disguise disguise);

		/** Sets the color of the disguise */
		protected abstract void setColor(Disguise disguise, AnimalColor color);
	}

	/** Used to handle interactions with farm animals */
	private abstract class DisguiseInteractionHarvestable extends DisguiseInteraction
	{
		@Override
		public void onInteract(Player disguised, Player observer)
		{
			ItemStack item = getPlayerMainItem(observer);
			if (item != null)
				setPlayerMainItem(observer, handleInteraction(disguised, observer, item));
		}

		/** Invoked whenever the disguised player has an item interacted upon them */
		protected abstract ItemStack handleInteraction(Player disguised, Player observer, ItemStack item);
	}

	/** Used to handle interactions with sheep disguise */
	private final class DisguiseInteractionSheep extends DisguiseInteractionDyeable
	{
		@Override
		public void onInteract(Player disguised, Player observer)
		{
			super.onInteract(disguised, observer);

			ItemStack item = getPlayerMainItem(observer);
			if (item != null && item.getType() == Material.SHEARS)
				item = handleShearing(disguised, item);
			setPlayerMainItem(observer, item);
		}

		@Override
		protected AnimalColor getColor(Disguise disguise)
		{
			return ((SheepWatcher) disguise.getWatcher()).getColor();
		}

		@Override
		protected void setColor(Disguise disguise, AnimalColor color)
		{
			((SheepWatcher) disguise.getWatcher()).setColor(color);
		}

		/** Handles the shearing of the given player */
		private final ItemStack handleShearing(Player player, ItemStack item)
		{
			Disguise disguise = getPlayerDisguise(player);
			SheepWatcher sheep = (SheepWatcher) disguise.getWatcher();
			if (sheep.isSheared())
				return item;

			float hungerCost = getSettings().getFloat(SkillAPIHelper.getRaceName(player) + "." + getName() + ".woolCost");
			Pangaea.getInstance().getHungerManager().changeSaturation(player, hungerCost);
			player.sendMessage(ChatColor.RED + "You were sheared by someone! Your wool was taken away!");

			sheep.setSheared(true);
			item.setDurability((short) (item.getDurability() + 1));
			if (item.getDurability() >= item.getType().getMaxDurability())
				item = null;
			ItemStack wool = new ItemStack(Material.WOOL, 1 + random.nextInt(2), (short) sheep.getColor().getId());
			player.getWorld().dropItemNaturally(player.getLocation(), wool);
			return item;
		}
	}

	/** Used to handle interactions with wolf disguise */
	private final class DisguiseInteractionWolf extends DisguiseInteractionDyeable
	{
		@Override
		protected AnimalColor getColor(Disguise disguise)
		{
			return ((WolfWatcher) disguise.getWatcher()).getCollarColor();
		}

		@Override
		protected void setColor(Disguise disguise, AnimalColor color)
		{
			((WolfWatcher) disguise.getWatcher()).setCollarColor(color);
		}
	}

	/** Used to handle interactions with cow disguises */
	private final class DisguiseInteractionCow extends DisguiseInteractionHarvestable
	{
		@Override
		protected ItemStack handleInteraction(Player disguised, Player observer, ItemStack item)
		{
			if (item.getType() != Material.BUCKET)

				return item;
			Chat.message(disguised, ChatColor.RED + "Someone milked you! How disturbing!");
			float hungerCost = getSettings().getFloat(SkillAPIHelper.getRaceName(disguised) + "." + getName() + ".milkCost");
			Pangaea.getInstance().getHungerManager().changeSaturation(disguised, hungerCost);
			item.setType(Material.MILK_BUCKET);
			return item;
		}
	}

	/** Used to handle interactions with cow disguises */
	private final class DisguiseInteractionMushroomCow extends DisguiseInteractionHarvestable
	{
		@Override
		protected ItemStack handleInteraction(Player disguised, Player observer, ItemStack item)
		{
			if (item.getType() != Material.BOWL)
				return item;

			disguised.sendMessage(ChatColor.RED + "Someone harvested mushrooms from you!");
			float hungerCost = getSettings().getFloat(SkillAPIHelper.getRaceName(disguised) + "." + getName() + ".mushroomCost");
			Pangaea.getInstance().getHungerManager().changeSaturation(disguised, hungerCost);

			item.setAmount(item.getAmount());
			if (item.getAmount() <= 0)
			{
				item.setType(Material.MUSHROOM_SOUP);
				item.setAmount(1);
			}
			else
				observer.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));

			return item;
		}
	}

	// ///////////////////////////////////////////////////// EVENT HANDLING DISGUISE INTERACTION

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEntityEvent event)
	{
		if (!(event.getRightClicked() instanceof Player))
			return;
		Player observer = event.getPlayer();
		Player disguised = (Player) event.getRightClicked();
		Disguise disguise = getPlayerDisguise(disguised);
		if (disguise == null)
			return;

		DisguiseInteraction interaction = getDisguiseInteraction(disguise.getType());
		if (interaction != null)
			interaction.onInteract(disguised, observer);
	}
}
