package com.hepolite.pangaea.skills;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.hepolite.pangaea.utility.InventoryHelper;
import com.hepolite.pangaea.utility.SkillAPIHelper;
import com.sucy.skill.api.player.PlayerSkill;

public class SkillCloudSeed extends Skill
{
	// TODO: Remove the "Cloud Seed" block after some amount of time

	public SkillCloudSeed()
	{
		super("Cloud Seed");
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || event.getAction() != Action.RIGHT_CLICK_AIR || item.getType() != Material.SNOW_BLOCK)
			return;
		PlayerSkill skill = SkillAPIHelper.getSkill(player, getName());
		if (skill == null)
			return;

		Location location = player.getEyeLocation();
		Block block = location.add(location.getDirection().multiply(3.0f)).getBlock();
		if (place(player, block))
			InventoryHelper.removeItem(player, new ItemStack(Material.SNOW_BLOCK, 1));
	}

	/** Attempts to place a cloud block at the given block */
	private final boolean place(Player player, Block block)
	{
		BlockState state = block.getState();
		block.setType(Material.SNOW_BLOCK);
		BlockPlaceEvent blockPlaceEvent = new BlockPlaceEvent(state.getBlock(), state, block, new ItemStack(Material.SNOW_BLOCK), player, false, EquipmentSlot.HAND);
		post(blockPlaceEvent);
		if (blockPlaceEvent.isCancelled())
		{
			block.setType(Material.AIR);
			return false;
		}
		block.getWorld().playSound(block.getLocation(), Sound.BLOCK_SNOW_PLACE, 0.5f, 0.0f);
		return true;
	}
}
