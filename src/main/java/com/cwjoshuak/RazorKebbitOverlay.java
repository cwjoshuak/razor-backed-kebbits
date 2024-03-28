package com.cwjoshuak;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;

import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ColorUtil;

class RazorKebbitOverlay extends Overlay {
	private final RazorKebbitPlugin plugin;
	private final RazorKebbitConfig config;


	@Inject
	public RazorKebbitOverlay(RazorKebbitPlugin plugin, RazorKebbitConfig config) {
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		if (!plugin.isInRazorKebbitArea()) {
			return null;
		}
		int finishId = plugin.getFinishId();

		// Draw start objects
		if (config.isStartShown() && (finishId == 0) && plugin.getCurrentPath().isEmpty()) {
			plugin.getBurrows().values().forEach((obj) -> OverlayUtil.renderTileOverlay(graphics, obj, "", config.getStartColor()));
		}

		// Draw trail objects
		if (config.isObjectShown() && !(finishId > 0) && !plugin.getCurrentPath().isEmpty()) {
			WorldPoint correct = Iterables.getLast(plugin.getCurrentPath());
			TileObject object = plugin.getTrailObjects().get(correct);
			drawObjectLocation(graphics, object, config.getObjectColor());
		}

		// Draw finish tunnels
		if (config.isBushesShown() && finishId > 0) {
			WorldPoint finishLoc = plugin.getEndLocations().get(finishId - 1);
			TileObject object = plugin.getBushes().get(finishLoc);
			drawObjectLocation(graphics, object, config.getBushesColor());
		}

		return null;
	}

	private void drawObjectLocation(Graphics2D graphics, TileObject object, Color color) {
		if (object == null) {
			return;
		}

		if (config.showClickBoxes()) {
			Shape clickbox = object.getClickbox();
			if (clickbox != null) {
				Color clickBoxColor = ColorUtil.colorWithAlpha(color, color.getAlpha() / 12);

				graphics.setColor(color);
				graphics.draw(clickbox);
				graphics.setColor(clickBoxColor);
				graphics.fill(clickbox);
			}
		} else {
			OverlayUtil.renderTileOverlay(graphics, object, "", color);
		}
	}
}
