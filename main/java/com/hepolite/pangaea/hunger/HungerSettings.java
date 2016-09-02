package com.hepolite.pangaea.hunger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pillar.logging.Log;
import com.hepolite.pillar.settings.Settings;

public class HungerSettings extends Settings
{
	// Control variables
	public boolean debugmode;
	public boolean enableStarvingDamage;
	public boolean enableStarvingInstaDeath;
	public boolean enableHealing;
	public float maxFoodAmount;
	public String cannotEatMessage;
	public SoundSetting eatingSound;

	private FoodSettings defaultFoodSettings;

	private Settings playerHungerValues;

	public HungerSettings()
	{
		super(Pangaea.getInstance(), "Hunger");
	}

	@Override
	protected void addDefaults()
	{
		set("General.debugmode", false);
		set("General.maxFoodAmount", 100.0);
		set("General.cannotEatMessage", "&bYou're not able to eat that!");
		set("General.disableHungerDamage", false);
		set("General.instaDeathOnStarve", false);
		set("General.enableHealing", true);
		set("General.eatingSound", new SoundSetting(true, Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f));

		set("Races.Default.Foods.apple.hunger", 20.0);
		set("Races.Default.Foods.apple.saturation", 12.0);
		set("Races.Default.Foods.baked_potato.hunger", 25.0);
		set("Races.Default.Foods.baked_potato.saturation", 36.0);
		set("Races.Default.Foods.beetroot.hunger", 5.0);
		set("Races.Default.Foods.beetroot.saturation", 6.0);
		set("Races.Default.Foods.beetroot_soup.hunger", 30.0);
		set("Races.Default.Foods.beetroot_soup.saturation", 36.0);
		set("Races.Default.Foods.bread.hunger", 25.0);
		set("Races.Default.Foods.bread.saturation", 30.0);
		// set("Races.Default.Foods.cake_slice.hunger", 10.0);
		// set("Races.Default.Foods.cake_slice.saturation", 2.0);
		set("Races.Default.Foods.carrot_item.hunger", 15.0);
		set("Races.Default.Foods.carrot_item.saturation", 24.0);
		set("Races.Default.Foods.chorus_fruit.hunger", 20.0);
		set("Races.Default.Foods.chorus_fruit.saturation", 12.0);
		set("Races.Default.Foods.raw_fish-2.hunger", 5.0);
		set("Races.Default.Foods.raw_fish-2.saturation", 1.0);
		set("Races.Default.Foods.cooked_chicken.hunger", 30.0);
		set("Races.Default.Foods.cooked_chicken.saturation", 36.0);
		set("Races.Default.Foods.cooked_fish-0.hunger", 25.0);
		set("Races.Default.Foods.cooked_fish-0.saturation", 30.0);
		set("Races.Default.Foods.cooked_mutton.hunger", 30.0);
		set("Races.Default.Foods.cooked_mutton.saturation", 48.0);
		set("Races.Default.Foods.grilled_pork.hunger", 40.0);
		set("Races.Default.Foods.grilled_pork.saturation", 64.0);
		set("Races.Default.Foods.cooked_rabbit.hunger", 25.0);
		set("Races.Default.Foods.cooked_rabbit.saturation", 30.0);
		set("Races.Default.Foods.cooked_fish-1.hunger", 30.0);
		set("Races.Default.Foods.cooked_fish-1.saturation", 48.0);
		set("Races.Default.Foods.cookie.hunger", 10.0);
		set("Races.Default.Foods.cookie.saturation", 2.0);
		set("Races.Default.Foods.golden_apple-0.hunger", 20.0);
		set("Races.Default.Foods.golden_apple-0.saturation", 48.0);
		set("Races.Default.Foods.golden_apple-0.chance", 1.0);
		set("Races.Default.Foods.golden_apple-0.effects", new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.REGENERATION, 100, 2), new PotionEffectSetting(PotionEffectType.ABSORPTION, 2400, 4) });
		set("Races.Default.Foods.golden_apple-1.hunger", 20.0);
		set("Races.Default.Foods.golden_apple-1.saturation", 48.0);
		set("Races.Default.Foods.golden_apple-1.chance", 1.0);
		set("Races.Default.Foods.golden_apple-1.effects", new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.REGENERATION, 400, 2), new PotionEffectSetting(PotionEffectType.ABSORPTION, 2400, 4), new PotionEffectSetting(PotionEffectType.DAMAGE_RESISTANCE, 6000, 1), new PotionEffectSetting(PotionEffectType.FIRE_RESISTANCE, 6000, 1) });
		set("Races.Default.Foods.golden_carrot.hunger", 30.0);
		set("Races.Default.Foods.golden_carrot.saturation", 72.0);
		set("Races.Default.Foods.melon.hunger", 10.0);
		set("Races.Default.Foods.melon.saturation", 6.0);
		set("Races.Default.Foods.mushroom_soup.hunger", 30.0);
		set("Races.Default.Foods.mushroom_soup.saturation", 36.0);
		set("Races.Default.Foods.poisonous_potato.hunger", 10.0);
		set("Races.Default.Foods.poisonous_potato.saturation", 6.0);
		set("Races.Default.Foods.poisonous_potato.chance", 0.6);
		set("Races.Default.Foods.poisonous_potato.effects", new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.POISON, 80, 1) });
		set("Races.Default.Foods.potato_item.hunger", 5.0);
		set("Races.Default.Foods.potato_item.saturation", 3.0);
		set("Races.Default.Foods.raw_fish-3.hunger", 5.0);
		set("Races.Default.Foods.raw_fish-3.saturation", 1.0);
		set("Races.Default.Foods.raw_fish-3.chance", 1.0);
		set("Races.Default.Foods.raw_fish-3.effects", new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.HUNGER, 300, 3), new PotionEffectSetting(PotionEffectType.CONFUSION, 300, 2), new PotionEffectSetting(PotionEffectType.POISON, 1200, 4) });
		set("Races.Default.Foods.pumpkin_pie.hunger", 40.0);
		set("Races.Default.Foods.pumpkin_pie.saturation", 24.0);
		set("Races.Default.Foods.rabbit_stew.hunger", 50.0);
		set("Races.Default.Foods.rabbit_stew.saturation", 60.0);
		set("Races.Default.Foods.raw_beef.hunger", 15.0);
		set("Races.Default.Foods.raw_beef.saturation", 9.0);
		set("Races.Default.Foods.raw_chicken.hunger", 10.0);
		set("Races.Default.Foods.raw_chicken.saturation", 6.0);
		set("Races.Default.Foods.raw_chicken.chance", 0.3);
		set("Races.Default.Foods.raw_chicken.effects", new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.HUNGER, 600, 1) });
		set("Races.Default.Foods.raw_fish-0.hunger", 10.0);
		set("Races.Default.Foods.raw_fish-0.saturation", 2.0);
		set("Races.Default.Foods.mutton.hunger", 10.0);
		set("Races.Default.Foods.mutton.saturation", 6.0);
		set("Races.Default.Foods.pork.hunger", 15.0);
		set("Races.Default.Foods.pork.saturation", 9.0);
		set("Races.Default.Foods.rabbit.hunger", 15.0);
		set("Races.Default.Foods.rabbit.saturation", 9.0);
		set("Races.Default.Foods.raw_fish-1.hunger", 10.0);
		set("Races.Default.Foods.raw_fish-1.saturation", 2.0);
		set("Races.Default.Foods.rotten_flesh.hunger", 20.0);
		set("Races.Default.Foods.rotten_flesh.saturation", 8.0);
		set("Races.Default.Foods.rotten_flesh.chance", 0.8);
		set("Races.Default.Foods.rotten_flesh.effects", new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.HUNGER, 600, 1) });
		set("Races.Default.Foods.spider_eye.hunger", 10.0);
		set("Races.Default.Foods.spider_eye.saturation", 16.0);
		set("Races.Default.Foods.spider_eye.chance", 0.3);
		set("Races.Default.Foods.spider_eye.effects", new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.POISON, 80, 1) });
		set("Races.Default.Foods.cooked_beef.hunger", 40.0);
		set("Races.Default.Foods.cooked_beef.saturation", 64.0);
	}

	@Override
	public void onReload()
	{
		debugmode = getBool("General.debugmode");
		enableStarvingDamage = !getBool("General.disableHungerDamage");
		enableStarvingInstaDeath = getBool("General.instaDeathOnStarve");
		enableHealing = getBool("General.enableHealing");
		maxFoodAmount = getFloat("General.maxFoodAmount");
		cannotEatMessage = getString("General.cannotEatMessage");
		eatingSound = getSound("General.eatingSound");

		loadFoodSettings();
		loadHungerValues();
	}

	@Override
	public void onSave()
	{
		saveHungerValues();
	}

	// ///////////////////////////////////////////////////////////////////////
	// Player hunger values // Player hunger values // Player hunger values //
	// ///////////////////////////////////////////////////////////////////////

	// Custom hunger system for players; ignores the hunger values that Minecraft by default uses
	private HashMap<UUID, HungerNode> playerHungerNodes;

	/** Returns the hunger node for the given player; returns null if the node didn't exist already */
	public final HungerNode getNode(Player player)
	{
		UUID uuid = player.getUniqueId();
		if (playerHungerNodes.containsKey(uuid))
			return playerHungerNodes.get(uuid);
		HungerNode node = getDefaultHungerNode();
		playerHungerNodes.put(uuid, node);
		return node;
	}

	/** Returns a default hunger node */
	public final HungerNode getDefaultHungerNode()
	{
		float baseValue = getFloat("General.maxFoodAmount");
		return new HungerNode(baseValue, baseValue, baseValue, 0.0f);
	}

	/** Saves all the hunger values of players to the config */
	private final void saveHungerValues()
	{
		if (playerHungerNodes == null || playerHungerValues == null)
			return;
		for (Entry<UUID, HungerNode> entry : playerHungerNodes.entrySet())
		{
			playerHungerValues.set(entry.getKey().toString() + ".max", entry.getValue().max);
			playerHungerValues.set(entry.getKey().toString() + ".hunger", entry.getValue().hunger);
			playerHungerValues.set(entry.getKey().toString() + ".saturation", entry.getValue().saturation);
			playerHungerValues.set(entry.getKey().toString() + ".exhaustion", entry.getValue().exhaustion);
		}
		playerHungerValues.save();
	}

	/** Loads all the hunger values of players from the config */
	private final void loadHungerValues()
	{
		playerHungerNodes = new HashMap<UUID, HungerNode>();
		playerHungerValues = new Settings(Pangaea.getInstance(), "PlayerHunger")
		{
		};
		playerHungerValues.initialize();

		Set<String> entries = playerHungerValues.getKeys("");
		for (String entry : entries)
		{
			float max = playerHungerValues.getFloat(entry + ".max");
			float hunger = playerHungerValues.getFloat(entry + ".hunger");
			float saturation = playerHungerValues.getFloat(entry + ".saturation");
			float exhaustion = playerHungerValues.getFloat(entry + ".exhaustion");
			playerHungerNodes.put(UUID.fromString(entry), new HungerNode(max, hunger, saturation, exhaustion));
		}
	}

	// ///////////////////////////////////////////////////////////////////////////////////////
	// Food hunger values // Food hunger values // Food hunger values // Food hunger values //
	// ///////////////////////////////////////////////////////////////////////////////////////

	// Custom hunger system for food; ignores the hunger values that Minecraft by default uses
	private HashMap<String, FoodSettings> foodSettings;

	/** Returns true if the given race is allowed to eat the given item */
	public final boolean canRaceEat(String race, ItemStack item)
	{
		item = item.clone();
		item.setAmount(1);
		String food = writeSimpleItem(item);
		if (defaultFoodSettings.forbidden.contains(food))
			return false;

		boolean hasFoodValues = false;
		FoodSettings settings = foodSettings.get(race);
		if (settings != null)
		{
			if (settings.forbidden.contains(food))
				return false;
			hasFoodValues = settings.hunger.containsKey(food) || settings.saturation.containsKey(food);
		}
		return hasFoodValues || defaultFoodSettings.hunger.containsKey(food) || defaultFoodSettings.saturation.containsKey(food);
	}

	/** Returns the information about the given item for the given race; returns null if the item is inedible or the race was invalid */
	public final FoodNode getFoodInfo(String race, ItemStack item)
	{
		item = item.clone();
		item.setAmount(1);
		String food = writeSimpleItem(item);

		FoodSettings settings = foodSettings.get(race);
		if (settings != null)
		{
			if (!settings.hunger.containsKey(food) && !settings.saturation.containsKey(food))
				settings = defaultFoodSettings;
		}
		else
			settings = defaultFoodSettings;
		if (!settings.hunger.containsKey(food) && !settings.saturation.containsKey(food))
			return null;

		float hunger = settings.hunger.get(food);
		float saturation = settings.saturation.get(food);
		float chance = settings.chance.containsKey(food) ? settings.chance.get(food) : 0.0f;
		List<PotionEffectSetting> effects = settings.effects.containsKey(food) ? settings.effects.get(food) : new LinkedList<PotionEffectSetting>();
		return new FoodNode(hunger, saturation, chance, effects);
	}

	/** Simple storage container, will hold information about all foods for a given race */
	public final static class FoodSettings
	{
		public final HashMap<String, Float> hunger;
		public final HashMap<String, Float> saturation;
		public final HashMap<String, Float> chance;
		public final HashMap<String, List<PotionEffectSetting>> effects;
		public final HashSet<String> forbidden;

		public FoodSettings()
		{
			hunger = new HashMap<String, Float>();
			saturation = new HashMap<String, Float>();
			chance = new HashMap<String, Float>();
			effects = new HashMap<String, List<PotionEffectSetting>>();
			forbidden = new HashSet<String>();
		}
	}

	/** Simple storage container, will hold information about what a single unit of food restores */
	public final static class FoodNode
	{
		public final float hunger, saturation, chance;
		public final List<PotionEffectSetting> effects;

		public FoodNode(float hunger, float saturation, float chance, List<PotionEffectSetting> effects)
		{
			this.hunger = hunger;
			this.saturation = saturation;
			this.chance = chance;
			this.effects = effects;
		}
	}

	/** Loads up all the various food settings from the config */
	private final void loadFoodSettings()
	{
		foodSettings = new HashMap<String, FoodSettings>();

		Set<String> races = getKeys("Races");
		for (String race : races)
		{
			FoodSettings settings = new FoodSettings();

			if (debugmode)
				Log.log("Located race '" + race + "'... Loading up data!");

			Set<String> foods = getKeys("Races." + race + ".Foods");
			for (String food : foods)
			{
				String path = "Races." + race + ".Foods." + food + ".";

				ItemStack item = parseSimpleItem(food);
				if (item == null)
					continue;
				item.setAmount(1);
				food = writeSimpleItem(item);

				if (has(path + "hunger"))
					settings.hunger.put(food, getFloat(path + "hunger"));
				if (has(path + "saturation"))
					settings.saturation.put(food, getFloat(path + "saturation"));
				if (has(path + "chance"))
					settings.chance.put(food, getFloat(path + "chance"));
				else
					settings.chance.put(food, 1.0f); // The chance is defaulted to 100%
				if (has(path + "effects"))
					settings.effects.put(food, getPotionEffects(path + "effects"));

				if (debugmode)
					Log.log("Loaded up food '" + food + "' under '" + race + "'!");
			}
			if (has("Races." + race + ".Forbidden"))
				settings.forbidden.addAll(getStringList("Races." + race + ".Forbidden"));

			foodSettings.put(race, settings);
		}

		defaultFoodSettings = foodSettings.get("Default");
	}

	// /////////////////////////////////////////////////////////////////////
	// CONFIG // CONFIG // CONFIG // CONFIG // CONFIG // CONFIG // CONFIG //
	// /////////////////////////////////////////////////////////////////////

	/** Returns a property that exists, preferring the main. Returns the alternative if the main doesn't exist */
	public final String getPath(String main, String alternative, String property)
	{
		if (has(main + "." + property))
			return main + "." + property;
		return alternative + "." + property;
	}
}
