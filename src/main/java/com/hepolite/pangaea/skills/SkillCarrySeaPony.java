package com.hepolite.pangaea.skills;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.util.Vector;

import com.hepolite.pangaea.events.PlayerExhaustionChangeEvent;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillCarrySeaPony extends SkillCarry
{
	public SkillCarrySeaPony()
	{
		super("Carry (Sea Pony)");
		setTickRate(5);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);
		if (!data.has(getName()))
			return;

		Player target = Bukkit.getPlayer((UUID) data.get(getName()));
		if (target != null && target.isValid())
		{
			Vector delta = player.getLocation().subtract(target.getLocation()).toVector();

			// Check for ejection states
			if (delta.lengthSquared() > 64.0 || !player.getLocation().getBlock().isLiquid() || target.isSneaking())
				data.remove(getName());
			
			// Drag the rider along
			else
			{
				target.setFlying(false);
				target.setVelocity(delta.multiply(0.25f));
			}
		}
		else
			data.remove(getName());
	}

	@Override
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerGainExhaustion(PlayerExhaustionChangeEvent event)
	{
		Player player = event.getPlayer();
		if (!Database.getPlayerData(player).has(getName()) || event.getNewExhaustion() <= event.getOldExhaustion())
			return;
		PlayerClass race = SkillAPIHelper.getRace(player);
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (race == null || skill == null)
			return;

		float modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".exhaustionModifier");
		float change = event.getNewExhaustion() - event.getOldExhaustion();
		event.setNewExhaustion(event.getOldExhaustion() + (1.0f + modifier) * change);
	}

	@Override
	protected float getModifier(Player player, String group)
	{
		if (!Database.getPlayerData(player).has(getName()))
			return 0.0f;
		return super.getModifier(player, group);
	}
}
