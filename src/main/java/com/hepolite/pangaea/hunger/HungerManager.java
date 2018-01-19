package com.hepolite.pangaea.hunger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.core.Manager;
import com.hepolite.pangaea.events.PlayerEatEvent;
import com.hepolite.pangaea.events.PlayerExhaustionChangeEvent;
import com.hepolite.pangaea.events.PlayerHungerChangeEvent;
import com.hepolite.pangaea.events.PlayerHungerUpdateMaxEvent;
import com.hepolite.pangaea.events.PlayerSaturationChangeEvent;
import com.hepolite.pangaea.hunger.HungerSettings.FoodNode;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.events.PotionEffectEvent;
import com.hepolite.pillar.settings.Settings.PotionEffectSetting;
import com.hepolite.pillar.utility.Damager;
import com.sucy.skill.api.player.PlayerClass;

public class HungerManager extends Manager
{
	// Control variables
	private final Random random = new Random();
	private int timer = 0;
	private boolean ignoreNextEatEvent = false;
	private boolean ignoreNextStarveEvent = true;

	public HungerManager()
	{
		super(new HungerSettings());

		killExoticGardenEatingSystem();
	}

	@Override
	public void onTick()
	{
		timer++;
		if (timer % 10 == 0)
			updateHungerValues();
		if (timer % 100 == 0)
			handleStarvingAndHealing();
		if (timer % 20 == 0)
			updateExhaustionValues();
		applyStatusEffects();
	}

	/** Invoked whenever the server is shutting down */
	public void onShutdown()
	{
		settings.save();
	}

	// ////////////////////////////////////////////////////////////////////////////////////

	/** Allows the hunger of a player to be changed by the given amount */
	public final void changeHunger(Player player, float amount)
	{
		HungerSettings settings = (HungerSettings) this.settings;
		HungerNode node = settings.getNode(player);
		PlayerHungerChangeEvent event = new PlayerHungerChangeEvent(player, node.hunger, node.hunger + amount, node.max);
		post(event);
		if (!event.isCancelled())
		{
			node.max = event.getMaxHunger();
			node.hunger = Math.max(0.0f, Math.min(event.getMaxHunger(), event.getNewHunger()));
			node.saturation = Math.min(node.hunger, node.saturation);
			player.setFoodLevel(Math.round(20.0f * node.hunger / node.max));
			player.setSaturation(20.0f * node.saturation / node.max);
		}
	}

	/** Allows the saturation of a player to be changed by the given amount */
	public final void changeSaturation(Player player, float amount)
	{
		HungerSettings settings = (HungerSettings) this.settings;
		HungerNode node = settings.getNode(player);
		PlayerSaturationChangeEvent event = new PlayerSaturationChangeEvent(player, node.saturation, node.saturation + amount, node.hunger);
		post(event);
		if (!event.isCancelled())
		{
			node.saturation = Math.min(node.hunger, event.getNewSaturation());
			if (node.saturation < 0.0f)
			{
				changeHunger(player, node.saturation);
				node.saturation = 0.0f;
			}
			player.setSaturation(20.0f * node.saturation / node.max);
		}
	}

	/** Allows the exhaustion of a player to be changed by the given amount */
	public final void changeExhaustion(Player player, float amount)
	{
		HungerSettings settings = (HungerSettings) this.settings;
		HungerNode node = settings.getNode(player);
		PlayerExhaustionChangeEvent event = new PlayerExhaustionChangeEvent(player, node.exhaustion, node.exhaustion + amount);
		post(event);
		if (!event.isCancelled())
			node.exhaustion = Math.max(0.0f, event.getNewExhaustion());
	}

	/** Force-updates the hunger values for all players on the client-side */
	private final void updateHungerValues()
	{
		HungerSettings settings = (HungerSettings) this.settings;
		for (Player player : Bukkit.getOnlinePlayers())
		{
			HungerNode node = settings.getNode(player);
			PlayerHungerUpdateMaxEvent event = new PlayerHungerUpdateMaxEvent(player, settings.maxFoodAmount);
			post(event);
			node.max = event.getMaxHunger();
			player.setFoodLevel(Math.round(20.0f * node.hunger / node.max));
			player.setSaturation(20.0f * node.saturation / node.max);
		}
	}

	/** Handle exhaustion drain and hunger loss */
	private final void updateExhaustionValues()
	{
		HungerSettings settings = (HungerSettings) this.settings;
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
				continue;
			PlayerClass race = SkillAPIHelper.getRace(player);
			String name = race == null ? "" : race.getData().getName();

			// Gain exhaustion
			float gain = 0.0f;
			if (player.isFlying())
				gain = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Exhaustion.fly"));
			else if (player.isSprinting())
				gain = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Exhaustion.run"));
			else if (player.isSneaking())
				gain = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Exhaustion.sneak"));
			else if (Pangaea.getInstance().getSkillManager().getPlayerVelocity(player).lengthSquared() > 0.01)
				gain = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Exhaustion.walk"));
			else
				gain = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Exhaustion.idle"));
			changeExhaustion(player, gain);

			// Lose exhaustion
			HungerNode node = settings.getNode(player);
			float reduction = settings.getFloat(settings.getPath(name, "Races.Default", "Exhaustion.reduction"));
			float convertRate = settings.getFloat(settings.getPath(name, "Races.Default", "Exhaustion.convertRate"));

			float restingPoint = gain / reduction;
			if (node.exhaustion > restingPoint)
				node.exhaustion -= settings.getFloat(settings.getPath(name, "Races.Default", "Exhaustion.rest"));

			float loss = Math.max(0.0f, reduction * node.exhaustion);
			changeSaturation(player, -convertRate * loss);
			node.exhaustion -= loss;
		}
	}

	/** Handle starvation and healing */
	@SuppressWarnings("deprecation")
	private final void handleStarvingAndHealing()
	{
		ignoreNextStarveEvent = false;
		HungerSettings settings = (HungerSettings) this.settings;
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
				continue;
			String name = SkillAPIHelper.getRaceName(player);

			HungerNode node = settings.getNode(player);
			if (settings.enableStarvingDamage && node.hunger == 0.0f)
			{
				double starveDamage = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Hunger.starveDamage"));
			//	List<String> list = settings.starvedToDeathMessages;
			//	String message = list.size() == 0 ? "<player> starved to death" : list.get(random.nextInt(list.size()));
			//	Damager.setNextDeathMessage(message);
				Damager.doDamage(starveDamage, player, DamageCause.STARVATION);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
			}

			float healLevel = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Hunger.healLevel"));
			if (player.getHealth() < player.getMaxHealth() && settings.enableHealing && node.hunger >= healLevel * node.max)
			{
				double healAmount = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Hunger.healAmount"));
				float healSaturationCost = settings.getFloat(settings.getPath("Races." + name, "Races.Default", "Hunger.healSaturationCost"));
				Damager.doHeal(healAmount, player, RegainReason.SATIATED);
				changeSaturation(player, -healSaturationCost);
			}
		}
		ignoreNextStarveEvent = true;
	}

	/** Applies the effects of hunger status effect */
	private final void applyStatusEffects()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			for (PotionEffect effect : player.getActivePotionEffects())
			{
				if (effect.getType().equals(PotionEffectType.HUNGER))
					changeSaturation(player, -0.05f * (float) (1 + effect.getAmplifier()));
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////

	/** Allows the player to consume the specified item; returns true if the item was consumed */
	private final boolean playerConsumeItem(Player player, ItemStack item)
	{
		HungerSettings settings = (HungerSettings) this.settings;
		FoodNode foodNode = settings.getFoodInfo(player, item);
		if (foodNode == null)
			return false;

		HungerNode playerNode = settings.getNode(player);
		if (playerNode.hunger >= playerNode.max && !foodNode.alwaysEdible)
			return false;

		PlayerEatEvent event = new PlayerEatEvent(player, item);
		post(event);
		if (event.isCancelled())
			return false;

		changeHunger(player, foodNode.hunger);
		changeSaturation(player, foodNode.saturation);
		if (random.nextFloat() < foodNode.chance)
		{
			for (PotionEffectSetting setting : foodNode.effects)
				player.addPotionEffect(setting.create());
		}

		ItemStack newItem = item.clone();
		int amount = newItem.getAmount() - 1;
		if (amount <= 0)
			newItem = null;
		else
			newItem.setAmount(amount);

		ItemStack mainHand = player.getInventory().getItemInMainHand();
		if (mainHand != null && mainHand.isSimilar(item))
			player.getInventory().setItemInMainHand(newItem);
		else
			player.getInventory().setItemInOffHand(newItem);

		settings.eatingSound.play(player.getLocation());
		return true;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	// EVENTS // EVENTS // EVENTS // EVENTS // EVENTS // EVENTS // EVENTS // EVENTS // EVENTS //
	// /////////////////////////////////////////////////////////////////////////////////////////

	/** Tracks the player, providing them with a proper hunger value once they first join */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		HungerSettings settings = (HungerSettings) this.settings;
		@SuppressWarnings("unused")
		// DO NOT REMOVE THIS! This is here to initialize the player hunger node when first logging in
		HungerNode node = settings.getNode(event.getPlayer());
		changeHunger(event.getPlayer(), 0.0f);
	}

	/** Tracks the player, providing them with a proper hunger value once they first join */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		HungerSettings settings = (HungerSettings) this.settings;
		HungerNode node = settings.getNode(event.getPlayer());
		node.hunger = node.max;
		node.saturation = node.max;
		node.exhaustion = 0.0f;
	}

	/** Used to apply the saturation potion effect reliably */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerPotionEffect(PotionEffectEvent event)
	{
		Entity entity = event.getEntity();
		PotionEffect effect = event.getEffect();
		if (!entity.getType().equals(EntityType.PLAYER) || !effect.getType().equals(PotionEffectType.SATURATION))
			return;
		Player player = (Player) event.getEntity();

		float amount = 5.0f * (float) (1 + effect.getAmplifier()) * effect.getDuration();
		if (amount > 0.0f)
		{
			changeHunger(player, amount);
			changeSaturation(player, amount);
		}
		else
			changeSaturation(player, 2.0f * amount);

		event.setCancelled(true);
	}

	/** Used to block the player from eating foods that the player shouldn't be able to eat, and to eat stuff that isn't usually edible */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		HungerSettings settings = (HungerSettings) this.settings;
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;
		ItemStack item = event.getItem();
		if (item == null || item.getType() == Material.AIR)
			return;
		Action action = event.getAction();

		if (settings.canPlayerEat(player, item))
		{
			if (!item.getType().isEdible() && action == Action.RIGHT_CLICK_AIR)
			{
				// Want to be able to cancel the eating in other plugins
				PlayerItemConsumeEvent consumeEvent = new PlayerItemConsumeEvent(player, item);
				ignoreNextEatEvent = true;
				post(consumeEvent);
				ignoreNextEatEvent = false;
				if (consumeEvent.isCancelled())
					return;

				// If the item was consumed, perform no additional action
				if (playerConsumeItem(player, item))
					event.setCancelled(true);
			}
		}
		else
		{
			if (item.getType().isEdible())
			{
				if (action == Action.RIGHT_CLICK_AIR)
					Chat.message(player, settings.cannotEatMessage);
			}
		}
	}

	/** Allows the user to consume food that has custom effects and food values */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerConsumeItem(PlayerItemConsumeEvent event)
	{
		ItemStack item = event.getItem();
		if (ignoreNextEatEvent || item.getType() == Material.POTION || item.getType() == Material.MILK_BUCKET)
			return;

		if (((HungerSettings) settings).canPlayerEat(event.getPlayer(), item))
			playerConsumeItem(event.getPlayer(), item);
		event.setCancelled(true);
	}

	/** Called when a player receives any damage; will look for the starving events and kill players if instadeath on starvation is enabled */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerStarve(EntityDamageEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER || event.getCause() != DamageCause.STARVATION)
			return;

		HungerSettings settings = (HungerSettings) this.settings;
		if (ignoreNextStarveEvent || !settings.enableStarvingDamage)
			event.setCancelled(true);
		if (settings.enableStarvingInstaDeath)
			event.setDamage(1000000.0);
	}

	/** Called when a player receives any damage; will look for the starving events and kill players if instadeath on starvation is enabled */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerHeal(EntityRegainHealthEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER || event.getRegainReason() != RegainReason.SATIATED)
			return;

		HungerSettings settings = (HungerSettings) this.settings;
		if (ignoreNextStarveEvent || !settings.enableHealing)
			event.setCancelled(true);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////

	/** Completely destroys the approach Exotic Garden use for eating food */
	private final void killExoticGardenEatingSystem()
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Pangaea.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				Plugin exoticGarden = Bukkit.getPluginManager().getPlugin("ExoticGarden");
				if (exoticGarden == null)
					return;

				try
				{
					Class<?> itemUseEventClass = Class.forName("me.mrCookieSlime.CSCoreLibPlugin.events.ItemUseEvent");
					Method getHandlerList = itemUseEventClass.getMethod("getHandlerList");
					HandlerList handlers = (HandlerList) getHandlerList.invoke(null);
					handlers.unregister(exoticGarden);
				}
				catch (Exception e)
				{
				}
			}
		}, 1);
	}
}
