package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.hepolite.mmob.handlers.LootDropHandler;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillImprovedFishing extends Skill
{
	public SkillImprovedFishing()
	{
		super("Improved Fishing");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerFish(PlayerFishEvent event)
	{
		if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH || !(event.getCaught() instanceof Item))
			return;
		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;
		Item item = (Item) event.getCaught();
		ItemStack stack = item.getItemStack();
		String path = race.getData().getName() + "." + getName() + ".";

		Material type = stack.getType();
		if (type == Material.RAW_FISH)
		{
			int fishAmount = getSettings().getInt(path + "fishAmount");
			stack.setAmount(stack.getAmount() + random.nextInt(fishAmount));
		}
		else
		{
			float mmobItemChance = getSettings().getFloat(path + "mmobItemChance");
			if (random.nextFloat() < mmobItemChance)
			{
				String mainGroup = getSettings().getString(path + ".mmobGroup");
				String negativeGroup = getSettings().getString(path + ".mmobNegativeGroup");
				float negativeChance = getSettings().getFloat(path + ".mmobNegativeChance");
				stack = LootDropHandler.getRandomItem(mainGroup);
				if (random.nextFloat() < negativeChance)
					LootDropHandler.applyRandomItemEffect(stack, negativeGroup);
				if (random.nextFloat() < negativeChance)
					LootDropHandler.applyRandomItemEffect(stack, negativeGroup);
			}
		}

		item.setItemStack(stack);
	}
}
