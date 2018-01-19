package com.hepolite.pangaea.utility;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper
{
	/** The value used in itemstacks to represent any meta value */
	public final static short WILDCARD = 32767;
	
	/** Removes one item from the player's inventory */
	public final static void removeItem(Player player, ItemStack item)
	{
		removeItem(player.getInventory(), item);
	}

	/** Removes one item from the given inventory */
	public final static void removeItem(Inventory inventory, ItemStack item)
	{
		HashMap<Integer, ItemStack> map = inventory.removeItem(item);
		if (!map.isEmpty())
		{
			for (ItemStack requirement : map.values())
			{
				for (int i = 0; i < inventory.getSize(); i++)
				{
					ItemStack current = inventory.getItem(i);
					if (current != null && current.getType() == requirement.getType() && (current.getDurability() == item.getDurability() || current.getDurability() == WILDCARD))
					{
						int amount = current.getAmount();
						current.setAmount(Math.max(0, amount - requirement.getAmount()));
						if (current.getAmount() == 0)
							inventory.setItem(i, null);
						requirement.setAmount(Math.max(0, requirement.getAmount() - amount));
					}
					if (requirement.getAmount() == 0)
						break;
				}
			}
		}
	}
}
