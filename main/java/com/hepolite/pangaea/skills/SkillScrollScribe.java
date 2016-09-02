package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;

public class SkillScrollScribe extends SkillProduce
{
	public SkillScrollScribe()
	{
		super("Scroll Scribe");
	}

	@Override
	protected void modifyFinalItems(Player player, ItemStack item)
	{
		if (item.getType() != Material.ENCHANTED_BOOK)
			return;
		PlayerClass race = SkillAPIHelper.getRace(player);
		if (race == null)
			return;

		List<Enchantment> enchantments = getSettings().getEnchantmentTypes(race.getData().getName() + "." + getName() + ".enchantments");
		if (enchantments.size() == 0)
			return;
		Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));

		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		meta.addStoredEnchant(enchantment, 1, false);
		item.setItemMeta(meta);
	}
}
