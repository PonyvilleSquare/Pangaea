package com.hepolite.pangaea.movement;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.utility.SkillAPIHelper;

public class FallDamageModifier implements IFallDamageModifier
{
	@Override
	public float getMultiplier(Player player)
	{
		return 0;
	}

	@Override
	public float getFlat(Player player)
	{
		ItemStack boots = player.getEquipment().getBoots();
		if (boots != null)
		{
			MovementSettings settings = (MovementSettings) Pangaea.getInstance().getMovementManager().getSettings();
			String raceName = SkillAPIHelper.getRaceName(player);

			float featherFallEfficiency = settings.getFloat(settings.getPath(raceName, "Default", "Impact.featherFallEfficiency"));
			return -featherFallEfficiency * (float) boots.getEnchantmentLevel(Enchantment.PROTECTION_FALL);
		}
		return 0.0f;
	}
}
