package com.hepolite.pangaea.skills;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.hepolite.pillar.logging.Log;
import com.hepolite.pillar.utility.EntityHelper;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillAdvancedBrewer extends Skill
{
	public SkillAdvancedBrewer()
	{
		super("Advanced Brewer");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBrew(BrewEvent event)
	{
		List<Player> players = EntityHelper.getPlayersInRange(event.getBlock().getLocation(), 5.0f);
		for (Player player : players)
		{
			PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
			if (skill == null)
				continue;
			String race = SkillAPIHelper.getRaceName(player);
			
			for (ItemStack item : event.getContents().getContents())
			{
				float chance = getSettings().getFloat(race + "." + getName() + ".chance");
				if (random.nextFloat() > chance)
					continue;
				
				if (isValid(item))
					upgrade(item);
			}
		}
	}
	
	/** Validates that the item is appropriate for upgrading */
	private final boolean isValid(ItemStack item)
	{
		if (item == null || item.getType() != Material.POTION)
			return false;
		PotionMeta meta = (PotionMeta) item.getItemMeta();
		
		boolean isAmplified = meta.getBasePotionData().isUpgraded();
		boolean isExtended = meta.getBasePotionData().isExtended();
		boolean isSplash = ((item.getDurability() & 0x4000) != 0);
		
		Log.log("Potion properties:");
		Log.log(String.format("Amplified: %b", isAmplified));
		Log.log(String.format("Extended: %b", isExtended));
		Log.log(String.format("Splash: %b", isSplash));
		Log.log("");
		
		return isAmplified && isExtended && isSplash;
	}
	
	/** Takes in one itemstack, upgrading the potion effects in that item */
	private final void upgrade(ItemStack item)
	{
		
	}
}
