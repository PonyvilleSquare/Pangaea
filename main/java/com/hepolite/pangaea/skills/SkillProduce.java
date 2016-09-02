package com.hepolite.pangaea.skills;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pangaea.hunger.HungerNode;
import com.hepolite.pangaea.hunger.HungerSettings;
import com.hepolite.pillar.chat.Chat;
import com.sucy.skill.api.player.PlayerClass;

public class SkillProduce extends SkillCastTriggered
{
	public SkillProduce(String name)
	{
		super(name, true);
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Player player = event.getPlayer();
		PlayerClass race = event.getRace();

		// Check that the player has all the needed resources, and consume them
		float hungerCost = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Cost.hunger");
		float saturationCost = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Cost.saturation");
		List<ItemStack> itemCost = getSettings().getItems(race.getData().getName() + "." + getName() + ".Cost.items");
		List<ItemStack> products = getSettings().getItems(race.getData().getName() + "." + getName() + ".products");

		List<String> neededResources = hasResources(player, hungerCost, saturationCost, itemCost);
		if (neededResources != null)
		{
			Chat.message(player, "&cYou still need these resources:");
			for (String string : neededResources)
				Chat.message(player, "&f- " + string);
			return false;
		}

		consumeResources(player, hungerCost, saturationCost, itemCost);
		handoutProducts(player, products);
		return true;
	}

	/** Checks if the player has all the needed resources; returns a list of needed resourceds, or null if nothing is needed */
	private final List<String> hasResources(Player player, float hunger, float saturation, List<ItemStack> items)
	{
		List<String> neededResources = new LinkedList<String>();

		if (hunger > 0.0f || saturation > 0.0f)
		{
			HungerNode node = ((HungerSettings) Pangaea.getInstance().getHungerManager().getSettings()).getNode(player);
			if (node.hunger < hunger)
				neededResources.add(String.format("%.0f hunger", Math.ceil(hunger - node.hunger)));
			if (node.hunger + node.saturation < hunger + saturation)
				neededResources.add(String.format("%.0f saturation", Math.ceil((hunger + saturation) - (node.hunger + node.saturation))));
		}
		if (items.size() != 0)
			neededResources.addAll(hasItems(player, items));
		return (neededResources.size() == 0 ? null : neededResources);
	}

	/** Checks if the player has all of the given items; returns a list of all the needed resources */
	private final List<String> hasItems(Player player, List<ItemStack> items)
	{
		Inventory inventory = player.getInventory();
		List<String> list = new LinkedList<String>();
		for (ItemStack requirement : items)
		{
			int foundAmount = 0;
			for (ItemStack item : inventory.getContents())
			{
				if (item != null && item.getType() == requirement.getType())
					foundAmount += item.getAmount();
			}
			if (foundAmount < requirement.getAmount())
				list.add(requirement.getType().toString().toLowerCase().replaceAll("_", " ") + " (" + (requirement.getAmount() - foundAmount) + ")");
		}
		return list;
	}

	/** Consumes the resources that are needed */
	private final void consumeResources(Player player, float hunger, float saturation, List<ItemStack> items)
	{
		if (hunger > 0.0)
			Pangaea.getInstance().getHungerManager().changeHunger(player, -hunger);
		if (saturation > 0.0)
			Pangaea.getInstance().getHungerManager().changeSaturation(player, -saturation);

		Inventory inventory = player.getInventory();
		for (ItemStack item : items)
		{
			for (int i = 0; i < inventory.getSize(); i++)
			{
				ItemStack current = inventory.getItem(i);
				if (current != null && current.getType() == item.getType())
				{
					int amount = current.getAmount();
					current.setAmount(Math.max(0, amount - item.getAmount()));
					if (current.getAmount() == 0)
						inventory.setItem(i, null);
					item.setAmount(Math.max(0, item.getAmount() - amount));
				}
				if (item.getAmount() == 0)
					break;
			}
		}
	}

	/** Gives the relevant items to the player */
	private final void handoutProducts(Player player, List<ItemStack> items)
	{
		Inventory inventory = player.getInventory();
		for (ItemStack item : items)
		{
			modifyFinalItems(player, item);
			if (inventory.firstEmpty() == -1)
				player.getWorld().dropItem(player.getEyeLocation(), item);
			else
				inventory.addItem(item);
		}
		onProduceGoods(player);
	}

	/** Allows the modification of the end items that are handed out */
	protected void modifyFinalItems(Player player, ItemStack item)
	{
	}

	/** Invoked when the final goods are produced */
	protected void onProduceGoods(Player player)
	{
	}
}
