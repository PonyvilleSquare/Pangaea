package com.hepolite.pangaea.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillLeapOfJoy extends SkillMovementDash
{
	public SkillLeapOfJoy()
	{
		super("Leap of Joy", true);
	}

	@Override
	protected void onTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);

		// Check if the leap should be cancelled early or not
		if (!player.getLocation().getBlock().isLiquid() || player.getEyeLocation().getBlock().getRelative(0, 1, 0).getType().isSolid())
		{
			data.remove(getName());
			return;
		}
		player.setFlying(false);

		// Calculate the velocity of the player
		Vector velocity = (Vector) data.get(getName());
		double direction = Math.atan2(velocity.getZ(), velocity.getX());
		float angle = getSettings().getFloat(race.getData().getName() + "." + getName() + ".angle") * (float) Math.PI / 180.0f;
		float factor = getSettings().getFloat(race.getData().getName() + "." + getName() + ".rate");
		float speed = getSettings().getFloat(race.getData().getName() + "." + getName() + ".speed");

		speed = (float) velocity.length() * (1.0f - factor) + speed * factor;
		double x = speed * Math.cos(direction) * Math.cos(angle);
		double y = speed * Math.sin(angle);
		double z = speed * Math.sin(direction) * Math.cos(angle);
		velocity = (velocity.multiply(1.0f - factor)).add((new Vector(x, y, z)).multiply(factor));

		data.set(getName(), velocity);
	}
}
