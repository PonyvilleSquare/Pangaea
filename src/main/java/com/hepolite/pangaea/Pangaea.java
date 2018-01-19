package com.hepolite.pangaea;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.hepolite.pangaea.account.AccountManager;
import com.hepolite.pangaea.entities.EntityManager;
import com.hepolite.pangaea.flight.FlightManager;
import com.hepolite.pangaea.hunger.HungerManager;
import com.hepolite.pangaea.instruction.InstructionManager;
import com.hepolite.pangaea.movement.MovementManager;
import com.hepolite.pangaea.permission.PermissionManager;
import com.hepolite.pangaea.rp.RoleplayManager;
import com.hepolite.pangaea.skills.SkillManager;

public class Pangaea extends JavaPlugin
{
	// Control variables
	private static Pangaea instance = null;

	private InstructionManager instructionManager = null;
	private HungerManager hungerManager = null;
	private MovementManager movementManager = null;
	private FlightManager flightManager = null;
	private SkillManager skillManager = null;
	private AccountManager accountManager = null;
	private PermissionManager permissionManager = null;
	private EntityManager entityManager = null;
	private RoleplayManager roleplayManager = null;

	@Override
	public void onEnable()
	{
		instance = this;

		instructionManager = new InstructionManager();
		hungerManager = new HungerManager();
		movementManager = new MovementManager();
		flightManager = new FlightManager();
		skillManager = new SkillManager();
		accountManager = new AccountManager();
		permissionManager = new PermissionManager();
		entityManager = new EntityManager();
		roleplayManager = new RoleplayManager();

		hungerManager.initialize();
		movementManager.initialize();
		skillManager.initialize();
		accountManager.initialize();
		permissionManager.initialize();
		entityManager.initialize();
	}

	@Override
	public void onDisable()
	{
		hungerManager.onShutdown();
		skillManager.onShutdown();
		accountManager.onShutdown();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return instructionManager.onCommand(sender, cmd, label, args);
	}

	// //////////////////////////////////////////////////////////////

	/** Returns the Pangaea plugin instance */
	public final static Pangaea getInstance()
	{
		return instance;
	}

	/** Returns the instruction manager */
	public final InstructionManager getInstructionManager()
	{
		return instructionManager;
	}

	/** Returns the hunger manager */
	public final HungerManager getHungerManager()
	{
		return hungerManager;
	}

	/** Returns the movement manager */
	public final MovementManager getMovementManager()
	{
		return movementManager;
	}

	/** Returns the flight manager */
	public final FlightManager getFlightManager()
	{
		return flightManager;
	}

	/** Returns the skill manager */
	public final SkillManager getSkillManager()
	{
		return skillManager;
	}

	/** Returns the account manager */
	public final AccountManager getAccountManager()
	{
		return accountManager;
	}

	/** Returns the permission manager */
	public final PermissionManager getPermissionManager()
	{
		return permissionManager;
	}
	
	/** Returns the entity manager */
	public final EntityManager getEntityManager()
	{
		return entityManager;
	}
	
	/** Returns the roleplay manager */
	public final RoleplayManager getRoleplayManager()
	{
		return roleplayManager;
	}
}
