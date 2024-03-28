package com.cwjoshuak;

import java.awt.Color;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface RazorKebbitConfig extends Config {
	@ConfigItem(
		position = 0,
		keyName = "showStart",
		name = "Show Start Objects",
		description = "Show highlights for starting burrows"
	)
	default boolean isStartShown() {
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "showClickboxes",
		name = "Show Clickboxes",
		description = "Show clickboxes on trail objects and tunnels instead of tiles"
	)
	default boolean showClickBoxes() {
		return false;
	}

	@Alpha
	@ConfigItem(
		position = 2,
		keyName = "colorStart",
		name = "Start Color",
		description = "Color for burrows that start the trails"
	)
	default Color getStartColor() {
		return Color.CYAN;
	}

	@ConfigItem(
		position = 3,
		keyName = "showBushes",
		name = "Show End Bushes",
		description = "Show highlights for bushes with razor-backed kebbits"
	)
	default boolean isBushesShown() {
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 4,
		keyName = "colorBushes",
		name = "Bush Color",
		description = "Color for tunnels with razor-backed kebbits"
	)
	default Color getBushesColor() {
		return Color.GREEN;
	}

	@ConfigItem(
		position = 5,
		keyName = "showObject",
		name = "Show Trail Objects",
		description = "Show highlights for plants"
	)
	default boolean isObjectShown() {
		return true;
	}

	@Alpha
	@ConfigItem(
		position = 6,
		keyName = "colorGameObject",
		name = "Trail Object Color",
		description = "Color for plants"
	)
	default Color getObjectColor() {
		return Color.CYAN;
	}

	@ConfigItem(
		position = 7,
		keyName = "dynamicMenuEntrySwap",
		name = "Dynamically swap trail menu entries",
		description = "Swap menu entries to only make the correct trail clickable."
	)
	default boolean dynamicMenuEntrySwap() {
		return true;
	}
}
