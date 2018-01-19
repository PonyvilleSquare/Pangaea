package com.hepolite.pangaea.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.LootHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillLuckOfTheSea extends Skill
{
	public SkillLuckOfTheSea()
	{
		super("Luck of the Sea");
		setTickRate(1200);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		String path = race.getData().getName() + "." + getName();
		String section = LootHelper.getLootSection(getSettings(), path);
		ItemStack item = getSettings().getItem(path + "." + section);
		if (item == null)
			return;

		double px = 32.0 * random.nextDouble();
		double py = 8.0 * random.nextDouble() - 4.0;
		double pz = 32.0 - px;
		Location location = player.getLocation().add(px - 16.0, py, pz - 16.0);
		if (location.getBlock().isLiquid())
			player.getWorld().dropItemNaturally(location, item);
	}
}
