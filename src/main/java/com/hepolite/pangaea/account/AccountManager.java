package com.hepolite.pangaea.account;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pangaea.core.Manager;
import com.hepolite.pillar.chat.Chat;
import com.hepolite.pillar.settings.Settings;

public class AccountManager extends Manager
{
	int timer = 0;
	int maxAccounts = 0;
	int waitTime = 0;

	public AccountManager()
	{
		super(new Settings(Pangaea.getInstance(), "Accounts")
		{
		});
		load();
	}

	@Override
	public void onTick()
	{
		timer++;
		if (timer % 12000 == 0)
			save();
	}

	/** Invoked whenever the server is shutting down */
	public void onShutdown()
	{
		save();
	}

	/** Saves the config file and everything it contains */
	private final void save()
	{
		for (Entry<UUID, PlayerAccount> entry : playerAccounts.entrySet())
		{
			String path = "Player." + entry.getKey().toString() + ".";
			settings.set(path + "current", entry.getValue().currentAccount);
			settings.set(path + "last", entry.getValue().lastChange);
			settings.set(path + "next", entry.getValue().nextChange);
		}

		settings.set("General.age", timer);
		settings.save();
	}

	/** Loads up the config file and everything it contains */
	private final void load()
	{
		settings.reload();
		timer = settings.getInt("General.age");
		maxAccounts = settings.getInt("General.maxAccounts");
		waitTime = settings.getInt("General.waitTime");

		Set<String> players = settings.getKeys("Player");
		for (String player : players)
		{
			String path = "Player." + player + ".";
			UUID uuid = UUID.fromString(player);
			PlayerAccount account = new PlayerAccount();
			account.currentAccount = settings.getInt(path + "current");
			account.lastChange = settings.getInt(path + "last");
			account.nextChange = settings.getInt(path + "next");
			playerAccounts.put(uuid, account);
		}
	}

	/** Attempts to perform an account swap for the given player */
	public final void changeAccount(Player player, int accountId)
	{
		UUID uuid = player.getUniqueId();
		PlayerAccount account = (playerAccounts.containsKey(uuid)) ? playerAccounts.get(uuid) : new PlayerAccount();

		if (account.currentAccount == accountId)
		{
			Chat.message(player, "&cYou are currently using that account");
			return;
		}
		if (accountId < 1 || accountId > maxAccounts)
		{
			Chat.message(player, "&cYou may only use accounts from 1 to " + maxAccounts);
			return;
		}
		if (account.nextChange > timer)
		{
			int duration = account.nextChange - timer;
			int days = duration / (20 * 60 * 60 * 24);
			int hours = (duration - 20 * 60 * 60 * 24 * days) / (20 * 60 * 60);
			int minutes = (duration - 20 * 60 * 60 * (24 * days + hours)) / (20 * 60);
			if (days == 0 && hours == 0)
				Chat.message(player, String.format("&cYou may not change your account for another &6%d&c minutes", minutes));
			else if (days == 0)
				Chat.message(player, String.format("&cYou may not change your account for another &6%d&c hours and &6%d&c minutes", hours, minutes));
			else
				Chat.message(player, String.format("&cYou may not change your account for another &6%d&c days, &6%d&c hours and &6%d&c minutes", days, hours, minutes));
			return;
		}

		account.currentAccount = accountId;
		account.lastChange = timer;
		account.nextChange = timer + waitTime;
		player.performCommand("class acc " + accountId);
		playerAccounts.put(uuid, account);
	}

	public void changeAccountForcefully(Player player, int accountId)
	{
		UUID uuid = player.getUniqueId();
		PlayerAccount account = (playerAccounts.containsKey(uuid)) ? playerAccounts.get(uuid) : new PlayerAccount();

		account.currentAccount = accountId;
		account.lastChange = timer;
		account.nextChange = timer + waitTime;
		player.performCommand("class acc " + accountId);
		playerAccounts.put(uuid, account);
	}

	// ///////////////////////////////////////////////////////////////////

	private final HashMap<UUID, PlayerAccount> playerAccounts = new HashMap<UUID, PlayerAccount>();

	public final static class PlayerAccount
	{
		public int currentAccount = 1;	// The current account the player is using
		public int lastChange = -1;		// When the last change happened
		public int nextChange = -1;		// When the next change is allowed to happen
	}
}
