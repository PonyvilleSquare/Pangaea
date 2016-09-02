package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillQuickCatch extends Skill
{
	// For bats, only apply while on the ground (Change to Nimble Hooves?)

	public SkillQuickCatch()
	{
		super("Quick Catch");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerTakeDamage(EntityDamageByEntityEvent event)
	{
		if (event.getEntityType() != EntityType.PLAYER)
			return;
		Player player = (Player) event.getEntity();
		DamageCause cause = event.getCause();
		if (cause != DamageCause.PROJECTILE || !(event.getDamager() instanceof Arrow))
			return;
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;

		float chance = getSettings().getFloat(race.getData().getName() + "." + getName() + ".Level " + skill.getLevel() + ".chance");
		if (random.nextFloat() < chance)
		{
			player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			event.getDamager().remove();
			event.setCancelled(true);
		}
	}
}
