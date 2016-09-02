package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
		List<ItemStack> items = getSettings().getItems(race.getData().getName() + "." + getName() + ".items");
		if (items.isEmpty())
			return;
		ItemStack item = items.get(random.nextInt(items.size()));

		double px = 32.0 * random.nextDouble();
		double py = 8.0 * random.nextDouble() - 4.0;
		double pz = 32.0 - px;
		Location location = player.getLocation().add(px - 16.0, py, pz - 16.0);
		if (location.getBlock().isLiquid())
			player.getWorld().dropItemNaturally(location, item);
	}
}
