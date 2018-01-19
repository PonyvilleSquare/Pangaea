package com.hepolite.pangaea.skills;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerClass;

public class SkillDisarm extends SkillCastTriggered
{
	public SkillDisarm()
	{
		super("Disarm", true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();

		float range = getSettings().getFloat(race.getData().getName() + "." + getName() + ".range");
		LivingEntity entity = EntityHelper.getEntityInSight(player, range, player);
		if (entity == null)
		{
			Chat.message(player, ChatColor.RED + "There was no valid target in front of you!");
			return false;
		}

		if (entity instanceof Player)
		{
			Player target = (Player) entity;
			ItemStack item = entity.getEquipment().getItemInMainHand();
			if (item != null && item.getType() != Material.AIR)
			{
				Inventory inventory = target.getInventory();
				int slot = getJunkSlot(inventory, item);
				if (slot != -1)
				{
					target.getEquipment().setItemInMainHand(inventory.getItem(slot));
					inventory.setItem(slot, item);
					return true;
				}
			}
			else
				Chat.message(player, ChatColor.RED + "There was no valid target in front of you!");
		}
		else
		{
			EntityEquipment equipment = entity.getEquipment();
			ItemStack item = equipment == null ? null : equipment.getItemInMainHand();
			if (item != null && item.getType() != Material.AIR)
			{
				entity.getWorld().dropItemNaturally(entity.getLocation(), item);
				equipment.setItemInMainHand(null);
				return true;
			}
			else
				Chat.message(player, ChatColor.RED + "There was no valid target in front of you!");
		}
		return false;
	}

	/** Locates an inventory slot id which contains no item, or a junk item, and is not the given item */
	private final int getJunkSlot(Inventory inventory, ItemStack item)
	{
		List<Integer> possibleSlots = new ArrayList<Integer>();
		for (int i = Math.min(36, inventory.getSize()) - 1; i >= 0; i--)
		{
			ItemStack current = inventory.getItem(i);
			if (current == null || current.getType() == Material.AIR || (current.getType().getMaxDurability() == 0 && !item.equals(current) && (!current.hasItemMeta() || !current.getItemMeta().hasDisplayName())))
				possibleSlots.add(i);
		}
		return possibleSlots.size() == 0 ? -1 : possibleSlots.get(random.nextInt(possibleSlots.size()));
	}
}
