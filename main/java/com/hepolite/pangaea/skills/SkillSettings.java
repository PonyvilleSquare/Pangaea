package com.hepolite.pangaea.skills;

import com.hepolite.pangaea.Pangaea;
import com.hepolite.pillar.settings.Settings;

public class SkillSettings extends Settings
{
	private Settings persistentSkillData;

	public SkillSettings()
	{
		super(Pangaea.getInstance(), "Skills");
	}

	@Override
	public void onReload()
	{
		loadSkillData();
	}

	@Override
	public void onSave()
	{
		saveSkillData();
	}

	// /////////////////////////////////////////////////////////////////////

	/** Returns the persisiten skill data structure */
	public final Settings getSkillData()
	{
		return persistentSkillData;
	}

	/** Loads up all the persistent skill data */
	private final void loadSkillData()
	{
		persistentSkillData = new Settings(Pangaea.getInstance(), "SkillData")
		{
		};
		persistentSkillData.initialize();

		for (Skill skill : Pangaea.getInstance().getSkillManager().getSkills())
			skill.onReload(persistentSkillData);
	}

	/** Saves all the persistent skill data */
	public final void saveSkillData()
	{
		if (persistentSkillData == null)
			return;
		for (Skill skill : Pangaea.getInstance().getSkillManager().getSkills())
			skill.onSave(persistentSkillData);
		persistentSkillData.save();
	}

	// /////////////////////////////////////////////////////////////////////
	// CONFIG // CONFIG // CONFIG // CONFIG // CONFIG // CONFIG // CONFIG //
	// /////////////////////////////////////////////////////////////////////

	/** Returns a float from the config, using the default section if the alternative doesn't exist */
	public final float getFloat(String def, String alt, String property)
	{
		if (has(alt + "." + property))
			return getFloat(alt + "." + property);
		return getFloat(def + "." + property);
	}

	/** Returns an integer from the config, using the default section if the alternative doesn't exist */
	public final int getInt(String def, String alt, String property)
	{
		if (has(alt + "." + property))
			return getInt(alt + "." + property);
		return getInt(def + "." + property);
	}
}
