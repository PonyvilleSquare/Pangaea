package com.hepolite.pangaea.movement;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pillar.settings.Settings;

public class MovementSettings extends Settings
{
	public MovementSettings()
	{
		super(Pangaea.getInstance(), "Movement");
	}

	// /////////////////////////////////////////////////////////////////////
	// CONFIG // CONFIG // CONFIG // CONFIG // CONFIG // CONFIG // CONFIG //
	// /////////////////////////////////////////////////////////////////////

	/** Returns a property that exists, preferring the main. Returns the alternative if the main doesn't exist */
	public final String getPath(String main, String alternative, String property)
	{
		if (has(main + "." + property))
			return main + "." + property;
		return alternative + "." + property;
	}
}
