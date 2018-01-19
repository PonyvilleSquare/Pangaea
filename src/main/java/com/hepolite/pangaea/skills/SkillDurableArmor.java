package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.rit.sucy.event.ItemLoseDurabilityEvent;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillDurableArmor extends Skill
{
	public SkillDurableArmor()
	{
		super("Durable Armor");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onArmorTakeDamage(ItemLoseDurabilityEvent event)
	{
		ItemStack item = event.getItem();
		String name = item.getType().toString().toLowerCase();
		if (!name.contains("helmet") || !name.contains("chestplate") || !name.contains("leggings") || !name.contains("boots"))
			return;
		Player player = event.getPlayer();
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;

		float chance = getSettings().getFloat(race.getData().getName() + "." + getName() + ".chance");
		if (random.nextFloat() < chance)
			event.setCancelled(true);
	}
}
