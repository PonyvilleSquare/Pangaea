package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.events.PlayerAirChangeEvent;
import com.hepolite.pillar.utility.NBTAPI;
import com.hepolite.pillar.utility.NBTAPI.NBTTag;

public class SkillCraftRespirator extends SkillProduce
{
	public SkillCraftRespirator()
	{
		super("Craft Respirator");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerAirChange(PlayerAirChangeEvent event)
	{
		if (event.getNewAir() >= event.getOldAir())
			return;
		float change = event.getNewAir() - event.getOldAir();
		float modifier = 0.0f;

		Player player = event.getPlayer();
		EntityEquipment equipment = player.getEquipment();

		ItemStack[] items = new ItemStack[] { equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots() };
		for (ItemStack item : items)
		{
			if (item == null || !NBTAPI.hasTag(item))
				continue;

			NBTTag tag = NBTAPI.getTag(item);
			if (tag.hasKey(getName()))
				modifier += tag.getFloat(getName());
		}

		event.setNewAir(event.getOldAir() + change * (1.0f - modifier / (modifier + 100.0f)));
	}
}
