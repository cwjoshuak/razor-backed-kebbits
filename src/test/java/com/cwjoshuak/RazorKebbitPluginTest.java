package com.cwjoshuak;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RazorKebbitPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RazorKebbitPlugin.class);
		RuneLite.main(args);
	}
}
