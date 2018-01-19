package com.hepolite.pangaea.hunger;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.utility.SkillAPIHelper;
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
	public List<String> starvedToDeathMessages;
	public SoundSetting eatingSound;

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
		set("General.starvedToDeathMessage", new String[] { "<player> starved to death" });
		set("General.disableHungerDamage", false);
		set("General.instaDeathOnStarve", false);
		set("General.enableHealing", true);
		set("General.eatingSound", new SoundSetting(true, Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f));

		String path = "Default.Foods.";
		addFood(path, "apple", "fruit", 20.0f, 12.0f, false, 1.0f, null);
		addFood(path, "baked_potato", "vegetable", 25.0f, 36.0f, false, 1.0f, null);
		addFood(path, "beetroot", "vegetable", 6.0f, 30.0f, false, 1.0f, null);
		addFood(path, "beetroot_soup", "vegetable", 30.0f, 36.0f, false, 1.0f, null);
		addFood(path, "bread", "grain", 25.0f, 30.0f, false, 1.0f, null);
		// addFood(path, "cake_slice", "grain", 10.0f, 2.0f, false, 1.0f, null);
		addFood(path, "carrot_item", "vegetable", 15.0f, 24.0f, false, 1.0f, null);
		addFood(path, "chorus_fruit", "fruit", 20.0f, 12.0f, false, 1.0f, null);
		addFood(path, "raw_fish-2", "meat", 5.0f, 1.0f, false, 1.0f, null);
		addFood(path, "cooked_chicken", "meat", 30.0f, 36.0f, false, 1.0f, null);
		addFood(path, "cooked_fish", "meat", 25.0f, 30.0f, false, 1.0f, null);
		addFood(path, "cooked_mutton", "meat", 30.0f, 48.0f, false, 1.0f, null);
		addFood(path, "grilled_pork", "meat", 40.0f, 64.0f, false, 1.0f, null);
		addFood(path, "cooked_rabbit", "meat", 25.0f, 30.0f, false, 1.0f, null);
		addFood(path, "cooked_fish-1", "meat", 30.0f, 48.0f, false, 1.0f, null);
		addFood(path, "cookie", "grain", 10.0f, 2.0f, false, 1.0f, null);
		addFood(path, "golden_apple", "fruit", 20.0f, 48.0f, true, 1.0f, new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.REGENERATION, 100, 2), new PotionEffectSetting(PotionEffectType.ABSORPTION, 2400, 4) });
		addFood(path, "golden_apple-1", "fruit", 20.0f, 48.0f, true, 1.0f, new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.REGENERATION, 400, 2), new PotionEffectSetting(PotionEffectType.ABSORPTION, 2400, 4), new PotionEffectSetting(PotionEffectType.DAMAGE_RESISTANCE, 6000, 1), new PotionEffectSetting(PotionEffectType.FIRE_RESISTANCE, 6000, 1) });
		addFood(path, "golden_carrot", "vegetable", 30.0f, 72.0f, false, 1.0f, null);
		addFood(path, "melon", "fruit", 10.0f, 6.0f, false, 1.0f, null);
		addFood(path, "mushroom_soup", "vegetable", 30.0f, 36.0f, false, 1.0f, null);
		addFood(path, "poisonous_potato", "vegetable", 10.0f, 6.0f, false, 0.6f, new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.POISON, 80, 1) });
		addFood(path, "potato_item", "vegetable", 5.0f, 3.0f, false, 1.0f, null);
		addFood(path, "raw_fish-3", "meat", 5.0f, 1.0f, false, 1.0f, new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.HUNGER, 300, 3), new PotionEffectSetting(PotionEffectType.CONFUSION, 300, 2), new PotionEffectSetting(PotionEffectType.POISON, 1200, 4) });
		addFood(path, "pumpkin_pie", "grain fruit", 40.0f, 24.0f, false, 1.0f, null);
		addFood(path, "rabbit_stew", "meat", 50.0f, 60.0f, false, 1.0f, null);
		addFood(path, "raw_beef", "meat", 15.0f, 9.0f, false, 1.0f, null);
		addFood(path, "raw_chicken", "meat", 10.0f, 6.0f, false, 0.3f, new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.HUNGER, 600, 1) });
		addFood(path, "raw_fish", "meat", 10.0f, 2.0f, false, 1.0f, null);
		addFood(path, "mutton", "meat", 10.0f, 6.0f, false, 1.0f, null);
		addFood(path, "pork", "meat", 15.0f, 9.0f, false, 1.0f, null);
		addFood(path, "rabbit", "meat", 15.0f, 9.0f, false, 1.0f, null);
		addFood(path, "raw_fish-1", "meat", 10.0f, 2.0f, false, 1.0f, null);
		addFood(path, "rotten_flesh", "meat", 20.0f, 8.0f, false, 0.8f, new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.HUNGER, 600, 1) });
		addFood(path, "spider_eye", "meat", 10.0f, 16.0f, false, 1.0f, new PotionEffectSetting[] { new PotionEffectSetting(PotionEffectType.POISON, 80, 1) });
		addFood(path, "cooked_beef", "meat", 40.0f, 64.0f, false, 1.0f, null);
	}

	/** Adds one food to the config */
	private final void addFood(String path, String food, String categories, float hunger, float saturation, boolean alwaysEdible, float chance, PotionEffectSetting[] effects)
	{
		set(path + food + ".categories", categories);
		if (alwaysEdible)
			set(path + food + ".alwaysEdible", alwaysEdible);
		if (hunger != 0.0f)
			set(path + food + ".hunger", hunger);
		if (saturation != 0.0f)
			set(path + food + ".saturation", saturation);
		if (effects != null)
		{
			set(path + food + ".chance", chance);
			set(path + food + ".effects", effects);
		}
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
		starvedToDeathMessages = getStringList("General.starvedToDeathMessage");
		eatingSound = getSound("General.eatingSound");

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

	/** Returns the hunger node for the given player; will create a default node if the player had none, and store it */
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

	/** Returns true if the given item is food for the given player */
	public final boolean isFood(Player player, ItemStack item)
	{
		item = item.clone();
		item.setAmount(1);

		String simpleFood = writeSimpleItem(item, false);
		String namedFood = writeSimpleItem(item);
		String defaultPath = "Default.Foods.";
		String playerPath = "Races." + SkillAPIHelper.getRaceName(player) + ".Foods.";

		return has(playerPath + simpleFood) || has(defaultPath + simpleFood) || has(playerPath + namedFood) || has(defaultPath + namedFood);
	}

	/** Returns true if the given player is allowed to eat the given item */
	public final boolean canPlayerEat(Player player, ItemStack item)
	{
		FoodNode node = getFoodInfo(player, item);
		if (node == null)
			return false;

		String string = getString("Races." + SkillAPIHelper.getRaceName(player) + ".forbidden");
		if (string == null)
			return true;

		String[] categories = string.split(" ");
		for (String category : categories)
		{
			if (node.categories.contains(category))
				return false;
		}

		return true;
	}

	/** Returns the information about the given item for the given player */
	public final FoodNode getFoodInfo(Player player, ItemStack item)
	{
		item = item.clone();
		item.setAmount(1);
		FoodNode node = getFoodInfo(player, writeSimpleItem(item, true));
		return node != null ? node : getFoodInfo(player, writeSimpleItem(item, false));
	}

	private final FoodNode getFoodInfo(Player player, String item)
	{
		String race = SkillAPIHelper.getRaceName(player);
		String pathRace = "Races." + race + ".Foods." + item;
		String pathDefault = "Default.Foods." + item;

		if (!has(pathRace) && !has(pathDefault))
			return null;

		String categories = getString(getPath(pathRace, pathDefault, "categories"));
		boolean alwaysEdible = getBool(getPath(pathRace, pathDefault, "alwaysEdible"));
		float hunger = getFloat(getPath(pathRace, pathDefault, "hunger"));
		float saturation = getFloat(getPath(pathRace, pathDefault, "saturation"));
		float chance = getFloat(getPath(pathRace, pathDefault, "chance"));
		List<PotionEffectSetting> effects = getPotionEffects(getPath(pathRace, pathDefault, "effects"));

		return new FoodNode(categories, alwaysEdible, hunger, saturation, chance, effects);
	}

	/** Simple storage container, will hold information about what a single unit of food restores */
	public final static class FoodNode
	{
		public final String categories;
		public final boolean alwaysEdible;
		public final float hunger, saturation, chance;
		public final List<PotionEffectSetting> effects;

		public FoodNode(String categories, boolean alwaysEdible, float hunger, float saturation, float chance, List<PotionEffectSetting> effects)
		{
			this.categories = categories;
			this.alwaysEdible = alwaysEdible;
			this.hunger = hunger;
			this.saturation = saturation;
			this.chance = chance;
			this.effects = effects;
		}
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
