package com.hepolite.pangaea.skills;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillFertilizing extends Skill
{
	public SkillFertilizing()
	{
		super("Fertilizing");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || event.getAction() != Action.RIGHT_CLICK_BLOCK || item.getType() != Material.INK_SACK || item.getDurability() != (short) 15)
			return;
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null)
			return;
		Material material = event.getClickedBlock().getType();
		if (material != Material.POTATO && material != Material.CARROT && material != Material.CROPS && material != Material.BEETROOT_BLOCK && material != Material.MELON_STEM && material != Material.PUMPKIN_STEM)
			return;

		if (grow(event.getClickedBlock()))
			player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
	}

	/** Grows crops if it isn't fully grown yet */
	@SuppressWarnings("deprecation")
	private final boolean grow(Block block)
	{
		// CURSE YOU, MINECRAFT! I *HATE* your special cases for crap...
		if (block.getType() == Material.BEETROOT_BLOCK)
		{
			if (block.getData() == 3)
				return false;
			block.setData((byte) 3);
			return true;
		}
		if (block.getData() == 7)
			return false;
		block.setData((byte) 7);
		return true;
	}
}
