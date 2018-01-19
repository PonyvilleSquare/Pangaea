package com.hepolite.pangaea.entities;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pillar.settings.Settings;
import com.hepolite.pillar.utility.EntityHelper;

public class EntityFish extends Entity
{
	private Bat bat;
	private Item fish;

	private double timer = 0.0;

	private boolean isInWater = true;
	private boolean isFleeing = false;
	private float swimLevel = 64.0f;
	private float swimSpeed = 0.15f, swimFleeSpeed = 0.5f;
	private boolean xAxis = true;

    @Override
	public void onSpawn()
	{
		int type = 0;
		if (random.nextFloat() < 0.05f)
			type = 3;
		else if (random.nextFloat() < 0.1f)
			type = 2;
		else if (random.nextFloat() < 0.2f)
			type = 1;
		else
			type = 0;

		ItemStack item = new ItemStack(Material.RAW_FISH, 1, (short) type);
		fish = location.getWorld().dropItemNaturally(location, item);
		bat = (Bat) location.getWorld().spawnEntity(location, EntityType.BAT);
		bat.setPassenger(fish);
		bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0, true), true);
		bat.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 100000, 0, true), true);
	}

	@Override
	public void onDespawn()
	{
		if (bat != null)
			bat.remove();
		if (fish != null)
			fish.remove();
	}

	@Override
	public void onTick()
	{
		if (bat == null || bat.isDead() || !bat.isValid() || bat.getPassenger() == null)
		{
			if (bat != null && bat.isDead())
				fish = null;
			despawn();
			return;
		}
		timer += 2.0 * Math.PI / 200.0;

		updateState();

		bat.setVelocity(calculateVelocity());
		setLocation(bat.getLocation());
	}

	/** Obtains the state of the fish */
	private final void updateState()
	{
		Settings settings = Pangaea.getInstance().getEntityManager().getSettings();

		isInWater = location.getBlock().isLiquid() && location.getBlock().getRelative(0, 1, 0).isLiquid();
		isFleeing = !EntityHelper.getPlayersInRange(location, 12.0f).isEmpty();

		if (random.nextFloat() < 0.004f)
		{
			float min = settings.getFloat("Fish.minDepth");
			float max = settings.getFloat("Fish.maxDepth");
			swimLevel = min + (max - min) * random.nextFloat();
		}
		if (random.nextFloat() < 0.005f)
			xAxis = !xAxis;
		if (random.nextFloat() < 0.005f)
		{
			float min = settings.getFloat("Fish.minSpeed");
			float max = settings.getFloat("Fish.maxSpeed");
			swimSpeed = min + (max - min) * random.nextFloat();
			min = settings.getFloat("Fish.minFleeSpeed");
			max = settings.getFloat("Fish.maxFleeSpeed");
			swimFleeSpeed = min + (max - min) * random.nextFloat();
		}
	}

	/** Calculates the velocity of the fish */
	private final Vector calculateVelocity()
	{
		if (!isInWater)
			return fish.getVelocity().add(new Vector(0.0, -0.04, 0.0));
		if (isFleeing)
			return calculateFleeVector();
		return calculateStandardVelocity();
	}

	/** Gets a standard velocity profile */
	private final Vector calculateStandardVelocity()
	{
		double t = timer % (2.0 * Math.PI);
		double x = swimSpeed * Math.cos(t);
		double z = swimSpeed * x * Math.sin(t);

		double h = swimLevel - location.getY();
		double y = 0.1f * swimSpeed * h;

		return xAxis ? new Vector(x, y, z) : new Vector(z, y, x);
	}

	/** Calculates the fleeing velocity */
	private final Vector calculateFleeVector()
	{
		Collection<Player> players = EntityHelper.getPlayersInRange(location, 12.0f);
		Player nearest = null;
		double distance = 12.0;
		for (Player player : players)
		{
			double d = location.distance(player.getLocation());
			if (d <= distance)
			{
				distance = d;
				nearest = player;
			}
		}
		if (nearest != null)
		{
			double factor = 1.0 - distance / 12.0;
			Vector v = location.toVector().subtract(nearest.getLocation().toVector()).normalize().multiply(swimFleeSpeed * factor);

			double h = swimLevel - location.getY();
			double y = 0.1f * swimSpeed * h;

			v.setY(Math.abs(y) > Math.abs(v.getY()) ? y : v.getY());

			return v;
		}
		return new Vector(0.0, 0.0, 0.0);
	}
}
