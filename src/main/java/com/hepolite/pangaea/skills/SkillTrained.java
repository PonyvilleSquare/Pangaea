package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillTrained extends SkillMovement
{
	public SkillTrained()
	{
		super("Trained");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDealDamage(EntityDamageByEntityEvent event)
	{
		if (!(event.getDamager() instanceof Player) || event.getCause() != DamageCause.ENTITY_ATTACK)
			return;
		float modifier = getModifier((Player) event.getDamager(), "Damage");
		if (modifier != 0.0f)
			event.setDamage(Math.max(0.0, event.getDamage() * (1.0f + modifier)));
	}

	@Override
	protected float getModifier(Player player, String group)
	{
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null)
			return 0.0f;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || !item.getType().toString().contains("_SWORD"))
			return 0.0f;
		return super.getModifier(player, group);
	}
}
