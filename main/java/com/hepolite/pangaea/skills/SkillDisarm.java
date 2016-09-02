package com.hepolite.pangaea.skills;

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

		if (entity instanceof Player)
		{
			Player target = (Player) entity;
			ItemStack item = entity.getEquipment().getItemInMainHand();
			if (item != null && item.getType() != Material.AIR)
			{
				Inventory inventory = target.getInventory();
				for (int i = 0; i < inventory.getSize(); i++)
				{
					ItemStack current = inventory.getItem(i);
					if (!item.equals(current))
					{
						if (current == null || current.getType() == Material.AIR || current.getType().getMaxDurability() == 0)
						{
							target.getEquipment().setItemInMainHand(current);
							inventory.setItem(i, item);
							return true;
						}
					}
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
				equipment.setItemInOffHand(null);
				entity.getWorld().dropItemNaturally(entity.getLocation(), item);
				return true;
			}
			else
				Chat.message(player, ChatColor.RED + "There was no valid target in front of you!");
		}
		return false;
	}
}
