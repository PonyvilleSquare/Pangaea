package com.hepolite.pangaea.hunger;

public class HungerNode
{
	public float max = 0.0f;
	public float hunger = 0.0f;
	public float saturation = 0.0f;
	public float exhaustion = 0.0f;
	
	public HungerNode(float max, float hunger, float saturation, float exhaustion)
	{
		this.max = max;
		this.hunger = hunger;
		this.saturation = saturation;
		this.exhaustion = exhaustion;
	}
}
