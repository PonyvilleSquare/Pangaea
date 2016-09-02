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

public class SkillGreenHooves extends Skill
{
	public SkillGreenHooves()
	{
		super("Green Hooves");
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
		if (material != Material.RED_ROSE && material != Material.YELLOW_FLOWER && material != Material.DOUBLE_PLANT)
			return;

		if (grow(event.getClickedBlock()))
			player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
	}

	/** Grows a flower from another flower */
	@SuppressWarnings("deprecation")
	private final boolean grow(Block block)
	{
		boolean grew = false;
		for (int x = -2; x <= 2; x++)
			for (int z = -2; z <= 2; z++)
				for (int y = 0; y <= 2; y++)
				{
					Block currentBlock = block.getRelative(x, y, z);
					if (random.nextFloat() < 0.35f && currentBlock.getType() == Material.AIR && currentBlock.getRelative(0, -1, 0).getType() == Material.GRASS)
					{
						grew = true;
						if (random.nextFloat() < 0.2f && currentBlock.getRelative(0, 1, 0).getType() == Material.AIR) // 20% chance of growing a large flower
						{
							byte[] possiblePlants = new byte[] { 0, 1, 4, 5 };
							byte plant = possiblePlants[random.nextInt(possiblePlants.length)];
							currentBlock.setType(Material.DOUBLE_PLANT);
							currentBlock.setData(plant);
							currentBlock.getRelative(0, 1, 0).setType(Material.DOUBLE_PLANT);
							currentBlock.getRelative(0, 1, 0).setData((byte) (plant + 8));
						}
						else if (random.nextFloat() < 0.1f) // Ten flowers are available in total; two different block ids are used, though
							currentBlock.setType(Material.YELLOW_FLOWER);
						else
						{
							currentBlock.setType(Material.RED_ROSE);
							currentBlock.setData((byte) random.nextInt(10)); // At most nine different flowers are possible on this block id
						}
					}
				}
		return grew;
	}
}
