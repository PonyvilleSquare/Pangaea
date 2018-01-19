package com.hepolite.pangaea.utility;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import com.hepolite.pillar.settings.Settings;

public class LootHelper
{
	private final static Random random = new Random();

	/** Attempts to find a random section under the given section; will return the name of that section. Returns null if no sections could be found */
	public final static String getLootSection(Settings settings, String section)
	{
		Set<String> sections = settings.getKeys(section);
		TreeMap<Float, String> map = new TreeMap<Float, String>();

		// Build the map of valid sections
		float totalWeight = 0.0f;
		for (String currentSection : sections)
		{
			if (settings.has(section + "." + currentSection + ".weight"))
			{
				totalWeight += settings.getFloat(section + "." + currentSection + ".weight");
				map.put(totalWeight, currentSection);
			}
		}

		// Grab one of the nodes in the tree
		float old = 0.0f;
		float index = totalWeight * random.nextFloat();
		for (Iterator<Entry<Float, String>> it = map.entrySet().iterator(); it.hasNext();)
		{
			Entry<Float, String> entry = it.next();
			if (old <= index && entry.getKey() > index)
				return entry.getValue();
			old = entry.getKey();
		}
		return null;
	}
}
