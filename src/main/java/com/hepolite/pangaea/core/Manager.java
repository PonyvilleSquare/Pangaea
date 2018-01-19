package com.hepolite.pangaea.core;

import com.hepolite.pillar.listener.Listener;
import com.hepolite.pillar.settings.Settings;

public class Manager extends Listener
{
	// Control variables
	protected final Settings settings;

	public Manager(final Settings settings)
	{
		this.settings = settings;
	}
	
	/** Initializes the manager */
	public final void initialize()
	{
		if (settings != null)
			settings.initialize();
	}

	/** Returns the settings object */
	public final Settings getSettings()
	{
		return settings;
	}
}
