package com.hepolite.pangaea.skills;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillTeamwork extends Skill
{
	private final HashMap<UUID, MobNode> hurtMobs = new HashMap<UUID, MobNode>();

	private int timer = 0;

	public SkillTeamwork()
	{
		super("Teamwork");
	}

	@Override
	public void onTick()
	{
		if (timer++ % 20 != 0)
			return;
		List<UUID> mobsToRemove = new LinkedList<UUID>();
		for (Entry<UUID, MobNode> entry : hurtMobs.entrySet())
		{
			entry.getValue().lifetime -= 20;
			if (entry.getValue().lifetime <= 0)
				mobsToRemove.add(entry.getKey());
		}
		for (UUID uuid : mobsToRemove)
			hurtMobs.remove(uuid);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDealDamage(EntityDamageByEntityEvent event)
	{
		Player attacker = getAttacker(event);
		if (attacker == null)
			return;

		MobNode node = hurtMobs.get(event.getEntity().getUniqueId());
		if (shouldAmplifyDamage(node, attacker))
			event.setDamage(Math.max(0.0, event.getDamage() * (1.0f + node.modifier)));
		if (node == null || !node.attacker.equals(attacker.getUniqueId()))
		{
			PlayerClass race = SkillAPIHelper.getRace(attacker);
			PlayerSkill skill = SkillAPIHelper.getSkill(attacker, getName());
			if (race == null || skill == null)
				return;
			float modifier = getSettings().getFloat(race.getData().getName() + "." + getName() + ".modifier");
			hurtMobs.put(event.getEntity().getUniqueId(), new MobNode(attacker, modifier));
		}
	}

	/** Returns a player if the damage should be amplified, null otherwise */
	private final boolean shouldAmplifyDamage(MobNode node, Player attacker)
	{
		return (node == null ? false : !node.attacker.equals(attacker.getUniqueId()));
	}

	/** Returns a player object from the event, if a player was attacking */
	private final Player getAttacker(EntityDamageByEntityEvent event)
	{
		Entity attacker = event.getDamager();
		if (attacker instanceof Projectile)
		{
			ProjectileSource source = ((Projectile) attacker).getShooter();
			if (source instanceof LivingEntity)
				attacker = (Entity) source;
		}
		return (attacker instanceof Player ? (Player) attacker : null);
	}

	private final class MobNode
	{
		public int lifetime = 300;
		public final UUID attacker;
		public final float modifier;

		public MobNode(Player attacker, float modifier)
		{
			this.attacker = attacker.getUniqueId();
			this.modifier = modifier;
		}
	}
}
