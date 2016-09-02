package com.hepolite.pangaea.skills;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.events.PlayerCastSkillEvent;
import com.hepolite.pillar.database.Database;
import com.hepolite.pillar.database.PlayerData;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillSwipe extends SkillCastTriggered
{

	public SkillSwipe()
	{
		super("Swipe", false);
		setTickRate(1);
	}

	@Override
	public void onSkillTick(Player player, PlayerClass race, PlayerSkill skill, int tickNumber)
	{
		PlayerData data = Database.getPlayerData(player);
		if (!data.has(getName()))
			return;

		Location location = player.getLocation();
		location.setYaw(location.getYaw() + 360.0f / 7.0f);
		player.teleport(location);
		player.setVelocity(Pangaea.getInstance().getSkillManager().getPlayerVelocity(player));
	}

	@Override
	protected boolean onCast(PlayerCastSkillEvent event)
	{
		Database.getPlayerData(event.getPlayer()).set(getName(), true, 7);
		return true;
	}
}
